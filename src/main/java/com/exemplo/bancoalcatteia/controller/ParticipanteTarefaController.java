package com.exemplo.bancoalcatteia.controller;

import com.exemplo.bancoalcatteia.dto.ParticipanteTarefaDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/participation-requests")
@CrossOrigin(origins = "*")
public class ParticipanteTarefaController {

    // Lista temporária para simular persistência - substituir por service real
    private final List<ParticipanteTarefaDTO> participacoesPendentes = new ArrayList<>();
    private Integer proximoId = 1;

    @PostMapping
    public ResponseEntity<Map<String, Object>> criarPedidoParticipacao(@RequestBody Map<String, Object> requestData) {
        Long tarefaId = Long.valueOf(requestData.get("taskId").toString());
        String requester = (String) requestData.get("requester");
        
        ParticipanteTarefaDTO participacao = new ParticipanteTarefaDTO();
        participacao.setId(proximoId++);
        participacao.setTarefaId(tarefaId);
        participacao.setUsuarioNome(requester);
        participacao.setStatusParticipacao("PENDENTE");
        participacao.setDataConvite(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        participacao.setAtivo(true);
        
        participacoesPendentes.add(participacao);
        
        Map<String, Object> response = Map.of(
            "id", participacao.getId(),
            "message", "Pedido de participação enviado com sucesso",
            "taskId", tarefaId,
            "requester", requester,
            "status", "PENDENTE"
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ParticipanteTarefaDTO>> listarPedidosPorUsuario(@RequestParam String userId) {
        // Por enquanto, filtra por nome do usuário (em uma implementação real, usaria ID)
        List<ParticipanteTarefaDTO> pedidosUsuario = participacoesPendentes.stream()
            .filter(p -> p.isPendente() && p.isAtivo())
            .toList();
            
        return ResponseEntity.ok(pedidosUsuario);
    }

    @PostMapping("/{requestId}/accept")
    public ResponseEntity<Map<String, String>> aceitarPedido(@PathVariable Long requestId) {
        ParticipanteTarefaDTO participacao = participacoesPendentes.stream()
            .filter(p -> p.getId().equals(requestId))
            .findFirst()
            .orElse(null);
            
        if (participacao != null) {
            participacao.setStatusParticipacao("ACEITO");
            participacao.setDataResposta(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            Map<String, String> response = Map.of(
                "message", "Pedido aceito com sucesso",
                "requestId", requestId.toString(),
                "status", "ACEITO"
            );
            return ResponseEntity.ok(response);
        }
        
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{requestId}/reject")
    public ResponseEntity<Map<String, String>> rejeitarPedido(@PathVariable Long requestId) {
        ParticipanteTarefaDTO participacao = participacoesPendentes.stream()
            .filter(p -> p.getId().equals(requestId))
            .findFirst()
            .orElse(null);
            
        if (participacao != null) {
            participacao.setStatusParticipacao("REJEITADO");
            participacao.setDataResposta(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            Map<String, String> response = Map.of(
                "message", "Pedido rejeitado com sucesso",
                "requestId", requestId.toString(),
                "status", "REJEITADO"
            );
            return ResponseEntity.ok(response);
        }
        
        return ResponseEntity.notFound().build();
    }
} 
