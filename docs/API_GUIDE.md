# 📖 Guia Completo da API - Banco Alcatteia

## 📋 Sobre Este Guia

Este documento contém exemplos detalhados de como usar todos os endpoints da API REST do sistema Banco Alcatteia. 

**Pré-requisito:** Sistema rodando em `http://localhost:8080`

> 💡 Para instalação, consulte: [README.md](README.md)

## 🔐 Sistema de Autenticação

### Tipos de Usuário
- **FUNC** (Funcionário): Gerencia tarefas e próprias informações
- **LIDER** (Líder): Acesso total ao sistema
- **RH** (Recursos Humanos): Gerencia usuários, times e dashboards

### Realizando Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "usuario@bancoalcatteia.com",
  "senha": "123456"
}
```

**Resposta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "expiresAt": "2025-06-22T06:46:10.000+00:00",
  "user": {
    "id": 1,
    "nome": "Nome do Usuário",
    "email": "usuario@bancoalcatteia.com",
    "tipoUsuario": "RH"
  }
}
```

### Usuários de Demonstração
| Email | Senha | Tipo | Permissões |
|-------|-------|------|------------|
| `ana.silva@bancoalcatteia.com` | `123456` | RH | Gerencia usuários, times, dashboards |
| `carlos.mendes@bancoalcatteia.com` | `123456` | LIDER | Acesso total |
| `fernanda.costa@bancoalcatteia.com` | `123456` | FUNC | Gerencia tarefas |
| `joao.santos@bancoalcatteia.com` | `123456` | FUNC | Gerencia tarefas |

### Usando o Token
Inclua o token JWT no header de todas as requisições:
```
Authorization: Bearer SEU_TOKEN_AQUI
```

**Validade do Token:** 9 horas

## 📋 Gestão de Tarefas

### Listar Tarefas
```http
GET /api/tarefas
Authorization: Bearer {token}
```

### Criar Tarefa
```http
POST /api/tarefas
Authorization: Bearer {token}
Content-Type: application/json

{
  "titulo": "Implementar nova funcionalidade",
  "descricao": "Descrição detalhada da tarefa",
  "criadoPorId": 1,
  "responsavelId": 2,
  "dataEntrega": "2025-12-31",
  "estimativaHoras": 8.5,
  "prioridade": "ALTA",
  "status": "TODO",
  "usuariosParaConvidar": [3, 4]
}
```

### Status de Tarefas
- **TODO**: A fazer
- **DOING**: Em progresso  
- **DONE**: Concluída

### Prioridades
- **BAIXA**: Baixa prioridade
- **MEDIA**: Média prioridade
- **ALTA**: Alta prioridade

### Gerenciar Participantes

#### Convidar Participante
```http
POST /api/tarefas/{tarefaId}/convidar/{usuarioId}
Authorization: Bearer {token}
```

#### Responder Convite (Aceitar)
```http
POST /api/tarefas/{tarefaId}/responder-convite/{usuarioId}
Authorization: Bearer {token}
Content-Type: application/json

{
  "aceitar": true
}
```

#### Responder Convite (Rejeitar)
```http
POST /api/tarefas/{tarefaId}/responder-convite/{usuarioId}
Authorization: Bearer {token}
Content-Type: application/json

{
  "aceitar": false,
  "motivoRejeicao": "Não tenho disponibilidade no momento"
}
```

#### Listar Convites Pendentes
```http
GET /api/tarefas/convites-pendentes/{usuarioId}
Authorization: Bearer {token}
```

## 👥 Gestão de Usuários

### Listar Usuários (RH/LIDER)
```http
GET /api/usuarios
Authorization: Bearer {token}
```

### Criar Usuário (RH/LIDER)
```http
POST /api/usuarios
Authorization: Bearer {token}
Content-Type: application/json

{
  "nome": "Novo Usuário",
  "email": "novo@bancoalcatteia.com",
  "senha": "senhaSegura123",
  "cpf": "123.456.789-00",
  "telefone": "(11) 99999-9999",
  "tipoUsuario": "FUNC",
  "dataNascimento": "1990-01-01",
  "ativo": true
}
```

### Atualizar Usuário
```http
PUT /api/usuarios/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "nome": "Nome Atualizado",
  "telefone": "(11) 88888-8888",
  "ativo": true
}
```

## 🎤 Sistema de Karaokê

### Criar Sessão de Karaokê
```http
POST /api/sessoes-karaoke
Authorization: Bearer {token}
Content-Type: application/json

{
  "data": "2025-12-25T20:00:00",
  "local": "Sala de Reuniões A",
  "descricao": "Confraternização de fim de ano",
  "organizadorId": 1
}
```

