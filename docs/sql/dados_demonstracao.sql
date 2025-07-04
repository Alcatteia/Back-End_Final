-- ==================================================
-- BANCO ALCATTEIA - ESTRUTURA E DADOS DE DEMONSTRAÇÃO
-- ==================================================
-- MySQL dump 10.13  Distrib 8.0.27, for Win64 (x86_64)
--
-- Host: localhost    Database: banco_alcatteia
-- ------------------------------------------------------
-- Server version	8.0.27

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

-- ==================================================
-- ESTRUTURA DAS TABELAS
-- ==================================================

-- ==================================================  
-- SCRIPT DE MIGRAÇÃO - CHECK HUMOR v3.0
-- ==================================================

-- Verificar se a tabela check_humor já existe
SET @table_exists = (SELECT COUNT(*) FROM information_schema.tables 
                     WHERE table_schema = DATABASE() AND table_name = 'check_humor');

-- Se a tabela existe, fazer migração dos dados antigos
SET @sql_update1 = IF(@table_exists > 0, 
    "UPDATE check_humor SET humor = 'FELIZ' WHERE humor IN ('Ótimo', 'OTIMO')",
    "SELECT 'Migração não necessária - tabela não existe' as status");

SET @sql_update2 = IF(@table_exists > 0, 
    "UPDATE check_humor SET humor = 'ANIMADO' WHERE humor IN ('Bom', 'BOM')",
    "SELECT 'Migração não necessária - tabela não existe' as status");

SET @sql_update3 = IF(@table_exists > 0, 
    "UPDATE check_humor SET humor = 'CALMO' WHERE humor IN ('Neutro', 'NEUTRO')",
    "SELECT 'Migração não necessária - tabela não existe' as status");

SET @sql_update4 = IF(@table_exists > 0, 
    "UPDATE check_humor SET humor = 'DESANIMADO' WHERE humor IN ('Ruim', 'RUIM')",
    "SELECT 'Migração não necessária - tabela não existe' as status");

SET @sql_update5 = IF(@table_exists > 0, 
    "UPDATE check_humor SET humor = 'ESTRESSADO' WHERE humor IN ('Péssimo', 'PESSIMO')",
    "SELECT 'Migração não necessária - tabela não existe' as status");

-- Executar migrações
PREPARE stmt FROM @sql_update1; EXECUTE stmt; DEALLOCATE PREPARE stmt;
PREPARE stmt FROM @sql_update2; EXECUTE stmt; DEALLOCATE PREPARE stmt;
PREPARE stmt FROM @sql_update3; EXECUTE stmt; DEALLOCATE PREPARE stmt;
PREPARE stmt FROM @sql_update4; EXECUTE stmt; DEALLOCATE PREPARE stmt;
PREPARE stmt FROM @sql_update5; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Desabilitar verificações para limpeza e criação
SET FOREIGN_KEY_CHECKS = 0;

-- Remover tabelas na ordem correta (dependências primeiro)
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
DROP TABLE IF EXISTS `sessao_karaoke`;
DROP TABLE IF EXISTS `kanban_categorias`;
DROP TABLE IF EXISTS `musicas`;
DROP TABLE IF EXISTS `times`;
DROP TABLE IF EXISTS `usuarios`;

-- Remover tabelas duplicadas/desnecessárias se existirem
DROP TABLE IF EXISTS `avaliacaos`;
DROP TABLE IF EXISTS `checkhumors`;
DROP TABLE IF EXISTS `kanbancategorias`;
DROP TABLE IF EXISTS `kanbantarefas`;
DROP TABLE IF EXISTS `membrotimes`;
DROP TABLE IF EXISTS `musicacantadas`;
DROP TABLE IF EXISTS `sessaokaraokes`;
DROP TABLE IF EXISTS `feedback`;

-- ==================================================
-- CRIAÇÃO DAS TABELAS
-- ==================================================

-- Tabela: usuarios
CREATE TABLE `usuarios` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `senha` varchar(255) NOT NULL,
  `tipo_usuario` enum('FUNC','LIDER','RH') NOT NULL,
  `data_nascimento` date NOT NULL,
  `data_criacao` datetime DEFAULT CURRENT_TIMESTAMP,
  `data_atualizacao` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `ativo` boolean DEFAULT TRUE,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_usuarios_email` (`email`),
  KEY `idx_usuarios_tipo` (`tipo_usuario`),
  KEY `idx_usuarios_ativo` (`ativo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Tabela: times
