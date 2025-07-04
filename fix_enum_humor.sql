-- Script para corrigir o enum humor na tabela check_humor
-- Execute este script no seu cliente MySQL

-- 1. Primeiro, vamos ver os dados atuais
SELECT DISTINCT humor FROM check_humor;

-- 2. Alterar a definição do enum para incluir 'Neutro'
ALTER TABLE check_humor 
MODIFY COLUMN humor ENUM('Contente', 'Motivado', 'Calmo', 'Neutro', 'Desmotivado', 'Estressado') NOT NULL;

-- 3. Verificar se a alteração foi aplicada
DESCRIBE check_humor;

-- 4. Verificar os dados novamente
SELECT DISTINCT humor FROM check_humor; 