package com.exemplo.bancoalcatteia.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioHumorDTO {
    
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String periodo; // Ex: "01/01/2025 - 15/01/2025"
    
    // Estatísticas de distribuição de humores
    private Map<String, Long> estatisticasHumor; // CONTENTE: 10, MOTIVADO: 8, etc.
    private Map<String, Double> percentuaisHumor; // CONTENTE: 35.7%, MOTIVADO: 28.6%, etc.
    
    // Métricas gerais
    private Long totalRegistros;
    private Long totalUsuarios; // Usuários únicos que registraram humor
    private Double mediaGeral; // Média numérica dos humores (1-5)
    private String humorPredominante; // Humor mais comum no período
    
    // Análise de tendência
    private String tendencia; // "MELHORANDO", "ESTÁVEL", "PIORANDO"
    private Double variacao; // Variação percentual em relação ao período anterior
    
    // Comentários e insights
    private List<CheckHumorDTO> comentarios; // Comentários não anônimos do período
    private List<CheckHumorDTO> comentariosAnonimos; // Comentários anônimos (sem identificação)
    private Long totalComentarios;
    
    // Estatísticas por usuário (para gestores)
    private List<UsuarioHumorDTO> detalhePorUsuario;
    
    // Comparação com período anterior
    private ComparacaoHumorDTO comparacao;
    
    // Alertas e recomendações
    private List<String> alertas; // Ex: "Aumento de 20% em humores negativos"
    private List<String> recomendacoes; // Ex: "Considere atividades de bem-estar"
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UsuarioHumorDTO {
        private Integer usuarioId;
        private String nomeUsuario;
        private Long totalRegistros;
        private Double mediaHumor;
        private String humorPredominante;
        private String ultimoHumor;
        private LocalDate ultimoRegistro;
        private Boolean precisaAtencao; // Baseado em critérios de humor baixo
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComparacaoHumorDTO {
        private Double mediaPeriodoAnterior;
        private Double mediaAtual;
        private String tendenciaGeral;
        private Long totalRegistrosAnterior;
        private Long totalRegistrosAtual;
        private Map<String, Double> variacoesPorHumor;
    }
} 
