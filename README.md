# Backend Alcatteia

Sistema backend para gestão de humor organizacional e kanban de tarefas desenvolvido em Spring Boot.

## Tecnologias

- Java 17
- Spring Boot 3.x
- Spring Security
- JPA/Hibernate
- MySQL
- Maven

## Estrutura do Projeto

```
src/main/java/com/exemplo/bancoalcatteia/
├── controller/          # Controllers REST
├── service/             # Lógica de negócio
├── repository/          # Acesso a dados
├── model/               # Entidades JPA
├── dto/                 # Data Transfer Objects
├── exception/           # Sistema de exceções
├── config/              # Configurações
└── utils/               # Utilitários
```

## Configuração

### Pré-requisitos
- Java 17+
- Maven 3.6+
- MySQL 8.0+

### Instalação
1. Clone o repositório
2. Configure o banco de dados no `application.properties`
3. Execute: `mvn clean compile`
4. Inicie: `mvn spring-boot:run`

### Acesso
- API Base: http://localhost:8080
- Health Check: http://localhost:8080/actuator/health

## Sistema de Autenticação

### Endpoints Públicos
- `POST /usuarios/login` - Login
- `POST /usuarios/criar` - Registro
- `POST /usuarios/logout` - Logout

### Roles
- **RH** - Acesso completo
- **LIDER** - Gestão de equipe
- **FUNC** - Acesso básico

## APIs Principais

### Humor
- `GET /api/check-humor/meu-humor-hoje` - Humor do usuário
- `POST /api/check-humor/registrar` - Registrar humor
- `GET /api/humor/relatorios/quinzenal` - Relatório quinzenal

### Kanban
- `GET /api/kanban/categorias` - Listar categorias
- `GET /api/kanban/tarefas` - Listar tarefas
- `POST /api/kanban/tarefas` - Criar tarefa

### Dashboard
- `GET /dashboards` - Métricas gerais
- `GET /api/dashboard/metricas` - Indicadores

## Sistema de Tratamento de Erros

### Códigos HTTP
- **400** - Bad Request
- **401** - Unauthorized
- **403** - Forbidden
- **404** - Not Found
- **409** - Conflict
- **500** - Internal Server Error

### Formato de Resposta de Erro
```json
{
  "status": 400,
  "error": "Business Rule Error",
  "message": "Nome da categoria é obrigatório",
  "path": "/api/kanban/categorias",
  "timestamp": "2025-01-07 15:30:45"
}
```

## Execução

### Desenvolvimento
```bash
mvn spring-boot:run -Dspring.profiles.active=dev
```

### Testes
```bash
mvn test
```

## Licença

Este projeto está sob licença MIT.

---

Desenvolvido pela equipe Alcatteia 