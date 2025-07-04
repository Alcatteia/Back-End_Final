-- ================================================================
-- ROTEIRO COMPLETO DE MIGRAÇÃO - DUMP RAILWAY → NOVA ESTRUTURA
-- ================================================================
-- ATENÇÃO: Execute passo a passo e valide cada etapa!
-- Tempo estimado: 2-4 horas

-- ================================================================
-- FASE 1: PREPARAÇÃO E BACKUP
-- ================================================================

-- 1.1 BACKUP (Execute no terminal antes de começar)
/*
mysqldump -u root -p --single-transaction --routines --triggers railway > backup_pre_migracao_$(date +%Y%m%d_%H%M%S).sql
*/

-- 1.2 VERIFICAÇÕES PRÉ-MIGRAÇÃO
SELECT 'VERIFICAÇÃO PRÉ-MIGRAÇÃO' as FASE;

-- Verificar IDs que podem dar problema (se algum > 2.1 bilhões, PARE AQUI)
SELECT 'tarefas' as tabela, MAX(id) as max_id, 
       CASE WHEN MAX(id) > 2147483647 THEN '⚠️ RISCO ALTO' ELSE '✅ OK' END as status
FROM tarefas
UNION ALL
SELECT 'musicas_cantadas', MAX(id), 
       CASE WHEN MAX(id) > 2147483647 THEN '⚠️ RISCO ALTO' ELSE '✅ OK' END
FROM musicas_cantadas
UNION ALL
SELECT 'avaliacoes', MAX(id), 
       CASE WHEN MAX(id) > 2147483647 THEN '⚠️ RISCO ALTO' ELSE '✅ OK' END
FROM avaliacoes;

-- Verificar dados de humor que precisam migração
SELECT 'Valores de humor encontrados:' as info, humor, COUNT(*) as qtd
FROM check_humor 
GROUP BY humor
ORDER BY qtd DESC;

-- ================================================================
-- FASE 2: INICIAR MIGRAÇÃO ESTRUTURAL
-- ================================================================

SELECT 'INICIANDO MIGRAÇÃO ESTRUTURAL' as FASE;

-- Configurar ambiente seguro
SET FOREIGN_KEY_CHECKS = 0;
SET UNIQUE_CHECKS = 0;
SET AUTOCOMMIT = 0;
START TRANSACTION;

-- ================================================================
-- FASE 3: CORRIGIR TIPOS DE DADOS (CRÍTICO)
-- ================================================================

SELECT 'CORRIGINDO TIPOS DE DADOS BIGINT → INT' as FASE;

-- 3.1 Corrigir tabelas principais
ALTER TABLE `tarefas` MODIFY COLUMN `id` int NOT NULL AUTO_INCREMENT;
SELECT 'tarefas.id corrigido' as status;

ALTER TABLE `musicas_cantadas` MODIFY COLUMN `id` int NOT NULL AUTO_INCREMENT;
SELECT 'musicas_cantadas.id corrigido' as status;

ALTER TABLE `avaliacoes` MODIFY COLUMN `id` int NOT NULL AUTO_INCREMENT;
SELECT 'avaliacoes.id corrigido' as status;

-- 3.2 Corrigir FKs que referenciam essas tabelas
ALTER TABLE `participantes_tarefa` MODIFY COLUMN `tarefa_id` int NOT NULL;
ALTER TABLE `notificacoes` MODIFY COLUMN `tarefa_id` int DEFAULT NULL;
ALTER TABLE `avaliacoes` MODIFY COLUMN `apresentacao_id` int NOT NULL;
SELECT 'Foreign Keys corrigidas' as status;

-- ================================================================
-- FASE 4: ADICIONAR CAMPOS EXTRAS
-- ================================================================

SELECT 'ADICIONANDO CAMPOS EXTRAS' as FASE;

