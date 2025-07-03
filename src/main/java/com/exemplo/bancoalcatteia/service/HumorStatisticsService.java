package com.exemplo.bancoalcatteia.service;

import com.exemplo.bancoalcatteia.model.CheckHumor;
import com.exemplo.bancoalcatteia.repository.CheckHumorRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service responsável por cálculos estatísticos e métricas de humor
 * Implementa Single Responsibility Principle para estatísticas
 */
@Service
public class HumorStatisticsService {

    private final CheckHumorRepository checkHumorRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public HumorStatisticsService(CheckHumorRepository checkHumorRepository) {
        this.checkHumorRepository = checkHumorRepository;
    }

    /**
     * Busca estatísticas gerais do período com cache
     */
    @Cacheable(value = "humor-statistics", key = "#inicio.toString() + '-' + #fim.toString()")
    public Map<String, Object> buscarEstatisticasPeriodo(LocalDate inicio, LocalDate fim) {
        Map<String, Object> stats = new HashMap<>();
        
        long totalRegistros = checkHumorRepository.countByDataRegistroBetween(inicio, fim);
        Double mediaGeral = checkHumorRepository.findAverageHumorValue(inicio, fim);
        List<Object[]> distribuicao = checkHumorRepository.findHumorStatistics(inicio, fim);
        long registrosHoje = checkHumorRepository.countRegistrosHoje();
        
        stats.put("totalRegistros", totalRegistros);
        stats.put("mediaGeral", mediaGeral != null ? Math.round(mediaGeral * 100.0) / 100.0 : 0.0);
        stats.put("distribuicaoHumor", converterDistribuicao(distribuicao));
        stats.put("registrosHoje", registrosHoje);
        stats.put("periodo", inicio.format(formatter) + " - " + fim.format(formatter));
        
        return stats;
    }

    /**
     * Calcula estatísticas de distribuição de humores
     */
    public Map<String, Long> calcularEstatisticasHumor(List<CheckHumor> registros) {
        return registros.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getHumor().name(),
                        Collectors.counting()
                ));
    }

    /**
     * Calcula percentuais de distribuição de humores
     */
    public Map<String, Double> calcularPercentuais(Map<String, Long> estatisticas, int total) {
        return estatisticas.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> Math.round((entry.getValue() * 100.0 / total) * 100.0) / 100.0
                ));
    }

    /**
     * Calcula média geral dos humores em escala numérica
     */
    public Double calcularMediaGeral(List<CheckHumor> registros) {
        return registros.stream()
                .mapToInt(r -> r.getHumor().getValorNumerico())
                .average()
                .orElse(0.0);
    }

    /**
     * Encontra o humor predominante no período
     */
    public String encontrarHumorPredominante(Map<String, Long> estatisticas) {
        return estatisticas.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("CALMO");
    }

    /**
     * Conta usuários únicos que registraram humor
     */
    public Long contarUsuariosUnicos(List<CheckHumor> registros) {
        return registros.stream()
                .map(r -> r.getUsuario().getId())
                .distinct()
                .count();
    }

    /**
     * Calcula variação percentual entre dois períodos com cache
     */
    @Cacheable(value = "humor-trends", key = "#inicio.toString() + '-' + #fim.toString() + '-variation'")
    public Double calcularVariacao(LocalDate inicio, LocalDate fim) {
        long dias = fim.toEpochDay() - inicio.toEpochDay() + 1;
        LocalDate inicioAnterior = inicio.minusDays(dias);
        LocalDate fimAnterior = inicio.minusDays(1);
        
        Double mediaAtual = checkHumorRepository.findAverageHumorValue(inicio, fim);
        Double mediaAnterior = checkHumorRepository.findAverageHumorValue(inicioAnterior, fimAnterior);
        
        if (mediaAtual == null || mediaAnterior == null || mediaAnterior == 0) {
            return 0.0;
        }
        
        return Math.round(((mediaAtual - mediaAnterior) / mediaAnterior * 100) * 100.0) / 100.0;
    }

    /**
     * Converte distribuição de Object[] para Map
     */
    private Map<String, Long> converterDistribuicao(List<Object[]> distribuicao) {
        return distribuicao.stream()
                .collect(Collectors.toMap(
                        row -> ((CheckHumor.Humor) row[0]).name(),
                        row -> (Long) row[1]
                ));
    }
} 

