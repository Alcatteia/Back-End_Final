package com.exemplo.bancoalcatteia.controller;

import com.exemplo.bancoalcatteia.constants.HumorConstants;
import com.exemplo.bancoalcatteia.dto.RelatorioHumorDTO;
import com.exemplo.bancoalcatteia.exception.BusinessException;
import com.exemplo.bancoalcatteia.service.RelatorioHumorService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Controller responsável exclusivamente por relatórios de humor
 * Implementa Single Responsibility Principle para relatórios
 */
@RestController
@RequestMapping("/api/humor/relatorios")
public class HumorReportController {

    private final RelatorioHumorService relatorioService;

    public HumorReportController(RelatorioHumorService relatorioService) {
        this.relatorioService = relatorioService;
    }

    /**
     * Gerar relatório quinzenal completo
     * GET /api/humor/relatorios/quinzenal
     */
    @GetMapping("/quinzenal")
    public ResponseEntity<RelatorioHumorDTO> gerarRelatorioQuinzenal(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        
        RelatorioHumorDTO relatorio = relatorioService.gerarRelatorioQuinzenal(inicio, fim);
        return ResponseEntity.ok(relatorio);
    }

    /**
     * Relatório pré-definido dos últimos 15 dias
     * GET /api/humor/relatorios/default
     */
    @GetMapping("/default")
    public ResponseEntity<RelatorioHumorDTO> relatorioDefault() {
        LocalDate fim = LocalDate.now();
        LocalDate inicio = fim.minusDays(HumorConstants.DIAS_RELATORIO_QUINZENAL); // Últimos 15 dias
        
        RelatorioHumorDTO relatorio = relatorioService.gerarRelatorioQuinzenal(inicio, fim);
        return ResponseEntity.ok(relatorio);
    }

    /**
     * Relatório mensal
     * GET /api/humor/relatorios/mensal
     */
    @GetMapping("/mensal")
    public ResponseEntity<RelatorioHumorDTO> relatorioMensal(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate mes) {
        
        LocalDate inicio, fim;
        
        if (mes == null) {
            // Mês atual
            LocalDate hoje = LocalDate.now();
            inicio = hoje.withDayOfMonth(1);
            fim = hoje.withDayOfMonth(hoje.lengthOfMonth());
        } else {
            inicio = mes.withDayOfMonth(1);
            fim = mes.withDayOfMonth(mes.lengthOfMonth());
        }
        
        RelatorioHumorDTO relatorio = relatorioService.gerarRelatorioQuinzenal(inicio, fim);
        return ResponseEntity.ok(relatorio);
    }

    /**
     * Relatório personalizado por período
     * GET /api/humor/relatorios/personalizado
     */
    @GetMapping("/personalizado")
    public ResponseEntity<RelatorioHumorDTO> relatorioPersonalizado(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        
        // Validar período máximo usando constante
        if (inicio.plusMonths(HumorConstants.MESES_MAXIMO_RELATORIO).isBefore(fim)) {
            throw new BusinessException("Período do relatório não pode exceder " + 
                HumorConstants.MESES_MAXIMO_RELATORIO + " meses");
        }
        
        RelatorioHumorDTO relatorio = relatorioService.gerarRelatorioQuinzenal(inicio, fim);
        return ResponseEntity.ok(relatorio);
    }
} 
