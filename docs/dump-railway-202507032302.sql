-- MySQL dump 10.13  Distrib 8.0.27, for Win64 (x86_64)
--
-- Host: maglev.proxy.rlwy.net    Database: railway
-- ------------------------------------------------------
-- Server version	9.3.0

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

--
-- Table structure for table `avaliacoes`
--

DROP TABLE IF EXISTS `avaliacoes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `avaliacoes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `avaliador_id` int NOT NULL,
  `apresentacao_id` bigint NOT NULL,
  `nota` int NOT NULL,
  `comentario` text,
  `data_avaliacao` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_avaliacoes_avaliador_apresentacao` (`avaliador_id`,`apresentacao_id`),
  KEY `idx_avaliacoes_apresentacao` (`apresentacao_id`),
  KEY `idx_avaliacoes_nota` (`nota`),
  CONSTRAINT `fk_avaliacoes_apresentacao` FOREIGN KEY (`apresentacao_id`) REFERENCES `musicas_cantadas` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_avaliacoes_avaliador` FOREIGN KEY (`avaliador_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE,
  CONSTRAINT `chk_avaliacoes_nota` CHECK (((`nota` >= 1) and (`nota` <= 5)))
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `avaliacoes`
--

LOCK TABLES `avaliacoes` WRITE;
/*!40000 ALTER TABLE `avaliacoes` DISABLE KEYS */;
INSERT INTO `avaliacoes` VALUES (1,1,1,5,'Performance histórica! Melhor Bohemian Rhapsody que já ouvi no karaokê.','2025-06-16 00:55:50'),(2,2,2,5,'Fernanda tem voz perfeita para bossa nova.','2025-06-16 00:55:50'),(3,9,3,4,'Juliana sempre traz energia positiva para o ambiente.','2025-06-16 00:55:50'),(4,1,4,5,'João conseguiu emocionar com Imagine, muito sensível.','2025-06-16 00:55:50'),(5,6,5,5,'Carlos mostrou que entende de rock clássico.','2025-06-16 00:55:50'),(6,9,6,5,'Roberto surpreendeu com conhecimento de MPB.','2025-06-16 00:55:50'),(7,2,7,4,'Pedro mandou muito bem no Guns N\' Roses.','2025-06-16 00:55:50'),(8,1,8,5,'Lucas roubou a cena com Don\'t Stop Me Now!','2025-06-16 00:55:50');
/*!40000 ALTER TABLE `avaliacoes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `check_humor`
--

DROP TABLE IF EXISTS `check_humor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `check_humor` (
  `id` int NOT NULL AUTO_INCREMENT,
  `usuario_id` int NOT NULL,
  `data_registro` date NOT NULL,
  `humor` enum('Péssimo','Ruim','Neutro','Bom','Ótimo') NOT NULL,
  `observacao` text,
  `data_criacao` datetime DEFAULT CURRENT_TIMESTAMP,
  `anonimo` bit(1) DEFAULT NULL,
  `bem_estar_pontos` int DEFAULT NULL,
  `confirmado` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_check_humor_usuario_data` (`usuario_id`,`data_registro`),
  UNIQUE KEY `UKhh6vltjm6glfg91lys4n9oain` (`usuario_id`,`data_registro`),
  KEY `idx_check_humor_data` (`data_registro`),
  KEY `idx_check_humor_humor` (`humor`),
  CONSTRAINT `fk_check_humor_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `check_humor`
--

LOCK TABLES `check_humor` WRITE;
/*!40000 ALTER TABLE `check_humor` DISABLE KEYS */;
INSERT INTO `check_humor` VALUES (1,3,'2025-06-16','Ótimo','Projeto aprovado pelo cliente! Equipe está motivada.','2025-06-16 00:56:01',NULL,NULL,NULL),(2,5,'2025-06-16','Ruim','Dia produtivo, consegui finalizar a interface do dashboard.','2025-06-16 00:56:01',NULL,NULL,NULL),(3,7,'2025-06-16','Neutro','Dia normal de trabalho, sem intercorrências.','2025-06-16 00:56:01',NULL,NULL,NULL),(4,4,'2025-06-16','Bom','Testes passaram todos, código funcionando perfeitamente.','2025-06-16 00:56:01',NULL,NULL,NULL),(5,10,'2025-06-16','Ótimo','Karaokê de ontem foi incrível! Ambiente está ótimo.','2025-06-16 00:56:01',NULL,NULL,NULL),(6,2,'2025-06-16','Bom','Sprint está no prazo, equipe colaborando bem.','2025-06-16 00:56:01',NULL,NULL,NULL),(7,8,'2025-06-16','Neutro','Alguns bugs encontrados, mas nada crítico.','2025-06-16 00:56:01',NULL,NULL,NULL),(8,6,'2025-06-16','Bom','Deploy realizado com sucesso, produção estável.','2025-06-16 00:56:01',NULL,NULL,NULL),(9,3,'2025-06-09','Bom','Refatoração dos controllers concluída.','2025-06-16 00:56:01',NULL,NULL,NULL),(10,5,'2025-06-10','Ótimo','Feedback positivo da reunião com UX.','2025-06-16 00:56:01',NULL,NULL,NULL),(11,7,'2025-06-11','Ruim','Muitos bugs reportados, pressão alta.','2025-06-16 00:56:01',NULL,NULL,NULL),(12,4,'2025-06-12','Bom','Documentação da API atualizada.','2025-06-16 00:56:01',NULL,NULL,NULL),(13,10,'2025-06-13','Neutro','Configuração de ambiente demorou mais que esperado.','2025-06-16 00:56:01',NULL,NULL,NULL);
/*!40000 ALTER TABLE `check_humor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dashboards`
--

DROP TABLE IF EXISTS `dashboards`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dashboards` (
  `id` int NOT NULL AUTO_INCREMENT,
  `usuario_id` int NOT NULL,
  `tipo_dashboard` varchar(50) NOT NULL,
  `configuracao` json DEFAULT NULL,
  `data_criacao` datetime DEFAULT CURRENT_TIMESTAMP,
  `data_atualizacao` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `metricas` json DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dashboards_usuario` (`usuario_id`),
  KEY `idx_dashboards_tipo` (`tipo_dashboard`),
  CONSTRAINT `fk_dashboards_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dashboards`
--

LOCK TABLES `dashboards` WRITE;
/*!40000 ALTER TABLE `dashboards` DISABLE KEYS */;
INSERT INTO `dashboards` VALUES (1,1,'RH',NULL,'2025-06-16 00:56:06','2025-06-16 00:56:06',NULL),(2,9,'RH',NULL,'2025-06-16 00:56:06','2025-06-16 00:56:06',NULL),(3,2,'LIDER',NULL,'2025-06-16 00:56:06','2025-06-16 00:56:06',NULL),(4,6,'LIDER',NULL,'2025-06-16 00:56:06','2025-06-16 00:56:06',NULL),(5,3,'FUNC',NULL,'2025-06-16 00:56:06','2025-06-16 00:56:06',NULL),(6,5,'FUNC',NULL,'2025-06-16 00:56:06','2025-06-16 00:56:06',NULL);
/*!40000 ALTER TABLE `dashboards` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `feedback`
--

DROP TABLE IF EXISTS `feedback`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `feedback` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `de_usuario_id` int NOT NULL,
  `para_usuario_id` int NOT NULL,
  `assunto` varchar(70) NOT NULL,
  `descricao` text,
  `data` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_feedback_assunto` (`assunto`),
  KEY `fk_feedback_de_usuario_id` (`de_usuario_id`),
  KEY `fk_feedback_para_usuario_id` (`para_usuario_id`),
  CONSTRAINT `fk_feedback_de_usuario_id` FOREIGN KEY (`de_usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_feedback_para_usuario_id` FOREIGN KEY (`para_usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feedback`
--

LOCK TABLES `feedback` WRITE;
/*!40000 ALTER TABLE `feedback` DISABLE KEYS */;
/*!40000 ALTER TABLE `feedback` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `feedbacks`
--

DROP TABLE IF EXISTS `feedbacks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `feedbacks` (
  `id` int NOT NULL AUTO_INCREMENT,
  `anonimo` bit(1) NOT NULL,
  `ativo` bit(1) NOT NULL,
  `conteudo` text NOT NULL,
  `data_criacao` datetime(6) NOT NULL,
  `data_resposta` datetime(6) DEFAULT NULL,
  `prioridade` enum('BAIXA','MEDIA','ALTA','CRITICA') NOT NULL,
  `resposta` text,
  `status` enum('PENDENTE','EM_ANALISE','RESPONDIDO','FECHADO','REJEITADO') NOT NULL,
  `tipo` enum('SUGESTAO','RECLAMACAO','ELOGIO','BUG_REPORT','MELHORIA','DUVIDA') NOT NULL,
  `titulo` varchar(200) NOT NULL,
  `respondido_por_id` int DEFAULT NULL,
  `usuario_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrr6qx558t1nhp31rwxqdy4ifg` (`respondido_por_id`),
  KEY `FKgtgu6bncmt52y55s9ato7f4lw` (`usuario_id`),
  CONSTRAINT `FKgtgu6bncmt52y55s9ato7f4lw` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`),
  CONSTRAINT `FKrr6qx558t1nhp31rwxqdy4ifg` FOREIGN KEY (`respondido_por_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feedbacks`
--

LOCK TABLES `feedbacks` WRITE;
/*!40000 ALTER TABLE `feedbacks` DISABLE KEYS */;
/*!40000 ALTER TABLE `feedbacks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jobs`
--

DROP TABLE IF EXISTS `jobs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `jobs` (
  `id` int NOT NULL AUTO_INCREMENT,
  `ativo` bit(1) NOT NULL,
  `cron_expression` varchar(255) DEFAULT NULL,
  `data_agendamento` datetime(6) NOT NULL,
  `data_conclusao` datetime(6) DEFAULT NULL,
  `data_criacao` datetime(6) NOT NULL,
  `data_execucao` datetime(6) DEFAULT NULL,
  `descricao` text,
  `erro` text,
  `max_tentativas` int DEFAULT NULL,
  `nome` varchar(100) NOT NULL,
  `parametros` json DEFAULT NULL,
  `resultado` text,
  `status` enum('AGENDADO','EXECUTANDO','CONCLUIDO','ERRO','CANCELADO','PAUSADO') NOT NULL,
  `tentativas` int DEFAULT NULL,
  `tipo` enum('NOTIFICACAO_PRAZO','BACKUP_DADOS','LIMPEZA_LOGS','RELATORIO_AUTOMATICO','ENVIO_EMAIL','SINCRONIZACAO','MANUTENCAO') NOT NULL,
  `criado_por_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKaaem9q481p058b2lf2f1mou5k` (`criado_por_id`),
  CONSTRAINT `FKaaem9q481p058b2lf2f1mou5k` FOREIGN KEY (`criado_por_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jobs`
--

LOCK TABLES `jobs` WRITE;
/*!40000 ALTER TABLE `jobs` DISABLE KEYS */;
/*!40000 ALTER TABLE `jobs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kanban_categorias`
--

DROP TABLE IF EXISTS `kanban_categorias`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kanban_categorias` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(50) NOT NULL,
  `descricao` text,
  `cor` varchar(7) DEFAULT '#6C757D',
  `ordem` int NOT NULL DEFAULT '1',
  `usuario_id` int DEFAULT NULL,
  `time_id` int DEFAULT NULL,
  `data_criacao` datetime DEFAULT CURRENT_TIMESTAMP,
  `ativo` tinyint(1) DEFAULT '1',
  `criado_por_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_kanban_categorias_ordem` (`ordem`),
  KEY `idx_kanban_categorias_usuario` (`usuario_id`),
  KEY `idx_kanban_categorias_time` (`time_id`),
  KEY `idx_kanban_categorias_ativo` (`ativo`),
  KEY `FKp6j17lwwthy88ni657jwwa9ca` (`criado_por_id`),
  CONSTRAINT `fk_kanban_categorias_time` FOREIGN KEY (`time_id`) REFERENCES `times` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_kanban_categorias_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FKp6j17lwwthy88ni657jwwa9ca` FOREIGN KEY (`criado_por_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kanban_categorias`
--

LOCK TABLES `kanban_categorias` WRITE;
/*!40000 ALTER TABLE `kanban_categorias` DISABLE KEYS */;
INSERT INTO `kanban_categorias` VALUES (1,'Backlog','Tarefas aguardando para serem iniciadas','#6C757D',1,1,1,'2025-06-16 00:55:31',1,NULL),(2,'Em Progresso','Tarefas sendo desenvolvidas atualmente','#FFC107',2,1,1,'2025-06-16 00:55:31',1,NULL),(3,'Em Revisão','Tarefas aguardando revisão de código','#17A2B8',3,1,1,'2025-06-16 00:55:31',1,NULL),(4,'Teste','Tarefas em fase de testes','#FD7E14',4,1,1,'2025-06-16 00:55:31',1,NULL),(5,'Concluído','Tarefas finalizadas e entregues','#28A745',5,1,1,'2025-06-16 00:55:31',1,NULL),(6,'Bloqueado','Tarefas com impedimentos','#DC3545',6,1,1,'2025-06-16 00:55:31',1,NULL);
/*!40000 ALTER TABLE `kanban_categorias` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kanban_tarefas`
--

DROP TABLE IF EXISTS `kanban_tarefas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
  `horas_trabalhadas` decimal(5,2) DEFAULT '0.00',
  `ordem` int DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `idx_kanban_tarefas_categoria` (`categoria_id`),
  KEY `idx_kanban_tarefas_responsavel` (`responsavel_id`),
  KEY `idx_kanban_tarefas_criado_por` (`criado_por_id`),
  KEY `idx_kanban_tarefas_status` (`status`),
  KEY `idx_kanban_tarefas_prioridade` (`prioridade`),
  KEY `idx_kanban_tarefas_ordem` (`ordem`),
  CONSTRAINT `fk_kanban_tarefas_categoria` FOREIGN KEY (`categoria_id`) REFERENCES `kanban_categorias` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_kanban_tarefas_criado_por` FOREIGN KEY (`criado_por_id`) REFERENCES `usuarios` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_kanban_tarefas_responsavel` FOREIGN KEY (`responsavel_id`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kanban_tarefas`
--

LOCK TABLES `kanban_tarefas` WRITE;
/*!40000 ALTER TABLE `kanban_tarefas` DISABLE KEYS */;
INSERT INTO `kanban_tarefas` VALUES (1,'Refatorar Controllers','Separar lógica de negócio em services',3,3,1,'2025-06-16 00:55:36','2024-03-30',NULL,'MEDIA','ATIVA',NULL,0.00,1),(2,'Implementar Validações','Adicionar Bean Validation nos DTOs',2,4,1,'2025-06-16 00:55:36','2024-04-01',NULL,'ALTA','ATIVA',NULL,0.00,1),(3,'Criar Tela de Karaokê','Desenvolver interface para sessões de karaokê',1,5,1,'2025-06-16 00:55:36','2024-04-15',NULL,'MEDIA','ATIVA',NULL,0.00,1),(4,'Testes Unitários Services','Criar testes unitários para todos os services',1,8,2,'2025-06-16 00:55:36','2024-04-20',NULL,'BAIXA','ATIVA',NULL,0.00,1),(5,'Deploy Pipeline CI/CD','Configurar pipeline automatizado de deploy',4,6,2,'2025-06-16 00:55:36','2024-03-28',NULL,'ALTA','ATIVA',NULL,0.00,1),(6,'Monitoring e Logs','Implementar sistema de monitoramento',5,6,1,'2025-06-16 00:55:36','2024-03-20',NULL,'MEDIA','CONCLUIDA',NULL,0.00,1);
/*!40000 ALTER TABLE `kanban_tarefas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `membros_time`
--

DROP TABLE IF EXISTS `membros_time`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `membros_time` (
  `id` int NOT NULL AUTO_INCREMENT,
  `usuario_id` int NOT NULL,
  `time_id` int NOT NULL,
  `data_entrada` datetime(6) NOT NULL,
  `data_saida` datetime(6) DEFAULT NULL,
  `ativo` tinyint(1) DEFAULT '1',
  `observacoes` varchar(255) DEFAULT NULL,
  `tipo_membro` enum('LIDER','VICE_LIDER','MEMBRO','COLABORADOR') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_membros_time_usuario_time` (`usuario_id`,`time_id`),
  KEY `idx_membros_time_time` (`time_id`),
  KEY `idx_membros_time_ativo` (`ativo`),
  CONSTRAINT `fk_membros_time_time` FOREIGN KEY (`time_id`) REFERENCES `times` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_membros_time_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `membros_time`
--

LOCK TABLES `membros_time` WRITE;
/*!40000 ALTER TABLE `membros_time` DISABLE KEYS */;
INSERT INTO `membros_time` VALUES (1,4,1,'2024-01-15 00:00:00.000000',NULL,1,NULL,'LIDER'),(2,5,1,'2024-01-20 00:00:00.000000',NULL,1,NULL,'LIDER'),(3,6,1,'2024-01-25 00:00:00.000000',NULL,1,NULL,'LIDER'),(4,10,1,'2024-02-01 00:00:00.000000',NULL,1,NULL,'LIDER'),(5,8,2,'2024-01-15 00:00:00.000000',NULL,1,NULL,'LIDER'),(6,7,2,'2024-01-22 00:00:00.000000',NULL,1,NULL,'LIDER'),(7,9,2,'2024-01-30 00:00:00.000000',NULL,1,NULL,'LIDER'),(8,10,3,'2024-02-01 00:00:00.000000',NULL,1,NULL,'LIDER'),(9,9,3,'2024-02-05 00:00:00.000000',NULL,1,NULL,'LIDER'),(10,1,4,'2024-01-10 00:00:00.000000',NULL,1,NULL,'LIDER'),(11,9,4,'2024-01-15 00:00:00.000000',NULL,1,NULL,'LIDER'),(12,8,5,'2024-01-20 00:00:00.000000',NULL,1,NULL,'LIDER'),(13,10,5,'2024-02-10 00:00:00.000000',NULL,1,NULL,'LIDER');
/*!40000 ALTER TABLE `membros_time` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `musicas`
--

DROP TABLE IF EXISTS `musicas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `musicas` (
  `id` int NOT NULL AUTO_INCREMENT,
  `titulo` varchar(150) NOT NULL,
  `artista` varchar(100) NOT NULL,
  `genero` varchar(50) DEFAULT NULL,
  `idioma` varchar(50) DEFAULT NULL,
  `duracao` time DEFAULT NULL,
  `link_video` varchar(500) DEFAULT NULL,
  `data_cadastro` datetime DEFAULT CURRENT_TIMESTAMP,
  `ativo` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `idx_musicas_artista` (`artista`),
  KEY `idx_musicas_genero` (`genero`),
  KEY `idx_musicas_idioma` (`idioma`),
  KEY `idx_musicas_ativo` (`ativo`),
  FULLTEXT KEY `ft_musicas_titulo_artista` (`titulo`,`artista`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `musicas`
--

LOCK TABLES `musicas` WRITE;
/*!40000 ALTER TABLE `musicas` DISABLE KEYS */;
INSERT INTO `musicas` VALUES (1,'Bohemian Rhapsody','Queen','Rock','Inglês','05:55:00','https://www.youtube.com/watch?v=fJ9rUzIMcZQ','2025-06-16 00:55:39',1),(2,'Imagine','John Lennon','Pop Rock','Inglês','03:01:00','https://www.youtube.com/watch?v=YkgkThdzX-8','2025-06-16 00:55:39',1),(3,'Garota de Ipanema','Tom Jobim','Bossa Nova','Português','02:56:00','https://www.youtube.com/watch?v=UJkxFhFRFDA','2025-06-16 00:55:39',1),(4,'Hotel California','Eagles','Rock','Inglês','06:31:00','https://www.youtube.com/watch?v=09839DpTctU','2025-06-16 00:55:39',1),(5,'Águas de Março','Elis Regina','MPB','Português','03:15:00','https://www.youtube.com/watch?v=xRqI5R6L7ow','2025-06-16 00:55:39',1),(6,'Don\'t Stop Believin\'','Journey','Rock','Inglês','04:10:00','https://www.youtube.com/watch?v=1k8craCGpgs','2025-06-16 00:55:39',1),(7,'Mas que Nada','Jorge Ben Jor','Samba','Português','02:35:00','https://www.youtube.com/watch?v=YQQ2StSwg5s','2025-06-16 00:55:39',1),(8,'Sweet Child O\' Mine','Guns N\' Roses','Hard Rock','Inglês','05:03:00','https://www.youtube.com/watch?v=1w7OgIMMRc4','2025-06-16 00:55:39',1),(9,'Corcovado','Tom Jobim','Bossa Nova','Português','03:28:00','https://www.youtube.com/watch?v=Lz-yk9jCYKs','2025-06-16 00:55:39',1),(10,'Don\'t Stop Me Now','Queen','Rock','Inglês','03:29:00','https://www.youtube.com/watch?v=HgzGwKwLmgM','2025-06-16 00:55:39',1);
/*!40000 ALTER TABLE `musicas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `musicas_cantadas`
--

DROP TABLE IF EXISTS `musicas_cantadas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `musicas_cantadas` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `usuario_id` int NOT NULL,
  `musica_id` int NOT NULL,
  `sessao_id` int NOT NULL,
  `ordem_cantada` int NOT NULL,
  `nota` decimal(3,2) DEFAULT NULL,
  `comentarios` text,
  `data_apresentacao` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_musicas_cantadas_sessao_ordem` (`sessao_id`,`ordem_cantada`),
  KEY `idx_musicas_cantadas_usuario` (`usuario_id`),
  KEY `idx_musicas_cantadas_musica` (`musica_id`),
  KEY `idx_musicas_cantadas_nota` (`nota`),
  CONSTRAINT `fk_musicas_cantadas_musica` FOREIGN KEY (`musica_id`) REFERENCES `musicas` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_musicas_cantadas_sessao` FOREIGN KEY (`sessao_id`) REFERENCES `sessao_karaoke` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_musicas_cantadas_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE,
  CONSTRAINT `chk_musicas_cantadas_nota` CHECK (((`nota` >= 0) and (`nota` <= 10)))
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `musicas_cantadas`
--

LOCK TABLES `musicas_cantadas` WRITE;
/*!40000 ALTER TABLE `musicas_cantadas` DISABLE KEYS */;
INSERT INTO `musicas_cantadas` VALUES (1,3,1,1,1,9.20,'Performance incrível! Emocionou a todos.','2025-06-16 00:55:48'),(2,5,3,1,2,8.50,'Voz suave, perfeito para bossa nova.','2025-06-16 00:55:48'),(3,7,6,1,3,7.80,'Energia contagiante!','2025-06-16 00:55:48'),(4,4,2,1,4,8.00,'Interpretação muito emotiva.','2025-06-16 00:55:48'),(5,2,4,2,1,8.70,'Solo de guitarra no vocal foi genial!','2025-06-16 00:55:48'),(6,8,5,2,2,9.00,'Conhece bem MPB, excelente escolha.','2025-06-16 00:55:48'),(7,6,8,2,3,8.30,'Rock pesado, energia total!','2025-06-16 00:55:48'),(8,8,10,3,1,9.50,'Freddie Mercury brasileiro! Sensacional!','2025-06-16 00:55:48'),(9,3,8,3,2,8.10,'Quebrou tudo no rock!','2025-06-16 00:55:48'),(10,7,6,3,3,8.80,'Journey sempre emociona!','2025-06-16 00:55:48');
/*!40000 ALTER TABLE `musicas_cantadas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notificacoes`
--

DROP TABLE IF EXISTS `notificacoes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notificacoes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `confirmacao_leitura` bit(1) NOT NULL,
  `data_criacao` datetime(6) NOT NULL,
  `texto_notificacao` text NOT NULL,
  `tipo_notificacao` enum('TAREFA_ATRIBUIDA','PRAZO_CONCLUSAO','COMENTARIO','TAREFA_ATRASADA','ATUALIZACAO_SOLICITADA','TAREFA_CONCLUIDA','TAREFA_ACEITA','TAREFA_REJEITADA') NOT NULL,
  `tarefa_id` bigint DEFAULT NULL,
  `usuario_notificado_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKlx8vk1lb6poh1bel1w480buyv` (`tarefa_id`),
  KEY `FKj8u7wfhox7wfperkglvqwb5r8` (`usuario_notificado_id`),
  CONSTRAINT `FKj8u7wfhox7wfperkglvqwb5r8` FOREIGN KEY (`usuario_notificado_id`) REFERENCES `usuarios` (`id`),
  CONSTRAINT `FKlx8vk1lb6poh1bel1w480buyv` FOREIGN KEY (`tarefa_id`) REFERENCES `tarefas` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notificacoes`
--

LOCK TABLES `notificacoes` WRITE;
/*!40000 ALTER TABLE `notificacoes` DISABLE KEYS */;
/*!40000 ALTER TABLE `notificacoes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `participantes_tarefa`
--

DROP TABLE IF EXISTS `participantes_tarefa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `participantes_tarefa` (
  `id` int NOT NULL AUTO_INCREMENT,
  `ativo` bit(1) NOT NULL,
  `data_convite` datetime(6) NOT NULL,
  `data_resposta` datetime(6) DEFAULT NULL,
  `motivo_rejeicao` text,
  `status_participacao` enum('PENDENTE','ACEITO','REJEITADO','REMOVIDO') NOT NULL,
  `tarefa_id` bigint NOT NULL,
  `usuario_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKaw0m3l15kebswb383hh43blde` (`tarefa_id`),
  KEY `FKshyjkxivout8hq7015a4q223t` (`usuario_id`),
  CONSTRAINT `FKaw0m3l15kebswb383hh43blde` FOREIGN KEY (`tarefa_id`) REFERENCES `tarefas` (`id`),
  CONSTRAINT `FKshyjkxivout8hq7015a4q223t` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `participantes_tarefa`
--

LOCK TABLES `participantes_tarefa` WRITE;
/*!40000 ALTER TABLE `participantes_tarefa` DISABLE KEYS */;
/*!40000 ALTER TABLE `participantes_tarefa` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `relatorios`
--

DROP TABLE IF EXISTS `relatorios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `relatorios`
--

LOCK TABLES `relatorios` WRITE;
/*!40000 ALTER TABLE `relatorios` DISABLE KEYS */;
INSERT INTO `relatorios` VALUES (1,'Relatório Mensal - Backend','MENSAL','2024-02-01','2024-02-29',1,1,'2025-06-16 00:56:09','Relatório mensal do time de Backend: 85% das tarefas concluídas no prazo.','HTML','CONCLUIDO'),(2,'Sprint 12 - Backend','SEMANAL','2024-03-01','2024-03-07',2,1,'2025-06-16 00:56:09','Sprint 12 finalizada com sucesso. Todas as user stories entregues.','HTML','CONCLUIDO'),(3,'Relatório RH Mensal','MENSAL','2024-02-01','2024-02-29',9,4,'2025-06-16 00:56:09','Relatório RH: Taxa de satisfação dos funcionários em 92%.','HTML','CONCLUIDO'),(4,'Relatório Trimestral Geral','TRIMESTRAL','2024-01-01','2024-03-31',1,NULL,'2025-06-16 00:56:09','Relatório trimestral geral: Produtividade aumentou 15% no período.','HTML','CONCLUIDO'),(5,'Sprint Frontend','SEMANAL','2024-03-01','2024-03-07',8,2,'2025-06-16 00:56:09','Frontend: Nova interface entregue e aprovada pelos usuários.','HTML','CONCLUIDO'),(6,'Relatório DevOps','MENSAL','2024-02-01','2024-02-29',8,5,'2025-06-16 00:56:09','DevOps: 99.9% de uptime em produção. Zero incidentes críticos.','HTML','CONCLUIDO');
/*!40000 ALTER TABLE `relatorios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rooms`
--

DROP TABLE IF EXISTS `rooms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rooms` (
  `room_id` varchar(20) NOT NULL,
  `room_name` text NOT NULL,
  `host_id` int NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`room_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rooms`
--

LOCK TABLES `rooms` WRITE;
/*!40000 ALTER TABLE `rooms` DISABLE KEYS */;
INSERT INTO `rooms` VALUES ('room_1750897769807','Daily',1,'2025-06-26 00:29:30');
/*!40000 ALTER TABLE `rooms` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sessao_karaoke`
--

DROP TABLE IF EXISTS `sessao_karaoke`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sessao_karaoke`
--

LOCK TABLES `sessao_karaoke` WRITE;
/*!40000 ALTER TABLE `sessao_karaoke` DISABLE KEYS */;
INSERT INTO `sessao_karaoke` VALUES (1,'Happy Hour Karaokê','2024-03-15 19:00:00','Sala de Reuniões A','Sessão descontraída de karaokê após o expediente',1,NULL,'FINALIZADA','2025-06-16 00:55:41'),(2,'Karaokê dos Clássicos','2024-03-22 18:30:00','Auditório Principal','Noite dedicada aos clássicos da música',9,NULL,'FINALIZADA','2025-06-16 00:55:41'),(3,'Rock Night','2024-03-29 19:00:00','Sala de Descompressão','Especial Rock com as melhores bandas',1,NULL,'FINALIZADA','2025-06-16 00:55:41'),(4,'MPB e Bossa Nova','2024-04-05 18:00:00','Terraço','Tarde especial com música brasileira',9,NULL,'FINALIZADA','2025-06-16 00:55:41'),(5,'Internacional Hits','2024-04-12 19:30:00','Auditório Principal','Os maiores sucessos internacionais',1,NULL,'AGENDADA','2025-06-16 00:55:41');
/*!40000 ALTER TABLE `sessao_karaoke` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tarefas`
--

DROP TABLE IF EXISTS `tarefas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tarefas` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `titulo` varchar(255) NOT NULL,
  `descricao` text,
  `status` enum('TODO','DOING','DONE') DEFAULT 'TODO',
  `prioridade` enum('BAIXA','MEDIA','ALTA') DEFAULT 'MEDIA',
  `responsavel_id` int DEFAULT NULL,
  `criado_por_id` int NOT NULL,
  `data_criacao` datetime DEFAULT CURRENT_TIMESTAMP,
  `data_entrega` date DEFAULT NULL,
  `data_conclusao` datetime DEFAULT NULL,
  `estimativa_horas` decimal(5,2) DEFAULT NULL,
  `horas_trabalhadas` decimal(5,2) DEFAULT '0.00',
  `data_ultima_atualizacao` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_tarefas_status` (`status`),
  KEY `idx_tarefas_prioridade` (`prioridade`),
  KEY `idx_tarefas_responsavel` (`responsavel_id`),
  KEY `idx_tarefas_criado_por` (`criado_por_id`),
  KEY `idx_tarefas_data_entrega` (`data_entrega`),
  CONSTRAINT `fk_tarefas_criado_por` FOREIGN KEY (`criado_por_id`) REFERENCES `usuarios` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_tarefas_responsavel` FOREIGN KEY (`responsavel_id`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tarefas`
--

LOCK TABLES `tarefas` WRITE;
/*!40000 ALTER TABLE `tarefas` DISABLE KEYS */;
INSERT INTO `tarefas` VALUES (1,'Implementar Sistema de Login','Desenvolver sistema de autenticação com JWT','DOING','ALTA',5,1,'2025-06-16 00:55:34','2024-04-15',NULL,NULL,0.00,NULL),(2,'Criar Dashboard Principal','Desenvolver dashboard com métricas principais','TODO','MEDIA',7,1,'2025-06-16 00:55:34','2024-04-20',NULL,NULL,0.00,NULL),(3,'Testes de Integração API','Implementar testes automatizados para todas as APIs','DOING','ALTA',10,4,'2025-06-16 00:55:34','2024-04-10',NULL,NULL,0.00,NULL),(4,'Documentação da API','Criar documentação completa usando Swagger','TODO','BAIXA',6,4,'2025-06-16 00:55:34','2024-04-25',NULL,NULL,0.00,NULL),(5,'Setup Ambiente Produção','Configurar ambiente de produção na AWS','DONE','ALTA',8,1,'2025-06-16 00:55:34','2024-04-05',NULL,NULL,0.00,NULL);
/*!40000 ALTER TABLE `tarefas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `times`
--

DROP TABLE IF EXISTS `times`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `times` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nome_time` varchar(100) NOT NULL,
  `descricao` text,
  `lider_id` int DEFAULT NULL,
  `data_criacao` datetime DEFAULT CURRENT_TIMESTAMP,
  `data_atualizacao` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `ativo` tinyint(1) DEFAULT '1',
  `cor` varchar(7) DEFAULT NULL,
  `data_ultima_atualizacao` datetime(6) DEFAULT NULL,
  `criado_por_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_times_nome` (`nome_time`),
  KEY `idx_times_lider` (`lider_id`),
  KEY `idx_times_ativo` (`ativo`),
  CONSTRAINT `fk_times_lider` FOREIGN KEY (`lider_id`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `times`
--

LOCK TABLES `times` WRITE;
/*!40000 ALTER TABLE `times` DISABLE KEYS */;
INSERT INTO `times` VALUES (1,'Desenvolvimento Backend',NULL,4,'2025-06-16 00:54:54','2025-06-16 00:54:54',1,NULL,NULL,0),(2,'Desenvolvimento Frontend',NULL,8,'2025-06-16 00:54:54','2025-06-16 00:54:54',1,NULL,NULL,0),(3,'Quality Assurance',NULL,4,'2025-06-16 00:54:54','2025-06-16 00:54:54',1,NULL,NULL,0),(4,'Recursos Humanos',NULL,1,'2025-06-16 00:54:54','2025-06-16 00:54:54',1,NULL,NULL,0),(5,'DevOps',NULL,8,'2025-06-16 00:54:54','2025-06-16 00:54:54',1,NULL,NULL,0);
/*!40000 ALTER TABLE `times` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `senha` varchar(255) NOT NULL,
  `tipo_usuario` enum('FUNC','LIDER','RH') NOT NULL,
  `data_criacao` date DEFAULT NULL,
  `data_atualizacao` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `ativo` tinyint(1) DEFAULT '1',
  `descricao` varchar(255) DEFAULT NULL,
  `imagem` varchar(350) DEFAULT NULL,
  `data_ultima_atualizacao` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_usuarios_email` (`email`),
  KEY `idx_usuarios_tipo` (`tipo_usuario`),
  KEY `idx_usuarios_ativo` (`ativo`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (1,'Raquel','raquel.moreira@gmail.com','12345678','RH','2025-06-14','2025-07-02 03:14:22',1,'awn',NULL,NULL),(2,'Carlos Mendes','carlos.mendes@bancoalcatteia.com','senha123','LIDER','2025-06-16','2025-06-16 00:54:51',1,NULL,NULL,NULL),(3,'Fernanda Costa','fernanda.costa@bancoalcatteia.com','senha123','FUNC','2025-06-16','2025-06-30 01:04:18',1,'fbhfbhfb',NULL,NULL),(4,'João Santos','joao.santos@bancoalcatteia.com','senha123','FUNC','2025-06-16','2025-06-16 00:54:51',1,NULL,NULL,NULL),(5,'Maria Oliveira','maria.oliveira@bancoalcatteia.com','senha123','FUNC','2025-06-16','2025-06-16 00:54:51',1,NULL,NULL,NULL),(6,'Pedro Lima','pedro.lima@bancoalcatteia.com','senha123','LIDER','2025-06-16','2025-06-16 00:54:51',1,NULL,NULL,NULL),(7,'Juliana Rocha','juliana.rocha@bancoalcatteia.com','senha123','FUNC','2025-06-16','2025-06-16 00:54:51',1,NULL,NULL,NULL),(8,'Roberto Alves','roberto.alves@bancoalcatteia.com','senha123','FUNC','2025-06-16','2025-06-16 00:54:51',1,NULL,NULL,NULL),(9,'Patrícia Nunes','patricia.nunes@bancoalcatteia.com','senha123','RH','2025-06-16','2025-06-16 00:54:51',1,NULL,NULL,NULL),(10,'Lucas Ferreira','lucas.ferreira@bancoalcatteia.com','senha123','FUNC','2025-06-16','2025-06-16 00:54:51',1,NULL,NULL,NULL),(11,'Fulano da Silva','fulano.silva@gmail.com','123456','RH','2025-06-21','2025-07-01 03:10:04',1,'eeeeeeeeeeeeeeeeeeeee',NULL,NULL),(12,'Karina da Silva','karina.silva@gmail.com','123456','LIDER','2025-06-22','2025-06-22 16:25:54',1,NULL,NULL,NULL),(13,'Talita Vitoria','talitavitoria@gmail.com','123456','LIDER','2025-06-25','2025-06-25 16:33:30',1,NULL,NULL,NULL),(14,'Alencar','alencar@gmail.com','123456','FUNC','2025-06-25','2025-06-25 16:39:39',1,NULL,NULL,NULL),(15,'Rita','rita@gmail.com','senha123','LIDER','2025-06-27','2025-06-27 01:09:52',1,NULL,NULL,NULL),(16,'Cabral','cabral@gmail.com','123456','LIDER','2025-06-29','2025-06-30 01:59:11',1,'oier',NULL,NULL),(17,'Felipe','felipe@gmail.com','123456','LIDER','2025-06-29','2025-07-01 03:15:48',1,'cabuloso',NULL,NULL),(18,'Heverton','heverton@gmail.com','senha123','RH','2025-06-30','2025-07-01 01:09:17',1,NULL,NULL,NULL),(19,'Alice ','alice@gmail.com','123456','FUNC','2025-06-30','2025-07-01 03:17:06',1,'oie',NULL,NULL),(20,'Cleber','cleber@gmail.com','12345','FUNC','2025-06-30','2025-07-01 03:03:24',1,'djknvkjdvnjkd',NULL,NULL),(21,'Jailson','jaja@gmail.com','123456','RH','2025-07-01','2025-07-01 03:08:11',1,'Olá eu sou o Jailson',NULL,NULL),(22,'Maria','maria@gmail.com','123','FUNC','2025-07-01','2025-07-01 03:20:59',1,'leeeeeeeeeeee',NULL,NULL),(23,'Ester','ester@gmail.com','senha123','LIDER','2025-07-01','2025-07-01 03:27:59',1,NULL,NULL,NULL),(24,'Rafaela','rafa@gmail.com','123456','LIDER','2025-07-01','2025-07-01 03:30:57',1,NULL,NULL,NULL);
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'railway'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-07-03 23:03:07
