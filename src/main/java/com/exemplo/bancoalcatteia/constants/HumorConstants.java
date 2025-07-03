package com.exemplo.bancoalcatteia.constants;

/**
 * Constantes centralizadas para o sistema de Check de Humor
 * Elimina magic numbers e strings hardcoded seguindo Clean Code
 */
public final class HumorConstants {

    // === CONSTANTES DE TENDÊNCIA ===
    
    /**
     * Limiar para determinar tendência MELHORANDO vs ESTÁVEL
     */
    public static final double TENDENCIA_MELHORANDO_THRESHOLD = 0.3;
    
    /**
     * Limiar para determinar tendência PIORANDO vs ESTÁVEL
     */
    public static final double TENDENCIA_PIORANDO_THRESHOLD = -0.3;

    // === CONSTANTES DE ANÁLISE ===
    
    /**
     * Média abaixo da qual usuário precisa de atenção especial
     */
    public static final double MEDIA_ATENCAO_THRESHOLD = 2.5;
    
    /**
     * Média abaixo da qual são sugeridas ações de bem-estar
     */
    public static final double MEDIA_BEM_ESTAR_THRESHOLD = 3.5;
    
    /**
     * Percentual de humores negativos que gera alerta
     */
    public static final double PERCENTUAL_NEGATIVO_ALERTA = 30.0;
    
    /**
     * Percentual de humores estressados que gera alerta crítico
     */
    public static final double PERCENTUAL_ESTRESSE_CRITICO = 15.0;
    
    /**
     * Número mínimo de registros para análise estatística confiável
     */
    public static final int REGISTROS_MINIMOS_ANALISE = 5;

    // === CONSTANTES DE PERÍODO ===
    
    /**
     * Número de dias para período padrão de comentários
     */
    public static final int DIAS_COMENTARIOS_PADRAO = 15;
    
    /**
     * Número de dias para relatório quinzenal
     */
    public static final int DIAS_RELATORIO_QUINZENAL = 14;
    
    /**
     * Número máximo de meses para relatório personalizado
     */
    public static final int MESES_MAXIMO_RELATORIO = 3;
    
    /**
     * Número de registros históricos para análise de tendência individual
     */
    public static final int HISTORICO_TENDENCIA_INDIVIDUAL = 7;

    // === CONSTANTES DE VALIDAÇÃO ===
    
    /**
     * Tamanho máximo para comentários/observações
     */
    public static final int TAMANHO_MAXIMO_OBSERVACAO = 1000;
    
    /**
     * Tamanho máximo para mensagens de alerta
     */
    public static final int TAMANHO_MAXIMO_ALERTA = 200;

    // === STRINGS PADRONIZADAS ===
    
    /**
     * Mensagens de tendência
     */
    public static final String TENDENCIA_MELHORANDO = "MELHORANDO";
    public static final String TENDENCIA_ESTAVEL = "ESTÁVEL";
    public static final String TENDENCIA_PIORANDO = "PIORANDO";
    public static final String TENDENCIA_INSUFICIENTE = "INSUFICIENTE";
    
    /**
     * Categorias de risco
     */
    public static final String RISCO_EXCELENTE = "EXCELENTE";
    public static final String RISCO_BOM = "BOM";
    public static final String RISCO_MODERADO = "MODERADO";
    public static final String RISCO_ATENCAO = "ATENÇÃO";
    public static final String RISCO_CRITICO = "CRÍTICO";
    public static final String RISCO_INDETERMINADO = "INDETERMINADO";
    
    /**
     * Roles de usuário
     */
    public static final String ROLE_FUNC = "ROLE_FUNC";
    public static final String ROLE_RH = "ROLE_RH";
    public static final String ROLE_LIDER = "ROLE_LIDER";
    
    /**
     * Usuário anônimo
     */
    public static final String USUARIO_ANONIMO = "Anônimo";
    
    /**
     * Humor padrão para casos indefinidos
     */
    public static final String HUMOR_PADRAO = "CALMO";

    // === LIMIARES DE CATEGORIA DE RISCO ===
    
    public static final double RISCO_EXCELENTE_MIN = 4.0;
    public static final double RISCO_BOM_MIN = 3.5;
    public static final double RISCO_MODERADO_MIN = 3.0;
    public static final double RISCO_ATENCAO_MIN = 2.5;
    // Abaixo de 2.5 = CRÍTICO

    // === FORMATOS DE DATA ===
    
    public static final String FORMATO_DATA_RELATORIO = "dd/MM/yyyy";
    public static final String FORMATO_DATA_ISO = "yyyy-MM-dd";

    // === MENSAGENS PADRONIZADAS ===
    
    public static final String MSG_USUARIO_NAO_ENCONTRADO = "Usuário não encontrado";
    public static final String MSG_REGISTRO_DUPLICADO = "Usuário já registrou humor hoje. Apenas um registro por dia é permitido.";
    public static final String MSG_HUMOR_OBRIGATORIO = "Humor é obrigatório";
    public static final String MSG_USUARIO_ID_OBRIGATORIO = "ID do usuário é obrigatório";
    public static final String MSG_USUARIO_NAO_AUTENTICADO = "Usuário não autenticado";
    public static final String MSG_CHECK_HUMOR_NAO_ENCONTRADO = "Check de humor não encontrado";

    // Novas constantes para o sistema de check-in avançado
    public static final int OPCAO_HUMOR_MIN = 1;
    public static final int OPCAO_HUMOR_MAX = 5;
    
    // Limites de bem-estar
    public static final int BEM_ESTAR_EXCELENTE = 10;
    public static final int BEM_ESTAR_POSITIVO = 5;
    public static final int BEM_ESTAR_EQUILIBRADO = 0;
    public static final int BEM_ESTAR_ATENCAO = -5;
    
    // Mensagens de bem-estar
    public static final String MSG_BEM_ESTAR_EXCELENTE = "Excelente bem-estar! 🎉";
    public static final String MSG_BEM_ESTAR_POSITIVO = "Bem-estar positivo! ⭐";
    public static final String MSG_BEM_ESTAR_EQUILIBRADO = "Bem-estar equilibrado ⚖️";
    public static final String MSG_BEM_ESTAR_ATENCAO = "Considere cuidar mais de si 💙";
    public static final String MSG_BEM_ESTAR_APOIO = "Busque apoio, você não está sozinho 🤝";

    // Construtor privado para evitar instanciação
    private HumorConstants() {
        throw new UnsupportedOperationException("Esta é uma classe de constantes e não deve ser instanciada");
    }
} 