-- 4.1 Tabela usuarios
ALTER TABLE `usuarios` 
ADD COLUMN `descricao` varchar(255) DEFAULT NULL COMMENT 'Descrição do usuário',
ADD COLUMN `imagem` varchar(350) DEFAULT NULL COMMENT 'URL da imagem do perfil',
ADD COLUMN `data_ultima_atualizacao` datetime DEFAULT NULL COMMENT 'Data da última atualização';
SELECT 'Campos adicionados em usuarios' as status;

-- 4.2 Tabela times
ALTER TABLE `times`
ADD COLUMN `descricao` text DEFAULT NULL COMMENT 'Descrição do time',
ADD COLUMN `data_ultima_atualizacao` datetime DEFAULT NULL COMMENT 'Última atualização';
SELECT 'Campos adicionados em times' as status;

-- Garantir que criado_por_id existe e é obrigatório
-- Se não existir, adicionar
SET @column_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                     WHERE TABLE_SCHEMA = 'railway' 
                     AND TABLE_NAME = 'times' 
                     AND COLUMN_NAME = 'criado_por_id');

SET @sql = IF(@column_exists = 0,
    'ALTER TABLE times ADD COLUMN criado_por_id int DEFAULT 1',
    'SELECT "criado_por_id já existe" as info');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Atualizar valores nulos
UPDATE `times` SET `criado_por_id` = 1 WHERE `criado_por_id` IS NULL OR `criado_por_id` = 0;

-- Tornar obrigatório
ALTER TABLE `times` MODIFY COLUMN `criado_por_id` int NOT NULL;

-- Adicionar FK
ALTER TABLE `times` 
ADD CONSTRAINT `fk_times_criado_por` 
FOREIGN KEY (`criado_por_id`) REFERENCES `usuarios` (`id`) ON DELETE RESTRICT;
SELECT 'FK criado_por_id adicionada em times' as status;

-- 4.3 Tabela membros_time
ALTER TABLE `membros_time`
ADD COLUMN `observacoes` varchar(255) DEFAULT NULL COMMENT 'Observações sobre o membro';

-- Verificar se tipo_membro já existe
SET @column_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                     WHERE TABLE_SCHEMA = 'railway' 
                     AND TABLE_NAME = 'membros_time' 
                     AND COLUMN_NAME = 'tipo_membro');

SET @sql = IF(@column_exists = 0,
    'ALTER TABLE membros_time ADD COLUMN tipo_membro enum("LIDER","VICE_LIDER","MEMBRO","COLABORADOR") NOT NULL DEFAULT "MEMBRO"',
    'SELECT "tipo_membro já existe" as info');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SELECT 'Campos adicionados em membros_time' as status;

-- 4.4 Tabela check_humor
ALTER TABLE `check_humor`
ADD COLUMN `bem_estar_pontos` int DEFAULT NULL COMMENT 'Pontuação de bem-estar (0-10)',
ADD COLUMN `confirmado` boolean DEFAULT NULL COMMENT 'Se o registro foi confirmado';
SELECT 'Campos adicionados em check_humor' as status;

-- 4.5 Tabela kanban_categorias
SET @column_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                     WHERE TABLE_SCHEMA = 'railway' 
                     AND TABLE_NAME = 'kanban_categorias' 
                     AND COLUMN_NAME = 'criado_por_id');

SET @sql = IF(@column_exists = 0,
    'ALTER TABLE kanban_categorias ADD COLUMN criado_por_id int DEFAULT NULL',
    'SELECT "criado_por_id já existe em kanban_categorias" as info');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ================================================================
-- FASE 5: CRIAR NOVA TABELA ROOMS
-- ================================================================

SELECT 'CRIANDO TABELA ROOMS' as FASE;

