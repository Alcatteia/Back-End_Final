package com.exemplo.bancoalcatteia.service;

import com.exemplo.bancoalcatteia.dto.KanbanCategoriaDTO;
import com.exemplo.bancoalcatteia.model.KanbanCategoria;
import com.exemplo.bancoalcatteia.model.Usuarios;
import com.exemplo.bancoalcatteia.model.Time;
import com.exemplo.bancoalcatteia.repository.KanbanCategoriaRepository;
import com.exemplo.bancoalcatteia.repository.UsuarioRepository;
import com.exemplo.bancoalcatteia.repository.TimeRepository;
import com.exemplo.bancoalcatteia.service.CurrentUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class KanbanCategoriaService {

    private final KanbanCategoriaRepository kanbanCategoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final TimeRepository timeRepository;
    private final CurrentUserService currentUserService;

    public KanbanCategoriaService(KanbanCategoriaRepository kanbanCategoriaRepository,
                                 UsuarioRepository usuarioRepository,
                                 TimeRepository timeRepository,
                                 CurrentUserService currentUserService) {
        this.kanbanCategoriaRepository = kanbanCategoriaRepository;
        this.usuarioRepository = usuarioRepository;
        this.timeRepository = timeRepository;
        this.currentUserService = currentUserService;
    }

    /**
     * Lista todas as categorias ativas
     */
    public List<KanbanCategoriaDTO> listarTodos() {
        List<KanbanCategoria> categorias = kanbanCategoriaRepository.findByAtivoTrueOrderByOrdemAscNomeAsc();
        return convertToDTOsComEstatisticas(categorias);
    }

    /**
     * Lista categorias do usuário logado
     */
    public List<KanbanCategoriaDTO> listarMinhasCategorias() {
        Integer usuarioId = currentUserService.getCurrentUserId();
        List<KanbanCategoria> categorias = kanbanCategoriaRepository.findByCriadoPorIdAndAtivoTrueOrderByOrdemAscNomeAsc(usuarioId);
        return convertToDTOsComEstatisticas(categorias);
    }

    /**
     * Lista categorias por time
     */
    public List<KanbanCategoriaDTO> listarCategoriasPorTime(Integer timeId) {
        List<KanbanCategoria> categorias = kanbanCategoriaRepository.findByTimeIdAndAtivoTrueOrderByOrdemAscNomeAsc(timeId);
        return convertToDTOsComEstatisticas(categorias);
    }

    public KanbanCategoriaDTO buscarPorId(Integer id) {
        KanbanCategoria categoria = kanbanCategoriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria Kanban com ID " + id + " não encontrada"));
        return convertToDTOComEstatisticas(categoria);
    }

    public KanbanCategoriaDTO criar(KanbanCategoriaDTO categoriaDTO) {
        validarCategoria(categoriaDTO);
        
        KanbanCategoria categoria = convertToEntity(categoriaDTO);
        
        // Definir ordem automaticamente se não informada
        if (categoria.getOrdem() == null) {
            if (categoria.getCriadoPor() != null) {
                Integer ordem = kanbanCategoriaRepository.findNextOrdemByUsuario(categoria.getCriadoPor().getId());
                categoria.setOrdem(ordem != null ? ordem : 1);
            } else if (categoria.getTime() != null) {
                Integer ordem = kanbanCategoriaRepository.findNextOrdemByTime(categoria.getTime().getId());
                categoria.setOrdem(ordem != null ? ordem : 1);
            } else {
                categoria.setOrdem(1);
            }
        }
        
        KanbanCategoria categoriaSalva = kanbanCategoriaRepository.save(categoria);
        return convertToDTOComEstatisticas(categoriaSalva);
    }

    public KanbanCategoriaDTO atualizar(Integer id, KanbanCategoriaDTO categoriaDTO) {
        KanbanCategoria categoriaExistente = kanbanCategoriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria Kanban com ID " + id + " não encontrada"));

        validarCategoria(categoriaDTO);
        
        // Atualizar campos
        categoriaExistente.setNome(categoriaDTO.getNome());
        categoriaExistente.setCor(categoriaDTO.getCor());
        categoriaExistente.setDescricao(categoriaDTO.getDescricao());
        
        if (categoriaDTO.getOrdem() != null) {
            categoriaExistente.setOrdem(categoriaDTO.getOrdem());
        }
        
        // Atualizar relacionamentos se informados
        if (categoriaDTO.getUsuarioId() != null) {
            Usuarios usuarios = usuarioRepository.findById(categoriaDTO.getUsuarioId())
                    .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
            categoriaExistente.setCriadoPor(usuarios);
        }
        
        if (categoriaDTO.getTimeId() != null) {
            Time time = timeRepository.findById(categoriaDTO.getTimeId())
                    .orElseThrow(() -> new EntityNotFoundException("Time não encontrado"));
            categoriaExistente.setTime(time);
        }

        KanbanCategoria categoriaAtualizada = kanbanCategoriaRepository.save(categoriaExistente);
        return convertToDTOComEstatisticas(categoriaAtualizada);
    }

    /**
     * Soft delete - marca como inativo
     */
    public void deletar(Integer id) {
        KanbanCategoria categoria = kanbanCategoriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria Kanban com ID " + id + " não encontrada"));
        
        categoria.setAtivo(false);
        kanbanCategoriaRepository.save(categoria);
    }

    /**
     * Reordenar categorias
     */
    public List<KanbanCategoriaDTO> reordenar(List<Integer> idsOrdenados) {
        for (int i = 0; i < idsOrdenados.size(); i++) {
            Integer id = idsOrdenados.get(i);
            KanbanCategoria categoria = kanbanCategoriaRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Categoria com ID " + id + " não encontrada"));
            categoria.setOrdem(i + 1);
            kanbanCategoriaRepository.save(categoria);
        }
        
        return listarTodos();
    }

    private void validarCategoria(KanbanCategoriaDTO categoriaDTO) {
        if (categoriaDTO.getNome() == null || categoriaDTO.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da categoria é obrigatório");
        }
        
        // Validar duplicação de nome por usuário
        if (categoriaDTO.getUsuarioId() != null) {
            if (kanbanCategoriaRepository.existsByNomeAndCriadoPorIdAndAtivoTrue(
                    categoriaDTO.getNome(), categoriaDTO.getUsuarioId())) {
                throw new IllegalArgumentException("Já existe uma categoria com este nome para este usuário");
            }
        }
        
        // Validar duplicação de nome por time
        if (categoriaDTO.getTimeId() != null) {
            if (kanbanCategoriaRepository.existsByNomeAndTimeIdAndAtivoTrue(
                    categoriaDTO.getNome(), categoriaDTO.getTimeId())) {
                throw new IllegalArgumentException("Já existe uma categoria com este nome para este time");
            }
        }
    }

    private List<KanbanCategoriaDTO> convertToDTOsComEstatisticas(List<KanbanCategoria> categorias) {
        // Buscar estatísticas em batch
        List<Object[]> estatisticasTotal = kanbanCategoriaRepository.countTarefasByCategoria();
        List<Object[]> estatisticasAtivas = kanbanCategoriaRepository.countTarefasAtivasByCategoria();
        
        Map<Integer, Long> totalPorCategoria = estatisticasTotal.stream()
                .collect(Collectors.toMap(row -> ((Number) row[0]).intValue(), row -> (Long) row[1]));
        
        Map<Integer, Long> ativasPorCategoria = estatisticasAtivas.stream()
                .collect(Collectors.toMap(row -> ((Number) row[0]).intValue(), row -> (Long) row[1]));
        
        return categorias.stream()
                .map(categoria -> {
                    KanbanCategoriaDTO dto = convertToDTO(categoria);
                    
                    // Adicionar estatísticas
                    Long total = totalPorCategoria.getOrDefault(categoria.getId(), 0L);
                    Long ativas = ativasPorCategoria.getOrDefault(categoria.getId(), 0L);
                    
                    dto.setTotalTarefas(total);
                    dto.setTarefasAtivas(ativas);
                    dto.setTarefasConcluidas(total - ativas);
                    
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private KanbanCategoriaDTO convertToDTOComEstatisticas(KanbanCategoria categoria) {
        return convertToDTOsComEstatisticas(List.of(categoria)).get(0);
    }

    private KanbanCategoriaDTO convertToDTO(KanbanCategoria categoria) {
        return KanbanCategoriaDTO.builder()
                .id(categoria.getId())
                .nome(categoria.getNome())
                .descricao(categoria.getDescricao())
                .cor(categoria.getCor())
                .ordem(categoria.getOrdem())
                .usuarioId(categoria.getCriadoPor() != null ? categoria.getCriadoPor().getId() : null)
                .usuarioNome(categoria.getCriadoPor() != null ? categoria.getCriadoPor().getNome() : null)
                .timeId(categoria.getTime() != null ? categoria.getTime().getId() : null)
                .timeNome(categoria.getTime() != null ? categoria.getTime().getNomeTime() : null)
                .dataCriacao(categoria.getDataCriacao())
                .ativo(categoria.getAtivo())
                .build();
    }

    private KanbanCategoria convertToEntity(KanbanCategoriaDTO dto) {
        KanbanCategoria categoria = new KanbanCategoria();
        categoria.setNome(dto.getNome());
        categoria.setCor(dto.getCor());
        categoria.setDescricao(dto.getDescricao());
        categoria.setOrdem(dto.getOrdem());
        
        if (dto.getUsuarioId() != null) {
            Usuarios usuarios = usuarioRepository.findById(dto.getUsuarioId())
                    .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
            categoria.setCriadoPor(usuarios);
        }
        
        if (dto.getTimeId() != null) {
            Time time = timeRepository.findById(dto.getTimeId())
                    .orElseThrow(() -> new EntityNotFoundException("Time não encontrado"));
            categoria.setTime(time);
        }
        
        return categoria;
    }
} 

