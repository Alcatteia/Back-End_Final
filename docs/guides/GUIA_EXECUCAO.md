# 🚀 GUIA DE EXECUÇÃO - MIGRAÇÃO COMPLETA

## ⏱️ Tempo Estimado: 2-4 horas
## 🔧 Pré-requisitos: MySQL, Backup, Acesso de administrador

---

## 📋 CHECKLIST PRÉ-MIGRAÇÃO

### ✅ Preparação Obrigatória
- [ ] **Backup completo realizado**
- [ ] **Aplicação parada** (evitar escritas durante migração)
- [ ] **Espaço em disco suficiente** (pelo menos 2x o tamanho atual)
- [ ] **Conexão estável** com o banco de dados
- [ ] **Janela de manutenção** agendada (4-6 horas)

### ⚠️ Verificações Críticas
- [ ] **IDs não excedem 2.1 bilhões** (senão a migração falhará)
- [ ] **Integridade referencial OK**
- [ ] **Nenhuma transação longa em execução**

---

## 🎯 EXECUÇÃO PASSO A PASSO

### **PASSO 1: BACKUP** (5-10 min)
```bash
# No terminal (fora do MySQL)
mysqldump -u root -p --single-transaction --routines --triggers railway > backup_$(date +%Y%m%d_%H%M%S).sql

# Verificar se foi criado
ls -la backup_*.sql
```

### **PASSO 2: EXECUTAR MIGRAÇÃO** (30-60 min)
```bash
# Conectar ao MySQL
mysql -u root -p railway

# Executar o arquivo de migração
source roteiro_migracao.sql
```

### **PASSO 3: MONITORAR EXECUÇÃO**
Durante a execução, observe:
- ✅ Mensagens de status aparecem
- ⚠️ Nenhum erro SQL
- 📊 Validações retornam valores esperados

### **PASSO 4: VALIDAR RESULTADOS**
As validações automáticas devem mostrar:
```sql
-- ✅ Todos os status devem ser "OK"
-- ✅ Humor migrado corretamente
-- ✅ Bem-estar pontos calculados
-- ✅ Rooms criadas
-- ✅ Times sem criador = 0
```

### **PASSO 5: FINALIZAR**
Se **TODAS** as validações passaram:
```sql
-- Descomentar e executar:
COMMIT;
```

Se **ALGUMA** validação falhou:
```sql
-- Executar imediatamente:
ROLLBACK;
```

---

## 🔍 VALIDAÇÕES MANUAIS ADICIONAIS

### Após COMMIT, execute:
```sql
-- 1. Verificar estrutura das tabelas
DESCRIBE usuarios;
DESCRIBE times;
DESCRIBE check_humor;
DESCRIBE rooms;

-- 2. Verificar dados migrados
SELECT humor, COUNT(*) FROM check_humor GROUP BY humor;
SELECT COUNT(*) FROM rooms;

-- 3. Verificar integridade
SELECT COUNT(*) FROM times WHERE criado_por_id IS NULL;  -- Deve ser 0
```

---

## ⚡ TESTE DA APLICAÇÃO

### Após migração bem-sucedida:
1. **Reiniciar aplicação Spring Boot**
2. **Testar endpoints principais**:
   - Login/autenticação
   - Dashboard
   - Check humor
   - Criação de tarefas
3. **Verificar logs** da aplicação
4. **Testar nova funcionalidade** de rooms (se implementada)

---

## 🚨 PLANO DE EMERGÊNCIA

### Se algo der errado:

#### **Durante a migração:**
```sql
-- PARE IMEDIATAMENTE e execute:
ROLLBACK;
SET FOREIGN_KEY_CHECKS = 1;
```

#### **Depois do COMMIT:**
```bash
# Restaurar do backup:
mysql -u root -p railway < backup_YYYYMMDD_HHMMSS.sql
```

#### **Problemas comuns:**
- **Erro "Data too long"** → IDs excedem INT, usar ROLLBACK
- **Foreign key constraint fails** → Dados órfãos, verificar integridade
- **Duplicate entry** → Executar novamente pode ter duplicatas

---

## 📊 MONITORAMENTO PÓS-MIGRAÇÃO

### Primeiras 24 horas:
- [ ] **Performance** da aplicação normal
- [ ] **Logs** sem erros relacionados ao banco
- [ ] **Funcionalidades** principais funcionando
- [ ] **Backup pós-migração** realizado

### Primeira semana:
- [ ] **Usuários** relataram problemas?
- [ ] **Check humor** funcionando corretamente?
- [ ] **Nova estrutura** sendo utilizada?

---

## 📝 DOCUMENTAÇÃO DA MIGRAÇÃO

### Registrar:
- **Data/hora** de execução
- **Problemas** encontrados
- **Tempo total** gasto
- **Tamanho** do banco antes/depois
- **Validações** executadas

### Arquivos gerados:
- `backup_YYYYMMDD_HHMMSS.sql` (manter por 30 dias)
- `roteiro_migracao.sql` (versionado)
- `ANALISE_COMPARATIVA.md` (documentação)

---

## 🎉 SUCESSO!

Se chegou até aqui sem problemas:
1. ✅ **Migração concluída**
2. ✅ **Estrutura modernizada**  
3. ✅ **Dados preservados**
4. ✅ **Nova funcionalidade disponível**

**Próximos passos:**
- Implementar frontend para rooms
- Aproveitar novos campos (descrição, imagem, etc.)
- Monitorar performance
- Planejar próximas melhorias 