# Documentação do Backend Alcatteia

## 📚 **Índice Geral**

### **🚀 Guias Técnicos** (`guides/`)
- **[TRATAMENTO_ERROS.md](guides/TRATAMENTO_ERROS.md)** - Sistema completo de exceções e tratamento de erros
- **[COMUNICACAO_CAMADAS_GUIA.md](guides/COMUNICACAO_CAMADAS_GUIA.md)** - Padrões de comunicação entre camadas
- **[GUIA_PREPARACAO_PRODUCAO.md](guides/GUIA_PREPARACAO_PRODUCAO.md)** - Checklist completo para produção
- **[GUIA_EXECUCAO.md](guides/GUIA_EXECUCAO.md)** - Como executar o projeto localmente
- **[ANALISE_COMPARATIVA.md](guides/ANALISE_COMPARATIVA.md)** - Análise comparativa das implementações
- **[ROTEIRO_MIGRACAO_COMPLETO.md](guides/ROTEIRO_MIGRACAO_COMPLETO.md)** - Roteiro completo de migração

### **🗃️ Scripts SQL** (`sql/`)
- **[banco_alcatteia_completo.sql](sql/banco_alcatteia_completo.sql)** - Schema completo do banco de dados
- **[dados_demonstracao.sql](sql/dados_demonstracao.sql)** - Dados de demonstração principais
- **[dados_demonstracao_parte2.sql](sql/dados_demonstracao_parte2.sql)** - Dados complementares
- **[dados_demonstracao_updated.sql](sql/dados_demonstracao_updated.sql)** - Dados atualizados
- **[migracao_melhorias.sql](sql/migracao_melhorias.sql)** - Script de melhorias e otimizações
- **[roteiro_migracao.sql](sql/roteiro_migracao.sql)** - Roteiro de migração SQL
- **[fix_enum_humor.sql](sql/fix_enum_humor.sql)** - Correção de enum de humor

### **📡 APIs e Postman** (`postman/`)
- **[BancoAlcatteia_JWT.postman_collection.json](postman/BancoAlcatteia_JWT.postman_collection.json)** - Coleção principal
- **[Alcatteia_API_Endpoints.json](postman/Alcatteia_API_Endpoints.json)** - Endpoints da API
- **[validado_Alcatteia API Endpoints.postman_collection](postman/validado_Alcatteia%20API%20Endpoints.postman_collection)** - Coleção validada
- **[validado_v2_Alcatteia API Endpoints.postman_collection](postman/validado_v2_Alcatteia%20API%20Endpoints.postman_collection)** - Coleção v2 validada

### **🏗️ Arquitetura e API**
- **[ARCHITECTURE.md](ARCHITECTURE.md)** - Documentação da arquitetura do sistema
- **[API_GUIDE.md](API_GUIDE.md)** - Guia completo da API REST
- **[POSTMAN_CORRECTIONS.md](POSTMAN_CORRECTIONS.md)** - Correções e melhorias no Postman

### **🚢 Deploy e Produção**
- **[DEPLOY_REALIZADO.md](DEPLOY_REALIZADO.md)** - Relatório do deploy realizado
- **[RESUMO_EXECUTIVO.md](RESUMO_EXECUTIVO.md)** - Resumo executivo do projeto
- **[IMPLEMENTACAO_FINALIZADA.md](IMPLEMENTACAO_FINALIZADA.md)** - Relatório de implementação finalizada

### **🗄️ Banco de Dados**
- **[dump-railway-202507032302.sql](dump-railway-202507032302.sql)** - Dump do banco Railway

---

## 🎯 **Guia de Uso Rápido**

### **Para Desenvolvedores**
1. Leia o **[README.md](../README.md)** principal
2. Configure seguindo **[GUIA_EXECUCAO.md](guides/GUIA_EXECUCAO.md)**
3. Entenda o sistema de erros em **[TRATAMENTO_ERROS.md](guides/TRATAMENTO_ERROS.md)**
4. Use as coleções **[Postman](postman/)** para testar APIs

### **Para Deploy**
1. Siga o **[GUIA_PREPARACAO_PRODUCAO.md](guides/GUIA_PREPARACAO_PRODUCAO.md)**
2. Execute scripts SQL em **[sql/](sql/)**
3. Configure ambiente conforme **[ARCHITECTURE.md](ARCHITECTURE.md)**

### **Para Entender o Sistema**
1. Leia **[ANALISE_COMPARATIVA.md](guides/ANALISE_COMPARATIVA.md)**
2. Consulte **[COMUNICACAO_CAMADAS_GUIA.md](guides/COMUNICACAO_CAMADAS_GUIA.md)**
3. Veja **[API_GUIDE.md](API_GUIDE.md)** para endpoints

---

**Última atualização:** Janeiro 2025  
**Versão:** 2.0.0 