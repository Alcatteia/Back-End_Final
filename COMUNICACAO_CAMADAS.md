# Comunicação Entre Camadas - Boas Práticas

## Visão Geral

Este documento define o padrão de comunicação entre as camadas Controller, Service e Repository para garantir que erros sejam propagados corretamente e o usuário receba informações claras.

## Arquitetura de Camadas

```
┌─────────────────┐
│   CONTROLLER    │ ← Recebe requisições HTTP
│                 │ ← NÃO trata exceções (deixa para GlobalExceptionHandler)
│                 │ ← Apenas valida parâmetros HTTP básicos
└─────────────────┘
         │
         ▼
┌─────────────────┐
│    SERVICE      │ ← Contém lógica de negócio
│                 │ ← Faz validações de regras de negócio
│                 │ ← Lança exceções específicas (BusinessException, etc.)
│                 │ ← Coordena operações entre repositories
└─────────────────┘
         │
         ▼
┌─────────────────┐
│   REPOSITORY    │ ← Acesso aos dados
│                 │ ← Pode lançar EntityNotFoundException
│                 │ ← Não faz validações de negócio
└─────────────────┘
         │
         ▼
┌─────────────────┐
│ GlobalException │ ← Captura TODAS as exceções
│    Handler      │ ← Converte para ErrorResponse padronizado
│                 │ ← Retorna HTTP status apropriado
└─────────────────┘
```

## Padrões por Camada

### 1. **Controllers - O QUE FAZER**

#### ✅ **Bom Exemplo:**
```java
@RestController
@RequestMapping("/api/tarefas")
public class TarefaController {
    
    private final TarefaService tarefaService;
    
    @PostMapping
    public ResponseEntity<TarefaDTO> criar(@Valid @RequestBody TarefaDTO dto) {
        // ✅ Deixa o service tratar validações e exceções
        TarefaDTO tarefa = tarefaService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(tarefa);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TarefaDTO> buscar(@PathVariable Integer id) {
        // ✅ Service lança EntityNotFoundException se não encontrar
        TarefaDTO tarefa = tarefaService.buscarPorId(id);
        return ResponseEntity.ok(tarefa);
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<TarefaDTO> atualizarStatus(
            @PathVariable Integer id, 
            @RequestBody Map<String, String> dados) {
        
        String novoStatus = dados.get("status");
        // ✅ Service valida se status é válido
        TarefaDTO tarefa = tarefaService.atualizarStatus(id, novoStatus);
        return ResponseEntity.ok(tarefa);
    }
}
```

#### ❌ **Evitar:**
```java
@PostMapping
public ResponseEntity<?> criar(@RequestBody TarefaDTO dto) {
    try {
        // ❌ Controller não deve fazer validações de negócio
        if (dto.getTitulo() == null) {
            return ResponseEntity.badRequest().body("Título obrigatório");
        }
        
        TarefaDTO tarefa = tarefaService.criar(dto);
        return ResponseEntity.ok(tarefa);
    } catch (Exception e) {
        // ❌ Captura genérica perde informação do erro
        return ResponseEntity.badRequest().build();
    }
}
```

### 2. **Services - O QUE FAZER**