### Registrar Música Cantada
```http
POST /api/musicas-cantadas
Authorization: Bearer {token}
Content-Type: application/json

{
  "usuarioId": 1,
  "musicaId": 5,
  "sessaoId": 2,
  "ordemCantada": 3,
  "nota": 8.5,
  "comentarios": "Excelente apresentação!"
}
```

## 😊 Sistema de Check Humor

### Registrar Humor do Dia
```http
POST /api/check-humor
Authorization: Bearer {token}
Content-Type: application/json

{
  "usuarioId": 1,
  "humor": "FELIZ",
  "observacao": "Dia produtivo e motivador!"
}
```

### Tipos de Humor
- **MUITO_FELIZ**: Muito Feliz 😄
- **FELIZ**: Feliz 😊
- **NEUTRO**: Neutro 😐
- **TRISTE**: Triste 😢
- **MUITO_TRISTE**: Muito Triste 😭

## 📊 Sistema de Notificações

### Listar Notificações
```http
GET /api/notificacoes/usuario/{usuarioId}
Authorization: Bearer {token}
```

### Marcar como Lida
```http
PUT /api/notificacoes/{id}/marcar-lida
Authorization: Bearer {token}
```

### Tipos de Notificação
- **TAREFA_ATRIBUIDA**: Nova tarefa atribuída
- **TAREFA_ACEITA**: Tarefa aceita por participante  
- **TAREFA_REJEITADA**: Tarefa rejeitada por participante
- **TAREFA_CONCLUIDA**: Tarefa foi concluída
- **PRAZO_CONCLUSAO**: Prazo de conclusão aproximando
- **TAREFA_ATRASADA**: Tarefa está atrasada
- **COMENTARIO**: Novo comentário na tarefa
- **HUMOR_REGISTRADO**: Humor registrado com sucesso

## 📈 Dashboards e Relatórios

### Visualizar Dashboard
```http
GET /api/dashboard/usuario/{usuarioId}
Authorization: Bearer {token}
```

### Gerar Relatório de Produtividade
```http
GET /api/relatorios/produtividade
Authorization: Bearer {token}
?dataInicio=2025-01-01&dataFim=2025-12-31
```

## 🔧 Endpoints de Monitoramento

### Verificar Saúde do Sistema
```http
GET /actuator/health
```

### Tarefas com Problemas

#### Tarefas Atrasadas
```http
GET /api/tarefas/atrasadas
Authorization: Bearer {token}
```

#### Tarefas Sem Atualização
```http
GET /api/tarefas/sem-atualizacao
Authorization: Bearer {token}
```

## ⚠️ Códigos de Erro Comuns

| Código | Descrição | Solução |
|--------|-----------|---------|
| 401 | Token inválido ou expirado | Fazer login novamente |
| 403 | Sem permissão para a operação | Verificar role do usuário |
| 404 | Recurso não encontrado | Verificar ID do recurso |
| 400 | Dados inválidos | Verificar formato da requisição |

## 🛠️ Configurações Avançadas

### Variáveis de Ambiente
```properties
# JWT
jwt.secret-key=SUA_CHAVE_SECRETA_AQUI
jwt.expiration=32400000

# CORS  
cors.allowed-origins=http://localhost:3000,http://localhost:3001

# Banco de Dados
DATABASE_URL=jdbc:mysql://localhost:3306/banco_alcatteia
DATABASE_USERNAME=root
DATABASE_PASSWORD=sua_senha
```

### Logs
Os logs do sistema são salvos em:
- **Console**: Logs em tempo real
- **Arquivo**: `logs/banco-alcatteia.log`

## 🚨 Troubleshooting

### Problema: Token Expirado
**Solução:** Fazer login novamente para obter novo token

### Problema: Erro 403 Forbidden  
**Solução:** Verificar se o usuário tem permissão para a operação

### Problema: Banco de Dados não Conecta
**Solução:** 
1. Verificar se MySQL está rodando
2. Confirmar credenciais no `application.properties`
3. Criar database se não existir

### Problema: Servidor não Inicia
**Solução:**
1. Verificar se porta 8080 está livre
2. Confirmar versão do Java (17+)
3. Executar `mvn clean install`

## 📞 Suporte

Para dúvidas e suporte técnico:
- **Email**: suporte@bancoalcatteia.com
- **Documentação**: Este arquivo
- **Issues**: Use o sistema de issues do repositório

---

**Versão do Sistema:** 1.0.0  
**Última Atualização:** Dezembro 2025 