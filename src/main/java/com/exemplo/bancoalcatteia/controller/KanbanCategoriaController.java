package com.exemplo.bancoalcatteia.controller;

import com.exemplo.bancoalcatteia.dto.KanbanCategoriaDTO;
import com.exemplo.bancoalcatteia.service.KanbanCategoriaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Controller para gerenciamento de categorias do Kanban
 * Endpoints organizados para facilitar uso no frontend
 */
@RestController
@RequestMapping("/api/kanban/categorias")
public class KanbanCategoriaController {
    
    private final KanbanCategoriaService kanbanCategoriaService;

    public KanbanCategoriaController(KanbanCategoriaService kanbanCategoriaService) {
        this.kanbanCategoriaService = kanbanCategoriaService;
    }

    /**
     * Listar todas as categorias ativas
     * GET /api/kanban/categorias
     */
    @GetMapping
    public ResponseEntity<List<KanbanCategoriaDTO>> listarTodos() {
        List<KanbanCategoriaDTO> categorias = kanbanCategoriaService.listarTodos();
        return ResponseEntity.ok(categorias);
    }

    /**
     * Listar categorias do usuário logado
     * GET /api/kanban/categorias/minhas
     */
    @GetMapping("/minhas")
    public ResponseEntity<List<KanbanCategoriaDTO>> listarMinhasCategorias() {
        List<KanbanCategoriaDTO> categorias = kanbanCategoriaService.listarMinhasCategorias();
        return ResponseEntity.ok(categorias);
    }

    /**
     * Listar categorias por time
     * GET /api/kanban/categorias/time/{timeId}
     */
    @GetMapping("/time/{timeId}")
    public ResponseEntity<List<KanbanCategoriaDTO>> listarCategoriasPorTime(@PathVariable Integer timeId) {
        List<KanbanCategoriaDTO> categorias = kanbanCategoriaService.listarCategoriasPorTime(timeId);
        return ResponseEntity.ok(categorias);
    }

    /**
     * Buscar categoria por ID
     * GET /api/kanban/categorias/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<KanbanCategoriaDTO> buscarPorId(@PathVariable Integer id) {
        KanbanCategoriaDTO categoria = kanbanCategoriaService.buscarPorId(id);
        return ResponseEntity.ok(categoria);
    }

    /**
     * Criar nova categoria
     * POST /api/kanban/categorias
     */
    @PostMapping
    public ResponseEntity<KanbanCategoriaDTO> criar(@Valid @RequestBody KanbanCategoriaDTO categoriaDTO) {
        KanbanCategoriaDTO categoriaCriada = kanbanCategoriaService.criar(categoriaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaCriada);
    }

    /**
     * Atualizar categoria existente
     * PUT /api/kanban/categorias/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<KanbanCategoriaDTO> atualizar(@PathVariable Integer id, @Valid @RequestBody KanbanCategoriaDTO categoriaDTO) {
        KanbanCategoriaDTO categoriaAtualizada = kanbanCategoriaService.atualizar(id, categoriaDTO);
        return ResponseEntity.ok(categoriaAtualizada);
    }

    /**
     * Excluir categoria (soft delete)
     * DELETE /api/kanban/categorias/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        kanbanCategoriaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Reordenar categorias
     * PUT /api/kanban/categorias/reordenar
     */
    @PutMapping("/reordenar")
    public ResponseEntity<List<KanbanCategoriaDTO>> reordenar(@RequestBody Map<String, List<Integer>> payload) {
        List<Integer> idsOrdenados = payload.get("idsOrdenados");
        List<KanbanCategoriaDTO> categoriasReordenadas = kanbanCategoriaService.reordenar(idsOrdenados);
        return ResponseEntity.ok(categoriasReordenadas);
    }
}
