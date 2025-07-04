-- ==================================================
-- BANCO ALCATTEIA - PARTE 2 - DEMAIS TABELAS
-- ==================================================

-- Tabela: kanban_tarefas (MANTENDO INT)
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

-- Tabela: musicas (MANTENDO INT)
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

-- Tabela: sessao_karaoke (MANTENDO INT)
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

-- Tabela: musicas_cantadas (MANTENDO INT)
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

-- Tabela: avaliacoes (MANTENDO INT)
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

-- Tabela: dashboards (MANTENDO INT)
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

-- Tabela: relatorios (MANTENDO INT)
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

-- Tabela: feedbacks (NOVA ESTRUTURA - prevalece sobre feedback)
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

-- Tabela: jobs (MANTENDO INT)
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

-- ==================================================
-- DADOS COMPLEMENTARES
-- ==================================================

-- Inserir categorias kanban
INSERT INTO kanban_categorias (nome, descricao, cor, ordem, usuario_id, time_id, criado_por_id) VALUES
('Backlog', 'Tarefas aguardando para serem iniciadas', '#6C757D', 1, 1, 1, 1),
('Em Progresso', 'Tarefas sendo desenvolvidas atualmente', '#FFC107', 2, 1, 1, 1),
('Em Revisão', 'Tarefas aguardando revisão de código', '#17A2B8', 3, 1, 1, 1),
('Teste', 'Tarefas em fase de testes', '#FD7E14', 4, 1, 1, 1),
('Concluído', 'Tarefas finalizadas e entregues', '#28A745', 5, 1, 1, 1),
('Bloqueado', 'Tarefas com impedimentos', '#DC3545', 6, 1, 1, 1);

-- Inserir tarefas
INSERT INTO tarefas (titulo, descricao, data_criacao, data_entrega, status, prioridade, responsavel_id, criado_por_id) VALUES
('Implementar Sistema de Login', 'Desenvolver sistema de autenticação com JWT', NOW(), '2024-04-15', 'DOING', 'ALTA', 3, 2),
('Criar Dashboard Principal', 'Desenvolver dashboard com métricas principais', NOW(), '2024-04-20', 'TODO', 'MEDIA', 5, 2),
('Testes de Integração API', 'Implementar testes automatizados para todas as APIs', NOW(), '2024-04-10', 'DOING', 'ALTA', 8, 2),
('Documentação da API', 'Criar documentação completa usando Swagger', NOW(), '2024-04-25', 'TODO', 'BAIXA', 4, 2),
('Setup Ambiente Produção', 'Configurar ambiente de produção na AWS', NOW(), '2024-04-05', 'DONE', 'ALTA', 6, 1);

-- Inserir participantes das tarefas
INSERT INTO participantes_tarefa (tarefa_id, usuario_id, status_participacao, data_convite, data_resposta) VALUES
(1, 3, 'ACEITO', NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 2 DAY),
(1, 4, 'ACEITO', NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 1 DAY),
(2, 5, 'PENDENTE', NOW() - INTERVAL 1 DAY, NULL),
(2, 7, 'PENDENTE', NOW() - INTERVAL 1 DAY, NULL),
(3, 8, 'ACEITO', NOW() - INTERVAL 3 DAY, NOW() - INTERVAL 3 DAY),
(5, 6, 'ACEITO', NOW() - INTERVAL 10 DAY, NOW() - INTERVAL 10 DAY),
(5, 10, 'ACEITO', NOW() - INTERVAL 10 DAY, NOW() - INTERVAL 9 DAY);

