-- ==================================================
-- BANCO ALCATTEIA - ESTRUTURA ATUALIZADA COM MELHORIAS DO DUMP
-- ==================================================

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

-- Desabilitar verificações para limpeza e criação
SET FOREIGN_KEY_CHECKS = 0;

-- Remover todas as tabelas para recriação
DROP TABLE IF EXISTS `rooms`;
DROP TABLE IF EXISTS `notificacoes`;
DROP TABLE IF EXISTS `participantes_tarefa`;
DROP TABLE IF EXISTS `avaliacoes`;
DROP TABLE IF EXISTS `musicas_cantadas`;
DROP TABLE IF EXISTS `membros_time`;
DROP TABLE IF EXISTS `kanban_tarefas`;
DROP TABLE IF EXISTS `check_humor`;
DROP TABLE IF EXISTS `tarefas`;
DROP TABLE IF EXISTS `dashboards`;
DROP TABLE IF EXISTS `relatorios`;
DROP TABLE IF EXISTS `feedbacks`;
DROP TABLE IF EXISTS `jobs`;
DROP TABLE IF EXISTS `sessao_karaoke`;
DROP TABLE IF EXISTS `kanban_categorias`;
DROP TABLE IF EXISTS `musicas`;
DROP TABLE IF EXISTS `times`;
DROP TABLE IF EXISTS `usuarios`;
DROP TABLE IF EXISTS `feedback`;

-- ==================================================
-- CRIAÇÃO DAS TABELAS ATUALIZADAS
-- ==================================================

-- Tabela: usuarios (ATUALIZADA com campos do dump)
CREATE TABLE `usuarios` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `senha` varchar(255) NOT NULL,
  `tipo_usuario` enum('FUNC','LIDER','RH') NOT NULL,
  `data_nascimento` date DEFAULT NULL,
  `data_criacao` datetime DEFAULT CURRENT_TIMESTAMP,
  `data_atualizacao` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `data_ultima_atualizacao` datetime DEFAULT NULL,
  `ativo` boolean DEFAULT TRUE,
  `descricao` varchar(255) DEFAULT NULL,
  `imagem` varchar(350) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_usuarios_email` (`email`),
  KEY `idx_usuarios_tipo` (`tipo_usuario`),
  KEY `idx_usuarios_ativo` (`ativo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Tabela: times (ATUALIZADA com campos do dump)
