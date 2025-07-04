package com.exemplo.bancoalcatteia.service;

import com.exemplo.bancoalcatteia.dto.KanbanTarefaDTO;
import com.exemplo.bancoalcatteia.exception.BusinessException;
import com.exemplo.bancoalcatteia.model.KanbanTarefa;
import com.exemplo.bancoalcatteia.model.KanbanCategoria;
import com.exemplo.bancoalcatteia.model.Usuarios;
import com.exemplo.bancoalcatteia.repository.KanbanTarefaRepository;
import com.exemplo.bancoalcatteia.repository.KanbanCategoriaRepository;
import com.exemplo.bancoalcatteia.repository.UsuarioRepository;
import com.exemplo.bancoalcatteia.service.CurrentUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class KanbanTarefaService {

    private final KanbanTarefaRepository kanbanTarefaRepository;
    private final KanbanCategoriaRepository kanbanCategoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CurrentUserService currentUserService;

    public List<KanbanTarefaDTO> listarTodos() {
        return kanbanTarefaRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar tarefas por categoria
     */
    public List<KanbanTarefaDTO> listarPorCategoria(Integer categoriaId) {
        if (categoriaId == null) {
            throw new BusinessException("ID da categoria é obrigatório");
        }
        
        if (!kanbanCategoriaRepository.existsById(categoriaId)) {
            throw new EntityNotFoundException("Categoria não encontrada com ID: " + categoriaId);
        }
        
        return kanbanTarefaRepository.findByCategoriaIdOrderByOrdemAscPrioridadeDescDataCriacaoAsc(categoriaId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar tarefas por filtros
     */
    public List<KanbanTarefaDTO> listarPorFiltros(Integer categoriaId, String status, Integer responsavelId, String prioridade) {
        KanbanTarefa.Status statusEnum = status != null ? KanbanTarefa.Status.valueOf(status.toUpperCase()) : null;
        KanbanTarefa.Prioridade prioridadeEnum = prioridade != null ? KanbanTarefa.Prioridade.valueOf(prioridade.toUpperCase()) : null;
        
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
        LocalDate proximosSeteDias = hoje.plusDays(7);
        return kanbanTarefaRepository.findTarefasComEntregaProxima(hoje, proximosSeteDias)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar tarefas do usuário logado
     */
    public List<KanbanTarefaDTO> listarMinhasTarefas() {
        Integer usuarioId = currentUserService.getCurrentUserId();
        if (usuarioId == null) {
            throw new BusinessException("Usuário não autenticado");
        }
        
        return kanbanTarefaRepository.findByResponsavelIdOrderByPrioridadeDescDataCriacaoAsc(usuarioId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public KanbanTarefaDTO buscarPorId(Integer id) {
        if (id == null) {
            throw new BusinessException("ID da tarefa é obrigatório");
        }
        
        KanbanTarefa kanbanTarefa = kanbanTarefaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada com ID: " + id));
        return convertToDTO(kanbanTarefa);
    }

    public KanbanTarefaDTO criar(KanbanTarefaDTO kanbanTarefaDTO) {
        validarTarefaDTO(kanbanTarefaDTO, true);
        
        KanbanTarefa kanbanTarefa = convertToEntity(kanbanTarefaDTO);
        kanbanTarefa.setDataCriacao(LocalDateTime.now());
        
        // Definir criador como usuário logado
        Integer criadoPorId = currentUserService.getCurrentUserId();
        if (criadoPorId != null) {
            Usuarios criadoPor = usuarioRepository.findById(criadoPorId)
                    .orElseThrow(() -> new EntityNotFoundException("Usuário criador não encontrado"));
            kanbanTarefa.setCriadoPor(criadoPor);
        }
        
        // Definir ordem automaticamente
        Integer proximaOrdem = kanbanTarefaRepository.findNextOrdemByCategoria(kanbanTarefaDTO.getCategoriaId());
        kanbanTarefa.setOrdem(proximaOrdem);
        
        KanbanTarefa kanbanTarefaSalva = kanbanTarefaRepository.save(kanbanTarefa);
        return convertToDTO(kanbanTarefaSalva);
    }

    public KanbanTarefaDTO atualizar(Integer id, KanbanTarefaDTO kanbanTarefaDTO) {
        if (id == null) {
            throw new BusinessException("ID da tarefa é obrigatório");
        }
        
        validarTarefaDTO(kanbanTarefaDTO, false);
        
        KanbanTarefa kanbanTarefaExistente = kanbanTarefaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada com ID: " + id));

        // Atualizar campos básicos
        kanbanTarefaExistente.setTitulo(kanbanTarefaDTO.getTitulo());
        kanbanTarefaExistente.setDescricao(kanbanTarefaDTO.getDescricao());
        
        // Atualizar categoria se fornecida
        if (kanbanTarefaDTO.getCategoriaId() != null) {
            KanbanCategoria categoria = kanbanCategoriaRepository.findById(kanbanTarefaDTO.getCategoriaId())
                    .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada com ID: " + kanbanTarefaDTO.getCategoriaId()));
            kanbanTarefaExistente.setCategoria(categoria);
        }
        
        // Atualizar responsável se fornecido
        if (kanbanTarefaDTO.getResponsavelId() != null) {
            Usuarios responsavel = usuarioRepository.findById(kanbanTarefaDTO.getResponsavelId())
                    .orElseThrow(() -> new EntityNotFoundException("Usuário responsável não encontrado com ID: " + kanbanTarefaDTO.getResponsavelId()));
            kanbanTarefaExistente.setResponsavel(responsavel);
        }
        
        // Atualizar enum fields se fornecidos
        if (kanbanTarefaDTO.getPrioridade() != null) {
            kanbanTarefaExistente.setPrioridade(KanbanTarefa.Prioridade.valueOf(kanbanTarefaDTO.getPrioridade().toUpperCase()));
        }
        
        if (kanbanTarefaDTO.getStatus() != null) {
            kanbanTarefaExistente.setStatus(KanbanTarefa.Status.valueOf(kanbanTarefaDTO.getStatus().toUpperCase()));
        }
        
        // Atualizar datas se fornecidas
        if (kanbanTarefaDTO.getDataEntrega() != null) {
            kanbanTarefaExistente.setDataEntrega(kanbanTarefaDTO.getDataEntrega());
        }
        
        // Atualizar campos numéricos se fornecidos
        if (kanbanTarefaDTO.getEstimativaHoras() != null) {
            kanbanTarefaExistente.setEstimativaHoras(kanbanTarefaDTO.getEstimativaHoras());
        }
        
        if (kanbanTarefaDTO.getHorasTrabalhadas() != null) {
            kanbanTarefaExistente.setHorasTrabalhadas(kanbanTarefaDTO.getHorasTrabalhadas());
        }

        KanbanTarefa kanbanTarefaAtualizada = kanbanTarefaRepository.save(kanbanTarefaExistente);
        return convertToDTO(kanbanTarefaAtualizada);
    }

    public void deletar(Integer id) {
        if (id == null) {
            throw new BusinessException("ID da tarefa é obrigatório");
        }
        
        if (!kanbanTarefaRepository.existsById(id)) {
            throw new EntityNotFoundException("Tarefa não encontrada com ID: " + id);
        }
        
        kanbanTarefaRepository.deleteById(id);
    }

    public KanbanTarefaDTO atualizarStatus(Integer id, String novoStatus) {
        if (id == null) {
            throw new BusinessException("ID da tarefa é obrigatório");
        }
        
        if (novoStatus == null || novoStatus.trim().isEmpty()) {
            throw new BusinessException("Status é obrigatório");
        }
        
        // Validar status
        try {
            KanbanTarefa.Status status = KanbanTarefa.Status.valueOf(novoStatus.toUpperCase());
            
            KanbanTarefa tarefa = kanbanTarefaRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada com ID: " + id));
            
            tarefa.setStatus(status);
            
            // Se marcada como concluída, definir data de conclusão
            if (status == KanbanTarefa.Status.CONCLUIDA) {
                tarefa.setDataConclusao(LocalDateTime.now());
            } else {
                tarefa.setDataConclusao(null);
            }
            
            KanbanTarefa tarefaAtualizada = kanbanTarefaRepository.save(tarefa);
            return convertToDTO(tarefaAtualizada);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Status inválido: " + novoStatus);
        }
    }

    /**
     * Atribuir responsavel a tarefa
     */
    public KanbanTarefaDTO atribuirResponsavel(Integer id, Integer responsavelId) {
        if (id == null) {
            throw new BusinessException("ID da tarefa é obrigatório");
        }
        
        KanbanTarefa tarefa = kanbanTarefaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada com ID: " + id));
        
        if (responsavelId != null) {
            Usuarios responsavel = usuarioRepository.findById(responsavelId)
                    .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + responsavelId));
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
        if (categoriaId == null) {
            throw new BusinessException("ID da categoria é obrigatório");
        }
        
        if (idsOrdenados == null || idsOrdenados.isEmpty()) {
            throw new BusinessException("Lista de IDs ordenados é obrigatória");
        }
        
        if (!kanbanCategoriaRepository.existsById(categoriaId)) {
            throw new EntityNotFoundException("Categoria não encontrada com ID: " + categoriaId);
        }
        
        // Atualizar ordem de cada tarefa
        for (int i = 0; i < idsOrdenados.size(); i++) {
            Integer tarefaId = idsOrdenados.get(i);
            KanbanTarefa tarefa = kanbanTarefaRepository.findById(tarefaId)
                    .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada com ID: " + tarefaId));
            
            if (!tarefa.getCategoria().getId().equals(categoriaId)) {
                throw new BusinessException("Tarefa ID " + tarefaId + " não pertence à categoria " + categoriaId);
            }
            
            tarefa.setOrdem(i + 1);
            kanbanTarefaRepository.save(tarefa);
        }
        
        return listarPorCategoria(categoriaId);
    }

    /**
     * Adicionar horas trabalhadas
     */
    public KanbanTarefaDTO adicionarHorasTrabalhadas(Integer id, BigDecimal horas) {
        if (id == null) {
            throw new BusinessException("ID da tarefa é obrigatório");
        }
        
        if (horas == null || horas.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Horas trabalhadas deve ser maior que zero");
        }
        
        KanbanTarefa tarefa = kanbanTarefaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada com ID: " + id));
        
        BigDecimal horasAtuais = tarefa.getHorasTrabalhadas() != null ? tarefa.getHorasTrabalhadas() : BigDecimal.ZERO;
        tarefa.setHorasTrabalhadas(horasAtuais.add(horas));
        
        KanbanTarefa tarefaAtualizada = kanbanTarefaRepository.save(tarefa);
        return convertToDTO(tarefaAtualizada);
    }

    private void validarTarefaDTO(KanbanTarefaDTO dto, boolean isCreation) {
        if (dto.getTitulo() == null || dto.getTitulo().trim().isEmpty()) {
            throw new BusinessException("Título da tarefa é obrigatório");
        }
        
        if (dto.getTitulo().length() > 255) {
            throw new BusinessException("Título da tarefa não pode exceder 255 caracteres");
        }
        
        if (isCreation && dto.getCategoriaId() == null) {
            throw new BusinessException("Categoria é obrigatória");
        }
        
        if (dto.getCategoriaId() != null && !kanbanCategoriaRepository.existsById(dto.getCategoriaId())) {
            throw new EntityNotFoundException("Categoria não encontrada com ID: " + dto.getCategoriaId());
        }
        
        if (dto.getResponsavelId() != null && !usuarioRepository.existsById(dto.getResponsavelId())) {
            throw new EntityNotFoundException("Usuário responsável não encontrado com ID: " + dto.getResponsavelId());
        }
        
        if (dto.getPrioridade() != null) {
            try {
                KanbanTarefa.Prioridade.valueOf(dto.getPrioridade().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Prioridade inválida: " + dto.getPrioridade());
            }
        }
        
        if (dto.getEstimativaHoras() != null && dto.getEstimativaHoras().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Estimativa de horas não pode ser negativa");
        }
        
        if (dto.getHorasTrabalhadas() != null && dto.getHorasTrabalhadas().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Horas trabalhadas não pode ser negativa");
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