-- Inserir tarefas kanban
INSERT INTO kanban_tarefas (titulo, descricao, categoria_id, responsavel_id, criado_por_id, data_criacao, data_entrega, prioridade, status) VALUES
('Refatorar Controllers', 'Separar lógica de negócio em services', 3, 3, 2, NOW(), '2024-03-30', 'MEDIA', 'ATIVA'),
('Implementar Validações', 'Adicionar Bean Validation nos DTOs', 2, 4, 2, NOW(), '2024-04-01', 'ALTA', 'ATIVA'),
('Criar Tela de Karaokê', 'Desenvolver interface para sessões de karaokê', 1, 5, 2, NOW(), '2024-04-15', 'MEDIA', 'ATIVA'),
('Testes Unitários Services', 'Criar testes unitários para todos os services', 1, 8, 2, NOW(), '2024-04-20', 'BAIXA', 'ATIVA'),
('Deploy Pipeline CI/CD', 'Configurar pipeline automatizado de deploy', 4, 6, 6, NOW(), '2024-03-28', 'ALTA', 'ATIVA'),
('Monitoring e Logs', 'Implementar sistema de monitoramento', 5, 6, 1, NOW(), '2024-03-20', 'MEDIA', 'CONCLUIDA');

-- Inserir músicas
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

-- Inserir sessões de karaokê
INSERT INTO sessao_karaoke (titulo, data, local, organizador_id, descricao, status) VALUES
('Happy Hour Karaokê', '2024-03-15 19:00:00', 'Sala de Reuniões A', 1, 'Sessão descontraída de karaokê após o expediente', 'FINALIZADA'),
('Karaokê dos Clássicos', '2024-03-22 18:30:00', 'Auditório Principal', 9, 'Noite dedicada aos clássicos da música', 'FINALIZADA'),
('Rock Night', '2024-03-29 19:00:00', 'Sala de Descompressão', 1, 'Especial Rock com as melhores bandas', 'FINALIZADA'),
('MPB e Bossa Nova', '2024-04-05 18:00:00', 'Terraço', 9, 'Tarde especial com música brasileira', 'FINALIZADA'),
('Internacional Hits', '2024-04-12 19:30:00', 'Auditório Principal', 1, 'Os maiores sucessos internacionais', 'AGENDADA');

-- Inserir músicas cantadas
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

-- Inserir avaliações
INSERT INTO avaliacoes (avaliador_id, apresentacao_id, nota, comentario, data_avaliacao) VALUES
(1, 1, 5, 'Performance histórica! Melhor Bohemian Rhapsody que já ouvi no karaokê.', NOW()),
(2, 2, 5, 'Fernanda tem voz perfeita para bossa nova.', NOW()),
(9, 3, 4, 'Juliana sempre traz energia positiva para o ambiente.', NOW()),
(1, 4, 5, 'João conseguiu emocionar com Imagine, muito sensível.', NOW()),
(6, 5, 5, 'Carlos mostrou que entende de rock clássico.', NOW()),
(9, 6, 5, 'Roberto surpreendeu com conhecimento de MPB.', NOW()),
(2, 7, 4, 'Pedro mandou muito bem no Guns N\' Roses.', NOW()),
(1, 8, 5, 'Lucas roubou a cena com Don\'t Stop Me Now!', NOW());

-- Inserir dashboards
INSERT INTO dashboards (usuario_id, tipo_dashboard, metricas) VALUES
(1, 'RH', '{"totalFuncionarios": 10, "checkHumorsHoje": 8, "satisfacaoMedia": 4.2}'),
(9, 'RH', '{"totalFuncionarios": 10, "checkHumorsHoje": 8, "satisfacaoMedia": 4.2}'),
(2, 'LIDER', '{"tarefasTime": 6, "tarefasConcluidas": 1, "produtividadeTime": 85}'),
(6, 'LIDER', '{"tarefasTime": 5, "tarefasConcluidas": 1, "produtividadeTime": 90}'),
(3, 'FUNC', '{"minhasTarefas": 2, "tarefasConcluidas": 0, "horasTrabalhadas": 32}'),
(5, 'FUNC', '{"minhasTarefas": 2, "tarefasConcluidas": 0, "horasTrabalhadas": 28}');

