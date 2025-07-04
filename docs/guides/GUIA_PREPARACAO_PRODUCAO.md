# Guia de Preparação para Produção

## Sistema de Tratamento de Erros - Alcatteia Backend

### ✅ **Implementações Concluídas**

#### 1. **Sistema de Tratamento de Erros**
- ✅ `GlobalExceptionHandler` - Tratamento centralizado de todas as exceções
- ✅ `ErrorResponse` - Formato padronizado de resposta de erro
- ✅ `BusinessException` - Exceção customizada para regras de negócio
- ✅ `AuthenticationException` - Exceção customizada para autenticação
- ✅ `ValidationUtils` - Utilitários para validações consistentes

#### 2. **Controllers Atualizados**
- ✅ `KanbanCategoriaController` - Removidos try-catch desnecessários
- ✅ `HumorReportController` - Substituído badRequest por BusinessException
- ✅ `CheckHumorAdvancedController` - Removidos todos os try-catch
- ✅ `ParticipanteTarefaController` - Substituído notFound por EntityNotFoundException
- ✅ `NotificacaoController` - Substituído notFound por EntityNotFoundException
- ✅ `CheckHumorController` - Substituído Optional.orElse por método que lança exceção
- ✅ `KanbanController` - Removidos try-catch desnecessários

#### 3. **Services Melhorados**
- ✅ `CheckHumorService` - Adicionado método `buscarHumorHojeObrigatorio()`
- ✅ `KanbanTarefaService` - Validações específicas implementadas
- ✅ `UsuarioService` - AuthenticationException para login

#### 4. **Sistema de Logs**
- ✅ Substituído `System.err.println` por `Logger` profissional
- ✅ Logs de warning para tentativas de autenticação e acesso negado
- ✅ Logs de error para exceções não tratadas com stack trace

---

## 📋 **Checklist de Preparação para Produção**

### 🔴 **CRÍTICO - Fazer antes de subir**

#### 1. **Limpeza de Código de Teste**
- ✅ Remover `TestErrorController.java`
- ✅ Remover endpoints `/test-errors/**` do `SecurityConfig`
- ⚠️ Verificar se não há outros controllers/endpoints de teste

#### 2. **Configuração de Logs**
- ✅ Configurar `logback-spring.xml` ou `application.properties` para logs estruturados
- ⚠️ Definir níveis de log apropriados (INFO para produção)
- ⚠️ Configurar rotação de logs
- ⚠️ Configurar logs centralizados (ELK Stack, CloudWatch, etc.)

#### 3. **Segurança**
- ⚠️ Revisar mensagens de erro para não expor informações sensíveis
- ⚠️ Configurar HTTPS obrigatório
- ⚠️ Revisar configurações de CORS
- ⚠️ Validar configurações de autenticação/autorização

### 🟡 **IMPORTANTE - Configurar**

#### 4. **Banco de Dados**
- ⚠️ Configurar connection pool apropriado
- ⚠️ Configurar backup automático
- ⚠️ Configurar monitoramento de performance
- ⚠️ Remover configurações de H2 Console se não necessário

#### 5. **Monitoramento**
- ⚠️ Configurar health checks
- ⚠️ Configurar métricas (Micrometer + Prometheus)
- ⚠️ Configurar alertas para erros críticos
- ⚠️ Configurar dashboard de monitoramento

#### 6. **Performance**
- ⚠️ Configurar cache (Redis/Hazelcast)
- ⚠️ Otimizar queries do banco
- ⚠️ Configurar compressão de resposta
- ⚠️ Configurar timeouts apropriados

### 🟢 **OPCIONAL - Melhorias**

#### 7. **Documentação**
- ⚠️ Atualizar documentação da API (Swagger)
- ⚠️ Documentar códigos de erro para frontend
- ⚠️ Criar runbook para operações

#### 8. **Testes**
- ⚠️ Adicionar testes de integração para tratamento de erros
- ⚠️ Testes de carga
- ⚠️ Testes de segurança

---

## 🛠️ **Configurações Recomendadas**

### **1. Configuração de Logs (application.properties)**
```properties
# Configuração de logs para produção
logging.level.root=WARN
logging.level.com.exemplo.bancoalcatteia=INFO
logging.level.org.springframework.security=WARN

# Arquivo de log
logging.file.name=logs/alcatteia-backend.log
logging.file.max-size=10MB
logging.file.max-history=30

# Formato de log
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
logging.pattern.console=%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
```