#### ✅ **Bom Exemplo:**
```java
@Service
@RequiredArgsConstructor
public class TarefaService {
    
    private final TarefaRepository repository;
    private final UsuarioRepository usuarioRepository;
    
    public TarefaDTO criar(TarefaDTO dto) {
        // ✅ Validações específicas com exceções claras
        ValidationUtils.validateNotEmpty(dto.getTitulo(), "Título");
        ValidationUtils.validateMaxLength(dto.getTitulo(), 255, "Título");
        ValidationUtils.validateNotNull(dto.getCategoriaId(), "Categoria");
        
        // ✅ Verificar se entidades relacionadas existem
        if (!categoriaRepository.existsById(dto.getCategoriaId())) {
            throw new EntityNotFoundException("Categoria não encontrada com ID: " + dto.getCategoriaId());
        }
        
        // ✅ Validações de regra de negócio específicas
        if (dto.getDataLimite() != null && dto.getDataLimite().isBefore(LocalDate.now())) {
            throw new BusinessException("Data limite não pode ser no passado");
        }
        
        Tarefa tarefa = convertToEntity(dto);
        Tarefa tarefaSalva = repository.save(tarefa);
        return convertToDTO(tarefaSalva);
    }
    
    public TarefaDTO buscarPorId(Integer id) {
        // ✅ Validação de parâmetro
        ValidationUtils.validateNotNull(id, "ID da tarefa");
        ValidationUtils.validatePositiveId(id, "ID da tarefa");
        
        // ✅ Exceção específica se não encontrar
        Tarefa tarefa = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada com ID: " + id));
        
        return convertToDTO(tarefa);
    }
    
    public TarefaDTO atualizarStatus(Integer id, String novoStatus) {
        // ✅ Validações de entrada
        ValidationUtils.validateNotNull(id, "ID da tarefa");
        ValidationUtils.validateNotEmpty(novoStatus, "Status");
        
        // ✅ Validação de regra de negócio
        List<String> statusValidos = List.of("PENDENTE", "EM_ANDAMENTO", "CONCLUIDA");
        ValidationUtils.validateInList(novoStatus, statusValidos, "Status");
        
        Tarefa tarefa = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada com ID: " + id));
        
        // ✅ Regra de negócio específica
        if ("CONCLUIDA".equals(tarefa.getStatus()) && !"CONCLUIDA".equals(novoStatus)) {
            throw new BusinessException("Não é possível alterar status de tarefa já concluída");
        }
        
        tarefa.setStatus(novoStatus);
        Tarefa tarefaAtualizada = repository.save(tarefa);
        return convertToDTO(tarefaAtualizada);
    }
}
```

#### ❌ **Evitar:**
```java
@Service
public class TarefaService {
    
    public TarefaDTO criar(TarefaDTO dto) {
        // ❌ Não fazer validações ou fazer validações fracas
        Tarefa tarefa = convertToEntity(dto);
        Tarefa tarefaSalva = repository.save(tarefa); // Pode dar erro de SQL
        return convertToDTO(tarefaSalva);
    }
    
    public TarefaDTO buscarPorId(Integer id) {
        // ❌ Retornar null em vez de lançar exceção
        Optional<Tarefa> tarefa = repository.findById(id);
        return tarefa.map(this::convertToDTO).orElse(null);
    }
    
    public TarefaDTO atualizarStatus(Integer id, String novoStatus) {
        try {
            // ❌ Try-catch genérico esconde problemas
            Tarefa tarefa = repository.findById(id).get(); // Pode dar NoSuchElementException
            tarefa.setStatus(novoStatus); // Pode ser status inválido
            repository.save(tarefa);
            return convertToDTO(tarefa);
        } catch (Exception e) {
            // ❌ Perde informação do erro original
            throw new RuntimeException("Erro ao atualizar status");
        }
    }
}
```

### 3. **Validações - Usando ValidationUtils**

#### ✅ **Exemplos de Uso:**
```java
@Service
public class UsuarioService {
    
    public UsuarioDTO criar(UsuarioDTO dto) {
        // Validações básicas
        ValidationUtils.validateNotEmpty(dto.getNome(), "Nome");
        ValidationUtils.validateMaxLength(dto.getNome(), 100, "Nome");
        ValidationUtils.validateNotEmpty(dto.getEmail(), "Email");
        ValidationUtils.validateEmail(dto.getEmail(), "Email");
        
        // Validações de negócio
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Email já está em uso");
        }
        
        ValidationUtils.validateMinLength(dto.getSenha(), 6, "Senha");
        
        // ... resto da lógica
    }
    
    public List<TarefaDTO> listarTarefasPorPeriodo(LocalDate inicio, LocalDate fim) {
        ValidationUtils.validateNotNull(inicio, "Data de início");
        ValidationUtils.validateNotNull(fim, "Data de fim");
        ValidationUtils.validateDateOrder(inicio, fim, "Data de início", "Data de fim");
        
        // Regra de negócio: máximo 1 ano de período
        if (inicio.plusYears(1).isBefore(fim)) {
            throw new BusinessException("Período não pode exceder 1 ano");
        }
        
        // ... resto da lógica
    }
}
```

