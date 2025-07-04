-- ==================================================
-- SCRIPT DE MIGRAÇÃO: INCORPORANDO MELHORIAS DO DUMP
-- ==================================================
-- Este script aplica as melhorias identificadas no dump do Railway
-- mantendo a consistência do script de demonstração original

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- Iniciar transação para rollback em caso de erro
START TRANSACTION;

-- ==================================================
-- 1. CORRIGIR INCONSISTÊNCIAS DE TIPOS DE DADOS
-- ==================================================

-- Verificar se há dados inconsistentes antes de aplicar correções
SELECT 'Verificando integridade dos dados...' as STATUS;

-- Verificar se existem IDs que excedem limite do INT
SELECT 
    'tarefas' as tabela,
    COUNT(*) as total_registros,
    MAX(id) as maior_id,
    CASE WHEN MAX(id) > 2147483647 THEN 'ATENÇÃO: ID excede limite INT' ELSE 'OK' END as status
FROM tarefas
UNION ALL
SELECT 
    'musicas_cantadas' as tabela,
    COUNT(*) as total_registros,
    MAX(id) as maior_id,
    CASE WHEN MAX(id) > 2147483647 THEN 'ATENÇÃO: ID excede limite INT' ELSE 'OK' END as status
FROM musicas_cantadas
UNION ALL
SELECT 
    'avaliacoes' as tabela,
    COUNT(*) as total_registros,
    MAX(id) as maior_id,
    CASE WHEN MAX(id) > 2147483647 THEN 'ATENÇÃO: ID excede limite INT' ELSE 'OK' END as status
FROM avaliacoes;

-- ==================================================
-- 2. ADICIONAR CAMPOS EXTRAS NAS TABELAS EXISTENTES
-- ==================================================

-- 2.1. Adicionar campos na tabela usuarios
SELECT 'Adicionando campos extras na tabela usuarios...' as STATUS;

ALTER TABLE `usuarios` 
ADD COLUMN `descricao` varchar(255) DEFAULT NULL COMMENT 'Descrição do usuário',
ADD COLUMN `imagem` varchar(350) DEFAULT NULL COMMENT 'URL da imagem do perfil',
ADD COLUMN `data_ultima_atualizacao` datetime DEFAULT NULL COMMENT 'Data da última atualização do perfil';

-- 2.2. Adicionar campos na tabela times
SELECT 'Adicionando campos extras na tabela times...' as STATUS;

-- Primeiro adicionar campo opcional
ALTER TABLE `times`
ADD COLUMN `data_ultima_atualizacao` datetime DEFAULT NULL COMMENT 'Data da última atualização do time';

-- Adicionar campo criado_por_id com valor padrão
ALTER TABLE `times`
ADD COLUMN `criado_por_id` int DEFAULT 1 COMMENT 'ID do usuário que criou o time';

-- Atualizar registros existentes
UPDATE `times` SET `criado_por_id` = 1 WHERE `criado_por_id` IS NULL;

-- Tornar o campo obrigatório
ALTER TABLE `times`
MODIFY COLUMN `criado_por_id` int NOT NULL;

-- Adicionar constraint FK
ALTER TABLE `times` 
ADD CONSTRAINT `fk_times_criado_por` 
FOREIGN KEY (`criado_por_id`) REFERENCES `usuarios` (`id`) ON DELETE RESTRICT;

-- 2.3. Adicionar campos na tabela membros_time
SELECT 'Adicionando campos extras na tabela membros_time...' as STATUS;

ALTER TABLE `membros_time`
ADD COLUMN `tipo_membro` enum('LIDER','VICE_LIDER','MEMBRO','COLABORADOR') NOT NULL DEFAULT 'MEMBRO' COMMENT 'Tipo de membro no time',
ADD COLUMN `observacoes` varchar(255) DEFAULT NULL COMMENT 'Observações sobre o membro';

-- Atualizar tipo_membro para líderes existentes
UPDATE `membros_time` m
JOIN `times` t ON m.time_id = t.id
SET m.tipo_membro = 'LIDER'
WHERE m.usuario_id = t.lider_id;

-- Modificar campo data_entrada para datetime
ALTER TABLE `membros_time`
MODIFY COLUMN `data_entrada` datetime NOT NULL;

-- 2.4. Adicionar campos na tabela check_humor
SELECT 'Adicionando campos extras na tabela check_humor...' as STATUS;

ALTER TABLE `check_humor`
ADD COLUMN `bem_estar_pontos` int DEFAULT NULL COMMENT 'Pontuação de bem-estar (0-10)',
ADD COLUMN `confirmado` boolean DEFAULT NULL COMMENT 'Se o registro foi confirmado pelo usuário';

-- Atualizar bem_estar_pontos baseado no humor
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

-- 2.5. Adicionar campo na tabela kanban_categorias
SELECT 'Adicionando campo extra na tabela kanban_categorias...' as STATUS;

