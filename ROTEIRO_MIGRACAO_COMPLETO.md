# ROTEIRO COMPLETO DE MIGRAÇÃO
## Do Dump Railway para Nova Estrutura Otimizada

### 📋 VISÃO GERAL
Esta migração irá:
- Corrigir inconsistências de tipos de dados (BIGINT → INT)
- Adicionar campos extras importantes
- Implementar nova funcionalidade de rooms
- Migrar dados de check_humor para novo enum
- Manter dados existentes preservados

### ⚠️ RISCOS E PRECAUÇÕES
- **RISCO ALTO**: Alteração de tipos de dados pode falhar se houver IDs > 2.1 bilhões
- **RISCO MÉDIO**: Migração de enum pode perder dados se houver valores não mapeados
- **RISCO BAIXO**: Adição de campos extras é segura

---

## 🚀 FASE 1: PREPARAÇÃO

### 1.1 Backup Completo
```bash
# Fazer backup completo do banco
mysqldump -u root -p --single-transaction --routines --triggers railway > backup_pre_migracao_$(date +%Y%m%d_%H%M%S).sql

# Verificar se o backup foi criado
ls -la backup_pre_migracao_*.sql
```

### 1.2 Análise do Estado Atual
```sql
-- Conectar ao banco
mysql -u root -p railway

-- Verificar versão e configurações
SELECT VERSION();
SHOW VARIABLES LIKE 'innodb_version';

-- Analisar tamanho das tabelas
SELECT 
    table_name,
    table_rows,
    ROUND(((data_length + index_length) / 1024 / 1024), 2) AS 'Size_MB'
FROM information_schema.tables 
WHERE table_schema = 'railway'
ORDER BY data_length + index_length DESC;
```

### 1.3 Verificações Pré-Migração
```sql
-- Verificar IDs que podem exceder INT
SELECT 'tarefas' as tabela, MAX(id) as max_id, 
       CASE WHEN MAX(id) > 2147483647 THEN 'RISCO ALTO' ELSE 'OK' END as status
FROM tarefas
UNION ALL
SELECT 'musicas_cantadas', MAX(id), 
       CASE WHEN MAX(id) > 2147483647 THEN 'RISCO ALTO' ELSE 'OK' END
FROM musicas_cantadas
UNION ALL
SELECT 'avaliacoes', MAX(id), 
       CASE WHEN MAX(id) > 2147483647 THEN 'RISCO ALTO' ELSE 'OK' END
FROM avaliacoes;

-- Verificar integridade referencial
SELECT 'Verificando FKs...' as status;
SET FOREIGN_KEY_CHECKS = 1;
```

---

## 🔧 FASE 2: MIGRAÇÃO ESTRUTURAL

### 2.1 Preparar Ambiente
```sql
-- Desabilitar verificações temporariamente
SET FOREIGN_KEY_CHECKS = 0;
SET UNIQUE_CHECKS = 0;
SET AUTOCOMMIT = 0;

-- Iniciar transação
START TRANSACTION;
```

### 2.2 Corrigir Tipos de Dados (CRÍTICO)
```sql
-- 2.2.1 Corrigir tarefas.id (BIGINT → INT)
ALTER TABLE `tarefas` MODIFY COLUMN `id` int NOT NULL AUTO_INCREMENT;

-- 2.2.2 Corrigir musicas_cantadas.id (BIGINT → INT)
ALTER TABLE `musicas_cantadas` MODIFY COLUMN `id` int NOT NULL AUTO_INCREMENT;

-- 2.2.3 Corrigir avaliacoes.id (BIGINT → INT)
ALTER TABLE `avaliacoes` MODIFY COLUMN `id` int NOT NULL AUTO_INCREMENT;

-- 2.2.4 Corrigir FKs que referenciam essas tabelas
ALTER TABLE `participantes_tarefa` MODIFY COLUMN `tarefa_id` int NOT NULL;
ALTER TABLE `notificacoes` MODIFY COLUMN `tarefa_id` int DEFAULT NULL;
ALTER TABLE `avaliacoes` MODIFY COLUMN `apresentacao_id` int NOT NULL;
```

