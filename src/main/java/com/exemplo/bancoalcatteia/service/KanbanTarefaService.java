package com.exemplo.bancoalcatteia.service;

import com.exemplo.bancoalcatteia.dto.KanbanTarefaDTO;
import com.exemplo.bancoalcatteia.model.KanbanCategoria;
import com.exemplo.bancoalcatteia.model.KanbanTarefa;
import com.exemplo.bancoalcatteia.model.Usuarios;
import com.exemplo.bancoalcatteia.repository.KanbanCategoriaRepository;
import com.exemplo.bancoalcatteia.repository.KanbanTarefaRepository;
import com.exemplo.bancoalcatteia.repository.UsuarioRepository;
import com.exemplo.bancoalcatteia.service.CurrentUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class KanbanTarefaService {

    private final KanbanTarefaRepository kanbanTarefaRepository;
    private final KanbanCategoriaRepository kanbanCategoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CurrentUserService currentUserService;

    public KanbanTarefaService(KanbanTarefaRepository kanbanTarefaRepository, 
                              KanbanCategoriaRepository kanbanCategoriaRepository,
                              UsuarioRepository usuarioRepository,
                              CurrentUserService currentUserService) {
        this.kanbanTarefaRepository = kanbanTarefaRepository;
        this.kanbanCategoriaRepository = kanbanCategoriaRepository;
        this.usuarioRepository = usuarioRepository;
        this.currentUserService = currentUserService;
    }

    public List<KanbanTarefaDTO> listarTodos() {
        return kanbanTarefaRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar tarefas por categoria
     */
    public List<KanbanTarefaDTO> listarPorCategoria(Integer categoriaId) {
        return kanbanTarefaRepository.findByCategoriaIdOrderByOrdemAscPrioridadeDescDataCriacaoAsc(categoriaId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar tarefas por filtros
     */
    public List<KanbanTarefaDTO> listarPorFiltros(Integer categoriaId, String status, Integer responsavelId, String prioridade) {
        KanbanTarefa.Status statusEnum = status != null ? KanbanTarefa.Status.valueOf(status) : null;
        KanbanTarefa.Prioridade prioridadeEnum = prioridade != null ? KanbanTarefa.Prioridade.valueOf(prioridade) : null;
        
        return kanbanTarefaRepository.findByFiltros(categoriaId, statusEnum, responsavelId, prioridadeEnum)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar tarefas atrasadas
     */
    public List<KanbanTarefaDTO> listarTarefasAtrasadas() {
        return kanbanTarefaRepository.findTarefasAtrasadas(LocalDate.now())
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar tarefas com entrega próxima (próximos 7 dias)
     */
    public List<KanbanTarefaDTO> listarTarefasComEntregaProxima() {
        LocalDate hoje = LocalDate.now();
        LocalDate dataLimite = hoje.plusDays(7);
        
        return kanbanTarefaRepository.findTarefasComEntregaProxima(hoje, dataLimite)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar tarefas do usuário logado
     */
    public List<KanbanTarefaDTO> listarMinhasTarefas() {
        Integer usuarioId = currentUserService.getCurrentUserId();
        return kanbanTarefaRepository.findByResponsavelIdOrderByPrioridadeDescDataCriacaoAsc(usuarioId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public KanbanTarefaDTO buscarPorId(Integer id) {
        KanbanTarefa kanbanTarefa = kanbanTarefaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tarefa Kanban com ID " + id + " não encontrada"));
        return convertToDTO(kanbanTarefa);
    }

    public KanbanTarefaDTO criar(KanbanTarefaDTO kanbanTarefaDTO) {
        validarKanbanTarefa(kanbanTarefaDTO);
        
        KanbanTarefa kanbanTarefa = convertToEntity(kanbanTarefaDTO);
        
        // Definir criador como usuário logado
        Integer criadoPorId = currentUserService.getCurrentUserId();
        Usuarios criadoPor = usuarioRepository.findById(criadoPorId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário criador não encontrado"));
        kanbanTarefa.setCriadoPor(criadoPor);
        
        // Definir ordem automaticamente se não informada
        if (kanbanTarefa.getOrdem() == null) {
            Integer proximaOrdem = kanbanTarefaRepository.findNextOrdemByCategoria(kanbanTarefa.getCategoria().getId());
            kanbanTarefa.setOrdem(proximaOrdem);
        }
        
        KanbanTarefa kanbanTarefaSalva = kanbanTarefaRepository.save(kanbanTarefa);
        return convertToDTO(kanbanTarefaSalva);
    }

    public KanbanTarefaDTO atualizar(Integer id, KanbanTarefaDTO kanbanTarefaDTO) {
        KanbanTarefa kanbanTarefaExistente = kanbanTarefaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tarefa Kanban com ID " + id + " não encontrada"));

        validarKanbanTarefa(kanbanTarefaDTO);
        
        // Atualizar campos básicos
        kanbanTarefaExistente.setTitulo(kanbanTarefaDTO.getTitulo());
        kanbanTarefaExistente.setDescricao(kanbanTarefaDTO.getDescricao());
        
        // Atualizar categoria
        if (kanbanTarefaDTO.getCategoriaId() != null) {
            KanbanCategoria categoria = kanbanCategoriaRepository.findById(kanbanTarefaDTO.getCategoriaId())
                    .orElseThrow(() -> new EntityNotFoundException("Categoria com ID " + kanbanTarefaDTO.getCategoriaId() + " não encontrada"));
            kanbanTarefaExistente.setCategoria(categoria);
        }
        
        // Atualizar responsável
        if (kanbanTarefaDTO.getResponsavelId() != null) {
            Usuarios responsavel = usuarioRepository.findById(kanbanTarefaDTO.getResponsavelId())
                    .orElseThrow(() -> new EntityNotFoundException("Usuário responsável com ID " + kanbanTarefaDTO.getResponsavelId() + " não encontrado"));
            kanbanTarefaExistente.setResponsavel(responsavel);
        }
        
        // Atualizar datas
        kanbanTarefaExistente.setDataEntrega(kanbanTarefaDTO.getDataEntrega());
        
        // Atualizar enum fields
        if (kanbanTarefaDTO.getPrioridade() != null) {
            kanbanTarefaExistente.setPrioridade(KanbanTarefa.Prioridade.valueOf(kanbanTarefaDTO.getPrioridade()));
        }
        
        if (kanbanTarefaDTO.getStatus() != null) {
            kanbanTarefaExistente.setStatus(KanbanTarefa.Status.valueOf(kanbanTarefaDTO.getStatus()));
        }
        
        // Atualizar campos numéricos
        kanbanTarefaExistente.setEstimativaHoras(kanbanTarefaDTO.getEstimativaHoras());
        kanbanTarefaExistente.setHorasTrabalhadas(kanbanTarefaDTO.getHorasTrabalhadas());
        kanbanTarefaExistente.setOrdem(kanbanTarefaDTO.getOrdem());

        KanbanTarefa kanbanTarefaAtualizada = kanbanTarefaRepository.save(kanbanTarefaExistente);
        return convertToDTO(kanbanTarefaAtualizada);
    }

    public void deletar(Integer id) {
        if (!kanbanTarefaRepository.existsById(id)) {
            throw new EntityNotFoundException("Tarefa Kanban com ID " + id + " não encontrada");
        }
        kanbanTarefaRepository.deleteById(id);
    }

    public KanbanTarefaDTO atualizarStatus(Integer id, String novoStatus) {
        KanbanTarefa kanbanTarefa = kanbanTarefaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tarefa Kanban com ID " + id + " não encontrada"));
        
        try {
            KanbanTarefa.Status status = KanbanTarefa.Status.valueOf(novoStatus.toUpperCase());
            kanbanTarefa.setStatus(status);
            
            KanbanTarefa tarefaAtualizada = kanbanTarefaRepository.save(kanbanTarefa);
            return convertToDTO(tarefaAtualizada);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status inválido: " + novoStatus);
        }
    }

    /**
     * Atribuir responsável à tarefa
     */
    public KanbanTarefaDTO atribuirResponsavel(Integer tarefaId, Integer responsavelId) {
        KanbanTarefa tarefa = kanbanTarefaRepository.findById(tarefaId)
                .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada"));
        
        if (responsavelId != null) {
            Usuarios responsavel = usuarioRepository.findById(responsavelId)
                    .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
            tarefa.setResponsavel(responsavel);
        } else {
            tarefa.setResponsavel(null);
        }
        
        KanbanTarefa tarefaAtualizada = kanbanTarefaRepository.save(tarefa);
        return convertToDTO(tarefaAtualizada);
    }

    /**
     * Reordenar tarefas em uma categoria
     */
    public List<KanbanTarefaDTO> reordenarTarefas(Integer categoriaId, List<Integer> idsOrdenados) {
        for (int i = 0; i < idsOrdenados.size(); i++) {
            Integer id = idsOrdenados.get(i);
            KanbanTarefa tarefa = kanbanTarefaRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Tarefa com ID " + id + " não encontrada"));
            tarefa.setOrdem(i + 1);
            kanbanTarefaRepository.save(tarefa);
        }
        
        return listarPorCategoria(categoriaId);
    }

    /**
     * Adicionar horas trabalhadas
     */
    public KanbanTarefaDTO adicionarHorasTrabalhadas(Integer tarefaId, BigDecimal horas) {
        KanbanTarefa tarefa = kanbanTarefaRepository.findById(tarefaId)
                .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada"));
        
        BigDecimal horasAtuais = tarefa.getHorasTrabalhadas() != null ? tarefa.getHorasTrabalhadas() : BigDecimal.ZERO;
        tarefa.setHorasTrabalhadas(horasAtuais.add(horas));
        
        KanbanTarefa tarefaAtualizada = kanbanTarefaRepository.save(tarefa);
        return convertToDTO(tarefaAtualizada);
    }

    private void validarKanbanTarefa(KanbanTarefaDTO kanbanTarefaDTO) {
        if (kanbanTarefaDTO.getTitulo() == null || kanbanTarefaDTO.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("Título da tarefa é obrigatório");
        }
        
        if (kanbanTarefaDTO.getCategoriaId() == null) {
            throw new IllegalArgumentException("Categoria é obrigatória");
        }
        
        if (!kanbanCategoriaRepository.existsById(kanbanTarefaDTO.getCategoriaId())) {
            throw new IllegalArgumentException("Categoria não encontrada");
        }
        
        if (kanbanTarefaDTO.getResponsavelId() != null && !usuarioRepository.existsById(kanbanTarefaDTO.getResponsavelId())) {
            throw new IllegalArgumentException("Usuário responsável não encontrado");
        }
        
        // Validar horas
        if (kanbanTarefaDTO.getEstimativaHoras() != null && kanbanTarefaDTO.getEstimativaHoras().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Estimativa de horas deve ser maior ou igual a zero");
        }
        
        if (kanbanTarefaDTO.getHorasTrabalhadas() != null && kanbanTarefaDTO.getHorasTrabalhadas().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Horas trabalhadas deve ser maior ou igual a zero");
        }
    }

    private KanbanTarefaDTO convertToDTO(KanbanTarefa kanbanTarefa) {
        KanbanTarefaDTO dto = KanbanTarefaDTO.builder()
                .id(kanbanTarefa.getId())
                .titulo(kanbanTarefa.getTitulo())
                .descricao(kanbanTarefa.getDescricao())
                .categoriaId(kanbanTarefa.getCategoria() != null ? kanbanTarefa.getCategoria().getId() : null)
                .categoriaNome(kanbanTarefa.getCategoria() != null ? kanbanTarefa.getCategoria().getNome() : null)
                .categoriaCor(kanbanTarefa.getCategoria() != null ? kanbanTarefa.getCategoria().getCor() : null)
                .responsavelId(kanbanTarefa.getResponsavel() != null ? kanbanTarefa.getResponsavel().getId() : null)
                .responsavelNome(kanbanTarefa.getResponsavel() != null ? kanbanTarefa.getResponsavel().getNome() : null)
                .criadoPorId(kanbanTarefa.getCriadoPor() != null ? kanbanTarefa.getCriadoPor().getId() : null)
                .criadoPorNome(kanbanTarefa.getCriadoPor() != null ? kanbanTarefa.getCriadoPor().getNome() : null)
                .dataCriacao(kanbanTarefa.getDataCriacao())
                .dataEntrega(kanbanTarefa.getDataEntrega())
                .dataConclusao(kanbanTarefa.getDataConclusao())
                .prioridade(kanbanTarefa.getPrioridade() != null ? kanbanTarefa.getPrioridade().name() : null)
                .status(kanbanTarefa.getStatus() != null ? kanbanTarefa.getStatus().name() : null)
                .estimativaHoras(kanbanTarefa.getEstimativaHoras())
                .horasTrabalhadas(kanbanTarefa.getHorasTrabalhadas())
                .ordem(kanbanTarefa.getOrdem())
                .build();
        
        // Campos calculados
        dto.setAtrasada(kanbanTarefa.isAtrasada());
        dto.setPorcentagemConclusao(kanbanTarefa.calcularPorcentagemConclusao());
        
        // Calcular dias restantes
        if (kanbanTarefa.getDataEntrega() != null) {
            long dias = ChronoUnit.DAYS.between(LocalDate.now(), kanbanTarefa.getDataEntrega());
            dto.setDiasRestantes(dias);
        }
        
        // Calcular tempo decorrido
        if (kanbanTarefa.getDataCriacao() != null) {
            long horas = ChronoUnit.HOURS.between(kanbanTarefa.getDataCriacao(), LocalDateTime.now());
            if (horas < 24) {
                dto.setTempoDecorrido(horas + "h");
            } else {
                long dias = horas / 24;
                dto.setTempoDecorrido(dias + "d");
            }
        }
        
        return dto;
    }

    private KanbanTarefa convertToEntity(KanbanTarefaDTO dto) {
        KanbanTarefa kanbanTarefa = new KanbanTarefa();
        kanbanTarefa.setTitulo(dto.getTitulo());
        kanbanTarefa.setDescricao(dto.getDescricao());
        
        if (dto.getCategoriaId() != null) {
            KanbanCategoria categoria = kanbanCategoriaRepository.findById(dto.getCategoriaId())
                    .orElseThrow(() -> new EntityNotFoundException("Categoria com ID " + dto.getCategoriaId() + " não encontrada"));
            kanbanTarefa.setCategoria(categoria);
        }
        
        if (dto.getResponsavelId() != null) {
            Usuarios responsavel = usuarioRepository.findById(dto.getResponsavelId())
                    .orElseThrow(() -> new EntityNotFoundException("Usuário responsável com ID " + dto.getResponsavelId() + " não encontrado"));
            kanbanTarefa.setResponsavel(responsavel);
        }
        
        kanbanTarefa.setDataEntrega(dto.getDataEntrega());
        
        if (dto.getPrioridade() != null) {
            kanbanTarefa.setPrioridade(KanbanTarefa.Prioridade.valueOf(dto.getPrioridade()));
        }
        
        if (dto.getStatus() != null) {
            kanbanTarefa.setStatus(KanbanTarefa.Status.valueOf(dto.getStatus()));
        }
        
        kanbanTarefa.setEstimativaHoras(dto.getEstimativaHoras());
        kanbanTarefa.setHorasTrabalhadas(dto.getHorasTrabalhadas());
        kanbanTarefa.setOrdem(dto.getOrdem());
        
        return kanbanTarefa;
    }
} 