-- Inserir relatórios
INSERT INTO relatorios (titulo, tipo, periodo_inicio, periodo_fim, gerado_por_id, time_id, data_geracao, conteudo) VALUES
('Relatório Mensal - Backend', 'MENSAL', '2024-02-01', '2024-02-29', 1, 1, NOW(), 'Relatório mensal do time de Backend: 85% das tarefas concluídas no prazo.'),
('Sprint 12 - Backend', 'SEMANAL', '2024-03-01', '2024-03-07', 2, 1, NOW(), 'Sprint 12 finalizada com sucesso. Todas as user stories entregues.'),
('Relatório RH Mensal', 'MENSAL', '2024-02-01', '2024-02-29', 9, 4, NOW(), 'Relatório RH: Taxa de satisfação dos funcionários em 92%.'),
('Relatório Trimestral Geral', 'TRIMESTRAL', '2024-01-01', '2024-03-31', 1, NULL, NOW(), 'Relatório trimestral geral: Produtividade aumentou 15% no período.'),
('Sprint Frontend', 'SEMANAL', '2024-03-01', '2024-03-07', 6, 2, NOW(), 'Frontend: Nova interface entregue e aprovada pelos usuários.'),
('Relatório DevOps', 'MENSAL', '2024-02-01', '2024-02-29', 6, 5, NOW(), 'DevOps: 99.9% de uptime em produção. Zero incidentes críticos.');

-- Inserir feedbacks
INSERT INTO feedbacks (usuario_id, tipo, titulo, conteudo, status, prioridade, anonimo, data_criacao, data_resposta, respondido_por_id, resposta) VALUES
(3, 'SUGESTAO', 'Melhorar interface do dashboard', 'Seria interessante ter widgets mais interativos no dashboard principal.', 'RESPONDIDO', 'MEDIA', false, NOW() - INTERVAL 5 DAY, NOW() - INTERVAL 2 DAY, 1, 'Obrigado pela sugestão! Estamos planejando uma atualização da interface para o próximo trimestre.'),
(5, 'ELOGIO', 'Excelente sistema de karaokê', 'O sistema de karaokê trouxe muito mais descontração para a equipe. Parabéns!', 'FECHADO', 'BAIXA', false, NOW() - INTERVAL 7 DAY, NOW() - INTERVAL 5 DAY, 9, 'Ficamos muito felizes com seu feedback! O objetivo era exatamente esse.'),
(8, 'BUG_REPORT', 'Erro ao salvar música no karaokê', 'Quando tento salvar uma música com caracteres especiais, o sistema apresenta erro.', 'EM_ANALISE', 'ALTA', false, NOW() - INTERVAL 3 DAY, NULL, NULL, NULL),
(4, 'MELHORIA', 'Notificações por email', 'Seria útil receber notificações de tarefas atribuídas por email também.', 'PENDENTE', 'MEDIA', false, NOW() - INTERVAL 1 DAY, NULL, NULL, NULL);

-- Inserir jobs
INSERT INTO jobs (nome, descricao, tipo, status, data_agendamento, data_execucao, data_conclusao, criado_por_id, cron_expression, parametros, resultado, erro, tentativas, max_tentativas) VALUES
('Notificar Prazos Tarefas', 'Verifica tarefas próximas ao vencimento e envia notificações', 'NOTIFICACAO_PRAZO', 'CONCLUIDO', NOW() - INTERVAL 1 HOUR, NOW() - INTERVAL 1 HOUR, NOW() - INTERVAL 55 MINUTE, 1, '0 9 * * *', '{"diasAntecedencia": 2}', '3 tarefas próximas ao vencimento encontradas. Notificações enviadas.', NULL, 1, 3),
('Backup Banco de Dados', 'Realiza backup completo do banco de dados', 'BACKUP_DADOS', 'CONCLUIDO', NOW() - INTERVAL 12 HOUR, NOW() - INTERVAL 12 HOUR, NOW() - INTERVAL 11 HOUR, 1, '0 2 * * *', '{"compressao": true, "local": "s3://backups/"}', 'Backup realizado com sucesso. Tamanho: 1.2GB', NULL, 1, 3),
('Relatório Semanal RH', 'Gera relatório semanal de humor da equipe', 'RELATORIO_AUTOMATICO', 'AGENDADO', NOW() + INTERVAL 2 DAY, NULL, NULL, 9, '0 8 * * 1', '{"tipo": "humor", "formato": "PDF"}', NULL, NULL, 0, 3); 