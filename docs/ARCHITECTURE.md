# 🏗️ Arquitetura do Sistema Banco Alcatteia

## 📋 Visão Geral

O **Banco Alcatteia** é uma aplicação corporativa desenvolvida em Spring Boot que integra múltiplas funcionalidades para gestão de equipes, com arquitetura moderna e segura.

## 🛠️ Tecnologias Utilizadas

### Backend
- **Java 17** - Linguagem principal
- **Spring Boot 3.2.3** - Framework principal
- **Spring Security 6** - Segurança e autenticação
- **Spring Data JPA** - Persistência de dados
- **JWT (jjwt 0.11.5)** - Tokens de autenticação
- **MySQL 8** - Banco de dados
- **Maven** - Gerenciamento de dependências
- **BCrypt** - Criptografia de senhas

### Arquitetura
- **REST API** - Comunicação via HTTP
- **JWT Authentication** - Autenticação stateless
- **Role-Based Access Control** - Autorização por papéis
- **Layered Architecture** - Separação de responsabilidades

## 🏗️ Estrutura do Projeto

```
src/main/java/com/exemplo/bancoalcatteia/
├── config/              # Configurações do sistema
│   ├── CorsConfig.java     # Configuração CORS
│   └── SecurityConfig.java # Configuração Spring Security
├── constants/           # Constantes centralizadas
│   └── SecurityConstants.java
├── controller/          # Controladores REST
│   ├── AuthController.java
│   ├── UsuarioController.java
│   ├── TarefaController.java
│   └── ...
├── dto/                # Data Transfer Objects
│   ├── auth/              # DTOs de autenticação
│   │   ├── LoginRequest.java
│   │   ├── LoginResponse.java
│   │   └── UserInfo.java
│   ├── UsuarioDTO.java
│   └── ...
├── exception/          # Tratamento de exceções
│   └── GlobalExceptionHandler.java
├── model/              # Entidades JPA
│   ├── Usuario.java
│   ├── Tarefa.java
│   └── ...
├── repository/         # Interfaces de acesso a dados
│   ├── UsuarioRepository.java
│   └── ...
├── security/           # Componentes de segurança
│   ├── JwtService.java              # Serviço JWT
│   ├── JwtAuthenticationFilter.java # Filtro de autenticação
│   └── UserDetailsServiceImpl.java  # UserDetailsService
├── service/            # Camada de lógica de negócio
│   ├── AuthService.java
│   ├── UsuarioService.java
│   └── ...
└── utils/              # Utilitários
    └── ValidationUtils.java
```

## 🔐 Arquitetura de Segurança

### Fluxo de Autenticação
```
┌─────────────┐    ┌──────────────┐    ┌─────────────┐
│   Cliente   │───▶│ AuthController│───▶│ AuthService │
└─────────────┘    └──────────────┘    └─────────────┘
                           │                    │
                           ▼                    ▼
                   ┌──────────────┐    ┌─────────────┐
                   │  JWT Token   │    │  Database   │
                   └──────────────┘    └─────────────┘
```

### Componentes de Segurança
- **JwtService**: Geração e validação de tokens
- **JwtAuthenticationFilter**: Intercepta requisições e valida tokens
- **SecurityConfig**: Configuração de segurança do Spring
- **UserDetailsServiceImpl**: Carrega dados do usuário para autenticação

### Sistema de Roles
- **LIDER**: Acesso total ao sistema
- **RH**: Gerencia usuários, times e dashboards  
- **FUNC**: Gerencia tarefas e próprias informações

## 📊 Modelo de Dados

### Entidades Principais

#### 🧑 Usuario
Entidade central que representa funcionários, líderes e RH.
```java
@Entity
public class Usuario {
    private Long id;
    private String nome;      // máx. 100 chars
    private String email;     // único, máx. 100 chars
    private String senha;     // criptografada com BCrypt
    private String cpf;       // máx. 14 chars
    private String telefone;  // máx. 20 chars
    private TipoUsuario tipoUsuario; // FUNC, LIDER, RH
    private LocalDate dataNascimento;
    private LocalDateTime dataCriacao;
    private boolean ativo;
}
```

#### 📝 Tarefa
Sistema de gestão de tarefas com participantes.
```java
@Entity
public class Tarefa {
    private Long id;
    private String titulo;
    private String descricao;
    private Status status;          // TODO, DOING, DONE
    private Prioridade prioridade;  // BAIXA, MEDIA, ALTA
    private Usuario responsavel;
    private Usuario criadoPor;
    private LocalDateTime dataCriacao;
    private LocalDate dataEntrega;
    private Double estimativaHoras;
    // Relacionamento com participantes
}
```

#### 👥 Time
Estrutura organizacional para agrupar funcionários.

#### 📋 KanbanTarefa & KanbanCategoria
Sistema visual de gestão de tarefas estilo Kanban.

#### 😊 CheckHumor
Sistema de monitoramento do bem-estar dos funcionários.