## Tipos de Exceção por Cenário

### **EntityNotFoundException** (404)
```java
// Quando uma entidade não é encontrada
Usuario usuario = usuarioRepository.findById(id)
    .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + id));
```

### **BusinessException** (400)
```java
// Violações de regras de negócio
if (tarefa.getStatus().equals("CONCLUIDA")) {
    throw new BusinessException("Não é possível editar tarefa concluída");
}

// Validações de dados
if (dto.getDataLimite().isBefore(LocalDate.now())) {
    throw new BusinessException("Data limite não pode ser no passado");
}
```

### **AuthenticationException** (401)
```java
// Problemas de autenticação
if (!passwordEncoder.matches(senha, usuario.getSenha())) {
    throw new AuthenticationException("Credenciais inválidas");
}
```

### **AccessDeniedException** (403)
```java
// Problemas de autorização
if (!currentUserService.canAccessUserData(usuarioId)) {
    throw new AccessDeniedException("Acesso negado para visualizar dados do usuário");
}
```

## Fluxo Completo de Erro

### **1. Requisição com Erro:**
```http
POST /api/tarefas
{
  "titulo": "",
  "categoriaId": 999
}
```

### **2. Controller (não trata erro):**
```java
@PostMapping
public ResponseEntity<TarefaDTO> criar(@Valid @RequestBody TarefaDTO dto) {
    TarefaDTO tarefa = tarefaService.criar(dto); // Exceção é lançada aqui
    return ResponseEntity.status(HttpStatus.CREATED).body(tarefa);
}
```

### **3. Service (lança exceção específica):**
```java
public TarefaDTO criar(TarefaDTO dto) {
    ValidationUtils.validateNotEmpty(dto.getTitulo(), "Título"); // ← EXCEÇÃO AQUI
    // ... resto não executa
}
```

### **4. GlobalExceptionHandler (captura e formata):**
```java
@ExceptionHandler(BusinessException.class)
public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex, WebRequest request) {
    ErrorResponse error = new ErrorResponse(400, "Business Rule Error", ex.getMessage(), request.getPath());
    return ResponseEntity.badRequest().body(error);
}
```

### **5. Resposta Final:**
```json
{
  "status": 400,
  "error": "Business Rule Error",
  "message": "Título é obrigatório",
  "path": "/api/tarefas",
  "timestamp": "2025-07-04 04:30:15"
}
```

## Checklist de Implementação

### **✅ Controller:**
- [ ] Remove todos os try-catch desnecessários
- [ ] Não faz validações de regra de negócio
- [ ] Usa @Valid para validações de DTO
- [ ] Deixa exceções "borbulharem" para o GlobalExceptionHandler

### **✅ Service:**
- [ ] Usa ValidationUtils para validações comuns
- [ ] Lança exceções específicas (BusinessException, EntityNotFoundException, etc.)
- [ ] Valida parâmetros de entrada
- [ ] Verifica existência de entidades relacionadas
- [ ] Implementa regras de negócio com validações claras

### **✅ Repository:**
- [ ] Não faz validações de negócio
- [ ] Pode lançar EntityNotFoundException quando apropriado
- [ ] Foca apenas no acesso aos dados

### **✅ GlobalExceptionHandler:**
- [ ] Captura todas as exceções
- [ ] Retorna ErrorResponse padronizado
- [ ] Usa códigos HTTP apropriados
- [ ] Não expõe detalhes internos sensíveis

## Benefícios

1. **🎯 Erros Claros**: Usuário recebe mensagens específicas sobre o problema
2. **🔧 Fácil Debugging**: Exceções específicas facilitam identificação de problemas
3. **📋 Consistência**: Todas as APIs retornam erros no mesmo formato
4. **🛡️ Robustez**: Validações adequadas previnem estados inconsistentes
5. **🧹 Código Limpo**: Responsabilidades bem definidas entre camadas 