### 2.3 Adicionar Campos Extras
```sql
-- 2.3.1 Usuarios
ALTER TABLE `usuarios` 
ADD COLUMN `descricao` varchar(255) DEFAULT NULL COMMENT 'Descrição do usuário',
ADD COLUMN `imagem` varchar(350) DEFAULT NULL COMMENT 'URL da imagem do perfil',
ADD COLUMN `data_ultima_atualizacao` datetime DEFAULT NULL COMMENT 'Data da última atualização',
DROP COLUMN `data_criacao` IF EXISTS,
ADD COLUMN `data_nascimento` date DEFAULT NULL COMMENT 'Data de nascimento',
ADD COLUMN `data_criacao` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'Data de criação da conta';

-- 2.3.2 Times
ALTER TABLE `times`
ADD COLUMN `descricao` text COMMENT 'Descrição do time',
ADD COLUMN `data_ultima_atualizacao` datetime DEFAULT NULL COMMENT 'Última atualização',
MODIFY COLUMN `criado_por_id` int NOT NULL COMMENT 'Usuário que criou o time';

-- Adicionar FK se não existir
ALTER TABLE `times` 
ADD CONSTRAINT `fk_times_criado_por` 
FOREIGN KEY (`criado_por_id`) REFERENCES `usuarios` (`id`) ON DELETE RESTRICT;

-- 2.3.3 Membros Time
ALTER TABLE `membros_time`
DROP COLUMN `data_entrada` IF EXISTS,
ADD COLUMN `data_entrada` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data de entrada no time',
ADD COLUMN `observacoes` varchar(255) DEFAULT NULL COMMENT 'Observações sobre o membro';

-- 2.3.4 Check Humor
ALTER TABLE `check_humor`
ADD COLUMN `bem_estar_pontos` int DEFAULT NULL COMMENT 'Pontuação de bem-estar (0-10)',
ADD COLUMN `confirmado` boolean DEFAULT NULL COMMENT 'Se o registro foi confirmado';

-- 2.3.5 Kanban Categorias
ALTER TABLE `kanban_categorias`
ADD COLUMN `criado_por_id` int DEFAULT NULL COMMENT 'Usuário que criou a categoria';

ALTER TABLE `kanban_categorias` 
ADD CONSTRAINT `fk_kanban_categorias_criado_por` 
FOREIGN KEY (`criado_por_id`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL;
```

### 2.4 Criar Nova Tabela Rooms
```sql
CREATE TABLE `rooms` (
  `room_id` varchar(20) NOT NULL,
  `room_name` text NOT NULL,
  `room_description` text DEFAULT NULL,
  `host_id` int NOT NULL,
  `max_participants` int DEFAULT NULL,
  `is_active` boolean DEFAULT TRUE,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`room_id`),
  KEY `idx_rooms_host` (`host_id`),
  KEY `idx_rooms_active` (`is_active`),
  KEY `idx_rooms_created_at` (`created_at`),
  CONSTRAINT `fk_rooms_host` FOREIGN KEY (`host_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Salas de reunião virtuais';
```

### 2.5 Remover Tabela Obsoleta
```sql
-- Remover tabela feedback antiga (manter apenas feedbacks)
DROP TABLE IF EXISTS `feedback`;
```

---

## 📊 FASE 3: MIGRAÇÃO DE DADOS

### 3.1 Migrar Enum Check_Humor
```sql
-- Verificar dados existentes
SELECT humor, COUNT(*) as qtd 
FROM check_humor 
GROUP BY humor
ORDER BY qtd DESC;

-- Migrar valores antigos para novos
UPDATE `check_humor` SET 
    `humor` = CASE 
        WHEN `humor` = 'Ótimo' THEN 'FELIZ'
        WHEN `humor` = 'Bom' THEN 'ANIMADO'
        WHEN `humor` = 'Neutro' THEN 'CALMO'
        WHEN `humor` = 'Ruim' THEN 'DESANIMADO'
        WHEN `humor` = 'Péssimo' THEN 'ESTRESSADO'
        ELSE `humor`
    END
WHERE `humor` IN ('Ótimo', 'Bom', 'Neutro', 'Ruim', 'Péssimo');

-- Calcular bem_estar_pontos baseado no humor
UPDATE `check_humor` SET 
    `bem_estar_pontos` = CASE 
        WHEN `humor` = 'FELIZ' THEN 9
        WHEN `humor` = 'ANIMADO' THEN 8
        WHEN `humor` = 'CALMO' THEN 7
        WHEN `humor` = 'DESANIMADO' THEN 4
        WHEN `humor` = 'ESTRESSADO' THEN 3
        ELSE 5
    END,
    `confirmado` = TRUE
WHERE `bem_estar_pontos` IS NULL;
```

### 3.2 Atualizar Dados de Usuários
```sql
-- Atualizar criado_por_id em times (usar primeiro usuário RH)
UPDATE `times` 
SET `criado_por_id` = (
    SELECT id FROM usuarios WHERE tipo_usuario = 'RH' LIMIT 1
)
WHERE `criado_por_id` = 0 OR `criado_por_id` IS NULL;

-- Atualizar criado_por_id em kanban_categorias
UPDATE `kanban_categorias` 
SET `criado_por_id` = (
    SELECT id FROM usuarios WHERE tipo_usuario = 'RH' LIMIT 1
)
WHERE `criado_por_id` IS NULL;

