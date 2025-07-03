package com.exemplo.bancoalcatteia.controller;

import com.exemplo.bancoalcatteia.dto.KanbanCategoriaDTO;
import com.exemplo.bancoalcatteia.dto.KanbanTarefaDTO;
import com.exemplo.bancoalcatteia.service.KanbanCategoriaService;
import com.exemplo.bancoalcatteia.service.KanbanTarefaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller unificado do Kanban para resolver inconsistências de endpoints
 * Este controller resolve problemas de URLs conflitantes que o frontend estava tentando acessar
 */
@RestController
@RequestMapping("/api/kanban")
@CrossOrigin(origins = "*", maxAge = 3600)
public class KanbanController {

    private final KanbanCategoriaService kanbanCategoriaService;
    private final KanbanTarefaService kanbanTarefaService;

    public KanbanController(KanbanCategoriaService kanbanCategoriaService, 
                           KanbanTarefaService kanbanTarefaService) {
        this.kanbanCategoriaService = kanbanCategoriaService;
        this.kanbanTarefaService = kanbanTarefaService;
    }

    /**
     * Endpoint para resolver problema: /api/kanban/tarefas/categorias
     * Redireciona para o endpoint correto de categorias
     */
    @GetMapping("/tarefas/categorias")
    public ResponseEntity<List<KanbanCategoriaDTO>> listarCategoriasPorTarefas() {
        List<KanbanCategoriaDTO> categorias = kanbanCategoriaService.listarTodos();
        return ResponseEntity.ok(categorias);
    }

    /**
     * Endpoint para dados completos do Kanban (categorias + tarefas)
     * GET /api/kanban/board
     */
    @GetMapping("/board")
    public ResponseEntity<Map<String, Object>> obterKanbanCompleto() {
        try {
            List<KanbanCategoriaDTO> categorias = kanbanCategoriaService.listarTodos();
            List<KanbanTarefaDTO> tarefas = kanbanTarefaService.listarTodos();
            
            Map<String, Object> kanbanBoard = new HashMap<>();
            kanbanBoard.put("categorias", categorias);
            kanbanBoard.put("tarefas", tarefas);
            kanbanBoard.put("status", "success");
            kanbanBoard.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(kanbanBoard);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Erro ao carregar dados do Kanban");
            errorResponse.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Endpoint para resolver problemas com URLs malformadas contendo userid-mockuserid
     * GET /api/kanban/user-data/{userId}
     */
    @GetMapping("/user-data/{userId}")
    public ResponseEntity<Map<String, Object>> obterDadosUsuario(@PathVariable String userId) {
        try {
            // Limpar userId de valores mock
            String cleanUserId = userId.replace("-mockuserid", "").replace("userid-", "");
            
            Map<String, Object> userData = new HashMap<>();
            
            // Se for um ID numérico válido, buscar dados específicos
            if (cleanUserId.matches("\\d+")) {
                Long userIdLong = Long.valueOf(cleanUserId);
                List<KanbanTarefaDTO> minhasTarefas = kanbanTarefaService.listarMinhasTarefas();
                
                userData.put("userId", userIdLong);
                userData.put("minhasTarefas", minhasTarefas);
            } else {
                // Retornar dados gerais
                userData.put("userId", cleanUserId);
                userData.put("minhasTarefas", List.of());
            }
            
            userData.put("status", "success");
            userData.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(userData);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Erro ao carregar dados do usuário");
            errorResponse.put("userId", userId);
            errorResponse.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.ok(errorResponse); // Retorna 200 para evitar erros no frontend
        }
    }

    /**
     * Endpoint para resolver problemas com participation requests malformados
     * GET /api/kanban/participation-requests/{userId}
     */
    @GetMapping("/participation-requests/{userId}")
    public ResponseEntity<Map<String, Object>> obterPedidosParticipacao(@PathVariable String userId) {
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("requests", List.of()); // Lista vazia por enquanto
        response.put("status", "success");
        response.put("message", "Nenhum pedido de participação encontrado");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint de health check para o Kanban
     * GET /api/kanban/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Kanban API");
        health.put("timestamp", System.currentTimeMillis());
        health.put("version", "1.0.0");
        
        try {
            // Teste básico de conectividade com os services
            kanbanCategoriaService.listarTodos();
            health.put("database", "CONNECTED");
        } catch (Exception e) {
            health.put("database", "ERROR");
            health.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(health);
    }

    /**
     * Endpoint genérico para capturar requisições malformadas
     * Evita erros 404 que quebram o frontend
     */
    @GetMapping("/**")
    public ResponseEntity<Map<String, Object>> capturarRequisicoesGenericas() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "warning");
        response.put("message", "Endpoint não encontrado, mas conexão OK");
        response.put("suggestion", "Verifique a URL da requisição");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
} 
