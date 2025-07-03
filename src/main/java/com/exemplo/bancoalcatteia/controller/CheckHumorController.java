package com.exemplo.bancoalcatteia.controller;

import com.exemplo.bancoalcatteia.constants.HumorConstants;
import com.exemplo.bancoalcatteia.dto.CheckHumorDTO;
import com.exemplo.bancoalcatteia.service.CheckHumorService;
import com.exemplo.bancoalcatteia.service.CurrentUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Controller responsável pelo CRUD básico de CheckHumor
 * Implementa Single Responsibility Principle para operações básicas de usuário
 */
@RestController
@RequestMapping("/api/check-humor")
public class CheckHumorController {
    
    private final CheckHumorService checkHumorService;
    private final CurrentUserService currentUserService;

    public CheckHumorController(CheckHumorService checkHumorService, 
                               CurrentUserService currentUserService) {
        this.checkHumorService = checkHumorService;
        this.currentUserService = currentUserService;
    }

    // === FUNCIONALIDADES DO USUÁRIO ===

    /**
     * Registrar humor do dia - qualquer usuário logado pode registrar
     * POST /api/check-humor/registrar
     */
    @PostMapping("/registrar")
    public ResponseEntity<CheckHumorDTO> registrarHumorDiario(@Valid @RequestBody CheckHumorDTO humorDTO) {
        try {
            CheckHumorDTO humorRegistrado = checkHumorService.registrarHumorDiario(humorDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(humorRegistrado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Buscar humor de hoje do usuário logado
     * GET /api/check-humor/meu-humor-hoje
     */
    @GetMapping("/meu-humor-hoje")
    public ResponseEntity<CheckHumorDTO> meuHumorHoje() {
        Integer usuarioId = currentUserService.getCurrentUserId();
        Optional<CheckHumorDTO> humor = checkHumorService.buscarHumorHoje(usuarioId);
        return humor.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Listar meus humores por período
     * GET /api/check-humor/meus-humores
     */
    @GetMapping("/meus-humores")
    public ResponseEntity<List<CheckHumorDTO>> listarMeusHumores(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        
        Integer usuarioId = currentUserService.getCurrentUserId();
        List<CheckHumorDTO> humores = checkHumorService.listarHumoresUsuario(usuarioId, inicio, fim);
        return ResponseEntity.ok(humores);
    }

    /**
     * Buscar comentários para cards laterais (todos os usuários)
     * GET /api/check-humor/comentarios
     */
    @GetMapping("/comentarios")
    public ResponseEntity<List<CheckHumorDTO>> buscarComentarios(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        
        // Se não especificar período, buscar últimos 15 dias
        if (inicio == null) {
            inicio = LocalDate.now().minusDays(HumorConstants.DIAS_COMENTARIOS_PADRAO);
        }
        if (fim == null) {
            fim = LocalDate.now();
        }
        
        List<CheckHumorDTO> comentarios = checkHumorService.buscarComentariosPeriodo(inicio, fim);
        return ResponseEntity.ok(comentarios);
    }

    // === FUNCIONALIDADES DE GESTORES (RH/LIDER) ===

    /**
     * Buscar humor do usuário hoje
     * GET /api/check-humor/hoje/{usuarioId}
     */
    @GetMapping("/hoje/{usuarioId}")
    public ResponseEntity<CheckHumorDTO> buscarHumorHoje(@PathVariable Integer usuarioId) {
        // Verificar se o usuário pode acessar esses dados
        if (!currentUserService.canAccessUserData(usuarioId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        Optional<CheckHumorDTO> humor = checkHumorService.buscarHumorHoje(usuarioId);
        return humor.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Listar humores de um usuário específico (apenas RH/LIDER)
     * GET /api/check-humor/usuarios/{usuarioId}
     */
    @GetMapping("/usuarios/{usuarioId}")
    public ResponseEntity<List<CheckHumorDTO>> listarHumoresUsuario(
            @PathVariable Integer usuarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        
        List<CheckHumorDTO> humores = checkHumorService.listarHumoresUsuario(usuarioId, inicio, fim);
        return ResponseEntity.ok(humores);
    }

    // === ENDPOINTS CRUD BÁSICOS (para manutenção) ===

    /**
     * Listar todos os registros com paginação (apenas RH/LIDER)
     * GET /api/check-humor
     */
    @GetMapping
    public ResponseEntity<Page<CheckHumorDTO>> listarTodos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "dataRegistro") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        
        // Validar parâmetros de paginação
        if (size > 100) size = 100; // Máximo 100 registros por página
        if (page < 0) page = 0;
        
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? 
            Sort.Direction.ASC : Sort.Direction.DESC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<CheckHumorDTO> humores = checkHumorService.listarTodosPaginado(pageable);
        
        return ResponseEntity.ok(humores);
    }

    /**
     * Buscar por ID (apenas RH/LIDER)
     * GET /api/check-humor/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<CheckHumorDTO> buscarPorId(@PathVariable Integer id) {
        try {
            CheckHumorDTO humor = checkHumorService.buscarPorId(id);
            return ResponseEntity.ok(humor);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Atualizar registro (apenas RH/LIDER)
     * PUT /api/check-humor/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<CheckHumorDTO> atualizar(@PathVariable Integer id, @Valid @RequestBody CheckHumorDTO humorDTO) {
        try {
            CheckHumorDTO humorAtualizado = checkHumorService.atualizar(id, humorDTO);
            return ResponseEntity.ok(humorAtualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Deletar registro (apenas RH/LIDER)
     * DELETE /api/check-humor/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        try {
            checkHumorService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
