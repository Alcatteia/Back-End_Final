# Sistema de Tratamento de Erros

## Visão Geral

O sistema implementa um tratamento padronizado de erros usando `@ControllerAdvice` para capturar e formatar exceções de forma consistente em toda a aplicação.

## Estrutura de Resposta de Erro

Todas as respostas de erro seguem o padrão `ErrorResponse`:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Usuário não encontrado",
  "path": "/usuarios/login",
  "timestamp": "2025-07-04 02:30:15"
}
```

## Tipos de Erro Tratados

### 1. **EntityNotFoundException** (404 - Not Found)
- **Uso**: Quando uma entidade não é encontrada no banco
- **Exemplo**: Usuário, tarefa ou registro não existe
- **Resposta**: HTTP 404

### 2. **AuthenticationException** (401 - Unauthorized)
- **Uso**: Problemas de autenticação (credenciais inválidas)
- **Exemplo**: Email/senha incorretos, usuário não encontrado
- **Resposta**: HTTP 401

### 3. **BusinessException** (400 - Bad Request)
- **Uso**: Violação de regras de negócio
- **Exemplo**: Operação não permitida, dados inválidos
- **Resposta**: HTTP 400

### 4. **AccessDeniedException** (403 - Forbidden)
- **Uso**: Usuário não tem permissão para a operação
- **Exemplo**: FUNC tentando acessar dados de RH
- **Resposta**: HTTP 403

### 5. **DataIntegrityViolationException** (409 - Conflict)
- **Uso**: Violação de integridade do banco de dados
- **Exemplo**: Email duplicado, referência não encontrada
- **Resposta**: HTTP 409

### 6. **MethodArgumentNotValidException** (400 - Bad Request)
- **Uso**: Erro de validação de campos (@Valid)
- **Exemplo**: Campo obrigatório vazio, formato inválido
- **Resposta**: HTTP 400

### 7. **IllegalArgumentException** (400 - Bad Request)
- **Uso**: Argumentos inválidos fornecidos
- **Exemplo**: Parâmetros fora do range esperado
- **Resposta**: HTTP 400

### 8. **IllegalStateException** (409 - Conflict)
- **Uso**: Estado inválido do sistema
- **Exemplo**: Tentar confirmar check-in de data anterior
- **Resposta**: HTTP 409

### 9. **Exception** (500 - Internal Server Error)
- **Uso**: Erros não tratados especificamente
- **Exemplo**: Erros inesperados do sistema
- **Resposta**: HTTP 500

## Como Usar

### Nos Services

```java
@Service
public class MeuService {
    
    public Usuario buscarUsuario(Integer id) {
        Usuario usuario = repository.findById(id);
        if (usuario == null) {
            throw new EntityNotFoundException("Usuário não encontrado com ID: " + id);
        }
        return usuario;
    }
    
    public void validarLogin(String email, String senha) {
        if (email == null || email.trim().isEmpty()) {
            throw new AuthenticationException("Email é obrigatório");
        }
        
        if (!isValidPassword(senha)) {
            throw new AuthenticationException("Senha inválida");
        }
    }
    
    public void validarRegra(String dados) {
        if (!isValidBusinessRule(dados)) {
            throw new BusinessException("Dados não atendem às regras de negócio");
        }
    }
}
```

### Nos Controllers

```java
@RestController
public class MeuController {
    
    @PostMapping("/usuarios")
    public ResponseEntity<Usuario> criar(@Valid @RequestBody UsuarioDTO dto) {
        // @Valid automaticamente gera MethodArgumentNotValidException se inválido
        Usuario usuario = service.criar(dto);
        return ResponseEntity.ok(usuario);
    }
    
    @GetMapping("/usuarios/{id}")
    public ResponseEntity<Usuario> buscar(@PathVariable Integer id) {
        // Se não encontrar, service lança EntityNotFoundException
        Usuario usuario = service.buscar(id);
        return ResponseEntity.ok(usuario);
    }
}
```

## Testando o Sistema

### Endpoints de Teste (REMOVER EM PRODUÇÃO)

A aplicação inclui endpoints para testar todos os tipos de erro:

```
GET /test-errors/info - Lista todos os endpoints de teste
GET /test-errors/not-found - Testa EntityNotFoundException
GET /test-errors/authentication - Testa AuthenticationException
GET /test-errors/business - Testa BusinessException
GET /test-errors/access-denied - Testa AccessDeniedException
GET /test-errors/data-integrity - Testa DataIntegrityViolationException
GET /test-errors/illegal-argument - Testa IllegalArgumentException
GET /test-errors/illegal-state - Testa IllegalStateException
GET /test-errors/invalid-param/abc - Testa MethodArgumentTypeMismatchException
GET /test-errors/generic-error - Testa Exception genérica
```

### Exemplo de Teste com cURL

```bash
# Teste de usuário não encontrado
curl -X GET http://localhost:8080/test-errors/not-found

# Resposta esperada:
{
  "status": 404,
  "error": "Not Found",
  "message": "Recurso de teste não encontrado",
  "path": "/test-errors/not-found",
  "timestamp": "2025-07-04 02:30:15"
}
```

## Boas Práticas

### 1. **Use exceções específicas**
```java
// ✅ Bom
throw new AuthenticationException("Credenciais inválidas");

// ❌ Evite
throw new RuntimeException("Erro de login");
```

### 2. **Mensagens claras e específicas**
```java
// ✅ Bom
throw new EntityNotFoundException("Usuário não encontrado com ID: " + id);

// ❌ Evite
throw new EntityNotFoundException("Não encontrado");
```

### 3. **Validação no início dos métodos**
```java
public void processar(String dados) {
    // Validar primeiro
    if (dados == null || dados.trim().isEmpty()) {
        throw new BusinessException("Dados são obrigatórios");
    }
    
    // Processar depois
    // ...
}
```

### 4. **Não expor detalhes internos**
```java
// ✅ Bom
throw new BusinessException("Operação não permitida");

// ❌ Evite
throw new BusinessException("SQLException: connection timeout at line 42");
```

## Configuração

O sistema está configurado automaticamente através do `@ControllerAdvice` no `GlobalExceptionHandler`. Não é necessária configuração adicional.

## Logs

Erros genéricos (Exception) são automaticamente logados no console para debugging:

```
Erro não tratado: RuntimeException - Erro genérico não tratado
java.lang.RuntimeException: Erro genérico não tratado
    at com.exemplo.bancoalcatteia.controller.TestErrorController.testGenericError(TestErrorController.java:59)
    ...
```

## Limpeza para Produção

Antes de colocar em produção:

1. **Remover** `TestErrorController.java`
2. **Remover** endpoints de teste do `SecurityConfig`
3. **Configurar** logs apropriados em vez de `System.err.println`
4. **Revisar** mensagens de erro para não expor informações sensíveis 