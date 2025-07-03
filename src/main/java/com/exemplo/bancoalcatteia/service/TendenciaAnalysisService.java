package com.exemplo.bancoalcatteia.service;

import com.exemplo.bancoalcatteia.model.CheckHumor;
import com.exemplo.bancoalcatteia.repository.CheckHumorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TendenciaAnalysisService {

    private final CheckHumorRepository checkHumorRepository;

    /**
     * Analisa tendência do humor no período
     */
    public String analisarTendencia(LocalDate dataInicio, LocalDate dataFim) {
        Double mediaAtual = checkHumorRepository.findAverageHumorValue(dataInicio, dataFim);
        
        // Calcular período anterior para comparação
        long dias = dataFim.toEpochDay() - dataInicio.toEpochDay() + 1;
        LocalDate inicioAnterior = dataInicio.minusDays(dias);
        LocalDate fimAnterior = dataInicio.minusDays(1);
        
        Double mediaAnterior = checkHumorRepository.findAverageHumorValue(inicioAnterior, fimAnterior);
        
        if (mediaAtual == null || mediaAnterior == null) {
            return "Dados insuficientes para análise";
        }
        
        double diferenca = mediaAtual - mediaAnterior;
        double percentualMudanca = (diferenca / mediaAnterior) * 100;
        
        if (Math.abs(percentualMudanca) < 5) {
            return "Estável";
        } else if (percentualMudanca > 0) {
            return String.format("Melhoria (%.1f%%)", percentualMudanca);
        } else {
            return String.format("Declínio (%.1f%%)", Math.abs(percentualMudanca));
        }
    }

    /**
     * Gera alertas baseados nas estatísticas
     */
    public List<String> gerarAlertas(Map<String, Long> estatisticas, int totalRegistros) {
        List<String> alertas = new ArrayList<>();
        
        if (totalRegistros == 0) {
            alertas.add("Nenhum registro de humor encontrado no período");
            return alertas;
        }
        
        // Calcular percentuais
        double percentualEstressado = (estatisticas.getOrDefault("ESTRESSADO", 0L) * 100.0) / totalRegistros;
        double percentualDesmotivado = (estatisticas.getOrDefault("DESMOTIVADO", 0L) * 100.0) / totalRegistros;
        double percentualNegativos = percentualEstressado + percentualDesmotivado;
        
        // Alertas baseados em thresholds
        if (percentualEstressado > 30) {
            alertas.add(String.format("Alto nível de estresse detectado: %.1f%% dos registros", percentualEstressado));
        }
        
        if (percentualDesmotivado > 25) {
            alertas.add(String.format("Nível preocupante de desmotivação: %.1f%% dos registros", percentualDesmotivado));
        }
        
        if (percentualNegativos > 50) {
            alertas.add("Mais de 50% dos registros indicam humor negativo - ação urgente necessária");
        }
        
        if (totalRegistros < 5) {
            alertas.add("Poucos registros de humor - incentive mais participação");
        }
        
        return alertas;
    }

    /**
     * Gera recomendações baseadas nos dados
     */
    public List<String> gerarRecomendacoes(Map<String, Long> estatisticas, Double mediaGeral) {
        List<String> recomendacoes = new ArrayList<>();
        
        if (mediaGeral == null || mediaGeral < 2.5) {
            recomendacoes.add("Considere implementar ações de bem-estar e suporte psicológico");
            recomendacoes.add("Revise a carga de trabalho e deadlines da equipe");
            recomendacoes.add("Promova atividades de team building e descontração");
        } else if (mediaGeral < 3.5) {
            recomendacoes.add("Mantenha comunicação próxima com a equipe");
            recomendacoes.add("Identifique fatores específicos de estresse");
            recomendacoes.add("Considere melhorias no ambiente de trabalho");
        } else {
            recomendacoes.add("Continue mantendo o bom ambiente de trabalho");
            recomendacoes.add("Compartilhe boas práticas com outras equipes");
        }
        
        // Recomendações específicas baseadas em padrões
        long totalEstressado = estatisticas.getOrDefault("ESTRESSADO", 0L);
        if (totalEstressado > 0) {
            recomendacoes.add("Ofereça apoio individual aos colaboradores em situação de estresse");
        }
        
        long totalMotivado = estatisticas.getOrDefault("MOTIVADO", 0L);
        long totalContente = estatisticas.getOrDefault("CONTENTE", 0L);
        if (totalMotivado + totalContente > totalEstressado) {
            recomendacoes.add("Aproveite o momento positivo para impulsionar projetos importantes");
        }
        
        return recomendacoes;
    }

    /**
     * Verifica se um usuário precisa de atenção especial baseado na média
     */
    public boolean precisaAtencao(Double mediaUsuario) {
        return mediaUsuario != null && mediaUsuario < 2.5;
    }
} 

