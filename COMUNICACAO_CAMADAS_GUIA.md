# Comunicação Entre Camadas - Guia Prático

## Problema Identificado

Antes da melhoria, os controllers tinham padrões problemáticos:

### ❌ **Antes (Problemático):**
```java
@PostMapping("/registrar")
public ResponseEntity<CheckHumorDTO> registrar(@RequestBody CheckHumorDTO dto) {
    try {
        CheckHumorDTO resultado = service.registrar(dto);
        return ResponseEntity.ok(resultado);
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().build(); // ❌ Perde informação do erro
    }
}
```

### ✅ **Depois (Correto):**
```java
@PostMapping("/registrar")
public ResponseEntity<CheckHumorDTO> registrar(@Valid @RequestBody CheckHumorDTO dto) {
    // ✅ Deixa exceções serem tratadas pelo GlobalExceptionHandler
    CheckHumorDTO resultado = service.registrar(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
}
```

## Padrão Implementado

### **1. Controller - Responsabilidades:**
- ✅ Receber e validar parâmetros HTTP básicos
- ✅ Chamar o service apropriado
- ✅ Retornar ResponseEntity com status correto
- ❌ NÃO capturar exceções com try-catch
- ❌ NÃO fazer validações de regra de negócio

### **2. Service - Responsabilidades:**
- ✅ Validar parâmetros de entrada
- ✅ Implementar regras de negócio
- ✅ Lançar exceções específicas
- ✅ Coordenar operações entre repositories
- ❌ NÃO capturar exceções genéricas

### **3. GlobalExceptionHandler - Responsabilidades:**
- ✅ Capturar TODAS as exceções
- ✅ Converter para ErrorResponse padronizado
- ✅ Retornar códigos HTTP apropriados

## Exemplo Prático Implementado

### **KanbanTarefaService (Melhorado):**
```java
public KanbanTarefaDTO atualizarStatus(Integer id, String novoStatus) {
    // ✅ Validações claras com exceções específicas
    if (id == null) {
        throw new BusinessException("ID da tarefa é obrigatório");
    }
    
    if (novoStatus == null || novoStatus.trim().isEmpty()) {
        throw new BusinessException("Status é obrigatório");
    }
    
    // ✅ Validação de valores permitidos
    List<String> statusValidos = List.of("A_FAZER", "EM_PROGRESSO", "CONCLUIDA");
    if (!statusValidos.contains(novoStatus.toUpperCase())) {
        throw new BusinessException("Status inválido. Valores permitidos: " + 
            String.join(", ", statusValidos));
    }
    
    // ✅ Buscar entidade com exceção específica
    KanbanTarefa tarefa = repository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada com ID: " + id));
    
    // ✅ Regra de negócio específica
    tarefa.setStatus(novoStatus.toUpperCase());
    
    KanbanTarefa tarefaAtualizada = repository.save(tarefa);
    return convertToDTO(tarefaAtualizada);
}
```

### **KanbanTarefaController (Melhorado):**
```java
@PutMapping("/{id}/status")
public ResponseEntity<KanbanTarefaDTO> atualizarStatus(
        @PathVariable Integer id, 
        @RequestBody Map<String, String> statusData) {
    
    String novoStatus = statusData.get("status");
    // ✅ Service faz todas as validações e lança exceções apropriadas
    KanbanTarefaDTO tarefa = kanbanTarefaService.atualizarStatus(id, novoStatus);
    return ResponseEntity.ok(tarefa);
}
```

## Fluxo de Erro Melhorado

### **1. Requisição Inválida:**
```http
PUT /api/kanban/tarefas/123/status
{
  "status": "INVALIDO"
}
```

### **2. Service Detecta Erro:**
```java
// Service lança exceção específica
throw new BusinessException("Status inválido. Valores permitidos: A_FAZER, EM_PROGRESSO, CONCLUIDA");
```

### **3. GlobalExceptionHandler Captura:**
```java
@ExceptionHandler(BusinessException.class)
public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex, WebRequest request) {
    ErrorResponse error = new ErrorResponse(400, "Business Rule Error", ex.getMessage(), getPath(request));
    return ResponseEntity.badRequest().body(error);
}
```

### **4. Resposta Final Padronizada:**
```json
{
  "status": 400,
  "error": "Business Rule Error",
  "message": "Status inválido. Valores permitidos: A_FAZER, EM_PROGRESSO, CONCLUIDA",
  "path": "/api/kanban/tarefas/123/status",
  "timestamp": "2025-07-04 04:30:15"
}
```

## ValidationUtils - Utilitário Criado

Para padronizar validações comuns:

```java
// Validações básicas
ValidationUtils.validateNotNull(id, "ID da tarefa");
ValidationUtils.validateNotEmpty(titulo, "Título");
ValidationUtils.validateMaxLength(titulo, 255, "Título");

// Validações numéricas
ValidationUtils.validatePositiveId(id, "ID");
ValidationUtils.validateNonNegativeNumber(horas, "Horas trabalhadas");

// Validações de lista
List<String> statusValidos = List.of("A_FAZER", "EM_PROGRESSO", "CONCLUIDA");
ValidationUtils.validateInList(status, statusValidos, "Status");

// Validações de data
ValidationUtils.validateNotPastDate(dataLimite, "Data limite");
ValidationUtils.validateDateOrder(inicio, fim, "Data início", "Data fim");
```

## Resultados Obtidos

### **✅ Benefícios:**
1. **Erros Claros**: Usuário recebe mensagens específicas
2. **Consistência**: Todas as APIs retornam erros no mesmo formato
3. **Manutenibilidade**: Fácil adicionar novas validações
4. **Debugging**: Exceções específicas facilitam identificação de problemas
5. **Robustez**: Validações adequadas previnem estados inconsistentes

### **📊 Comparação:**

| Aspecto | Antes | Depois |
|---------|-------|--------|
| **Erro no Controller** | `ResponseEntity.badRequest().build()` | Exceção específica tratada pelo GlobalExceptionHandler |
| **Mensagem de Erro** | Sem informação | "Status inválido. Valores permitidos: A_FAZER, EM_PROGRESSO, CONCLUIDA" |
| **Código HTTP** | Sempre 400 | 400, 401, 403, 404, 409, 500 conforme apropriado |
| **Formato** | Inconsistente | Sempre ErrorResponse padronizado |
| **Debugging** | Difícil | Fácil com stack trace e mensagem clara |

## Como Aplicar em Novos Controllers

### **1. Remover try-catch:**
```java
// ❌ Antes
try {
    return service.metodo();
} catch (Exception e) {
    return ResponseEntity.badRequest().build();
}

// ✅ Depois
return ResponseEntity.ok(service.metodo());
```

### **2. No Service, usar validações específicas:**
```java
// ✅ Validações com ValidationUtils
ValidationUtils.validateNotNull(parametro, "Nome do parâmetro");

// ✅ Exceções específicas
throw new BusinessException("Mensagem clara do problema");
throw new EntityNotFoundException("Entidade não encontrada com ID: " + id);
```

### **3. Deixar GlobalExceptionHandler tratar:**
- Não precisa configurar nada adicional
- Automaticamente captura e formata todas as exceções
- Retorna ErrorResponse padronizado

## Próximos Passos

1. **Aplicar padrão** em controllers restantes
2. **Usar ValidationUtils** em todos os services
3. **Remover try-catch** desnecessários
4. **Adicionar validações** específicas onde necessário
5. **Testar** com endpoints `/test-errors/*` para verificar funcionamento 