-- Atualizar tipo_membro baseado em liderança
UPDATE `membros_time` m
JOIN `times` t ON m.time_id = t.id
SET m.tipo_membro = 'LIDER'
WHERE m.usuario_id = t.lider_id;
```

### 3.3 Inserir Dados de Demonstração
```sql
-- Rooms de exemplo
INSERT INTO `rooms` (`room_id`, `room_name`, `room_description`, `host_id`, `max_participants`) VALUES
('room_daily_001', 'Daily Stand-up', 'Reunião diária da equipe de desenvolvimento', 2, 15),
('room_retro_001', 'Retrospectiva Sprint', 'Retrospectiva quinzenal da equipe', 6, 20),
('room_planning_001', 'Planning Mensal', 'Planejamento estratégico mensal', 1, 10),
('room_karaoke_001', 'Karaokê Virtual', 'Sala de karaokê para descontração', 9, 30),
('room_feedback_001', 'Sessão de Feedback', 'Sala para sessões de feedback', 1, 8),
('room_training_001', 'Treinamentos', 'Sala para treinamentos e workshops', 6, 25)
ON DUPLICATE KEY UPDATE 
    `room_name` = VALUES(`room_name`),
    `room_description` = VALUES(`room_description`);

-- Atualizar descrições dos usuários existentes
UPDATE `usuarios` SET 
    `descricao` = CASE 
        WHEN `nome` LIKE '%Raquel%' THEN 'Gestora de RH especializada em bem-estar organizacional'
        WHEN `nome` LIKE '%Carlos%' THEN 'Líder técnico com experiência em arquitetura de software'
        WHEN `nome` LIKE '%Fernanda%' THEN 'Desenvolvedora Full Stack com foco em React e Node.js'
        WHEN `nome` LIKE '%João%' THEN 'Desenvolvedor Backend especialista em APIs REST'
        WHEN `nome` LIKE '%Maria%' THEN 'Desenvolvedora Frontend com expertise em UX/UI'
        WHEN `nome` LIKE '%Pedro%' THEN 'Líder técnico e especialista em DevOps'
        WHEN `nome` LIKE '%Juliana%' THEN 'Desenvolvedora Frontend e Designer UX/UI'
        WHEN `nome` LIKE '%Roberto%' THEN 'Analista de Qualidade e Testes Automatizados'
        WHEN `nome` LIKE '%Patrícia%' THEN 'Analista de RH e Desenvolvimento Organizacional'
        WHEN `nome` LIKE '%Lucas%' THEN 'Desenvolvedor Full Stack e Especialista DevOps'
        ELSE `descricao`
    END
WHERE `descricao` IS NULL OR `descricao` = '';
```

---

## ✅ FASE 4: VALIDAÇÃO

### 4.1 Verificar Integridade Estrutural
```sql
-- Verificar se todas as FKs estão funcionando
SET FOREIGN_KEY_CHECKS = 1;

-- Verificar se há registros órfãos
SELECT 'Verificando integridade referencial...' as status;

-- Verificar times sem criador
SELECT t.id, t.nome_time, t.criado_por_id, u.nome as criador
FROM times t
LEFT JOIN usuarios u ON t.criado_por_id = u.id
WHERE u.id IS NULL;

-- Verificar membros sem usuário ou time
SELECT m.id, m.usuario_id, m.time_id, u.nome, t.nome_time
FROM membros_time m
LEFT JOIN usuarios u ON m.usuario_id = u.id
LEFT JOIN times t ON m.time_id = t.id
WHERE u.id IS NULL OR t.id IS NULL;
```

### 4.2 Verificar Dados Migrados
```sql
-- Verificar enum check_humor
SELECT humor, COUNT(*) as quantidade
FROM check_humor
GROUP BY humor
ORDER BY quantidade DESC;

-- Verificar bem_estar_pontos
SELECT 
    bem_estar_pontos,
    COUNT(*) as quantidade,
    AVG(bem_estar_pontos) as media
FROM check_humor
WHERE bem_estar_pontos IS NOT NULL
GROUP BY bem_estar_pontos
ORDER BY bem_estar_pontos;

-- Verificar rooms
SELECT 
    room_id,
    room_name,
    host_id,
    u.nome as host_nome,
    created_at
FROM rooms r
JOIN usuarios u ON r.host_id = u.id
ORDER BY created_at DESC;
```

### 4.3 Verificar Performance
```sql
-- Verificar se os índices estão sendo usados
EXPLAIN SELECT * FROM check_humor WHERE humor = 'FELIZ';
EXPLAIN SELECT * FROM rooms WHERE host_id = 1;
EXPLAIN SELECT * FROM membros_time WHERE tipo_membro = 'LIDER';

-- Verificar tamanho das tabelas após migração
SELECT 
    table_name,
    table_rows,
    ROUND(((data_length + index_length) / 1024 / 1024), 2) AS 'Size_MB'
