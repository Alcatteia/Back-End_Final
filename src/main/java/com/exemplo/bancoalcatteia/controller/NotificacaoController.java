package com.exemplo.bancoalcatteia.controller;

import com.exemplo.bancoalcatteia.dto.NotificacaoDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificacaoController {

    // Lista temporária para simular persistência - substituir por service real
    private final List<NotificacaoDTO> notificacoes = new ArrayList<>();
    private Integer proximoId = 1;

    @GetMapping
    public ResponseEntity<List<NotificacaoDTO>> listarNotificacoesPorUsuario(@RequestParam Long userId) {
        // Filtra notificações por usuário
        List<NotificacaoDTO> notificacesUsuario = notificacoes.stream()
            .filter(n -> n.getUsuarioNotificadoId().equals(userId))
            .toList();
            
        return ResponseEntity.ok(notificacesUsuario);
    }

    @PostMapping("/{notificationId}/read")
    public ResponseEntity<Map<String, String>> marcarComoLida(@PathVariable Long notificationId) {
        NotificacaoDTO notificacao = notificacoes.stream()
            .filter(n -> n.getId().equals(notificationId))
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundException("Notificação não encontrada com ID: " + notificationId));
            
        notificacao.setConfirmacaoLeitura(true);
        
        Map<String, String> response = Map.of(
            "message", "Notificação marcada como lida",
            "notificationId", notificationId.toString(),
            "status", "read"
        );
        return ResponseEntity.ok(response);
    }

    // Método auxiliar para criar notificações (usado internamente)
    @PostMapping
    public ResponseEntity<NotificacaoDTO> criarNotificacao(@RequestBody NotificacaoDTO notificacaoDTO) {
        notificacaoDTO.setId(proximoId++);
        notificacaoDTO.setDataCriacao(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        notificacaoDTO.setConfirmacaoLeitura(false);
        
        notificacoes.add(notificacaoDTO);
        
        return ResponseEntity.ok(notificacaoDTO);
    }

    // Endpoint para listar todas as notificações (para debug/admin)
    @GetMapping("/all")
    public ResponseEntity<List<NotificacaoDTO>> listarTodasNotificacoes() {
        return ResponseEntity.ok(notificacoes);
    }

    // Endpoint para marcar todas as notificações de um usuário como lidas
    @PostMapping("/mark-all-read")
    public ResponseEntity<Map<String, String>> marcarTodasComoLidas(@RequestParam Long userId) {
        long count = notificacoes.stream()
            .filter(n -> n.getUsuarioNotificadoId().equals(userId) && n.isNaoLida())
            .peek(n -> n.setConfirmacaoLeitura(true))
            .count();
            
        Map<String, String> response = Map.of(
            "message", count + " notificações marcadas como lidas",
            "userId", userId.toString(),
            "count", String.valueOf(count)
        );
        
        return ResponseEntity.ok(response);
    }
} 
