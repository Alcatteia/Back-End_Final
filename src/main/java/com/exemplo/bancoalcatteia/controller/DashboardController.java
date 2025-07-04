package com.exemplo.bancoalcatteia.controller;

import com.exemplo.bancoalcatteia.dto.DashboardDTO;
import com.exemplo.bancoalcatteia.model.Dashboard;
import com.exemplo.bancoalcatteia.model.Usuarios;
import com.exemplo.bancoalcatteia.repository.DashboardRepository;
import com.exemplo.bancoalcatteia.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dashboards")
public class DashboardController {
    private final DashboardRepository repository;
    private final UsuarioRepository usuarioRepository;

    public DashboardController(DashboardRepository repository, UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public ResponseEntity<List<DashboardDTO>> listarTodos() {
        List<DashboardDTO> dashboards = repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dashboards);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DashboardDTO> buscarPorId(@PathVariable Integer id) {
        Dashboard dashboard = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dashboard com ID " + id + " nao encontrado"));
        return ResponseEntity.ok(toDTO(dashboard));
    }

    @PostMapping
    public ResponseEntity<DashboardDTO> criar(@Valid @RequestBody DashboardDTO dto) {
        Dashboard dashboard = new Dashboard();
        if (dto.getUsuarioId() != null) {
            Usuarios usuario = usuarioRepository.findById(dto.getUsuarioId().intValue())
                    .orElseThrow(() -> new EntityNotFoundException("Usuário com ID " + dto.getUsuarioId() + " não encontrado"));
            dashboard.setUsuario(usuario);
        }
        dashboard.setTipoDashboard(dto.getTipoDashboard());
        dashboard.setTitulo(dto.getTitulo());
        Dashboard salvo = repository.save(dashboard);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(salvo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Dashboard com ID " + id + " nao encontrado");
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private DashboardDTO toDTO(Dashboard dashboard) {
        DashboardDTO dto = new DashboardDTO();
        dto.setId(dashboard.getId());
        dto.setUsuarioId(dashboard.getUsuario() != null ? dashboard.getUsuario().getId().longValue() : null);
        dto.setTipoDashboard(dashboard.getTipoDashboard());
        return dto;
    }
}