FROM information_schema.tables 
WHERE table_schema = 'railway'
ORDER BY data_length + index_length DESC;
```

---

## 🔄 FASE 5: COMMIT OU ROLLBACK

### 5.1 Se Tudo OK - Commit
```sql
-- Se todas as validações passaram
COMMIT;
SET FOREIGN_KEY_CHECKS = 1;
SET UNIQUE_CHECKS = 1;
SET AUTOCOMMIT = 1;

SELECT 'MIGRAÇÃO CONCLUÍDA COM SUCESSO!' as status;
```

### 5.2 Se Houver Problemas - Rollback
```sql
-- Se houver algum problema
ROLLBACK;
SET FOREIGN_KEY_CHECKS = 1;
SET UNIQUE_CHECKS = 1;
SET AUTOCOMMIT = 1;

-- Restaurar do backup
-- mysql -u root -p railway < backup_pre_migracao_YYYYMMDD_HHMMSS.sql
```

---

## 📈 FASE 6: PÓS-MIGRAÇÃO

### 6.1 Otimização
```sql
-- Adicionar índices para melhor performance
ALTER TABLE `usuarios` ADD INDEX `idx_usuarios_descricao` (`descricao`(100));
ALTER TABLE `usuarios` ADD INDEX `idx_usuarios_data_ultima_atualizacao` (`data_ultima_atualizacao`);
ALTER TABLE `times` ADD INDEX `idx_times_data_ultima_atualizacao` (`data_ultima_atualizacao`);
ALTER TABLE `membros_time` ADD INDEX `idx_membros_time_tipo` (`tipo_membro`);
ALTER TABLE `check_humor` ADD INDEX `idx_check_humor_bem_estar` (`bem_estar_pontos`);
ALTER TABLE `check_humor` ADD INDEX `idx_check_humor_confirmado` (`confirmado`);
ALTER TABLE `rooms` ADD INDEX `idx_rooms_active` (`is_active`);
ALTER TABLE `rooms` ADD INDEX `idx_rooms_created_at` (`created_at`);

-- Analisar tabelas para otimizar estatísticas
ANALYZE TABLE usuarios, times, membros_time, check_humor, rooms;
```

### 6.2 Limpeza
```sql
-- Remover dados temporários se houver
-- (Não aplicável nesta migração)

-- Verificar espaço em disco
SELECT 
    SUM(ROUND(((data_length + index_length) / 1024 / 1024), 2)) AS 'Total_Size_MB'
FROM information_schema.tables 
WHERE table_schema = 'railway';
```

### 6.3 Documentação
```sql
-- Gerar relatório final
SELECT 
    'RELATÓRIO FINAL DE MIGRAÇÃO' as titulo,
    NOW() as data_conclusao;

-- Contar registros por tabela
SELECT 'usuarios' as tabela, COUNT(*) as registros FROM usuarios
UNION ALL SELECT 'times', COUNT(*) FROM times
UNION ALL SELECT 'membros_time', COUNT(*) FROM membros_time
UNION ALL SELECT 'check_humor', COUNT(*) FROM check_humor
UNION ALL SELECT 'rooms', COUNT(*) FROM rooms
UNION ALL SELECT 'tarefas', COUNT(*) FROM tarefas
UNION ALL SELECT 'kanban_tarefas', COUNT(*) FROM kanban_tarefas
UNION ALL SELECT 'feedbacks', COUNT(*) FROM feedbacks
ORDER BY tabela;
```

---

## 🚨 PLANO DE CONTINGÊNCIA

### Em caso de falha crítica:
1. **PARAR imediatamente** a migração
2. **Executar ROLLBACK** se ainda na transação
3. **Restaurar do backup**: 
   ```bash
   mysql -u root -p railway < backup_pre_migracao_YYYYMMDD_HHMMSS.sql
   ```
4. **Analisar logs** para identificar a causa
5. **Corrigir problema** e tentar novamente

### Pontos críticos de falha:
- **Alteração de tipos BIGINT → INT**: Se houver IDs > 2.1 bilhões
- **Migração de enum**: Se houver valores não mapeados
- **Constraints FK**: Se houver dados órfãos

---

## 📝 CHECKLIST FINAL

### Antes da migração:
- [ ] Backup completo realizado
- [ ] Verificação de IDs grandes
- [ ] Análise de integridade referencial
- [ ] Ambiente de teste validado

### Durante a migração:
- [ ] Logs de erro monitorados
- [ ] Transações usadas adequadamente
- [ ] Validações executadas passo a passo
- [ ] Rollback pronto se necessário

### Após a migração:
- [ ] Integridade referencial verificada
- [ ] Dados migrados validados
- [ ] Performance testada
- [ ] Aplicação testada com nova estrutura
- [ ] Backup pós-migração realizado

### Tempo estimado: 2-4 horas
### Janela de manutenção recomendada: 4-6 horas 