#### 🎤 SessaoKaraoke & Musica
Sistema de karaokê corporativo com avaliações.

#### 📢 Notificacao
Sistema de notificações do sistema.

### Relacionamentos
- **Usuario** ↔ **Time** (N:N através de MembroTime)
- **Usuario** ↔ **Tarefa** (1:N como responsável/criador)
- **Usuario** ↔ **ParticipanteTarefa** (N:N para participação)
- **Tarefa** ↔ **Notificacao** (1:N)

## 🌐 Arquitetura REST API

### Camadas da Aplicação
```
┌─────────────────┐
│   Controllers   │ ← Endpoints REST, validação de entrada
└─────────────────┘
         │
┌─────────────────┐
│    Services     │ ← Lógica de negócio, transações
└─────────────────┘
         │
┌─────────────────┐
│  Repositories   │ ← Acesso a dados, queries JPA
└─────────────────┘
         │
┌─────────────────┐
│    Database     │ ← Persistência MySQL
└─────────────────┘
```

### Padrões de Resposta HTTP
- **200 OK**: Operação bem-sucedida
- **201 Created**: Recurso criado
- **204 No Content**: Operação sem retorno
- **400 Bad Request**: Dados inválidos
- **401 Unauthorized**: Token inválido/expirado
- **403 Forbidden**: Sem permissão
- **404 Not Found**: Recurso não encontrado

### Tratamento de Exceções
- **GlobalExceptionHandler**: Tratamento centralizado
- **Respostas padronizadas**: Formato JSON consistente
- **Logs estruturados**: Para debugging e auditoria

## 🔧 Configurações

### application.properties
```properties
# Servidor
server.port=8080
server.servlet.encoding.charset=UTF-8

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/banco_alcatteia
spring.datasource.username=${DATABASE_USERNAME:root}
spring.datasource.password=${DATABASE_PASSWORD:}

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true

# JWT
jwt.secret-key=${JWT_SECRET:chave_padrao_deve_ser_alterada}
jwt.expiration=${JWT_EXPIRATION:32400000}

# CORS
cors.allowed-origins=${CORS_ALLOWED_ORIGINS:http://localhost:3000,http://localhost:3001}

# Logs
logging.level.com.exemplo.bancoalcatteia=DEBUG
```

### Variáveis de Ambiente
Para produção, configure:
- `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`
- `JWT_SECRET` (chave forte e única)
- `CORS_ALLOWED_ORIGINS` (domínios permitidos)

## 🚀 Deploy e Escalabilidade

### Ambiente de Desenvolvimento
- **Profiles**: `dev`, `test`, `prod`
- **H2 Database**: Para testes
- **Docker**: Containerização disponível

### Ambiente de Produção
- **MySQL**: Banco de dados principal
- **Connection Pooling**: HikariCP configurado
- **SSL**: Recomendado para HTTPS
- **Load Balancer**: Para múltiplas instâncias

### Monitoramento
- **Actuator**: Endpoints de health check
- **Logs**: Estruturados para análise
- **Métricas**: Tempo de resposta, erros

## 🔍 Validações e Regras de Negócio

### Validações de Entrada
- **Bean Validation**: Anotações nos DTOs
- **ValidationUtils**: Validações customizadas
- **Sanitização**: Prevenção de XSS

### Regras de Negócio
- **Email único**: Por usuário
- **Roles específicas**: Para cada operação
- **Transações**: Para operações críticas
- **Soft Delete**: Para manter histórico

## 📈 Funcionalidades Exclusivas

### 🎤 Sistema de Karaokê Corporativo
- Promoção de integração entre equipes
- Sistema de pontuação e avaliações
- Catálogo de músicas organizadas
- Histórico de performances

### 😊 Monitoramento de Humor
- Check diário de humor dos funcionários
- Métricas de bem-estar da equipe
- Identificação proativa de problemas

### 📋 Duplo Sistema de Tarefas
- **Tarefas tradicionais**: Gestão formal
- **Kanban**: Visualização ágil e colaborativa

## 🔄 Melhorias Implementadas

### Código Limpo
- **Classes internas eliminadas**: Para melhor organização
- **Constantes centralizadas**: Em SecurityConstants
- **Validações padronizadas**: Em ValidationUtils
- **Imports otimizados**: Sem full paths

### Segurança
- **JWT stateless**: Sem sessões servidor
- **BCrypt**: Para hash de senhas
- **CORS configurável**: Para diferentes ambientes
- **Rate limiting**: Recomendado para produção

## 🎯 Próximos Passos

### Frontend
- React/Angular SPA
- Material UI/Bootstrap
- PWA para mobile

### Melhorias de Segurança
- Refresh tokens
- 2FA (Two-Factor Authentication)
- Audit logs
- Rate limiting

### Performance
- Cache Redis
- Database indexes
- CDN para assets

### Integração
- API de notificações push
- Integração com sistemas RH
- Relatórios avançados

---

**Arquitetura desenvolvida com foco em escalabilidade, segurança e manutenibilidade.** 