package com.exemplo.bancoalcatteia.controller;

import com.exemplo.bancoalcatteia.service.HumorStatisticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

/**
 * Controller responsável exclusivamente por estatísticas de humor
 * Implementa Single Responsibility Principle para métricas
 */
@RestController
@RequestMapping("/api/humor/estatisticas")
public class HumorStatisticsController {

    private final HumorStatisticsService statisticsService;

    public HumorStatisticsController(HumorStatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    /**
     * Buscar estatísticas gerais de um período
     * GET /api/humor/estatisticas/periodo
     */
    @GetMapping("/periodo")
    public ResponseEntity<Map<String, Object>> estatisticasPeriodo(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        
        // Se não especificar período, buscar último mês
        if (inicio == null) {
            inicio = LocalDate.now().minusMonths(1);
        }
        if (fim == null) {
            fim = LocalDate.now();
        }
        
        Map<String, Object> estatisticas = statisticsService.buscarEstatisticasPeriodo(inicio, fim);
        return ResponseEntity.ok(estatisticas);
    }

    /**
     * Estatísticas do mês atual
     * GET /api/humor/estatisticas/mes-atual
     */
    @GetMapping("/mes-atual")
    public ResponseEntity<Map<String, Object>> estatisticasMesAtual() {
        LocalDate hoje = LocalDate.now();
        LocalDate inicio = hoje.withDayOfMonth(1);
        LocalDate fim = hoje.withDayOfMonth(hoje.lengthOfMonth());
        
        Map<String, Object> estatisticas = statisticsService.buscarEstatisticasPeriodo(inicio, fim);
        return ResponseEntity.ok(estatisticas);
    }

    /**
     * Estatísticas da semana atual
     * GET /api/humor/estatisticas/semana-atual
     */
    @GetMapping("/semana-atual")
    public ResponseEntity<Map<String, Object>> estatisticasSemanaAtual() {
        LocalDate hoje = LocalDate.now();
        LocalDate inicio = hoje.minusDays(hoje.getDayOfWeek().getValue() - 1); // Segunda-feira
        LocalDate fim = inicio.plusDays(6); // Domingo
        
        Map<String, Object> estatisticas = statisticsService.buscarEstatisticasPeriodo(inicio, fim);
        return ResponseEntity.ok(estatisticas);
    }

    /**
     * Estatísticas de hoje
     * GET /api/humor/estatisticas/hoje
     */
    @GetMapping("/hoje")
    public ResponseEntity<Map<String, Object>> estatisticasHoje() {
        LocalDate hoje = LocalDate.now();
        
        Map<String, Object> estatisticas = statisticsService.buscarEstatisticasPeriodo(hoje, hoje);
        return ResponseEntity.ok(estatisticas);
    }

    /**
     * Dashboard resumido para RH
     * GET /api/humor/estatisticas/dashboard
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> dashboardRH() {
        LocalDate hoje = LocalDate.now();
        
        // Estatísticas dos últimos 30 dias
        LocalDate inicio = hoje.minusDays(30);
        Map<String, Object> dashboard = statisticsService.buscarEstatisticasPeriodo(inicio, hoje);
        
        // Adicionar dados comparativos
        LocalDate inicioAnterior = inicio.minusDays(30);
        LocalDate fimAnterior = inicio.minusDays(1);
        Map<String, Object> periodoAnterior = statisticsService.buscarEstatisticasPeriodo(inicioAnterior, fimAnterior);
        
        dashboard.put("periodoAnterior", periodoAnterior);
        dashboard.put("comparativo", true);
        
        return ResponseEntity.ok(dashboard);
    }
} 
