package com.exemplo.bancoalcatteia.controller;

import com.exemplo.bancoalcatteia.dto.KanbanTarefaDTO;
import com.exemplo.bancoalcatteia.service.KanbanTarefaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Controller para gerenciamento de tarefas do Kanban
 * API completa pronta para uso no frontend
 */
@RestController
@RequestMapping("/api/kanban/tarefas")
@CrossOrigin(origins = "*", maxAge = 3600)
public class KanbanTarefaController {
    
    private final KanbanTarefaService kanbanTarefaService;

    public KanbanTarefaController(KanbanTarefaService kanbanTarefaService) {
        this.kanbanTarefaService = kanbanTarefaService;
    }

    /**
     * Listar todas as tarefas
     * GET /api/kanban/tarefas
     */
    @GetMapping
    public ResponseEntity<List<KanbanTarefaDTO>> listarTodos() {
        List<KanbanTarefaDTO> kanbanTarefas = kanbanTarefaService.listarTodos();
        return ResponseEntity.ok(kanbanTarefas);
    }

    /**
     * Listar tarefas por categoria
     * GET /api/kanban/tarefas/categoria/{categoriaId}
     */
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<KanbanTarefaDTO>> listarPorCategoria(@PathVariable Integer categoriaId) {
        List<KanbanTarefaDTO> tarefas = kanbanTarefaService.listarPorCategoria(categoriaId);
        return ResponseEntity.ok(tarefas);
    }

    /**
     * Listar tarefas com filtros
     * GET /api/kanban/tarefas/filtros?categoriaId={id}&status={status}&responsavelId={id}&prioridade={prioridade}
     */
    @GetMapping("/filtros")
    public ResponseEntity<List<KanbanTarefaDTO>> listarPorFiltros(
            @RequestParam(required = false) Integer categoriaId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer responsavelId,
            @RequestParam(required = false) String prioridade) {
        
        List<KanbanTarefaDTO> tarefas = kanbanTarefaService.listarPorFiltros(categoriaId, status, responsavelId, prioridade);
        return ResponseEntity.ok(tarefas);
    }

    /**
     * Listar tarefas atrasadas
     * GET /api/kanban/tarefas/atrasadas
     */
    @GetMapping("/atrasadas")
    public ResponseEntity<List<KanbanTarefaDTO>> listarTarefasAtrasadas() {
        List<KanbanTarefaDTO> tarefas = kanbanTarefaService.listarTarefasAtrasadas();
        return ResponseEntity.ok(tarefas);
    }

    /**
     * Listar tarefas com entrega próxima
     * GET /api/kanban/tarefas/entrega-proxima
     */
    @GetMapping("/entrega-proxima")
    public ResponseEntity<List<KanbanTarefaDTO>> listarTarefasComEntregaProxima() {
        List<KanbanTarefaDTO> tarefas = kanbanTarefaService.listarTarefasComEntregaProxima();
        return ResponseEntity.ok(tarefas);
    }