### **2. Configuração de Produção (application-prod.properties)**
```properties
# Banco de dados
spring.datasource.url=jdbc:mysql://localhost:3306/alcatteia_prod
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5

# JPA
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false

# Segurança
server.servlet.session.timeout=30m
server.servlet.session.cookie.secure=true
server.servlet.session.cookie.http-only=true

# Compressão
server.compression.enabled=true
server.compression.mime-types=application/json,application/xml,text/html,text/xml,text/plain

# Cache
spring.cache.type=redis
spring.redis.host=${REDIS_HOST:localhost}
spring.redis.port=${REDIS_PORT:6379}
```

### **3. Dockerfile Recomendado**
```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/bancoalcatteia-*.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
```

---

## 🚨 **Códigos de Erro para Frontend**

### **Códigos HTTP Implementados**
| Código | Tipo | Descrição | Exemplo |
|--------|------|-----------|---------|
| 400 | Bad Request | Dados inválidos ou regra de negócio | Campos obrigatórios, formato inválido |
| 401 | Unauthorized | Problema de autenticação | Login/senha incorretos |
| 403 | Forbidden | Sem permissão para operação | FUNC tentando acessar dados de RH |
| 404 | Not Found | Recurso não encontrado | Usuário/tarefa não existe |
| 409 | Conflict | Conflito de dados | Email duplicado, referência inválida |
| 500 | Internal Server Error | Erro interno do sistema | Problemas não tratados |

### **Formato de Resposta de Erro**
```json
{
  "status": 400,
  "error": "Business Rule Error",
  "message": "Nome da categoria é obrigatório",
  "path": "/api/kanban/categorias",
  "timestamp": "2025-01-07 15:30:45"
}
```

---

## 🔧 **Scripts de Deploy**

### **1. Script de Build**
```bash
#!/bin/bash
# build.sh

echo "Iniciando build para produção..."

# Limpar target
mvn clean

# Executar testes
mvn test

# Build para produção
mvn package -Pprod -DskipTests

echo "Build concluído!"
```

### **2. Script de Health Check**
```bash
#!/bin/bash
# health-check.sh

HEALTH_URL="http://localhost:8080/actuator/health"
MAX_ATTEMPTS=30
ATTEMPT=1

while [ $ATTEMPT -le $MAX_ATTEMPTS ]; do
    echo "Tentativa $ATTEMPT de $MAX_ATTEMPTS..."
    
    HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" $HEALTH_URL)
    
    if [ $HTTP_CODE -eq 200 ]; then
        echo "✅ Aplicação está saudável!"
        exit 0
    fi
    
    echo "❌ Aplicação não está pronta (HTTP $HTTP_CODE)"
    sleep 5
    ATTEMPT=$((ATTEMPT + 1))
done

echo "🚨 Aplicação falhou ao inicializar após $MAX_ATTEMPTS tentativas"
exit 1
```

---

## 📊 **Monitoramento Recomendado**

### **Métricas Importantes**
- Taxa de erro por endpoint
- Tempo de resposta médio
- Uso de memória/CPU
- Conexões de banco ativas
- Número de usuários ativos

### **Alertas Críticos**
- Taxa de erro > 5%
- Tempo de resposta > 2s
- Uso de memória > 85%
- Banco de dados inacessível
- Aplicação fora do ar

---

## ✅ **Checklist Final**

Antes de fazer deploy em produção, verificar:

- [ ] Todos os controllers atualizados (sem try-catch desnecessários)
- [ ] TestErrorController removido
- [ ] Logs configurados adequadamente
- [ ] Configurações de segurança revisadas
- [ ] Variáveis de ambiente configuradas
- [ ] Health checks funcionando
- [ ] Backup do banco configurado
- [ ] Monitoramento ativo
- [ ] Documentação atualizada
- [ ] Testes de integração passando

---

## 🆘 **Rollback Plan**

Em caso de problemas:

1. **Verificar logs** para identificar o problema
2. **Rollback da aplicação** para versão anterior estável
3. **Verificar banco de dados** se houve alterações
4. **Notificar equipe** sobre o rollback
5. **Investigar causa raiz** antes do próximo deploy

---

*Este guia deve ser atualizado conforme novas implementações e lições aprendidas em produção.* 