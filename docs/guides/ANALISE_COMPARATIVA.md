# Análise Comparativa: Dump Railway vs Script de Demonstração

## Resumo Executivo
O dump do Railway contém algumas melhorias importantes que devem ser incorporadas no script de demonstração, mas há inconsistências críticas de tipos de dados que precisam ser corrigidas.

## Principais Diferenças Identificadas

### 1. INCONSISTÊNCIAS CRÍTICAS DE TIPOS DE DADOS

**Problema**: O dump usa `BIGINT` para algumas tabelas enquanto o script usa `INT` consistentemente.

**Tabelas Afetadas**:
- `tarefas.id` → BIGINT no dump, INT no script
- `musicas_cantadas.id` → BIGINT no dump, INT no script  
- `avaliacoes.id` → BIGINT no dump, INT no script
- `participantes_tarefa.tarefa_id` → BIGINT no dump, INT no script
- `notificacoes.tarefa_id` → BIGINT no dump, INT no script

**Recomendação**: Manter INT em todas as tabelas conforme o script original.

### 2. MELHORIAS IMPORTANTES DO DUMP

#### A. Campos Adicionais na Tabela `usuarios`
```sql
-- Campos extras no dump que devem ser adicionados:
`descricao` varchar(255) DEFAULT NULL,
`imagem` varchar(350) DEFAULT NULL,
`data_ultima_atualizacao` datetime DEFAULT NULL
```

#### B. Campos Adicionais na Tabela `times`
```sql
-- Campos extras no dump:
`data_ultima_atualizacao` datetime DEFAULT NULL,
`criado_por_id` int NOT NULL  -- Campo obrigatório
```

#### C. Campos Adicionais na Tabela `membros_time`
```sql
-- Campos extras no dump:
`tipo_membro` enum('LIDER','VICE_LIDER','MEMBRO','COLABORADOR') NOT NULL,
`observacoes` varchar(255) DEFAULT NULL,
`data_entrada` datetime NOT NULL  -- Mudança de date para datetime
```

#### D. Campos Adicionais na Tabela `check_humor`
```sql
-- Campos extras no dump:
`bem_estar_pontos` int DEFAULT NULL,
`confirmado` boolean DEFAULT NULL
```

#### E. Campos Adicionais na Tabela `kanban_categorias`
```sql
-- Campo extra no dump:
`criado_por_id` int DEFAULT NULL
```

### 3. NOVA FUNCIONALIDADE: TABELA ROOMS

**Tabela Nova no Dump**:
```sql
CREATE TABLE `rooms` (
  `room_id` varchar(20) NOT NULL,
  `room_name` text NOT NULL,
  `host_id` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`room_id`)
);
```

**Recomendação**: Adicionar esta tabela ao script de demonstração.

### 4. PROBLEMA COM ENUM CHECK_HUMOR

**Dump**: `enum('Péssimo','Ruim','Neutro','Bom','Ótimo')`
**Script**: `enum('FELIZ','ANIMADO','CALMO','DESANIMADO','ESTRESSADO')`

**Problema**: O dump tem valores em português antigos, o script tem valores em inglês modernos.

**Recomendação**: Manter o enum do script original e migrar os dados.

### 5. TABELA FEEDBACK vs FEEDBACKS

**Dump**: Tem duas tabelas `feedback` (antiga) e `feedbacks` (nova)
**Script**: Tem apenas `feedbacks` (nova estrutura)

**Recomendação**: Usar apenas a tabela `feedbacks` do script.

### 6. DADOS REAIS vs DADOS DE DEMONSTRAÇÃO

**Dump**: Contém usuários reais (IDs 11-24) com dados de produção
**Script**: Contém dados de demonstração completos e bem estruturados

**Recomendação**: Preservar os dados de demonstração do script original.

## Ações Recomendadas

### 1. IMEDIATAS (Críticas)
- [ ] Corrigir inconsistências de tipos BIGINT → INT
- [ ] Adicionar campos obrigatórios em `times.criado_por_id`
- [ ] Migrar dados de check_humor para novo enum

### 2. MELHORIAS (Importantes)
- [ ] Adicionar campos extras nas tabelas principais
- [ ] Implementar tabela `rooms` 
- [ ] Adicionar constraints FK para novos campos
- [ ] Atualizar dados de demonstração com novos campos

### 3. OPCIONAIS (Melhorias futuras)
- [ ] Manter apenas tabela `feedbacks` (remover `feedback`)
- [ ] Adicionar índices para novos campos
- [ ] Documentar nova funcionalidade de rooms

## Script de Migração Sugerido

```sql
-- 1. Adicionar campos extras às tabelas existentes
ALTER TABLE `usuarios` 
ADD COLUMN `descricao` varchar(255) DEFAULT NULL,
ADD COLUMN `imagem` varchar(350) DEFAULT NULL,
ADD COLUMN `data_ultima_atualizacao` datetime DEFAULT NULL;

-- 2. Adicionar campos em times
ALTER TABLE `times`
ADD COLUMN `data_ultima_atualizacao` datetime DEFAULT NULL,
ADD COLUMN `criado_por_id` int NOT NULL DEFAULT 1;

-- 3. Adicionar FK constraint
ALTER TABLE `times` 
ADD CONSTRAINT `fk_times_criado_por` 
FOREIGN KEY (`criado_por_id`) REFERENCES `usuarios` (`id`) ON DELETE RESTRICT;

-- 4. Adicionar campos em membros_time
ALTER TABLE `membros_time`
ADD COLUMN `tipo_membro` enum('LIDER','VICE_LIDER','MEMBRO','COLABORADOR') NOT NULL DEFAULT 'MEMBRO',
ADD COLUMN `observacoes` varchar(255) DEFAULT NULL,
MODIFY COLUMN `data_entrada` datetime NOT NULL;

-- 5. Adicionar campos em check_humor
ALTER TABLE `check_humor`
ADD COLUMN `bem_estar_pontos` int DEFAULT NULL,
ADD COLUMN `confirmado` boolean DEFAULT NULL;

-- 6. Criar tabela rooms
CREATE TABLE `rooms` (
  `room_id` varchar(20) NOT NULL,
  `room_name` text NOT NULL,
  `host_id` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`room_id`),
  KEY `idx_rooms_host` (`host_id`),
  CONSTRAINT `fk_rooms_host` FOREIGN KEY (`host_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE
);
```

## Conclusão

O script de demonstração deve prevalecer como base, mas incorporando as melhorias identificadas no dump. A prioridade é manter a consistência de tipos de dados (INT) e adicionar as funcionalidades que demonstram evolução do sistema.

**Status**: Pronto para implementação das correções. 