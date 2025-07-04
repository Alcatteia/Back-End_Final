-- Dados iniciais para o Banco Alcatteia
-- Este arquivo é executado automaticamente pelo Spring Boot

-- Usuários (senha: 123456 em BCrypt)
INSERT IGNORE INTO usuarios (id, nome, email, senha, tipo_usuario, data_nascimento, descricao, ativo) VALUES
(1, 'Ana Silva', 'ana.silva@bancoalcatteia.com', '$2a$10$Xl0yhvzLIaJCDdKBS5/l4eFt.PsLUj7QqJK5IhZGXl1FzUvLSQfK6', 'RH', '1985-03-15', 'Gestora de RH especializada em bem-estar organizacional', true),
(2, 'Carlos Mendes', 'carlos.mendes@bancoalcatteia.com', '$2a$10$Xl0yhvzLIaJCDdKBS5/l4eFt.PsLUj7QqJK5IhZGXl1FzUvLSQfK6', 'LIDER', '1988-07-22', 'Líder técnico com experiência em arquitetura de software', true),
(3, 'Fernanda Costa', 'fernanda.costa@bancoalcatteia.com', '$2a$10$Xl0yhvzLIaJCDdKBS5/l4eFt.PsLUj7QqJK5IhZGXl1FzUvLSQfK6', 'FUNC', '1992-11-10', 'Desenvolvedora Full Stack com foco em React e Node.js', true),
(4, 'João Santos', 'joao.santos@bancoalcatteia.com', '$2a$10$Xl0yhvzLIaJCDdKBS5/l4eFt.PsLUj7QqJK5IhZGXl1FzUvLSQfK6', 'FUNC', '1990-05-18', 'Desenvolvedor Backend especialista em APIs REST', true),
(5, 'Maria Oliveira', 'maria.oliveira@bancoalcatteia.com', '$2a$10$Xl0yhvzLIaJCDdKBS5/l4eFt.PsLUj7QqJK5IhZGXl1FzUvLSQfK6', 'FUNC', '1995-09-03', 'Desenvolvedora Frontend com expertise em UX/UI', true),
(6, 'Pedro Lima', 'pedro.lima@bancoalcatteia.com', '$2a$10$Xl0yhvzLIaJCDdKBS5/l4eFt.PsLUj7QqJK5IhZGXl1FzUvLSQfK6', 'LIDER', '1987-12-25', 'Líder técnico e especialista em DevOps', true),
(7, 'Juliana Rocha', 'juliana.rocha@bancoalcatteia.com', '$2a$10$Xl0yhvzLIaJCDdKBS5/l4eFt.PsLUj7QqJK5IhZGXl1FzUvLSQfK6', 'FUNC', '1993-01-14', 'Desenvolvedora Frontend e Designer UX/UI', true),
(8, 'Roberto Alves', 'roberto.alves@bancoalcatteia.com', '$2a$10$Xl0yhvzLIaJCDdKBS5/l4eFt.PsLUj7QqJK5IhZGXl1FzUvLSQfK6', 'FUNC', '1989-04-30', 'Analista de Qualidade e Testes Automatizados', true),
(9, 'Patrícia Nunes', 'patricia.nunes@bancoalcatteia.com', '$2a$10$Xl0yhvzLIaJCDdKBS5/l4eFt.PsLUj7QqJK5IhZGXl1FzUvLSQfK6', 'RH', '1991-08-12', 'Analista de RH e Desenvolvimento Organizacional', true),
(10, 'Lucas Ferreira', 'lucas.ferreira@bancoalcatteia.com', '$2a$10$Xl0yhvzLIaJCDdKBS5/l4eFt.PsLUj7QqJK5IhZGXl1FzUvLSQfK6', 'FUNC', '1994-06-07', 'Desenvolvedor Full Stack e Especialista DevOps', true);

-- Times
INSERT IGNORE INTO times (id, nome_time, lider_id, criado_por_id, descricao, cor, ativo) VALUES
(1, 'Desenvolvimento Backend', 2, 1, 'Time responsável pelo desenvolvimento das APIs e lógica de negócio', '#007bff', true),
(2, 'Desenvolvimento Frontend', 6, 1, 'Time responsável pelo desenvolvimento das interfaces e experiência do usuário', '#28a745', true),
(3, 'Quality Assurance', 8, 1, 'Time responsável pela qualidade e testes do sistema', '#ffc107', true),
(4, 'Recursos Humanos', 1, 1, 'Time responsável pela gestão de pessoas e bem-estar', '#dc3545', true),
(5, 'DevOps', 6, 1, 'Time responsável pela infraestrutura e deploy', '#6f42c1', true);

-- Check Humor (dados dos últimos dias)
INSERT IGNORE INTO check_humor (usuario_id, humor, observacao, anonimo, bem_estar_pontos, confirmado, data_registro) VALUES
(1, 'FELIZ', 'Equipe engajada e motivada', false, 9, true, CURDATE()),
(2, 'ANIMADO', 'Sprint no prazo, equipe colaborando bem', false, 8, true, CURDATE()),
(3, 'FELIZ', 'Projeto aprovado pelo cliente! Equipe está motivada', false, 9, true, CURDATE()),
(4, 'ANIMADO', 'Todos os testes passaram, código funcionando perfeitamente', false, 8, true, CURDATE()),
(5, 'ANIMADO', 'Interface do dashboard finalizada com sucesso', false, 8, true, CURDATE()),
(6, 'ANIMADO', 'Deploy realizado com sucesso, produção estável', false, 8, true, CURDATE()),
(7, 'CALMO', 'Dia produtivo de trabalho, sem intercorrências', false, 7, true, CURDATE()),
(8, 'CALMO', 'Alguns bugs encontrados, mas nada crítico', false, 7, true, CURDATE()),
(9, 'CALMO', 'Reuniões produtivas hoje', false, 7, true, CURDATE()),
(10, 'FELIZ', 'Karaokê foi incrível ontem! Ambiente está ótimo', false, 9, true, CURDATE());

-- Dashboards
INSERT IGNORE INTO dashboards (id, usuario_id, tipo_dashboard, metricas) VALUES
(1, 1, 'RH', '{"totalFuncionarios": 10, "checkHumorsHoje": 10, "satisfacaoMedia": 4.4}'),
(2, 2, 'LIDER', '{"tarefasTime": 6, "tarefasConcluidas": 1, "produtividadeTime": 85}'),
(3, 6, 'LIDER', '{"tarefasTime": 5, "tarefasConcluidas": 1, "produtividadeTime": 90}'); 