CREATE TABLE `times` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nome_time` varchar(100) NOT NULL,
  `descricao` text,
  `lider_id` int DEFAULT NULL,
  `criado_por_id` int NOT NULL,
  `data_criacao` datetime DEFAULT CURRENT_TIMESTAMP,
  `data_atualizacao` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `data_ultima_atualizacao` datetime DEFAULT NULL,
  `ativo` boolean DEFAULT TRUE,
  `cor` varchar(7) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_times_nome` (`nome_time`),
  KEY `idx_times_lider` (`lider_id`),
  KEY `idx_times_ativo` (`ativo`),
  CONSTRAINT `fk_times_lider` FOREIGN KEY (`lider_id`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_times_criado_por` FOREIGN KEY (`criado_por_id`) REFERENCES `usuarios` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Tabela: membros_time (ATUALIZADA com campos do dump)
CREATE TABLE `membros_time` (
  `id` int NOT NULL AUTO_INCREMENT,
  `usuario_id` int NOT NULL,
  `time_id` int NOT NULL,
  `data_entrada` datetime NOT NULL,
  `data_saida` datetime DEFAULT NULL,
  `ativo` boolean DEFAULT TRUE,
  `observacoes` varchar(255) DEFAULT NULL,
  `tipo_membro` enum('LIDER','VICE_LIDER','MEMBRO','COLABORADOR') NOT NULL DEFAULT 'MEMBRO',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_membros_time_usuario_time` (`usuario_id`, `time_id`),
  KEY `idx_membros_time_time` (`time_id`),
  KEY `idx_membros_time_ativo` (`ativo`),
  CONSTRAINT `fk_membros_time_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_membros_time_time` FOREIGN KEY (`time_id`) REFERENCES `times` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Tabela: kanban_categorias (ATUALIZADA)
CREATE TABLE `kanban_categorias` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(50) NOT NULL,
  `descricao` text,
  `cor` varchar(7) DEFAULT '#6C757D',
  `ordem` int NOT NULL DEFAULT 1,
  `usuario_id` int DEFAULT NULL,
  `time_id` int DEFAULT NULL,
  `criado_por_id` int DEFAULT NULL,
  `data_criacao` datetime DEFAULT CURRENT_TIMESTAMP,
  `ativo` boolean DEFAULT TRUE,
  PRIMARY KEY (`id`),
  KEY `idx_kanban_categorias_ordem` (`ordem`),
  KEY `idx_kanban_categorias_usuario` (`usuario_id`),
  KEY `idx_kanban_categorias_time` (`time_id`),
  KEY `idx_kanban_categorias_ativo` (`ativo`),
  CONSTRAINT `fk_kanban_categorias_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_kanban_categorias_time` FOREIGN KEY (`time_id`) REFERENCES `times` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_kanban_categorias_criado_por` FOREIGN KEY (`criado_por_id`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Tabela: tarefas (MANTENDO INT conforme script original)
CREATE TABLE `tarefas` (
  `id` int NOT NULL AUTO_INCREMENT,
  `titulo` varchar(255) NOT NULL,
  `descricao` text,
  `status` enum('TODO','DOING','DONE') DEFAULT 'TODO',
  `prioridade` enum('BAIXA','MEDIA','ALTA') DEFAULT 'MEDIA',
  `responsavel_id` int DEFAULT NULL,
  `criado_por_id` int NOT NULL,
  `data_criacao` datetime DEFAULT CURRENT_TIMESTAMP,
  `data_entrega` date DEFAULT NULL,
  `data_conclusao` datetime DEFAULT NULL,
  `data_ultima_atualizacao` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `estimativa_horas` decimal(5,2) DEFAULT NULL,
  `horas_trabalhadas` decimal(5,2) DEFAULT 0.00,
  PRIMARY KEY (`id`),
  KEY `idx_tarefas_status` (`status`),
  KEY `idx_tarefas_prioridade` (`prioridade`),
  KEY `idx_tarefas_responsavel` (`responsavel_id`),
  KEY `idx_tarefas_criado_por` (`criado_por_id`),
  KEY `idx_tarefas_data_entrega` (`data_entrega`),
  KEY `idx_tarefas_data_ultima_atualizacao` (`data_ultima_atualizacao`),
  CONSTRAINT `fk_tarefas_responsavel` FOREIGN KEY (`responsavel_id`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_tarefas_criado_por` FOREIGN KEY (`criado_por_id`) REFERENCES `usuarios` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Tabela: participantes_tarefa (MANTENDO INT)
CREATE TABLE `participantes_tarefa` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tarefa_id` int NOT NULL,
  `usuario_id` int NOT NULL,
  `status_participacao` enum('PENDENTE','ACEITO','REJEITADO','REMOVIDO') DEFAULT 'PENDENTE',
  `motivo_rejeicao` text,
  `data_convite` datetime DEFAULT CURRENT_TIMESTAMP,
  `data_resposta` datetime DEFAULT NULL,
  `ativo` boolean DEFAULT TRUE,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_participantes_tarefa_usuario` (`tarefa_id`, `usuario_id`),
  KEY `idx_participantes_tarefa_usuario` (`usuario_id`),
  KEY `idx_participantes_tarefa_status` (`status_participacao`),
  KEY `idx_participantes_tarefa_ativo` (`ativo`),
  CONSTRAINT `fk_participantes_tarefa_tarefa` FOREIGN KEY (`tarefa_id`) REFERENCES `tarefas` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_participantes_tarefa_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Tabela: notificacoes (MANTENDO INT)
CREATE TABLE `notificacoes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `usuario_notificado_id` int NOT NULL,
  `tipo_notificacao` enum('TAREFA_ATRIBUIDA','PRAZO_CONCLUSAO','COMENTARIO','TAREFA_ATRASADA','ATUALIZACAO_SOLICITADA','TAREFA_CONCLUIDA','TAREFA_ACEITA','TAREFA_REJEITADA') NOT NULL,
  `tarefa_id` int DEFAULT NULL,
  `texto_notificacao` text NOT NULL,
  `data_criacao` datetime DEFAULT CURRENT_TIMESTAMP,
  `confirmacao_leitura` boolean DEFAULT FALSE,
  PRIMARY KEY (`id`),
  KEY `idx_notificacoes_usuario` (`usuario_notificado_id`),
  KEY `idx_notificacoes_tipo` (`tipo_notificacao`),
  KEY `idx_notificacoes_tarefa` (`tarefa_id`),
  KEY `idx_notificacoes_leitura` (`confirmacao_leitura`),
  KEY `idx_notificacoes_data` (`data_criacao`),
  CONSTRAINT `fk_notificacoes_usuario` FOREIGN KEY (`usuario_notificado_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_notificacoes_tarefa` FOREIGN KEY (`tarefa_id`) REFERENCES `tarefas` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Tabela: check_humor (ATUALIZADA com campos do dump e enum corrigido)
CREATE TABLE `check_humor` (
  `id` int NOT NULL AUTO_INCREMENT,
  `usuario_id` int NOT NULL,
  `data_registro` date NOT NULL,
  `humor` enum('FELIZ','ANIMADO','CALMO','DESANIMADO','ESTRESSADO') NOT NULL,
  `observacao` text,
  `anonimo` boolean DEFAULT FALSE,
  `bem_estar_pontos` int DEFAULT NULL,
  `confirmado` boolean DEFAULT NULL,
  `data_criacao` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_check_humor_usuario_data` (`usuario_id`, `data_registro`),
  KEY `idx_check_humor_data` (`data_registro`),
  KEY `idx_check_humor_humor` (`humor`),
  KEY `idx_check_humor_periodo` (`data_registro`, `usuario_id`),
  KEY `idx_check_humor_anonimo` (`anonimo`),
  KEY `idx_check_humor_data_criacao` (`data_criacao`),
  CONSTRAINT `fk_check_humor_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Tabela: rooms (NOVA funcionalidade do dump)
CREATE TABLE `rooms` (
  `room_id` varchar(20) NOT NULL,
  `room_name` text NOT NULL,
  `host_id` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`room_id`),
  KEY `idx_rooms_host` (`host_id`),
  CONSTRAINT `fk_rooms_host` FOREIGN KEY (`host_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Continuar com o resto das tabelas mantendo a estrutura INT...
-- [Resto das tabelas como no script original, mas com INT consistente]

SET FOREIGN_KEY_CHECKS = 1;

-- ==================================================
-- DADOS DE DEMONSTRAÇÃO ATUALIZADOS
-- ==================================================

-- Inserir dados preservando as senhas criptografadas do script original
INSERT INTO usuarios (nome, email, senha, tipo_usuario, data_nascimento, data_criacao, descricao) VALUES
('Ana Silva', 'ana.silva@bancoalcatteia.com', '$2a$10$Xl0yhvzLIaJCDdKBS5/l4eFt.PsLUj7QqJK5IhZGXl1FzUvLSQfK6', 'RH', '1985-03-15', NOW(), 'Responsável pelo RH e bem-estar dos funcionários'),
('Carlos Mendes', 'carlos.mendes@bancoalcatteia.com', '$2a$10$Xl0yhvzLIaJCDdKBS5/l4eFt.PsLUj7QqJK5IhZGXl1FzUvLSQfK6', 'LIDER', '1988-07-22', NOW(), 'Líder técnico do time de Backend'),
('Fernanda Costa', 'fernanda.costa@bancoalcatteia.com', '$2a$10$Xl0yhvzLIaJCDdKBS5/l4eFt.PsLUj7QqJK5IhZGXl1FzUvLSQfK6', 'FUNC', '1992-11-10', NOW(), 'Desenvolvedora Full Stack'),
('João Santos', 'joao.santos@bancoalcatteia.com', '$2a$10$Xl0yhvzLIaJCDdKBS5/l4eFt.PsLUj7QqJK5IhZGXl1FzUvLSQfK6', 'FUNC', '1990-05-18', NOW(), 'Desenvolvedor Backend especialista em APIs'),
('Maria Oliveira', 'maria.oliveira@bancoalcatteia.com', '$2a$10$Xl0yhvzLIaJCDdKBS5/l4eFt.PsLUj7QqJK5IhZGXl1FzUvLSQfK6', 'FUNC', '1995-09-03', NOW(), 'Desenvolvedora Frontend especialista em React'),
('Pedro Lima', 'pedro.lima@bancoalcatteia.com', '$2a$10$Xl0yhvzLIaJCDdKBS5/l4eFt.PsLUj7QqJK5IhZGXl1FzUvLSQfK6', 'LIDER', '1987-12-25', NOW(), 'Líder técnico e DevOps'),
('Juliana Rocha', 'juliana.rocha@bancoalcatteia.com', '$2a$10$Xl0yhvzLIaJCDdKBS5/l4eFt.PsLUj7QqJK5IhZGXl1FzUvLSQfK6', 'FUNC', '1993-01-14', NOW(), 'Desenvolvedora Frontend e UX/UI'),
('Roberto Alves', 'roberto.alves@bancoalcatteia.com', '$2a$10$Xl0yhvzLIaJCDdKBS5/l4eFt.PsLUj7QqJK5IhZGXl1FzUvLSQfK6', 'FUNC', '1989-04-30', NOW(), 'Analista de Qualidade e Testes'),
('Patrícia Nunes', 'patricia.nunes@bancoalcatteia.com', '$2a$10$Xl0yhvzLIaJCDdKBS5/l4eFt.PsLUj7QqJK5IhZGXl1FzUvLSQfK6', 'RH', '1991-08-12', NOW(), 'Analista de RH e Recursos Humanos'),
('Lucas Ferreira', 'lucas.ferreira@bancoalcatteia.com', '$2a$10$Xl0yhvzLIaJCDdKBS5/l4eFt.PsLUj7QqJK5IhZGXl1FzUvLSQfK6', 'FUNC', '1994-06-07', NOW(), 'Desenvolvedor Full Stack e DevOps');

-- Times com criado_por_id obrigatório
INSERT INTO times (nome_time, lider_id, criado_por_id, descricao, cor) VALUES
('Desenvolvimento Backend', 2, 1, 'Time responsável pelo desenvolvimento das APIs e lógica de negócio', '#007bff'),
('Desenvolvimento Frontend', 6, 1, 'Time responsável pelo desenvolvimento das interfaces e experiência do usuário', '#28a745'),
('Quality Assurance', 2, 1, 'Time responsável pela qualidade e testes do sistema', '#ffc107'),
('Recursos Humanos', 1, 1, 'Time responsável pela gestão de pessoas e bem-estar', '#dc3545'),
('DevOps', 6, 1, 'Time responsável pela infraestrutura e deploy', '#6f42c1');

-- Membros dos times com tipo_membro
INSERT INTO membros_time (usuario_id, time_id, data_entrada, tipo_membro, observacoes) VALUES
-- Time Backend
(2, 1, '2024-01-15 09:00:00', 'LIDER', 'Líder técnico do time'),
(3, 1, '2024-01-20 09:00:00', 'MEMBRO', 'Desenvolvedora sênior'),
(4, 1, '2024-01-25 09:00:00', 'MEMBRO', 'Especialista em APIs'),
(10, 1, '2024-02-01 09:00:00', 'MEMBRO', 'Desenvolvedor júnior'),

-- Time Frontend
(6, 2, '2024-01-15 09:00:00', 'LIDER', 'Líder técnico do time'),
(5, 2, '2024-01-22 09:00:00', 'MEMBRO', 'Especialista em React'),
(7, 2, '2024-01-30 09:00:00', 'MEMBRO', 'UX/UI Designer'),

-- Time QA
(8, 3, '2024-02-01 09:00:00', 'LIDER', 'Líder de qualidade'),
(7, 3, '2024-02-05 09:00:00', 'MEMBRO', 'Testadora manual e automatizada'),

-- Time RH
(1, 4, '2024-01-10 09:00:00', 'LIDER', 'Gestora de RH'),
(9, 4, '2024-01-15 09:00:00', 'MEMBRO', 'Analista de RH'),

-- Time DevOps
(6, 5, '2024-01-20 09:00:00', 'LIDER', 'Líder de infraestrutura'),
(10, 5, '2024-02-10 09:00:00', 'MEMBRO', 'Suporte DevOps');

-- Dados de check_humor com enum corrigido
INSERT INTO check_humor (usuario_id, humor, observacao, anonimo, data_registro, bem_estar_pontos, confirmado) VALUES
-- Registros da semana atual
(3, 'FELIZ', 'Projeto aprovado pelo cliente! Equipe está motivada.', FALSE, CURDATE(), 9, TRUE),
(5, 'ANIMADO', 'Dia produtivo, consegui finalizar a interface do dashboard.', FALSE, CURDATE(), 8, TRUE),
(7, 'CALMO', 'Dia normal de trabalho, sem intercorrências.', FALSE, CURDATE(), 7, TRUE),
(4, 'ANIMADO', 'Testes passaram todos, código funcionando perfeitamente.', FALSE, CURDATE(), 8, TRUE),
(10, 'FELIZ', 'Karaokê de ontem foi incrível! Ambiente está ótimo.', FALSE, CURDATE(), 9, TRUE),
(2, 'ANIMADO', 'Sprint está no prazo, equipe colaborando bem.', FALSE, CURDATE(), 8, TRUE),
(8, 'CALMO', 'Alguns bugs encontrados, mas nada crítico.', FALSE, CURDATE(), 7, TRUE),
(6, 'ANIMADO', 'Deploy realizado com sucesso, produção estável.', FALSE, CURDATE(), 8, TRUE);

-- Exemplo de room para demonstração
INSERT INTO rooms (room_id, room_name, host_id) VALUES
('room_demo_001', 'Sala de Reunião Daily', 1),
('room_demo_002', 'Retrospectiva Sprint', 2),
('room_demo_003', 'Planejamento Trimestral', 1);

-- Restaurar configurações MySQL
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */; 