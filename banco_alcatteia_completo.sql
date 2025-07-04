-- ================================================================
-- BANCO ALCATTEIA - ESTRUTURA COMPLETA E OTIMIZADA
-- ================================================================
-- Script único para recriar todo o banco do zero
-- Incorpora todas as melhorias identificadas na análise

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

-- ================================================================
-- REMOVER TODAS AS TABELAS EXISTENTES
-- ================================================================

DROP DATABASE IF EXISTS `banco_alcatteia`;
CREATE DATABASE `banco_alcatteia` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `banco_alcatteia`;

-- ================================================================
-- CRIAR TODAS AS TABELAS COM ESTRUTURA OTIMIZADA
-- ================================================================

-- 1. USUÁRIOS
CREATE TABLE `usuarios` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `senha` varchar(255) NOT NULL,
  `tipo_usuario` enum('FUNC','LIDER','RH') NOT NULL,
  `data_nascimento` date DEFAULT NULL,
  `descricao` varchar(255) DEFAULT NULL,
  `imagem` varchar(350) DEFAULT NULL,
  `data_criacao` datetime DEFAULT CURRENT_TIMESTAMP,
  `data_atualizacao` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `data_ultima_atualizacao` datetime DEFAULT NULL,
  `ativo` boolean DEFAULT TRUE,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_usuarios_email` (`email`),
  KEY `idx_usuarios_tipo` (`tipo_usuario`),
  KEY `idx_usuarios_ativo` (`ativo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. TIMES
