package com.exemplo.bancoalcatteia.controller;

import com.exemplo.bancoalcatteia.dto.CheckHumorDTO;
import com.exemplo.bancoalcatteia.service.CheckHumorService;
import com.exemplo.bancoalcatteia.service.CurrentUserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

/**
 * Controller para funcionalidades avançadas de check-in de humor
 * Inclui validação de intervalos, bem-estar e personalização por gênero
 */
@RestController
@RequestMapping("/api/check-in-humor")
public class CheckHumorAdvancedController {

    private final CheckHumorService checkHumorService;
    private final CurrentUserService currentUserService;

    public CheckHumorAdvancedController(CheckHumorService checkHumorService,
                                       CurrentUserService currentUserService) {
        this.checkHumorService = checkHumorService;
        this.currentUserService = currentUserService;
    }

    /**
     * Iniciar processo de check-in (validação e menu)
     * GET /api/check-in-humor/iniciar
     */
    @GetMapping("/iniciar")
    public ResponseEntity<Map<String, Object>> iniciarCheckIn() {
        try {
            Integer usuarioId = currentUserService.getCurrentUserId();

            CheckHumorService.ValidacaoResult validacao =
                    checkHumorService.validarPodeRealizarCheckIn(usuarioId);

            String menuEmocoes = checkHumorService.gerarMenuEmocoes(usuarioId);

            Map<String, Object> response = Map.of(
                    "podeRealizar", validacao.podeRealizar(),
                    "mensagem", validacao.mensagem(),
                    "menuEmocoes", menuEmocoes
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Realizar check-in avançado
     * POST /api/check-in-humor/realizar
     */
    @PostMapping("/realizar")
    public ResponseEntity<CheckHumorDTO> realizarCheckIn(@Valid @RequestBody CheckHumorDTO checkInDTO) {
        try {
            Integer usuarioId = currentUserService.getCurrentUserId();
            checkInDTO.setUsuarioId(usuarioId);

            CheckHumorDTO resultado = checkHumorService.realizarCheckIn(checkInDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Confirmar ou cancelar seleção de humor
     * PUT /api/check-in-humor/{id}/confirmar
     */
    @PutMapping("/{id}/confirmar")
    public ResponseEntity<CheckHumorDTO> confirmarSelecao(
            @PathVariable Integer id,
            @RequestParam boolean confirmado) {
        try {
            CheckHumorDTO resultado = checkHumorService.confirmarSelecao(id, confirmado);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Ver humor de hoje do usuário logado
     * GET /api/check-in-humor/humor-hoje
     */
    @GetMapping("/humor-hoje")
    public ResponseEntity<CheckHumorDTO> verHumorHoje() {
        Integer usuarioId = currentUserService.getCurrentUserId();
        Optional<CheckHumorDTO> humor = checkHumorService.buscarHumorHoje(usuarioId);
        return humor.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Calcular bem-estar total por período
     * GET /api/check-in-humor/bem-estar-total
     */
    @GetMapping("/bem-estar-total")
    public ResponseEntity<Map<String, Object>> calcularBemEstarTotal(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {

        Integer usuarioId = currentUserService.getCurrentUserId();
        int pontuacaoTotal = checkHumorService.calcularBemEstarTotal(usuarioId, inicio, fim);
        String mensagem = checkHumorService.obterMensagemBemEstar(pontuacaoTotal);

        Map<String, Object> response = Map.of(
                "pontuacaoTotal", pontuacaoTotal,
                "periodo", Map.of("inicio", inicio, "fim", fim),
                "mensagem", mensagem
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Validação diária para verificar se pode fazer check-in
     * GET /api/check-in-humor/validacao-diaria
     */
    @GetMapping("/validacao-diaria")
    public ResponseEntity<Map<String, Object>> validacaoDiaria() {
        Integer usuarioId = currentUserService.getCurrentUserId();
        CheckHumorService.ValidacaoResult validacao =
                checkHumorService.validarPodeRealizarCheckIn(usuarioId);

        Map<String, Object> response = Map.of(
                "podeRealizar", validacao.podeRealizar(),
                "mensagem", validacao.mensagem()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Obter menu de emoções personalizado
     * GET /api/check-in-humor/menu-emocoes
     */
    @GetMapping("/menu-emocoes")
    public ResponseEntity<Map<String, Object>> obterMenuEmocoes() {
        try {
            Integer usuarioId = currentUserService.getCurrentUserId();
            String menuEmocoes = checkHumorService.gerarMenuEmocoes(usuarioId);

            Map<String, Object> response = Map.of(
                    "menuEmocoes", menuEmocoes
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 
