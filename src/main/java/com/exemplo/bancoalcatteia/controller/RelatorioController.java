package com.exemplo.bancoalcatteia.controller;

import com.exemplo.bancoalcatteia.dto.RelatorioDTO;
import com.exemplo.bancoalcatteia.model.Relatorio;
import com.exemplo.bancoalcatteia.model.Time;
import com.exemplo.bancoalcatteia.model.Usuarios;
import com.exemplo.bancoalcatteia.repository.RelatorioRepository;
import com.exemplo.bancoalcatteia.repository.TimeRepository;
import com.exemplo.bancoalcatteia.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/relatorios")
public class RelatorioController {
    private final RelatorioRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final TimeRepository timeRepository;

    public RelatorioController(RelatorioRepository repository, UsuarioRepository usuarioRepository, TimeRepository timeRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.timeRepository = timeRepository;
    }

    @GetMapping
    public ResponseEntity<List<RelatorioDTO>> listarTodos() {
        List<RelatorioDTO> relatorios = repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(relatorios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RelatorioDTO> buscarPorId(@PathVariable Integer id) {
        Relatorio relatorio = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Relatório com ID " + id + " não encontrado"));
        return ResponseEntity.ok(toDTO(relatorio));
    }

    @PostMapping
    public ResponseEntity<RelatorioDTO> criar(@Valid @RequestBody RelatorioDTO dto) {
        Relatorio relatorio = new Relatorio();
        relatorio.setTipo(dto.getTipo());
        if (dto.getGeradoPorId() != null) {
            Usuarios usuario = usuarioRepository.findById(dto.getGeradoPorId().intValue())
                    .orElseThrow(() -> new EntityNotFoundException("Usuário com ID " + dto.getGeradoPorId() + " não encontrado"));
            relatorio.setGeradoPor(usuario);
        }
        if (dto.getTimeId() != null) {
            Time time = timeRepository.findById(dto.getTimeId().intValue())
                    .orElseThrow(() -> new EntityNotFoundException("Time com ID " + dto.getTimeId() + " não encontrado"));
            relatorio.setTime(time);
        }
        relatorio.setDataGeracao(LocalDateTime.now());
        relatorio.setConteudo(dto.getConteudo());
        Relatorio salvo = repository.save(relatorio);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(salvo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Relatório com ID " + id + " não encontrado");
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private RelatorioDTO toDTO(Relatorio relatorio) {
        RelatorioDTO dto = new RelatorioDTO();
        dto.setId(relatorio.getId());
        dto.setTipo(relatorio.getTipo());
        dto.setGeradoPorId(relatorio.getGeradoPor() != null ? relatorio.getGeradoPor().getId().longValue() : null);
        dto.setTimeId(relatorio.getTime() != null ? relatorio.getTime().getId().longValue() : null);
        dto.setDataGeracao(relatorio.getDataGeracao() != null ? relatorio.getDataGeracao().toString() : null);
        dto.setConteudo(relatorio.getConteudo());
        return dto;
    }
}