    /**
     * Listar minhas tarefas (usuário logado)
     * GET /api/kanban/tarefas/minhas
     */
    @GetMapping("/minhas")
    public ResponseEntity<List<KanbanTarefaDTO>> listarMinhasTarefas() {
        List<KanbanTarefaDTO> tarefas = kanbanTarefaService.listarMinhasTarefas();
        return ResponseEntity.ok(tarefas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<KanbanTarefaDTO> buscarPorId(@PathVariable Integer id) {
        KanbanTarefaDTO kanbanTarefa = kanbanTarefaService.buscarPorId(id);
        return ResponseEntity.ok(kanbanTarefa);
    }

    @PostMapping
    public ResponseEntity<KanbanTarefaDTO> criar(@Valid @RequestBody KanbanTarefaDTO kanbanTarefaDTO) {
        KanbanTarefaDTO kanbanTarefaCriada = kanbanTarefaService.criar(kanbanTarefaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(kanbanTarefaCriada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<KanbanTarefaDTO> atualizar(@PathVariable Integer id, @Valid @RequestBody KanbanTarefaDTO kanbanTarefaDTO) {
        KanbanTarefaDTO kanbanTarefaAtualizada = kanbanTarefaService.atualizar(id, kanbanTarefaDTO);
        return ResponseEntity.ok(kanbanTarefaAtualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        kanbanTarefaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Alterar status da tarefa
     * PUT /api/kanban/tarefas/{id}/status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<KanbanTarefaDTO> atualizarStatus(@PathVariable Integer id, @RequestBody Map<String, String> statusData) {
        try {
            String novoStatus = statusData.get("status");
            KanbanTarefaDTO tarefaAtualizada = kanbanTarefaService.atualizarStatus(id, novoStatus);
            return ResponseEntity.ok(tarefaAtualizada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Atribuir responsável à tarefa
     * PUT /api/kanban/tarefas/{id}/responsavel
     */
    @PutMapping("/{id}/responsavel")
    public ResponseEntity<KanbanTarefaDTO> atribuirResponsavel(@PathVariable Integer id, @RequestBody Map<String, Integer> responsavelData) {
        try {
            Integer responsavelId = responsavelData.get("responsavelId");
            KanbanTarefaDTO tarefaAtualizada = kanbanTarefaService.atribuirResponsavel(id, responsavelId);
            return ResponseEntity.ok(tarefaAtualizada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Remover responsável da tarefa
     * DELETE /api/kanban/tarefas/{id}/responsavel
     */
    @DeleteMapping("/{id}/responsavel")
    public ResponseEntity<KanbanTarefaDTO> removerResponsavel(@PathVariable Integer id) {
        try {
            KanbanTarefaDTO tarefaAtualizada = kanbanTarefaService.atribuirResponsavel(id, null);
            return ResponseEntity.ok(tarefaAtualizada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Reordenar tarefas em uma categoria
     * PUT /api/kanban/tarefas/categoria/{categoriaId}/reordenar
     */
    @PutMapping("/categoria/{categoriaId}/reordenar")
    public ResponseEntity<List<KanbanTarefaDTO>> reordenarTarefas(@PathVariable Integer categoriaId, @RequestBody Map<String, List<Integer>> payload) {
        try {
            List<Integer> idsOrdenados = payload.get("idsOrdenados");
            List<KanbanTarefaDTO> tarefasReordenadas = kanbanTarefaService.reordenarTarefas(categoriaId, idsOrdenados);
            return ResponseEntity.ok(tarefasReordenadas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Adicionar horas trabalhadas
     * POST /api/kanban/tarefas/{id}/horas
     */
    @PostMapping("/{id}/horas")
    public ResponseEntity<KanbanTarefaDTO> adicionarHorasTrabalhadas(@PathVariable Integer id, @RequestBody Map<String, BigDecimal> horasData) {
        try {
            BigDecimal horas = horasData.get("horas");
            KanbanTarefaDTO tarefaAtualizada = kanbanTarefaService.adicionarHorasTrabalhadas(id, horas);
            return ResponseEntity.ok(tarefaAtualizada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Endpoints para compatibilidade com código antigo (deprecated)
    
    /**
     * @deprecated Use /api/kanban/tarefas/{id}/responsavel
     */
    @PostMapping("/{id}/assign")
    public ResponseEntity<Map<String, String>> atribuirUsuario(@PathVariable Integer id, @RequestBody Map<String, Object> userData) {
        try {
            Map<String, Object> user = (Map<String, Object>) userData.get("user");
            Integer userId = Integer.valueOf(user.get("id").toString());
            
            kanbanTarefaService.atribuirResponsavel(id, userId);
            
            return ResponseEntity.ok(Map.of(
                "message", "Usuário atribuído com sucesso",
                "userId", userId.toString(),
                "userName", user.get("name").toString()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Erro ao atribuir usuário"));
        }
    }

    /**
     * @deprecated Use DELETE /api/kanban/tarefas/{id}/responsavel
     */
    @PostMapping("/{id}/unassign")
    public ResponseEntity<Map<String, String>> removerUsuario(@PathVariable Integer id, @RequestBody Map<String, Object> userData) {
        try {
            kanbanTarefaService.atribuirResponsavel(id, null);
            
            return ResponseEntity.ok(Map.of(
                "message", "Usuário removido com sucesso"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Erro ao remover usuário"));
        }
    }

    /**
     * @deprecated Implementar sistema de comentários completo
     */
    @PostMapping("/{id}/comments")
    public ResponseEntity<Map<String, String>> adicionarComentario(@PathVariable Integer id, @RequestBody Map<String, Object> comentarioData) {
        String texto = (String) comentarioData.get("text");
        String autor = (String) comentarioData.get("author");
        
        // TODO: Implementar sistema de comentários completo
        Map<String, String> response = Map.of(
            "message", "Comentário adicionado com sucesso",
            "text", texto,
            "author", autor
        );
        return ResponseEntity.ok(response);
    }
}