CREATE TABLE `times` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nome_time` varchar(100) NOT NULL,
  `descricao` text DEFAULT NULL,
  `lider_id` int DEFAULT NULL,
  `criado_por_id` int NOT NULL,
  `cor` varchar(7) DEFAULT NULL,
  `data_criacao` datetime DEFAULT CURRENT_TIMESTAMP,
  `data_atualizacao` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `data_ultima_atualizacao` datetime DEFAULT NULL,
  `ativo` boolean DEFAULT TRUE,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_times_nome` (`nome_time`),
  KEY `idx_times_lider` (`lider_id`),
  KEY `idx_times_ativo` (`ativo`),
  CONSTRAINT `fk_times_lider` FOREIGN KEY (`lider_id`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_times_criado_por` FOREIGN KEY (`criado_por_id`) REFERENCES `usuarios` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. MEMBROS TIME
CREATE TABLE `membros_time` (
  `id` int NOT NULL AUTO_INCREMENT,
  `usuario_id` int NOT NULL,
  `time_id` int NOT NULL,
  `data_entrada` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `data_saida` datetime DEFAULT NULL,
  `tipo_membro` enum('LIDER','VICE_LIDER','MEMBRO','COLABORADOR') NOT NULL DEFAULT 'MEMBRO',
  `observacoes` varchar(255) DEFAULT NULL,
  `ativo` boolean DEFAULT TRUE,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_membros_time_usuario_time` (`usuario_id`, `time_id`),
  KEY `idx_membros_time_time` (`time_id`),
  KEY `idx_membros_time_tipo` (`tipo_membro`),
  CONSTRAINT `fk_membros_time_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_membros_time_time` FOREIGN KEY (`time_id`) REFERENCES `times` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. KANBAN CATEGORIAS
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
  CONSTRAINT `fk_kanban_categorias_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_kanban_categorias_time` FOREIGN KEY (`time_id`) REFERENCES `times` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_kanban_categorias_criado_por` FOREIGN KEY (`criado_por_id`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. TAREFAS
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
  KEY `idx_tarefas_responsavel` (`responsavel_id`),
  CONSTRAINT `fk_tarefas_responsavel` FOREIGN KEY (`responsavel_id`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_tarefas_criado_por` FOREIGN KEY (`criado_por_id`) REFERENCES `usuarios` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. PARTICIPANTES TAREFA
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
  CONSTRAINT `fk_participantes_tarefa_tarefa` FOREIGN KEY (`tarefa_id`) REFERENCES `tarefas` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_participantes_tarefa_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. NOTIFICAÇÕES
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
  CONSTRAINT `fk_notificacoes_usuario` FOREIGN KEY (`usuario_notificado_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_notificacoes_tarefa` FOREIGN KEY (`tarefa_id`) REFERENCES `tarefas` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. KANBAN TAREFAS
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
  CONSTRAINT `fk_kanban_tarefas_categoria` FOREIGN KEY (`categoria_id`) REFERENCES `kanban_categorias` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_kanban_tarefas_responsavel` FOREIGN KEY (`responsavel_id`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_kanban_tarefas_criado_por` FOREIGN KEY (`criado_por_id`) REFERENCES `usuarios` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 9. CHECK HUMOR (ESTRUTURA OTIMIZADA)
CREATE TABLE `check_humor` (
  `id` int NOT NULL AUTO_INCREMENT,
  `usuario_id` int NOT NULL,
  `data_registro` date NOT NULL,
  `humor` enum('FELIZ','ANIMADO','CALMO','DESANIMADO','ESTRESSADO') NOT NULL,
  `observacao` text,
  `anonimo` boolean DEFAULT FALSE,
  `bem_estar_pontos` int DEFAULT NULL,
  `confirmado` boolean DEFAULT TRUE,
  `data_criacao` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_check_humor_usuario_data` (`usuario_id`, `data_registro`),
  KEY `idx_check_humor_humor` (`humor`),
  KEY `idx_check_humor_bem_estar` (`bem_estar_pontos`),
  CONSTRAINT `fk_check_humor_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE,
  CONSTRAINT `chk_bem_estar_pontos` CHECK (`bem_estar_pontos` >= 0 AND `bem_estar_pontos` <= 10)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 10. ROOMS (NOVA FUNCIONALIDADE)
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
  CONSTRAINT `fk_rooms_host` FOREIGN KEY (`host_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 11. MÚSICAS
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
  FULLTEXT KEY `ft_musicas_titulo_artista` (`titulo`, `artista`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 12. SESSÃO KARAOKÊ
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
  CONSTRAINT `fk_sessao_karaoke_organizador` FOREIGN KEY (`organizador_id`) REFERENCES `usuarios` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 13. MÚSICAS CANTADAS
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
  CONSTRAINT `fk_musicas_cantadas_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_musicas_cantadas_musica` FOREIGN KEY (`musica_id`) REFERENCES `musicas` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_musicas_cantadas_sessao` FOREIGN KEY (`sessao_id`) REFERENCES `sessao_karaoke` (`id`) ON DELETE CASCADE,
  CONSTRAINT `chk_nota_musica` CHECK (`nota` >= 0 AND `nota` <= 10)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 14. AVALIAÇÕES
CREATE TABLE `avaliacoes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `avaliador_id` int NOT NULL,
  `apresentacao_id` int NOT NULL,
  `nota` int NOT NULL,
  `comentario` text,
  `data_avaliacao` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_avaliacoes_avaliador_apresentacao` (`avaliador_id`, `apresentacao_id`),
  CONSTRAINT `fk_avaliacoes_avaliador` FOREIGN KEY (`avaliador_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_avaliacoes_apresentacao` FOREIGN KEY (`apresentacao_id`) REFERENCES `musicas_cantadas` (`id`) ON DELETE CASCADE,
  CONSTRAINT `chk_nota_avaliacao` CHECK (`nota` >= 1 AND `nota` <= 5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 15. DASHBOARDS
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
  CONSTRAINT `fk_dashboards_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 16. RELATÓRIOS
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
  CONSTRAINT `fk_relatorios_gerado_por` FOREIGN KEY (`gerado_por_id`) REFERENCES `usuarios` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_relatorios_time` FOREIGN KEY (`time_id`) REFERENCES `times` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 17. FEEDBACKS
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
  CONSTRAINT `fk_feedbacks_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_feedbacks_respondido_por` FOREIGN KEY (`respondido_por_id`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 18. JOBS
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
  CONSTRAINT `fk_jobs_criado_por` FOREIGN KEY (`criado_por_id`) REFERENCES `usuarios` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ================================================================
-- INSERIR DADOS DE DEMONSTRAÇÃO COMPLETOS
-- ================================================================

-- USUÁRIOS (com senhas criptografadas BCrypt para "123456")
INSERT INTO usuarios (nome, email, senha, tipo_usuario, data_nascimento, descricao) VALUES
('Ana Silva', 'ana.silva@bancoalcatteia.com', '$2a$10$Xl0yhvzLIaJCDdKBS5/l4eFt.PsLUj7QqJK5IhZGXl1FzUvLSQfK6', 'RH', '1985-03-15', 'Gestora de RH especializada em bem-estar organizacional'),
('Carlos Mendes', 'carlos.mendes@bancoalcatteia.com', '$2a$10$Xl0yhvzLIaJCDdKBS5/l4eFt.PsLUj7QqJK5IhZGXl1FzUvLSQfK6', 'LIDER', '1988-07-22', 'Líder técnico com experiência em arquitetura de software'),
('Fernanda Costa', 'fernanda.costa@bancoalcatteia.com', '$2a$10$Xl0yhvzLIaJCDdKBS5/l4eFt.PsLUj7QqJK5IhZGXl1FzUvLSQfK6', 'FUNC', '1992-11-10', 'Desenvolvedora Full Stack com foco em React e Node.js'),
('João Santos', 'joao.santos@bancoalcatteia.com', '$2a$10$Xl0yhvzLIaJCDdKBS5/l4eFt.PsLUj7QqJK5IhZGXl1FzUvLSQfK6', 'FUNC', '1990-05-18', 'Desenvolvedor Backend especialista em APIs REST'),
('Maria Oliveira', 'maria.oliveira@bancoalcatteia.com', '$2a$10$Xl0yhvzLIaJCDdKBS5/l4eFt.PsLUj7QqJK5IhZGXl1FzUvLSQfK6', 'FUNC', '1995-09-03', 'Desenvolvedora Frontend com expertise em UX/UI'),
('Pedro Lima', 'pedro.lima@bancoalcatteia.com', '$2a$10$Xl0yhvzLIaJCDdKBS5/l4eFt.PsLUj7QqJK5IhZGXl1FzUvLSQfK6', 'LIDER', '1987-12-25', 'Líder técnico e especialista em DevOps'),
('Juliana Rocha', 'juliana.rocha@bancoalcatteia.com', '$2a$10$Xl0yhvzLIaJCDdKBS5/l4eFt.PsLUj7QqJK5IhZGXl1FzUvLSQfK6', 'FUNC', '1993-01-14', 'Desenvolvedora Frontend e Designer UX/UI'),
('Roberto Alves', 'roberto.alves@bancoalcatteia.com', '$2a$10$Xl0yhvzLIaJCDdKBS5/l4eFt.PsLUj7QqJK5IhZGXl1FzUvLSQfK6', 'FUNC', '1989-04-30', 'Analista de Qualidade e Testes Automatizados'),
('Patrícia Nunes', 'patricia.nunes@bancoalcatteia.com', '$2a$10$Xl0yhvzLIaJCDdKBS5/l4eFt.PsLUj7QqJK5IhZGXl1FzUvLSQfK6', 'RH', '1991-08-12', 'Analista de RH e Desenvolvimento Organizacional'),
('Lucas Ferreira', 'lucas.ferreira@bancoalcatteia.com', '$2a$10$Xl0yhvzLIaJCDdKBS5/l4eFt.PsLUj7QqJK5IhZGXl1FzUvLSQfK6', 'FUNC', '1994-06-07', 'Desenvolvedor Full Stack e Especialista DevOps');

-- TIMES
INSERT INTO times (nome_time, lider_id, criado_por_id, descricao, cor) VALUES
('Desenvolvimento Backend', 2, 1, 'Time responsável pelo desenvolvimento das APIs e lógica de negócio', '#007bff'),
('Desenvolvimento Frontend', 6, 1, 'Time responsável pelo desenvolvimento das interfaces e experiência do usuário', '#28a745'),
('Quality Assurance', 8, 1, 'Time responsável pela qualidade e testes do sistema', '#ffc107'),
('Recursos Humanos', 1, 1, 'Time responsável pela gestão de pessoas e bem-estar', '#dc3545'),
('DevOps', 6, 1, 'Time responsável pela infraestrutura e deploy', '#6f42c1');

-- MEMBROS DOS TIMES
INSERT INTO membros_time (usuario_id, time_id, data_entrada, tipo_membro, observacoes) VALUES
(2, 1, '2024-01-15 09:00:00', 'LIDER', 'Líder técnico do time de Backend'),
(3, 1, '2024-01-20 09:00:00', 'MEMBRO', 'Desenvolvedora sênior'),
(4, 1, '2024-01-25 09:00:00', 'MEMBRO', 'Especialista em APIs'),
(10, 1, '2024-02-01 09:00:00', 'MEMBRO', 'Desenvolvedor júnior em crescimento'),
(6, 2, '2024-01-15 09:00:00', 'LIDER', 'Líder técnico do Frontend'),
(5, 2, '2024-01-22 09:00:00', 'MEMBRO', 'Especialista em React'),
(7, 2, '2024-01-30 09:00:00', 'MEMBRO', 'Designer UX/UI'),
(8, 3, '2024-02-01 09:00:00', 'LIDER', 'Líder de qualidade'),
(7, 3, '2024-02-05 09:00:00', 'MEMBRO', 'Testadora manual e automatizada'),
(1, 4, '2024-01-10 09:00:00', 'LIDER', 'Gestora de RH'),
(9, 4, '2024-01-15 09:00:00', 'MEMBRO', 'Analista de RH'),
(6, 5, '2024-01-20 09:00:00', 'LIDER', 'Líder de infraestrutura'),
(10, 5, '2024-02-10 09:00:00', 'MEMBRO', 'Suporte DevOps');

-- CATEGORIAS KANBAN
INSERT INTO kanban_categorias (nome, descricao, cor, ordem, time_id, criado_por_id) VALUES
('Backlog', 'Tarefas aguardando para serem iniciadas', '#6C757D', 1, 1, 1),
('Em Progresso', 'Tarefas sendo desenvolvidas', '#FFC107', 2, 1, 1),
('Em Revisão', 'Tarefas aguardando code review', '#17A2B8', 3, 1, 1),
('Teste', 'Tarefas em fase de testes', '#FD7E14', 4, 1, 1),
('Concluído', 'Tarefas finalizadas', '#28A745', 5, 1, 1),
('Bloqueado', 'Tarefas com impedimentos', '#DC3545', 6, 1, 1);

-- CHECK HUMOR (dados dos últimos 30 dias)
INSERT INTO check_humor (usuario_id, humor, observacao, anonimo, bem_estar_pontos, data_registro) VALUES
(3, 'FELIZ', 'Projeto aprovado pelo cliente!', FALSE, 9, CURDATE()),
(5, 'ANIMADO', 'Interface do dashboard finalizada', FALSE, 8, CURDATE()),
(7, 'CALMO', 'Dia produtivo de trabalho', FALSE, 7, CURDATE()),
(4, 'ANIMADO', 'Todos os testes passaram', FALSE, 8, CURDATE()),
(10, 'FELIZ', 'Karaokê foi incrível ontem!', FALSE, 9, CURDATE()),
(2, 'ANIMADO', 'Sprint no prazo', FALSE, 8, CURDATE()),
(8, 'CALMO', 'Alguns bugs, mas nada crítico', FALSE, 7, CURDATE()),
(6, 'ANIMADO', 'Deploy realizado com sucesso', FALSE, 8, CURDATE()),
(1, 'FELIZ', 'Equipe engajada e motivada', FALSE, 9, CURDATE()),
(9, 'CALMO', 'Reuniões produtivas hoje', FALSE, 7, CURDATE());

-- ROOMS
INSERT INTO rooms (room_id, room_name, room_description, host_id, max_participants) VALUES
('room_daily_001', 'Daily Stand-up', 'Reunião diária da equipe de desenvolvimento', 2, 15),
('room_retro_001', 'Retrospectiva Sprint', 'Retrospectiva quinzenal da equipe', 6, 20),
('room_planning_001', 'Planning Mensal', 'Planejamento estratégico mensal', 1, 10),
('room_karaoke_001', 'Karaokê Virtual', 'Sala de karaokê para descontração da equipe', 9, 30),
('room_feedback_001', 'Sessão de Feedback', 'Sala para sessões de feedback e melhorias', 1, 8),
('room_training_001', 'Treinamentos', 'Sala para treinamentos e workshops', 6, 25);

-- TAREFAS
INSERT INTO tarefas (titulo, descricao, status, prioridade, responsavel_id, criado_por_id, data_entrega) VALUES
('Implementar Sistema de Login JWT', 'Desenvolver autenticação completa com JWT e refresh tokens', 'DOING', 'ALTA', 3, 2, '2024-04-15'),
('Criar Dashboard Principal', 'Dashboard com métricas principais e widgets interativos', 'TODO', 'MEDIA', 5, 2, '2024-04-20'),
('Testes de Integração API', 'Implementar testes automatizados para todas as APIs', 'DOING', 'ALTA', 8, 2, '2024-04-10'),
('Documentação da API', 'Documentação completa usando Swagger/OpenAPI', 'TODO', 'BAIXA', 4, 2, '2024-04-25'),
('Setup Ambiente Produção', 'Configurar ambiente de produção na AWS com CI/CD', 'DONE', 'ALTA', 6, 1, '2024-04-05'),
('Sistema de Rooms', 'Implementar funcionalidade de salas de reunião virtuais', 'TODO', 'MEDIA', 10, 1, '2024-05-01');

-- KANBAN TAREFAS
INSERT INTO kanban_tarefas (titulo, descricao, categoria_id, responsavel_id, criado_por_id, prioridade, status) VALUES
('Refatorar Controllers', 'Separar lógica de negócio em services', 3, 3, 2, 'MEDIA', 'ATIVA'),
('Implementar Validações', 'Bean Validation nos DTOs', 2, 4, 2, 'ALTA', 'ATIVA'),
('Tela de Karaokê', 'Interface para sessões de karaokê', 1, 5, 2, 'MEDIA', 'ATIVA'),
('Testes Unitários', 'Cobertura de 80% nos services', 1, 8, 2, 'BAIXA', 'ATIVA'),
('Pipeline CI/CD', 'Automatizar deploy', 4, 6, 6, 'ALTA', 'ATIVA'),
('Monitoring', 'Sistema de logs e métricas', 5, 6, 1, 'MEDIA', 'CONCLUIDA');

-- MÚSICAS
INSERT INTO musicas (titulo, artista, genero, idioma, duracao) VALUES
('Bohemian Rhapsody', 'Queen', 'Rock', 'Inglês', '5:55'),
('Imagine', 'John Lennon', 'Pop Rock', 'Inglês', '3:01'),
('Garota de Ipanema', 'Tom Jobim', 'Bossa Nova', 'Português', '2:56'),
('Hotel California', 'Eagles', 'Rock', 'Inglês', '6:31'),
('Águas de Março', 'Elis Regina', 'MPB', 'Português', '3:15');

-- SESSÕES DE KARAOKÊ
INSERT INTO sessao_karaoke (titulo, data, local, organizador_id, descricao, status) VALUES
('Happy Hour Karaokê', '2024-03-15 19:00:00', 'Sala A', 1, 'Sessão descontraída pós-expediente', 'FINALIZADA'),
('Rock Night', '2024-04-12 19:30:00', 'Auditório', 1, 'Especial Rock', 'AGENDADA');

-- DASHBOARDS
INSERT INTO dashboards (usuario_id, tipo_dashboard, metricas) VALUES
(1, 'RH', '{"totalFuncionarios": 10, "checkHumorsHoje": 10, "satisfacaoMedia": 4.4}'),
(2, 'LIDER', '{"tarefasTime": 6, "tarefasConcluidas": 1, "produtividadeTime": 85}'),
(6, 'LIDER', '{"tarefasTime": 5, "tarefasConcluidas": 1, "produtividadeTime": 90}');

-- FEEDBACKS
INSERT INTO feedbacks (usuario_id, tipo, titulo, conteudo, status, prioridade) VALUES
(3, 'SUGESTAO', 'Melhorar dashboard', 'Widgets mais interativos seriam úteis', 'PENDENTE', 'MEDIA'),
(5, 'ELOGIO', 'Sistema de karaokê', 'Trouxe mais descontração para a equipe', 'FECHADO', 'BAIXA'),
(8, 'BUG_REPORT', 'Erro ao salvar música', 'Caracteres especiais causam erro', 'EM_ANALISE', 'ALTA');

-- ================================================================
-- FINALIZAÇÃO
-- ================================================================

SET FOREIGN_KEY_CHECKS = 1;
SET UNIQUE_CHECKS = 1;

-- Restaurar configurações
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- ================================================================
-- VERIFICAÇÃO FINAL
-- ================================================================

SELECT 'BANCO ALCATTEIA CRIADO COM SUCESSO!' as STATUS;
SELECT 'Estrutura otimizada com todas as melhorias incorporadas' as OBSERVACAO;

-- Contagem de registros
SELECT 'usuarios' as tabela, COUNT(*) as registros FROM usuarios
UNION ALL SELECT 'times', COUNT(*) FROM times
UNION ALL SELECT 'membros_time', COUNT(*) FROM membros_time
UNION ALL SELECT 'check_humor', COUNT(*) FROM check_humor
UNION ALL SELECT 'rooms', COUNT(*) FROM rooms
UNION ALL SELECT 'tarefas', COUNT(*) FROM tarefas
ORDER BY tabela; 