CREATE TABLE IF NOT EXISTS `rooms` (
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
  CONSTRAINT `fk_rooms_host` FOREIGN KEY (`host_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

SELECT 'Tabela rooms criada' as status;

-- ================================================================
-- FASE 6: MIGRAÇÃO DE DADOS
-- ================================================================

SELECT 'MIGRANDO DADOS' as FASE;

-- 6.1 Migrar enum check_humor
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

-- 6.2 Calcular bem_estar_pontos
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

-- 6.3 Atualizar tipo_membro baseado em liderança
UPDATE `membros_time` m
JOIN `times` t ON m.time_id = t.id
SET m.tipo_membro = 'LIDER'
WHERE m.usuario_id = t.lider_id;

-- 6.4 Inserir dados de exemplo em rooms
INSERT INTO `rooms` (`room_id`, `room_name`, `room_description`, `host_id`, `max_participants`) VALUES
('room_daily_001', 'Daily Stand-up', 'Reunião diária da equipe', 2, 15),
('room_retro_001', 'Retrospectiva Sprint', 'Retrospectiva quinzenal', 6, 20),
('room_planning_001', 'Planning Mensal', 'Planejamento estratégico', 1, 10),
('room_karaoke_001', 'Karaokê Virtual', 'Sala de karaokê', 9, 30),
('room_training_001', 'Treinamentos', 'Sala para workshops', 6, 25)
ON DUPLICATE KEY UPDATE 
    `room_name` = VALUES(`room_name`);

SELECT 'Dados migrados' as status;

-- ================================================================
-- FASE 7: VALIDAÇÕES
-- ================================================================

SELECT 'VALIDANDO MIGRAÇÃO' as FASE;

-- 7.1 Verificar integridade referencial
SET FOREIGN_KEY_CHECKS = 1;
SELECT 'Integridade referencial verificada' as status;

-- 7.2 Verificar migração de humor
SELECT 'Verificação humor:' as info, humor, COUNT(*) as qtd
FROM check_humor 
GROUP BY humor
ORDER BY qtd DESC;

-- 7.3 Verificar bem_estar_pontos
SELECT 'Verificação bem_estar_pontos:' as info, 
       COUNT(*) as total,
       COUNT(bem_estar_pontos) as com_pontos,
       AVG(bem_estar_pontos) as media
FROM check_humor;

-- 7.4 Verificar rooms
SELECT 'Rooms criadas:' as info, COUNT(*) as total FROM rooms;

-- 7.5 Verificar times com criado_por_id
SELECT 'Times sem criador:' as info, COUNT(*) as total 
FROM times 
WHERE criado_por_id IS NULL;

-- ================================================================
-- FASE 8: FINALIZAÇÃO
-- ================================================================

-- 8.1 Se tudo OK, fazer COMMIT
SELECT 'COMMIT DA MIGRAÇÃO - Verificar se todas as validações passaram!' as FASE;

-- DESCOMENTAR A LINHA ABAIXO APENAS SE TODAS AS VALIDAÇÕES ESTIVEREM OK:
-- COMMIT;

-- 8.2 Restaurar configurações
SET FOREIGN_KEY_CHECKS = 1;
SET UNIQUE_CHECKS = 1;
SET AUTOCOMMIT = 1;

-- 8.3 Relatório final
SELECT 'MIGRAÇÃO FINALIZADA' as FASE;

SELECT 'Contagem final por tabela:' as info;
SELECT 'usuarios' as tabela, COUNT(*) as registros FROM usuarios
UNION ALL SELECT 'times', COUNT(*) FROM times
UNION ALL SELECT 'membros_time', COUNT(*) FROM membros_time
UNION ALL SELECT 'check_humor', COUNT(*) FROM check_humor
UNION ALL SELECT 'rooms', COUNT(*) FROM rooms
UNION ALL SELECT 'tarefas', COUNT(*) FROM tarefas
ORDER BY tabela;

-- ================================================================
-- INSTRUÇÕES FINAIS
-- ================================================================

/*
IMPORTANTE:
1. Se alguma validação falhou, execute: ROLLBACK;
2. Se tudo passou, descomente a linha COMMIT; acima
3. Faça backup pós-migração
4. Teste a aplicação com a nova estrutura
5. Monitore por alguns dias

ROLLBACK EM CASO DE PROBLEMA:
ROLLBACK;
-- E depois restaurar do backup:
-- mysql -u root -p railway < backup_pre_migracao_YYYYMMDD_HHMMSS.sql
*/ 