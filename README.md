# Backend Alcatteia - Sistema de Gestão de Humor e Kanban

Sistema backend robusto para gestão de humor organizacional e kanban de tarefas, implementado com Spring Boot e sistema avançado de tratamento de erros.

## 🚀 **Características Principais**

- **Sistema de Check-in de Humor** com análise de tendências
- **Kanban de Tarefas** com categorias personalizáveis
- **Relatórios Avançados** de humor organizacional
- **Sistema de Autenticação** baseado em sessões
- **Tratamento de Erros Robusto** com exceções customizadas
- **API RESTful** com documentação completa

## 🛠️ **Tecnologias**

- **Java 17** + Spring Boot 3.x
- **Spring Security** para autenticação
- **JPA/Hibernate** para persistência
- **MySQL** como banco de dados
- **Maven** para gerenciamento de dependências

## 📁 **Estrutura do Projeto**

```
alcatteia_back-end/
├── src/main/java/com/exemplo/bancoalcatteia/
│   ├── controller/          # Controllers REST
│   ├── service/             # Lógica de negócio
│   ├── repository/          # Acesso a dados
│   ├── model/               # Entidades JPA
│   ├── dto/                 # Data Transfer Objects
│   ├── exception/           # Sistema de exceções
│   ├── config/              # Configurações
│   └── utils/               # Utilitários
├── docs/
│   ├── guides/              # Guias técnicos
│   ├── sql/                 # Scripts de banco
│   └── postman/             # Coleções de API
└── README.md
```

## 🎯 **Sistema de Tratamento de Erros**

### **Códigos HTTP Implementados**
- **400** - Bad Request (dados inválidos)
- **401** - Unauthorized (autenticação falhou)
- **403** - Forbidden (sem permissão)
- **404** - Not Found (recurso não encontrado)
- **409** - Conflict (conflito de dados)
- **500** - Internal Server Error (erro interno)

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

## 🚀 **Como Executar**

### **Pré-requisitos**
- Java 17+
- Maven 3.6+
- MySQL 8.0+

### **Configuração**
1. Clone o repositório
2. Configure o banco de dados no `application.properties`
3. Execute os scripts SQL em `docs/sql/`
4. Compile: `mvn clean compile`
5. Execute: `mvn spring-boot:run`

### **Acesso**
- **API Base:** http://localhost:8080
- **Health Check:** http://localhost:8080/actuator/health

## 📚 **Documentação**

### **Guias Técnicos** (`docs/guides/`)
- **TRATAMENTO_ERROS.md** - Sistema de exceções
- **COMUNICACAO_CAMADAS_GUIA.md** - Padrões de arquitetura
- **GUIA_PREPARACAO_PRODUCAO.md** - Checklist para produção
- **IMPLEMENTACAO_FINALIZADA.md** - Resumo das implementações

### **Scripts SQL** (`docs/sql/`)
- **banco_alcatteia_completo.sql** - Schema completo
- **dados_demonstracao.sql** - Dados de exemplo
- **migracao_melhorias.sql** - Melhorias e otimizações

## 🔐 **Autenticação**

### **Endpoints Públicos**
- `POST /usuarios/login` - Login
- `POST /usuarios/criar` - Registro
- `POST /usuarios/logout` - Logout

### **Roles Implementados**
- **RH** - Acesso completo
- **LIDER** - Gestão de equipe
- **FUNC** - Acesso básico

## 📊 **APIs Principais**

### **Humor**
- `GET /api/check-humor/meu-humor-hoje` - Humor do usuário
- `POST /api/check-humor/registrar` - Registrar humor
- `GET /api/humor/relatorios/quinzenal` - Relatório quinzenal

### **Kanban**
- `GET /api/kanban/categorias` - Listar categorias
- `GET /api/kanban/tarefas` - Listar tarefas
- `POST /api/kanban/tarefas` - Criar tarefa

### **Dashboard**
- `GET /dashboards` - Métricas gerais
- `GET /api/dashboard/metricas` - Indicadores

## 🧪 **Testes**

```bash
# Executar testes
mvn test

# Executar com coverage
mvn test jacoco:report
```

## 🚢 **Deploy**

### **Ambiente de Desenvolvimento**
```bash
mvn spring-boot:run -Dspring.profiles.active=dev
```

### **Ambiente de Produção**
Seguir checklist em `docs/guides/GUIA_PREPARACAO_PRODUCAO.md`

## 🤝 **Contribuição**

1. Fork o projeto
2. Crie uma branch: `git checkout -b feature/nova-funcionalidade`
3. Commit: `git commit -m 'feat: adicionar nova funcionalidade'`
4. Push: `git push origin feature/nova-funcionalidade`
5. Abra um Pull Request

## 📝 **Changelog**

### **v2.0.0** - Sistema de Tratamento de Erros
- Implementado GlobalExceptionHandler
- Criadas exceções customizadas
- Atualizados todos os controllers
- Melhorado sistema de logs
- Documentação completa

### **v1.0.0** - Versão Inicial
- Sistema básico de humor e kanban
- Autenticação por sessão
- APIs RESTful

## 📄 **Licença**

Este projeto está sob licença MIT. Veja o arquivo LICENSE para mais detalhes.

---

**Desenvolvido pela equipe Alcatteia**  
**Última atualização:** Janeiro 2025 