ALTER TABLE `kanban_categorias`
ADD COLUMN `criado_por_id` int DEFAULT NULL COMMENT 'ID do usuário que criou a categoria';

-- Atualizar registros existentes
UPDATE `kanban_categorias` SET `criado_por_id` = 1 WHERE `criado_por_id` IS NULL;

-- Adicionar constraint FK
ALTER TABLE `kanban_categorias` 
ADD CONSTRAINT `fk_kanban_categorias_criado_por` 
FOREIGN KEY (`criado_por_id`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL;

-- ==================================================
-- 3. CRIAR NOVA TABELA ROOMS
-- ==================================================

SELECT 'Criando nova tabela rooms...' as STATUS;

CREATE TABLE `rooms` (
  `room_id` varchar(20) NOT NULL,
  `room_name` text NOT NULL,
  `host_id` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`room_id`),
  KEY `idx_rooms_host` (`host_id`),
  CONSTRAINT `fk_rooms_host` FOREIGN KEY (`host_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Salas de reunião virtuais';

-- ==================================================
-- 4. MIGRAR DADOS DE CHECK_HUMOR SE NECESSÁRIO
-- ==================================================

-- Verificar se existem dados com enum antigo
SELECT 'Verificando dados de check_humor...' as STATUS;

SELECT 
    humor,
    COUNT(*) as quantidade,
    CASE 
        WHEN humor IN ('FELIZ','ANIMADO','CALMO','DESANIMADO','ESTRESSADO') THEN 'OK'
        ELSE 'NECESSITA MIGRAÇÃO'
    END as status
FROM check_humor 
GROUP BY humor;

-- Se existirem dados antigos, fazer migração
-- (Este UPDATE só executará se houver dados antigos)
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

-- ==================================================
-- 5. INSERIR DADOS DE DEMONSTRAÇÃO PARA NOVAS FUNCIONALIDADES
-- ==================================================

-- 5.1. Adicionar dados de exemplo para rooms
SELECT 'Inserindo dados de exemplo para rooms...' as STATUS;

INSERT INTO `rooms` (`room_id`, `room_name`, `host_id`) VALUES
('room_demo_001', 'Sala de Reunião Daily', 1),
('room_demo_002', 'Retrospectiva Sprint', 2),
('room_demo_003', 'Planejamento Trimestral', 1),
('room_demo_004', 'Karaokê Virtual', 9),
('room_demo_005', 'Revisão de Código', 6)
ON DUPLICATE KEY UPDATE 
    `room_name` = VALUES(`room_name`),
    `host_id` = VALUES(`host_id`);

-- 5.2. Atualizar descrições dos usuários de demonstração
SELECT 'Atualizando descrições dos usuários...' as STATUS;

UPDATE `usuarios` SET 
    `descricao` = CASE 
        WHEN `id` = 1 THEN 'Responsável pelo RH e bem-estar dos funcionários'
        WHEN `id` = 2 THEN 'Líder técnico do time de Backend'
        WHEN `id` = 3 THEN 'Desenvolvedora Full Stack'
        WHEN `id` = 4 THEN 'Desenvolvedor Backend especialista em APIs'
        WHEN `id` = 5 THEN 'Desenvolvedora Frontend especialista em React'
        WHEN `id` = 6 THEN 'Líder técnico e DevOps'
        WHEN `id` = 7 THEN 'Desenvolvedora Frontend e UX/UI'
        WHEN `id` = 8 THEN 'Analista de Qualidade e Testes'
        WHEN `id` = 9 THEN 'Analista de RH e Recursos Humanos'
        WHEN `id` = 10 THEN 'Desenvolvedor Full Stack e DevOps'
        ELSE `descricao`
    END
WHERE `id` BETWEEN 1 AND 10;

-- 5.3. Adicionar cores aos times
SELECT 'Adicionando cores aos times...' as STATUS;

UPDATE `times` SET 
    `cor` = CASE 
        WHEN `nome_time` = 'Desenvolvimento Backend' THEN '#007bff'
        WHEN `nome_time` = 'Desenvolvimento Frontend' THEN '#28a745'
        WHEN `nome_time` = 'Quality Assurance' THEN '#ffc107'
        WHEN `nome_time` = 'Recursos Humanos' THEN '#dc3545'
        WHEN `nome_time` = 'DevOps' THEN '#6f42c1'
        ELSE `cor`
    END
WHERE `cor` IS NULL;

-- ==================================================
-- 6. ADICIONAR ÍNDICES PARA MELHOR PERFORMANCE
-- ==================================================

SELECT 'Adicionando índices para campos novos...' as STATUS;

-- Índices para campos de data_ultima_atualizacao
ALTER TABLE `usuarios` ADD INDEX `idx_usuarios_data_ultima_atualizacao` (`data_ultima_atualizacao`);
ALTER TABLE `times` ADD INDEX `idx_times_data_ultima_atualizacao` (`data_ultima_atualizacao`);

-- Índices para campos de tipo_membro
ALTER TABLE `membros_time` ADD INDEX `idx_membros_time_tipo` (`tipo_membro`);

-- Índices para campos de check_humor
ALTER TABLE `check_humor` ADD INDEX `idx_check_humor_bem_estar` (`bem_estar_pontos`);
ALTER TABLE `check_humor` ADD INDEX `idx_check_humor_confirmado` (`confirmado`);

-- Índices para rooms
ALTER TABLE `rooms` ADD INDEX `idx_rooms_created_at` (`created_at`);

-- ==================================================
-- 7. VALIDAÇÕES FINAIS
-- ==================================================

SELECT 'Executando validações finais...' as STATUS;

-- Verificar integridade referencial
SELECT 
    'times' as tabela,
    COUNT(*) as total,
    COUNT(criado_por_id) as com_criado_por,
    COUNT(*) - COUNT(criado_por_id) as sem_criado_por
FROM times;

-- Verificar rooms
SELECT 
    'rooms' as tabela,
    COUNT(*) as total_rooms,
    COUNT(DISTINCT host_id) as hosts_distintos
FROM rooms;

-- Verificar check_humor
SELECT 
    'check_humor' as tabela,
    COUNT(*) as total,
    COUNT(bem_estar_pontos) as com_pontos,
    COUNT(confirmado) as com_confirmado
FROM check_humor;

-- Verificar membros_time
SELECT 
    'membros_time' as tabela,
    COUNT(*) as total,
    COUNT(CASE WHEN tipo_membro = 'LIDER' THEN 1 END) as lideres,
    COUNT(CASE WHEN tipo_membro = 'MEMBRO' THEN 1 END) as membros
FROM membros_time;

-- ==================================================
-- 8. COMMIT OU ROLLBACK
-- ==================================================

-- Se chegou até aqui, commit das alterações
SELECT 'Migração concluída com sucesso!' as STATUS;
COMMIT;

-- Restaurar configurações
SET FOREIGN_KEY_CHECKS = 1;
SET UNIQUE_CHECKS = 1;

-- ==================================================
-- 9. VERIFICAÇÃO PÓS-MIGRAÇÃO
-- ==================================================

SELECT 'Verificação pós-migração:' as STATUS;

-- Contar registros nas tabelas principais
SELECT 'usuarios' as tabela, COUNT(*) as registros FROM usuarios
UNION ALL
SELECT 'times', COUNT(*) FROM times
UNION ALL
SELECT 'membros_time', COUNT(*) FROM membros_time
UNION ALL
SELECT 'check_humor', COUNT(*) FROM check_humor
UNION ALL
SELECT 'rooms', COUNT(*) FROM rooms
ORDER BY tabela;

-- Verificar se todos os campos foram adicionados
SHOW COLUMNS FROM usuarios;
SHOW COLUMNS FROM times;
SHOW COLUMNS FROM membros_time;
SHOW COLUMNS FROM check_humor;
SHOW COLUMNS FROM rooms;

-- ==================================================
-- SCRIPT DE ROLLBACK (COMENTADO)
-- ==================================================

/*
-- Para fazer rollback da migração, execute:

-- Remover tabela rooms
-- DROP TABLE `rooms`;

-- Remover campos adicionados
-- ALTER TABLE `usuarios` DROP COLUMN `descricao`, DROP COLUMN `imagem`, DROP COLUMN `data_ultima_atualizacao`;
-- ALTER TABLE `times` DROP FOREIGN KEY `fk_times_criado_por`, DROP COLUMN `criado_por_id`, DROP COLUMN `data_ultima_atualizacao`;
-- ALTER TABLE `membros_time` DROP COLUMN `tipo_membro`, DROP COLUMN `observacoes`;
-- ALTER TABLE `check_humor` DROP COLUMN `bem_estar_pontos`, DROP COLUMN `confirmado`;
-- ALTER TABLE `kanban_categorias` DROP FOREIGN KEY `fk_kanban_categorias_criado_por`, DROP COLUMN `criado_por_id`;

-- Remover índices
-- ALTER TABLE `usuarios` DROP INDEX `idx_usuarios_data_ultima_atualizacao`;
-- ALTER TABLE `times` DROP INDEX `idx_times_data_ultima_atualizacao`;
-- ALTER TABLE `membros_time` DROP INDEX `idx_membros_time_tipo`;
-- ALTER TABLE `check_humor` DROP INDEX `idx_check_humor_bem_estar`, DROP INDEX `idx_check_humor_confirmado`;
*/

-- Restaurar configurações MySQL
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- ==================================================
-- FIM DO SCRIPT DE MIGRAÇÃO
-- ================================================== 