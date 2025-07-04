-- SCRIPT DE MIGRAÇÃO - CORREÇÕES PRINCIPAIS
-- Incorpora melhorias do dump mantendo consistência do script original

-- 1. ADICIONAR CAMPOS EXTRAS - USUARIOS
ALTER TABLE `usuarios` 
ADD COLUMN `descricao` varchar(255) DEFAULT NULL,
ADD COLUMN `imagem` varchar(350) DEFAULT NULL,
ADD COLUMN `data_ultima_atualizacao` datetime DEFAULT NULL;

-- 2. ADICIONAR CAMPOS EXTRAS - TIMES
ALTER TABLE `times`
ADD COLUMN `data_ultima_atualizacao` datetime DEFAULT NULL,
ADD COLUMN `criado_por_id` int DEFAULT 1;

UPDATE `times` SET `criado_por_id` = 1 WHERE `criado_por_id` IS NULL;

ALTER TABLE `times`
MODIFY COLUMN `criado_por_id` int NOT NULL;

ALTER TABLE `times` 
ADD CONSTRAINT `fk_times_criado_por` 
FOREIGN KEY (`criado_por_id`) REFERENCES `usuarios` (`id`) ON DELETE RESTRICT;

-- 3. ADICIONAR CAMPOS EXTRAS - MEMBROS_TIME
ALTER TABLE `membros_time`
ADD COLUMN `tipo_membro` enum('LIDER','VICE_LIDER','MEMBRO','COLABORADOR') NOT NULL DEFAULT 'MEMBRO',
ADD COLUMN `observacoes` varchar(255) DEFAULT NULL;

-- 4. ADICIONAR CAMPOS EXTRAS - CHECK_HUMOR
ALTER TABLE `check_humor`
ADD COLUMN `bem_estar_pontos` int DEFAULT NULL,
ADD COLUMN `confirmado` boolean DEFAULT NULL;

-- 5. CRIAR TABELA ROOMS
CREATE TABLE `rooms` (
  `room_id` varchar(20) NOT NULL,
  `room_name` text NOT NULL,
  `host_id` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`room_id`),
  KEY `idx_rooms_host` (`host_id`),
  CONSTRAINT `fk_rooms_host` FOREIGN KEY (`host_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 6. DADOS DE EXEMPLO
INSERT INTO `rooms` VALUES
('room_demo_001', 'Sala de Reunião Daily', 1, NOW()),
('room_demo_002', 'Retrospectiva Sprint', 2, NOW()),
('room_demo_003', 'Planejamento Trimestral', 1, NOW());

-- 7. MIGRAÇÃO ENUM CHECK_HUMOR (se necessário)
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