CREATE TABLE `times` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nome_time` varchar(100) NOT NULL,
  `descricao` text,
  `lider_id` int DEFAULT NULL,
  `criado_por_id` int DEFAULT NULL,
  `data_criacao` datetime DEFAULT CURRENT_TIMESTAMP,
  `data_atualizacao` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `ativo` boolean DEFAULT TRUE,
  `cor` varchar(7) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_times_nome` (`nome_time`),
  KEY `idx_times_lider` (`lider_id`),
  KEY `idx_times_ativo` (`ativo`),
  CONSTRAINT `fk_times_lider` FOREIGN KEY (`lider_id`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_times_criado_por` FOREIGN KEY (`criado_por_id`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Tabela: membros_time
CREATE TABLE `membros_time` (
  `id` int NOT NULL AUTO_INCREMENT,
  `usuario_id` int NOT NULL,
  `time_id` int NOT NULL,
  `data_entrada` date NOT NULL,
  `data_saida` date DEFAULT NULL,
  `ativo` boolean DEFAULT TRUE,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_membros_time_usuario_time` (`usuario_id`, `time_id`),
  KEY `idx_membros_time_time` (`time_id`),
  KEY `idx_membros_time_ativo` (`ativo`),
  CONSTRAINT `fk_membros_time_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_membros_time_time` FOREIGN KEY (`time_id`) REFERENCES `times` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Tabela: kanban_categorias
CREATE TABLE `kanban_categorias` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(50) NOT NULL,
  `descricao` text,
  `cor` varchar(7) DEFAULT '#6C757D',
  `ordem` int NOT NULL DEFAULT 1,
  `usuario_id` int DEFAULT NULL,
  `time_id` int DEFAULT NULL,
  `data_criacao` datetime DEFAULT CURRENT_TIMESTAMP,
  `ativo` boolean DEFAULT TRUE,
  PRIMARY KEY (`id`),
  KEY `idx_kanban_categorias_ordem` (`ordem`),
  KEY `idx_kanban_categorias_usuario` (`usuario_id`),
  KEY `idx_kanban_categorias_time` (`time_id`),
  KEY `idx_kanban_categorias_ativo` (`ativo`),
  CONSTRAINT `fk_kanban_categorias_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_kanban_categorias_time` FOREIGN KEY (`time_id`) REFERENCES `times` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Tabela: tarefas (ATUALIZADA)
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

-- Tabela: participantes_tarefa (NOVA)
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

-- Tabela: notificacoes (NOVA)
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

-- Tabela: kanban_tarefas
CREATE TABLE `kanban_tarefas` (
  `id` int NOT NULL AUTO_INCREMENT,
  `titulo` varchar(100) NOT NULL,
  `descricao` text,
  `categoria_id` int NOT NULL,
  `responsavel_id` int DEFAULT NULL,
  `criado_por_id` int NOT NULL,
  `data_criacao` datetime DEFAULT CURRENT_TIMESTAMP,
  `data_entrega` date DEFAULT NULL,
  `data_conclusao` datetime DEFAULT NULL,
  `prioridade` enum('BAIXA','MEDIA','ALTA') DEFAULT 'MEDIA',
  `status` enum('ATIVA','PAUSADA','CONCLUIDA','CANCELADA') DEFAULT 'ATIVA',
  `estimativa_horas` decimal(5,2) DEFAULT NULL,
  `horas_trabalhadas` decimal(5,2) DEFAULT 0.00,
  `ordem` int DEFAULT 1,
  PRIMARY KEY (`id`),
  KEY `idx_kanban_tarefas_categoria` (`categoria_id`),
  KEY `idx_kanban_tarefas_responsavel` (`responsavel_id`),
  KEY `idx_kanban_tarefas_criado_por` (`criado_por_id`),
  KEY `idx_kanban_tarefas_status` (`status`),
  KEY `idx_kanban_tarefas_prioridade` (`prioridade`),
  KEY `idx_kanban_tarefas_ordem` (`ordem`),
  CONSTRAINT `fk_kanban_tarefas_categoria` FOREIGN KEY (`categoria_id`) REFERENCES `kanban_categorias` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_kanban_tarefas_responsavel` FOREIGN KEY (`responsavel_id`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_kanban_tarefas_criado_por` FOREIGN KEY (`criado_por_id`) REFERENCES `usuarios` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Tabela: musicas
CREATE TABLE `musicas` (
  `id` int NOT NULL AUTO_INCREMENT,
  `titulo` varchar(150) NOT NULL,
  `artista` varchar(100) NOT NULL,
  `genero` varchar(50) DEFAULT NULL,
  `idioma` varchar(50) DEFAULT NULL,
  `duracao` time DEFAULT NULL,
  `link_video` varchar(500) DEFAULT NULL,
  `data_cadastro` datetime DEFAULT CURRENT_TIMESTAMP,
  `ativo` boolean DEFAULT TRUE,
  PRIMARY KEY (`id`),
  KEY `idx_musicas_artista` (`artista`),
  KEY `idx_musicas_genero` (`genero`),
  KEY `idx_musicas_idioma` (`idioma`),
  KEY `idx_musicas_ativo` (`ativo`),
  FULLTEXT KEY `ft_musicas_titulo_artista` (`titulo`, `artista`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Tabela: sessao_karaoke
CREATE TABLE `sessao_karaoke` (
  `id` int NOT NULL AUTO_INCREMENT,
  `titulo` varchar(100) NOT NULL,
  `data` datetime DEFAULT CURRENT_TIMESTAMP,
  `local` varchar(100) NOT NULL,
  `descricao` text,
  `organizador_id` int NOT NULL,
  `max_participantes` int DEFAULT NULL,
  `status` enum('AGENDADA','EM_ANDAMENTO','FINALIZADA','CANCELADA') DEFAULT 'AGENDADA',
  `data_criacao` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_sessao_karaoke_organizador` (`organizador_id`),
  KEY `idx_sessao_karaoke_data` (`data`),
  KEY `idx_sessao_karaoke_status` (`status`),
  CONSTRAINT `fk_sessao_karaoke_organizador` FOREIGN KEY (`organizador_id`) REFERENCES `usuarios` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Tabela: musicas_cantadas
CREATE TABLE `musicas_cantadas` (
  `id` int NOT NULL AUTO_INCREMENT,
  `usuario_id` int NOT NULL,
  `musica_id` int NOT NULL,
  `sessao_id` int NOT NULL,
  `ordem_cantada` int NOT NULL,
  `nota` decimal(3,2) DEFAULT NULL,
  `comentarios` text,
  `data_apresentacao` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_musicas_cantadas_sessao_ordem` (`sessao_id`, `ordem_cantada`),
  KEY `idx_musicas_cantadas_usuario` (`usuario_id`),
  KEY `idx_musicas_cantadas_musica` (`musica_id`),
  KEY `idx_musicas_cantadas_nota` (`nota`),
  CONSTRAINT `fk_musicas_cantadas_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_musicas_cantadas_musica` FOREIGN KEY (`musica_id`) REFERENCES `musicas` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_musicas_cantadas_sessao` FOREIGN KEY (`sessao_id`) REFERENCES `sessao_karaoke` (`id`) ON DELETE CASCADE,
  CONSTRAINT `chk_musicas_cantadas_nota` CHECK ((`nota` >= 0 AND `nota` <= 10))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Tabela: avaliacoes
CREATE TABLE `avaliacoes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `avaliador_id` int NOT NULL,
  `apresentacao_id` int NOT NULL,
  `nota` int NOT NULL,
  `comentario` text,
  `data_avaliacao` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_avaliacoes_avaliador_apresentacao` (`avaliador_id`, `apresentacao_id`),
  KEY `idx_avaliacoes_apresentacao` (`apresentacao_id`),
  KEY `idx_avaliacoes_nota` (`nota`),
  CONSTRAINT `fk_avaliacoes_avaliador` FOREIGN KEY (`avaliador_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_avaliacoes_apresentacao` FOREIGN KEY (`apresentacao_id`) REFERENCES `musicas_cantadas` (`id`) ON DELETE CASCADE,
  CONSTRAINT `chk_avaliacoes_nota` CHECK ((`nota` >= 1 AND `nota` <= 5))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Tabela: check_humor
CREATE TABLE `check_humor` (
  `id` int NOT NULL AUTO_INCREMENT,
  `usuario_id` int NOT NULL,
  `data_registro` date NOT NULL,
  `humor` enum('FELIZ','ANIMADO','CALMO','DESANIMADO','ESTRESSADO') NOT NULL,
  `observacao` text,
  `anonimo` boolean DEFAULT FALSE,
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

-- Tabela: dashboards (ATUALIZADA)
CREATE TABLE `dashboards` (
  `id` int NOT NULL AUTO_INCREMENT,
  `usuario_id` int NOT NULL,
  `tipo_dashboard` varchar(50) NOT NULL,
  `configuracao` json DEFAULT NULL,
  `metricas` json DEFAULT NULL,
  `data_criacao` datetime DEFAULT CURRENT_TIMESTAMP,
  `data_atualizacao` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dashboards_usuario` (`usuario_id`),
  KEY `idx_dashboards_tipo` (`tipo_dashboard`),
  CONSTRAINT `fk_dashboards_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Tabela: relatorios
CREATE TABLE `relatorios` (
  `id` int NOT NULL AUTO_INCREMENT,
  `titulo` varchar(200) NOT NULL,
  `tipo` varchar(50) NOT NULL,
  `periodo_inicio` date DEFAULT NULL,
  `periodo_fim` date DEFAULT NULL,
  `gerado_por_id` int NOT NULL,
  `time_id` int DEFAULT NULL,
  `data_geracao` datetime DEFAULT CURRENT_TIMESTAMP,
  `conteudo` longtext NOT NULL,
  `formato` enum('HTML','PDF','JSON','CSV') DEFAULT 'HTML',
  `status` enum('GERANDO','CONCLUIDO','ERRO') DEFAULT 'CONCLUIDO',
  PRIMARY KEY (`id`),
  KEY `idx_relatorios_tipo` (`tipo`),
  KEY `idx_relatorios_gerado_por` (`gerado_por_id`),
  KEY `idx_relatorios_time` (`time_id`),
  KEY `idx_relatorios_data` (`data_geracao`),
  KEY `idx_relatorios_status` (`status`),
  CONSTRAINT `fk_relatorios_gerado_por` FOREIGN KEY (`gerado_por_id`) REFERENCES `usuarios` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_relatorios_time` FOREIGN KEY (`time_id`) REFERENCES `times` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Tabela: feedbacks (NOVA)
CREATE TABLE `feedbacks` (
  `id` int NOT NULL AUTO_INCREMENT,
  `usuario_id` int NOT NULL,
  `tipo` enum('SUGESTAO','RECLAMACAO','ELOGIO','BUG_REPORT','MELHORIA','DUVIDA') NOT NULL,
  `titulo` varchar(200) NOT NULL,
  `conteudo` text NOT NULL,
  `status` enum('PENDENTE','EM_ANALISE','RESPONDIDO','FECHADO','REJEITADO') DEFAULT 'PENDENTE',
  `prioridade` enum('BAIXA','MEDIA','ALTA','CRITICA') DEFAULT 'MEDIA',
  `anonimo` boolean DEFAULT FALSE,
  `data_criacao` datetime DEFAULT CURRENT_TIMESTAMP,
  `data_resposta` datetime DEFAULT NULL,
  `respondido_por_id` int DEFAULT NULL,
  `resposta` text,
  `ativo` boolean DEFAULT TRUE,
  PRIMARY KEY (`id`),
  KEY `idx_feedbacks_usuario` (`usuario_id`),
  KEY `idx_feedbacks_tipo` (`tipo`),
  KEY `idx_feedbacks_status` (`status`),
  KEY `idx_feedbacks_prioridade` (`prioridade`),
  KEY `idx_feedbacks_data_criacao` (`data_criacao`),
  KEY `idx_feedbacks_ativo` (`ativo`),
  KEY `idx_feedbacks_respondido_por` (`respondido_por_id`),
  CONSTRAINT `fk_feedbacks_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_feedbacks_respondido_por` FOREIGN KEY (`respondido_por_id`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Tabela: jobs (NOVA)
CREATE TABLE `jobs` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) NOT NULL,
  `descricao` text,
  `tipo` enum('NOTIFICACAO_PRAZO','BACKUP_DADOS','LIMPEZA_LOGS','RELATORIO_AUTOMATICO','ENVIO_EMAIL','SINCRONIZACAO','MANUTENCAO') NOT NULL,
  `status` enum('AGENDADO','EXECUTANDO','CONCLUIDO','ERRO','CANCELADO','PAUSADO') DEFAULT 'AGENDADO',
  `data_agendamento` datetime NOT NULL,
  `data_execucao` datetime DEFAULT NULL,
  `data_conclusao` datetime DEFAULT NULL,
  `data_criacao` datetime DEFAULT CURRENT_TIMESTAMP,
  `criado_por_id` int NOT NULL,
  `cron_expression` varchar(255) DEFAULT NULL,
  `parametros` json DEFAULT NULL,
  `resultado` text,
  `erro` text,
  `ativo` boolean DEFAULT TRUE,
  `tentativas` int DEFAULT 0,
  `max_tentativas` int DEFAULT 3,
  PRIMARY KEY (`id`),
  KEY `idx_jobs_tipo` (`tipo`),
  KEY `idx_jobs_status` (`status`),
  KEY `idx_jobs_data_agendamento` (`data_agendamento`),
  KEY `idx_jobs_data_execucao` (`data_execucao`),
  KEY `idx_jobs_criado_por` (`criado_por_id`),
  KEY `idx_jobs_ativo` (`ativo`),
  CONSTRAINT `fk_jobs_criado_por` FOREIGN KEY (`criado_por_id`) REFERENCES `usuarios` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

SET FOREIGN_KEY_CHECKS = 1;

-- ==================================================
-- 1. USUÁRIOS
-- ==================================================
-- IMPORTANTE: Senhas foram criptografadas com BCrypt
-- Senha padrão para todos: "123456"
-- Hash BCrypt: $2a$10$Xl0yhvzLIaJCDdKBS5/l4eFt.PsLUj7QqJK5IhZGXl1FzUvLSQfK6
INSERT INTO usuarios (nome, email, senha, tipo_usuario, data_nascimento, data_criacao) VALUES
('Ana Silva', 'ana.silva@bancoalcatteia.com', '$2a$10$Xl0yhvzLIaJCDdKBS5/l4eFt.PsLUj7QqJK5IhZGXl1FzUvLSQfK6', 'RH', '1985-03-15', NOW()),
('Carlos Mendes', 'carlos.mendes@bancoalcatteia.com', '$2a$10$Xl0yhvzLIaJCDdKBS5/l4eFt.PsLUj7QqJK5IhZGXl1FzUvLSQfK6', 'LIDER', '1988-07-22', NOW()),
('Fernanda Costa', 'fernanda.costa@bancoalcatteia.com', '$2a$10$Xl0yhvzLIaJCDdKBS5/l4eFt.PsLUj7QqJK5IhZGXl1FzUvLSQfK6', 'FUNC', '1992-11-10', NOW()),
('João Santos', 'joao.santos@bancoalcatteia.com', '$2a$10$Xl0yhvzLIaJCDdKBS5/l4eFt.PsLUj7QqJK5IhZGXl1FzUvLSQfK6', 'FUNC', '1990-05-18', NOW()),
('Maria Oliveira', 'maria.oliveira@bancoalcatteia.com', '$2a$10$Xl0yhvzLIaJCDdKBS5/l4eFt.PsLUj7QqJK5IhZGXl1FzUvLSQfK6', 'FUNC', '1995-09-03', NOW()),
('Pedro Lima', 'pedro.lima@bancoalcatteia.com', '$2a$10$Xl0yhvzLIaJCDdKBS5/l4eFt.PsLUj7QqJK5IhZGXl1FzUvLSQfK6', 'LIDER', '1987-12-25', NOW()),
('Juliana Rocha', 'juliana.rocha@bancoalcatteia.com', '$2a$10$Xl0yhvzLIaJCDdKBS5/l4eFt.PsLUj7QqJK5IhZGXl1FzUvLSQfK6', 'FUNC', '1993-01-14', NOW()),
('Roberto Alves', 'roberto.alves@bancoalcatteia.com', '$2a$10$Xl0yhvzLIaJCDdKBS5/l4eFt.PsLUj7QqJK5IhZGXl1FzUvLSQfK6', 'FUNC', '1989-04-30', NOW()),
('Patrícia Nunes', 'patricia.nunes@bancoalcatteia.com', '$2a$10$Xl0yhvzLIaJCDdKBS5/l4eFt.PsLUj7QqJK5IhZGXl1FzUvLSQfK6', 'RH', '1991-08-12', NOW()),
('Lucas Ferreira', 'lucas.ferreira@bancoalcatteia.com', '$2a$10$Xl0yhvzLIaJCDdKBS5/l4eFt.PsLUj7QqJK5IhZGXl1FzUvLSQfK6', 'FUNC', '1994-06-07', NOW());

-- ==================================================
-- 2. TIMES
-- ==================================================
INSERT INTO times (nome_time, lider_id, criado_por_id) VALUES
('Desenvolvimento Backend', 2, 1),
('Desenvolvimento Frontend', 6, 1),
('Quality Assurance', 2, 1),
('Recursos Humanos', 1, 1),
('DevOps', 6, 1);

-- ==================================================
-- 3. MEMBROS DOS TIMES
-- ==================================================
INSERT INTO membros_time (usuario_id, time_id, data_entrada) VALUES
-- Time Desenvolvimento Backend
(2, 1, '2024-01-15'), -- Carlos (Líder)
(3, 1, '2024-01-20'), -- Fernanda
(4, 1, '2024-01-25'), -- João
(10, 1, '2024-02-01'), -- Lucas

-- Time Desenvolvimento Frontend  
(6, 2, '2024-01-15'), -- Pedro (Líder)
(5, 2, '2024-01-22'), -- Maria
(7, 2, '2024-01-30'), -- Juliana

-- Time QA
(8, 3, '2024-02-01'), -- Roberto
(7, 3, '2024-02-05'), -- Juliana (também em QA)

-- Time Recursos Humanos
(1, 4, '2024-01-10'), -- Ana (Líder)
(9, 4, '2024-01-15'), -- Patrícia

-- Time DevOps
(6, 5, '2024-01-20'), -- Pedro (também líder DevOps)
(10, 5, '2024-02-10'); -- Lucas (também em DevOps)

-- ==================================================
-- 4. CATEGORIAS KANBAN
-- ==================================================
INSERT INTO kanban_categorias (nome, descricao, cor, ordem, usuario_id, time_id) VALUES
('Backlog', 'Tarefas aguardando para serem iniciadas', '#6C757D', 1, 1, 1),
('Em Progresso', 'Tarefas sendo desenvolvidas atualmente', '#FFC107', 2, 1, 1),
('Em Revisão', 'Tarefas aguardando revisão de código', '#17A2B8', 3, 1, 1),
('Teste', 'Tarefas em fase de testes', '#FD7E14', 4, 1, 1),
('Concluído', 'Tarefas finalizadas e entregues', '#28A745', 5, 1, 1),
('Bloqueado', 'Tarefas com impedimentos', '#DC3545', 6, 1, 1);

-- ==================================================
-- 5. TAREFAS TRADICIONAIS
-- ==================================================
-- NOTA: Tarefas com status 'TODO' e data de entrega vencida não são consideradas "atrasadas",
-- mas sim "não iniciadas com prazo vencido". Apenas tarefas com status 'DOING' podem estar atrasadas.
INSERT INTO tarefas (titulo, descricao, data_criacao, data_entrega, status, prioridade, responsavel_id, criado_por_id) VALUES
('Implementar Sistema de Login', 'Desenvolver sistema de autenticação com JWT', NOW(), '2024-04-15', 'DOING', 'ALTA', 3, 2),
('Criar Dashboard Principal', 'Desenvolver dashboard com métricas principais', NOW(), '2024-04-20', 'TODO', 'MEDIA', 5, 2),
('Testes de Integração API', 'Implementar testes automatizados para todas as APIs', NOW(), '2024-04-10', 'DOING', 'ALTA', 8, 2),
('Documentação da API', 'Criar documentação completa usando Swagger', NOW(), '2024-04-25', 'TODO', 'BAIXA', 4, 2),
('Setup Ambiente Produção', 'Configurar ambiente de produção na AWS', NOW(), '2024-04-05', 'DONE', 'ALTA', 6, 1);

-- ==================================================
-- 6. PARTICIPANTES DAS TAREFAS
-- ==================================================
INSERT INTO participantes_tarefa (tarefa_id, usuario_id, status_participacao, data_convite, data_resposta) VALUES
-- Tarefa 1 - Sistema de Login
(1, 3, 'ACEITO', NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 2 DAY),
(1, 4, 'ACEITO', NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 1 DAY),
-- Tarefa 2 - Dashboard
(2, 5, 'PENDENTE', NOW() - INTERVAL 1 DAY, NULL),
(2, 7, 'PENDENTE', NOW() - INTERVAL 1 DAY, NULL),
-- Tarefa 3 - Testes
(3, 8, 'ACEITO', NOW() - INTERVAL 3 DAY, NOW() - INTERVAL 3 DAY),
-- Tarefa 5 - Ambiente Produção
(5, 6, 'ACEITO', NOW() - INTERVAL 10 DAY, NOW() - INTERVAL 10 DAY),
(5, 10, 'ACEITO', NOW() - INTERVAL 10 DAY, NOW() - INTERVAL 9 DAY);

-- ==================================================
-- 7. NOTIFICAÇÕES
-- ==================================================
INSERT INTO notificacoes (usuario_notificado_id, tipo_notificacao, tarefa_id, texto_notificacao, confirmacao_leitura) VALUES
-- Notificações de tarefas atribuídas
(3, 'TAREFA_ATRIBUIDA', 1, 'Você foi convidado para participar da tarefa: Implementar Sistema de Login', false),
(5, 'TAREFA_ATRIBUIDA', 2, 'Você foi convidado para participar da tarefa: Criar Dashboard Principal', false),
(7, 'TAREFA_ATRIBUIDA', 2, 'Você foi convidado para participar da tarefa: Criar Dashboard Principal', false),
-- Notificações de aceitação
(2, 'TAREFA_ACEITA', 1, 'Fernanda Costa aceitou participar da tarefa: Implementar Sistema de Login', true),
(2, 'TAREFA_ACEITA', 3, 'Roberto Alves aceitou participar da tarefa: Testes de Integração API', true),
-- Notificações de conclusão
(1, 'TAREFA_CONCLUIDA', 5, 'A tarefa "Setup Ambiente Produção" foi concluída', true);

-- ==================================================
-- 8. TAREFAS KANBAN
-- ==================================================
INSERT INTO kanban_tarefas (titulo, descricao, categoria_id, responsavel_id, criado_por_id, data_criacao, data_entrega, prioridade, status) VALUES
('Refatorar Controllers', 'Separar lógica de negócio em services', 3, 3, 2, NOW(), '2024-03-30', 'MEDIA', 'ATIVA'),
('Implementar Validações', 'Adicionar Bean Validation nos DTOs', 2, 4, 2, NOW(), '2024-04-01', 'ALTA', 'ATIVA'),
('Criar Tela de Karaokê', 'Desenvolver interface para sessões de karaokê', 1, 5, 2, NOW(), '2024-04-15', 'MEDIA', 'ATIVA'),
('Testes Unitários Services', 'Criar testes unitários para todos os services', 1, 8, 2, NOW(), '2024-04-20', 'BAIXA', 'ATIVA'),
('Deploy Pipeline CI/CD', 'Configurar pipeline automatizado de deploy', 4, 6, 6, NOW(), '2024-03-28', 'ALTA', 'ATIVA'),
('Monitoring e Logs', 'Implementar sistema de monitoramento', 5, 6, 1, NOW(), '2024-03-20', 'MEDIA', 'CONCLUIDA');

-- ==================================================
-- 9. MÚSICAS PARA KARAOKÊ
-- ==================================================
INSERT INTO musicas (titulo, artista, genero, idioma, duracao, link_video) VALUES
('Bohemian Rhapsody', 'Queen', 'Rock', 'Inglês', '5:55', 'https://www.youtube.com/watch?v=fJ9rUzIMcZQ'),
('Imagine', 'John Lennon', 'Pop Rock', 'Inglês', '3:01', 'https://www.youtube.com/watch?v=YkgkThdzX-8'),
('Garota de Ipanema', 'Tom Jobim', 'Bossa Nova', 'Português', '2:56', 'https://www.youtube.com/watch?v=UJkxFhFRFDA'),
('Hotel California', 'Eagles', 'Rock', 'Inglês', '6:31', 'https://www.youtube.com/watch?v=09839DpTctU'),
('Águas de Março', 'Elis Regina', 'MPB', 'Português', '3:15', 'https://www.youtube.com/watch?v=xRqI5R6L7ow'),
('Don\'t Stop Believin\'', 'Journey', 'Rock', 'Inglês', '4:10', 'https://www.youtube.com/watch?v=1k8craCGpgs'),
('Mas que Nada', 'Jorge Ben Jor', 'Samba', 'Português', '2:35', 'https://www.youtube.com/watch?v=YQQ2StSwg5s'),
('Sweet Child O\' Mine', 'Guns N\' Roses', 'Hard Rock', 'Inglês', '5:03', 'https://www.youtube.com/watch?v=1w7OgIMMRc4'),
('Corcovado', 'Tom Jobim', 'Bossa Nova', 'Português', '3:28', 'https://www.youtube.com/watch?v=Lz-yk9jCYKs'),
('Don\'t Stop Me Now', 'Queen', 'Rock', 'Inglês', '3:29', 'https://www.youtube.com/watch?v=HgzGwKwLmgM');

-- ==================================================
-- 10. SESSÕES DE KARAOKÊ
-- ==================================================
INSERT INTO sessao_karaoke (titulo, data, local, organizador_id, descricao, status) VALUES
('Happy Hour Karaokê', '2024-03-15 19:00:00', 'Sala de Reuniões A', 1, 'Sessão descontraída de karaokê após o expediente', 'FINALIZADA'),
('Karaokê dos Clássicos', '2024-03-22 18:30:00', 'Auditório Principal', 9, 'Noite dedicada aos clássicos da música', 'FINALIZADA'),
('Rock Night', '2024-03-29 19:00:00', 'Sala de Descompressão', 1, 'Especial Rock com as melhores bandas', 'FINALIZADA'),
('MPB e Bossa Nova', '2024-04-05 18:00:00', 'Terraço', 9, 'Tarde especial com música brasileira', 'FINALIZADA'),
('Internacional Hits', '2024-04-12 19:30:00', 'Auditório Principal', 1, 'Os maiores sucessos internacionais', 'AGENDADA');

-- ==================================================
-- 11. MÚSICAS CANTADAS
-- ==================================================
INSERT INTO musicas_cantadas (usuario_id, musica_id, sessao_id, ordem_cantada, nota, comentarios) VALUES
(3, 1, 1, 1, 9.2, 'Performance incrível! Emocionou a todos.'),
(5, 3, 1, 2, 8.5, 'Voz suave, perfeito para bossa nova.'),
(7, 6, 1, 3, 7.8, 'Energia contagiante!'),
(4, 2, 1, 4, 8.0, 'Interpretação muito emotiva.'),
(2, 4, 2, 1, 8.7, 'Solo de guitarra no vocal foi genial!'),
(8, 5, 2, 2, 9.0, 'Conhece bem MPB, excelente escolha.'),
(6, 8, 2, 3, 8.3, 'Rock pesado, energia total!'),
(6, 10, 3, 1, 9.5, 'Freddie Mercury brasileiro! Sensacional!'),
(3, 8, 3, 2, 8.1, 'Quebrou tudo no rock!'),
(7, 6, 3, 3, 8.8, 'Journey sempre emociona!');

-- ==================================================
-- 12. AVALIAÇÕES
-- ==================================================
INSERT INTO avaliacoes (avaliador_id, apresentacao_id, nota, comentario, data_avaliacao) VALUES
(1, 1, 5, 'Performance histórica! Melhor Bohemian Rhapsody que já ouvi no karaokê.', NOW()),
(2, 2, 5, 'Fernanda tem voz perfeita para bossa nova.', NOW()),
(9, 3, 4, 'Juliana sempre traz energia positiva para o ambiente.', NOW()),
(1, 4, 5, 'João conseguiu emocionar com Imagine, muito sensível.', NOW()),
(6, 5, 5, 'Carlos mostrou que entende de rock clássico.', NOW()),
(9, 6, 5, 'Roberto surpreendeu com conhecimento de MPB.', NOW()),
(2, 7, 4, 'Pedro mandou muito bem no Guns N\' Roses.', NOW()),
(1, 8, 5, 'Lucas roubou a cena com Don\'t Stop Me Now!', NOW());

-- ==================================================
-- 13. CHECK DE HUMOR
-- ==================================================
INSERT INTO check_humor (usuario_id, humor, observacao, anonimo, data_registro) VALUES
-- Registros da semana atual
(3, 'FELIZ', 'Projeto aprovado pelo cliente! Equipe está motivada.', FALSE, CURDATE()),
(5, 'ANIMADO', 'Dia produtivo, consegui finalizar a interface do dashboard.', FALSE, CURDATE()),
(7, 'CALMO', 'Dia normal de trabalho, sem intercorrências.', FALSE, CURDATE()),
(4, 'ANIMADO', 'Testes passaram todos, código funcionando perfeitamente.', FALSE, CURDATE()),
(10, 'FELIZ', 'Karaokê de ontem foi incrível! Ambiente está ótimo.', FALSE, CURDATE()),
(2, 'ANIMADO', 'Sprint está no prazo, equipe colaborando bem.', FALSE, CURDATE()),
(8, 'CALMO', 'Alguns bugs encontrados, mas nada crítico.', FALSE, CURDATE()),
(6, 'ANIMADO', 'Deploy realizado com sucesso, produção estável.', FALSE, CURDATE()),

-- Registros da semana passada
(3, 'ANIMADO', 'Refatoração dos controllers concluída.', FALSE, DATE_SUB(CURDATE(), INTERVAL 7 DAY)),
(5, 'FELIZ', 'Feedback positivo da reunião com UX.', FALSE, DATE_SUB(CURDATE(), INTERVAL 6 DAY)),
(7, 'DESANIMADO', 'Muitos bugs reportados, pressão alta.', FALSE, DATE_SUB(CURDATE(), INTERVAL 5 DAY)),
(4, 'ANIMADO', 'Documentação da API atualizada.', FALSE, DATE_SUB(CURDATE(), INTERVAL 4 DAY)),
(10, 'FELIZ', 'Sessão de karaokê foi um sucesso!', FALSE, DATE_SUB(CURDATE(), INTERVAL 3 DAY)),
(2, 'CALMO', 'Reunião de alinhamento da sprint.', FALSE, DATE_SUB(CURDATE(), INTERVAL 2 DAY)),
(8, 'ANIMADO', 'Correções implementadas com sucesso.', FALSE, DATE_SUB(CURDATE(), INTERVAL 1 DAY)),

-- Registros quinzena anterior (para comparação)
(3, 'CALMO', 'Início do novo projeto, organizando tasks.', FALSE, DATE_SUB(CURDATE(), INTERVAL 14 DAY)),
(5, 'ANIMADO', 'Novo design aprovado pela equipe.', FALSE, DATE_SUB(CURDATE(), INTERVAL 13 DAY)),
(7, 'ESTRESSADO', 'Deadline muito apertado.', TRUE, DATE_SUB(CURDATE(), INTERVAL 12 DAY)), -- Anônimo
(4, 'CALMO', 'Revisão de código da equipe.', FALSE, DATE_SUB(CURDATE(), INTERVAL 11 DAY)),
(10, 'FELIZ', 'Integração da equipe no happy hour.', FALSE, DATE_SUB(CURDATE(), INTERVAL 10 DAY)),
(2, 'DESANIMADO', 'Problemas técnicos atrasaram entregas.', TRUE, DATE_SUB(CURDATE(), INTERVAL 9 DAY)), -- Anônimo
(8, 'ANIMADO', 'Resolvidos os problemas de performance.', FALSE, DATE_SUB(CURDATE(), INTERVAL 8 DAY)),

-- Comentários anônimos adicionais para demonstração
(1, 'ESTRESSADO', 'Muita pressão da gestão para entregar rapidamente.', TRUE, DATE_SUB(CURDATE(), INTERVAL 15 DAY)),
(6, 'DESANIMADO', 'Falta de reconhecimento pelo trabalho realizado.', TRUE, DATE_SUB(CURDATE(), INTERVAL 16 DAY)),
(9, 'CALMO', 'Trabalho remoto está funcionando bem.', FALSE, DATE_SUB(CURDATE(), INTERVAL 17 DAY)),
(1, 'FELIZ', 'Recebeu feedback positivo do cliente.', FALSE, DATE_SUB(CURDATE(), INTERVAL 18 DAY)),
(6, 'ANIMADO', 'Nova ferramenta melhorou muito a produtividade.', FALSE, DATE_SUB(CURDATE(), INTERVAL 19 DAY)),
(9, 'ESTRESSADO', 'Muitas reuniões, pouco tempo para desenvolvimento.', TRUE, DATE_SUB(CURDATE(), INTERVAL 20 DAY)),

-- Registros mais antigos para histórico
(3, 'FELIZ', 'Sucesso na apresentação para o cliente.', FALSE, DATE_SUB(CURDATE(), INTERVAL 21 DAY)),
(5, 'CALMO', 'Semana tranquila de manutenção.', FALSE, DATE_SUB(CURDATE(), INTERVAL 22 DAY)),
(7, 'ANIMADO', 'Nova tecnologia implementada com sucesso.', FALSE, DATE_SUB(CURDATE(), INTERVAL 23 DAY)),
(4, 'DESANIMADO', 'Bug crítico encontrado em produção.', FALSE, DATE_SUB(CURDATE(), INTERVAL 24 DAY)),
(10, 'FELIZ', 'Karaokê mensal foi excelente!', FALSE, DATE_SUB(CURDATE(), INTERVAL 25 DAY)),
(2, 'CALMO', 'Planejamento da nova sprint.', FALSE, DATE_SUB(CURDATE(), INTERVAL 26 DAY)),
(8, 'ESTRESSADO', 'Várias tarefas urgentes simultaneamente.', FALSE, DATE_SUB(CURDATE(), INTERVAL 27 DAY)),
(6, 'ANIMADO', 'Feature entregue antes do prazo.', FALSE, DATE_SUB(CURDATE(), INTERVAL 28 DAY));

-- ==================================================
-- 14. DASHBOARDS
-- ==================================================
INSERT INTO dashboards (usuario_id, tipo_dashboard, metricas) VALUES
(1, 'RH', '{"totalFuncionarios": 10, "checkHumorsHoje": 8, "satisfacaoMedia": 4.2}'),      -- Ana (RH)
(9, 'RH', '{"totalFuncionarios": 10, "checkHumorsHoje": 8, "satisfacaoMedia": 4.2}'),      -- Patrícia (RH)  
(2, 'LIDER', '{"tarefasTime": 6, "tarefasConcluidas": 1, "produtividadeTime": 85}'),       -- Carlos (Líder)
(6, 'LIDER', '{"tarefasTime": 5, "tarefasConcluidas": 1, "produtividadeTime": 90}'),       -- Pedro (Líder)
(3, 'FUNC', '{"minhasTarefas": 2, "tarefasConcluidas": 0, "horasTrabalhadas": 32}'),       -- Fernanda (Func)
(5, 'FUNC', '{"minhasTarefas": 2, "tarefasConcluidas": 0, "horasTrabalhadas": 28}');       -- Maria (Func)

-- ==================================================
-- 15. RELATÓRIOS
-- ==================================================
INSERT INTO relatorios (titulo, tipo, periodo_inicio, periodo_fim, gerado_por_id, time_id, data_geracao, conteudo) VALUES
('Relatório Mensal - Backend', 'MENSAL', '2024-02-01', '2024-02-29', 1, 1, NOW(), 'Relatório mensal do time de Backend: 85% das tarefas concluídas no prazo.'),
('Sprint 12 - Backend', 'SEMANAL', '2024-03-01', '2024-03-07', 2, 1, NOW(), 'Sprint 12 finalizada com sucesso. Todas as user stories entregues.'),
('Relatório RH Mensal', 'MENSAL', '2024-02-01', '2024-02-29', 9, 4, NOW(), 'Relatório RH: Taxa de satisfação dos funcionários em 92%.'),
('Relatório Trimestral Geral', 'TRIMESTRAL', '2024-01-01', '2024-03-31', 1, NULL, NOW(), 'Relatório trimestral geral: Produtividade aumentou 15% no período.'),
('Sprint Frontend', 'SEMANAL', '2024-03-01', '2024-03-07', 6, 2, NOW(), 'Frontend: Nova interface entregue e aprovada pelos usuários.'),
('Relatório DevOps', 'MENSAL', '2024-02-01', '2024-02-29', 6, 5, NOW(), 'DevOps: 99.9% de uptime em produção. Zero incidentes críticos.');

-- ==================================================
-- 16. FEEDBACKS
-- ==================================================
INSERT INTO feedbacks (usuario_id, tipo, titulo, conteudo, status, prioridade, anonimo, data_criacao, data_resposta, respondido_por_id, resposta) VALUES
-- Feedbacks públicos
(3, 'SUGESTAO', 'Melhorar interface do dashboard', 'Seria interessante ter widgets mais interativos no dashboard principal.', 'RESPONDIDO', 'MEDIA', false, NOW() - INTERVAL 5 DAY, NOW() - INTERVAL 2 DAY, 1, 'Obrigado pela sugestão! Estamos planejando uma atualização da interface para o próximo trimestre.'),
(5, 'ELOGIO', 'Excelente sistema de karaokê', 'O sistema de karaokê trouxe muito mais descontração para a equipe. Parabéns!', 'FECHADO', 'BAIXA', false, NOW() - INTERVAL 7 DAY, NOW() - INTERVAL 5 DAY, 9, 'Ficamos muito felizes com seu feedback! O objetivo era exatamente esse.'),
(8, 'BUG_REPORT', 'Erro ao salvar música no karaokê', 'Quando tento salvar uma música com caracteres especiais, o sistema apresenta erro.', 'EM_ANALISE', 'ALTA', false, NOW() - INTERVAL 3 DAY, NULL, NULL, NULL),
(4, 'MELHORIA', 'Notificações por email', 'Seria útil receber notificações de tarefas atribuídas por email também.', 'PENDENTE', 'MEDIA', false, NOW() - INTERVAL 1 DAY, NULL, NULL, NULL),

-- Feedbacks anônimos
(2, 'RECLAMACAO', 'Sobrecarga de trabalho', 'Tem muita tarefa sendo atribuída para poucas pessoas.', 'PENDENTE', 'ALTA', true, NOW() - INTERVAL 4 DAY, NULL, NULL, NULL),
(7, 'SUGESTAO', 'Home office flexível', 'Gostaria de ter mais opções de trabalho remoto.', 'EM_ANALISE', 'MEDIA', true, NOW() - INTERVAL 6 DAY, NULL, NULL, NULL),
(6, 'DUVIDA', 'Como funciona o sistema de humor?', 'Não entendi bem como usar o check de humor diário.', 'RESPONDIDO', 'BAIXA', true, NOW() - INTERVAL 8 DAY, NOW() - INTERVAL 6 DAY, 9, 'O check de humor é muito simples: apenas acesse diariamente e registre como você está se sentindo. Seus dados ficam anônimos se preferir.'),
(10, 'MELHORIA', 'Relatórios mais detalhados', 'Os relatórios poderiam ter mais gráficos e estatísticas.', 'PENDENTE', 'BAIXA', false, NOW() - INTERVAL 2 DAY, NULL, NULL, NULL);

-- ==================================================
-- 17. JOBS
-- ==================================================
INSERT INTO jobs (nome, descricao, tipo, status, data_agendamento, data_execucao, data_conclusao, criado_por_id, cron_expression, parametros, resultado, erro, tentativas, max_tentativas) VALUES
-- Jobs concluídos
('Notificar Prazos Tarefas', 'Verifica tarefas próximas ao vencimento e envia notificações', 'NOTIFICACAO_PRAZO', 'CONCLUIDO', NOW() - INTERVAL 1 HOUR, NOW() - INTERVAL 1 HOUR, NOW() - INTERVAL 55 MINUTE, 1, '0 9 * * *', '{"diasAntecedencia": 2}', '3 tarefas próximas ao vencimento encontradas. Notificações enviadas.', NULL, 1, 3),
('Backup Banco de Dados', 'Realiza backup completo do banco de dados', 'BACKUP_DADOS', 'CONCLUIDO', NOW() - INTERVAL 12 HOUR, NOW() - INTERVAL 12 HOUR, NOW() - INTERVAL 11 HOUR, 1, '0 2 * * *', '{"compressao": true, "local": "s3://backups/"}', 'Backup realizado com sucesso. Tamanho: 1.2GB', NULL, 1, 3),
('Limpeza Logs Sistema', 'Remove logs antigos do sistema', 'LIMPEZA_LOGS', 'CONCLUIDO', NOW() - INTERVAL 24 HOUR, NOW() - INTERVAL 24 HOUR, NOW() - INTERVAL 23 HOUR, 6, '0 3 * * 0', '{"diasManter": 30}', 'Logs removidos: 150MB liberados', NULL, 1, 3),

-- Jobs agendados
('Relatório Semanal RH', 'Gera relatório semanal de humor da equipe', 'RELATORIO_AUTOMATICO', 'AGENDADO', NOW() + INTERVAL 2 DAY, NULL, NULL, 9, '0 8 * * 1', '{"tipo": "humor", "formato": "PDF"}', NULL, NULL, 0, 3),
('Sincronização Dados', 'Sincroniza dados com sistema externo', 'SINCRONIZACAO', 'AGENDADO', NOW() + INTERVAL 6 HOUR, NULL, NULL, 6, '0 */6 * * *', '{"endpoint": "https://api.externa.com/sync"}', NULL, NULL, 0, 3),
('Manutenção Banco', 'Otimização e manutenção do banco de dados', 'MANUTENCAO', 'AGENDADO', NOW() + INTERVAL 1 DAY, NULL, NULL, 1, '0 4 * * 6', '{"otimizar": true, "reindexar": true}', NULL, NULL, 0, 3),

-- Job com erro para teste
('Envio Email Newsletter', 'Envia newsletter semanal para funcionários', 'ENVIO_EMAIL', 'ERRO', NOW() - INTERVAL 2 HOUR, NOW() - INTERVAL 2 HOUR, NOW() - INTERVAL 1 HOUR, 9, '0 10 * * 1', '{"template": "newsletter", "destinatarios": "todos"}', NULL, 'Erro de conexão com servidor SMTP', 2, 3);

-- ==================================================
-- 18. TAREFAS (NOVA ESTRUTURA)
-- ==================================================
INSERT INTO tarefas (titulo, descricao, data_criacao, data_entrega, status, prioridade, responsavel_id, criado_por_id) VALUES
('Implementar Sistema de Feedback', 'Desenvolver sistema completo para coleta de feedback dos usuários', NOW(), '2024-04-30', 'TODO', 'ALTA', 4, 2),
('Criar Sistema de Jobs', 'Implementar sistema de agendamento e execução de jobs', NOW(), '2024-05-15', 'DOING', 'ALTA', 6, 1),
('Melhorar Performance Dashboard', 'Otimizar carregamento do dashboard principal', NOW(), '2024-04-25', 'TODO', 'MEDIA', 3, 2),
('Implementar Notificações Email', 'Adicionar sistema de notificações por email', NOW(), '2024-05-10', 'TODO', 'MEDIA', 5, 1);

-- ==================================================
-- VERIFICAÇÃO DOS DADOS INSERIDOS
-- ==================================================

/*
SELECT 'Usuários' as Tabela, COUNT(*) as Total FROM usuarios
UNION ALL
SELECT 'Times', COUNT(*) FROM times  
UNION ALL
SELECT 'Membros Time', COUNT(*) FROM membros_time
UNION ALL
SELECT 'Categorias Kanban', COUNT(*) FROM kanban_categorias
UNION ALL
SELECT 'Tarefas', COUNT(*) FROM tarefas
UNION ALL
SELECT 'Participantes Tarefa', COUNT(*) FROM participantes_tarefa
UNION ALL
SELECT 'Notificações', COUNT(*) FROM notificacoes
UNION ALL
SELECT 'Tarefas Kanban', COUNT(*) FROM kanban_tarefas
UNION ALL
SELECT 'Músicas', COUNT(*) FROM musicas
UNION ALL
SELECT 'Sessões Karaokê', COUNT(*) FROM sessao_karaoke
UNION ALL
SELECT 'Músicas Cantadas', COUNT(*) FROM musicas_cantadas
UNION ALL
SELECT 'Avaliações', COUNT(*) FROM avaliacoes
UNION ALL
SELECT 'Check Humor', COUNT(*) FROM check_humor
UNION ALL
SELECT 'Dashboards', COUNT(*) FROM dashboards
UNION ALL
SELECT 'Relatórios', COUNT(*) FROM relatorios;
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

-- Dump completed
-- ================================================== 