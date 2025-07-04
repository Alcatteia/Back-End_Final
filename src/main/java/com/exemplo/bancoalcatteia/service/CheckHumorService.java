package com.exemplo.bancoalcatteia.service;

import com.exemplo.bancoalcatteia.constants.HumorConstants;
import com.exemplo.bancoalcatteia.dto.CheckHumorDTO;
import com.exemplo.bancoalcatteia.model.CheckHumor;
import com.exemplo.bancoalcatteia.model.Usuarios;
import com.exemplo.bancoalcatteia.repository.CheckHumorRepository;
import com.exemplo.bancoalcatteia.repository.UsuarioRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service responsável pelo CRUD básico de CheckHumor
 * Implementa Single Responsibility Principle - apenas operações básicas
 */
@Service
@Transactional
public class CheckHumorService {

    private final CheckHumorRepository checkHumorRepository;
    private final UsuarioRepository usuarioRepository;

    public CheckHumorService(CheckHumorRepository checkHumorRepository, UsuarioRepository usuarioRepository) {
        this.checkHumorRepository = checkHumorRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Registra humor diário do usuário (apenas um por dia)
     * Limpa cache quando novo humor é registrado
     */
    @CacheEvict(value = "user-humor-today", key = "#humorDTO.usuarioId + '-' + T(java.time.LocalDate).now().toString()")
    public CheckHumorDTO registrarHumorDiario(CheckHumorDTO humorDTO) {
        validarCheckHumor(humorDTO);
        
        LocalDate hoje = LocalDate.now();
        
        // Verificar se já existe registro hoje
        if (checkHumorRepository.existsByUsuarioIdAndDataRegistro(humorDTO.getUsuarioId(), hoje)) {
            throw new IllegalArgumentException(HumorConstants.MSG_REGISTRO_DUPLICADO);
        }
        
        CheckHumor checkHumor = convertToEntity(humorDTO);
        checkHumor.setDataRegistro(hoje);
        
        CheckHumor checkHumorSalvo = checkHumorRepository.save(checkHumor);
        return convertToDTO(checkHumorSalvo);
    }

    /**
     * Busca humor de hoje do usuário com cache
     */
    @Cacheable(value = "user-humor-today", key = "#usuarioId + '-' + T(java.time.LocalDate).now().toString()")
    public Optional<CheckHumorDTO> buscarHumorHoje(Integer usuarioId) {
        return checkHumorRepository.findByUsuarioIdAndDataRegistro(usuarioId, LocalDate.now())
                .map(this::convertToDTO);
    }

    /**
     * Busca humor de hoje do usuário - lança exceção se não encontrar
     */
    public CheckHumorDTO buscarHumorHojeObrigatorio(Integer usuarioId) {
        return buscarHumorHoje(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Humor de hoje não encontrado para o usuário ID: " + usuarioId));
    }

    /**
     * Lista humores do usuário por período
     */
    public List<CheckHumorDTO> listarHumoresUsuario(Integer usuarioId, LocalDate inicio, LocalDate fim) {
        return checkHumorRepository.findByUsuarioIdAndDataRegistroBetween(usuarioId, inicio, fim)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca comentários do período para cards laterais com cache
     */
    @Cacheable(value = "humor-comments", key = "#inicio.toString() + '-' + #fim.toString()")
    public List<CheckHumorDTO> buscarComentariosPeriodo(LocalDate inicio, LocalDate fim) {
        return checkHumorRepository.findByAnonimoFalseAndObservacaoIsNotNullAndDataRegistroBetweenOrderByDataCriacaoDesc(inicio, fim)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // === FUNCIONALIDADES AVANÇADAS DE CHECK-IN ===

    /**
     * Realiza check-in sem validação de intervalo
     */
    @CacheEvict(value = "user-humor-today", key = "#checkHumorDTO.usuarioId + '-' + T(java.time.LocalDate).now().toString()")
    public CheckHumorDTO realizarCheckIn(CheckHumorDTO checkHumorDTO) {
        validarCheckIn(checkHumorDTO);
        
        Usuarios usuarios = usuarioRepository.findById(checkHumorDTO.getUsuarioId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        CheckHumor checkIn = construirCheckInEntity(checkHumorDTO, usuarios);
        CheckHumor checkInSalvo = checkHumorRepository.save(checkIn);

        return convertToDTOAvancado(checkInSalvo);
    }

    /**
     * Sempre permite realizar check-in (sem validação de intervalo)
     */
    public ValidacaoResult validarPodeRealizarCheckIn(Integer usuarioId) {
        return new ValidacaoResult(true, "Pode realizar check-in");
    }

    /**
     * Confirma selecao de humor no check-in
     */
    public CheckHumorDTO confirmarSelecao(Integer checkInId, boolean confirmado) {
        CheckHumor checkIn = checkHumorRepository.findById(checkInId)
                .orElseThrow(() -> new EntityNotFoundException("Check-in com ID " + checkInId + " nao encontrado"));

        if (checkIn.getDataRegistro().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Nao eh possivel confirmar check-in de datas anteriores");
        }

        checkIn.setConfirmado(confirmado);
        CheckHumor checkInConfirmado = checkHumorRepository.save(checkIn);

        return convertToDTOAvancado(checkInConfirmado);
    }

    /**
     * Calcula pontuação total de bem-estar por período
     */
    public int calcularBemEstarTotal(Integer usuarioId, LocalDate inicio, LocalDate fim) {
        Integer total = checkHumorRepository.sumBemEstarPontosByUsuarioAndPeriod(usuarioId, inicio, fim);
        return total != null ? total : 0;
    }

    /**
     * Gera menu de emoções personalizado por gênero
     */
    public String gerarMenuEmocoes(Integer usuarioId) {
        Usuarios usuarios = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        return construirMenuEmocoes(null); // Campo sexo não disponível no modelo atual
    }

    /**
     * Obter mensagem de bem-estar baseada na pontuação
     */
    public String obterMensagemBemEstar(int pontuacaoTotal) {
        if (pontuacaoTotal >= HumorConstants.BEM_ESTAR_EXCELENTE) {
            return HumorConstants.MSG_BEM_ESTAR_EXCELENTE;
        } else if (pontuacaoTotal >= HumorConstants.BEM_ESTAR_POSITIVO) {
            return HumorConstants.MSG_BEM_ESTAR_POSITIVO;
        } else if (pontuacaoTotal >= HumorConstants.BEM_ESTAR_EQUILIBRADO) {
            return HumorConstants.MSG_BEM_ESTAR_EQUILIBRADO;
        } else if (pontuacaoTotal >= HumorConstants.BEM_ESTAR_ATENCAO) {
            return HumorConstants.MSG_BEM_ESTAR_ATENCAO;
        } else {
            return HumorConstants.MSG_BEM_ESTAR_APOIO;
        }
    }

    // === MÉTODOS CRUD BÁSICOS ===

    /**
     * Lista todos os registros
     */
    public List<CheckHumorDTO> listarTodos() {
        return checkHumorRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista todos os registros com paginação
     */
    public Page<CheckHumorDTO> listarTodosPaginado(Pageable pageable) {
        return checkHumorRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    /**
     * Busca por ID
     */
    public CheckHumorDTO buscarPorId(Integer id) {
        CheckHumor checkHumor = checkHumorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(HumorConstants.MSG_CHECK_HUMOR_NAO_ENCONTRADO + " com ID " + id));
        return convertToDTO(checkHumor);
    }

    /**
     * Atualiza registro
     */
    public CheckHumorDTO atualizar(Integer id, CheckHumorDTO checkHumorDTO) {
        CheckHumor checkHumorExistente = checkHumorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(HumorConstants.MSG_CHECK_HUMOR_NAO_ENCONTRADO + " com ID " + id));

        // Verificar se o usuario pode editar este registro
        if (checkHumorDTO.getUsuarioId() != null && 
            !checkHumorExistente.getUsuario().getId().equals(checkHumorDTO.getUsuarioId())) {
            throw new IllegalArgumentException("Usuario nao pode editar este registro");
        }
        
        checkHumorExistente.setObservacao(checkHumorDTO.getObservacao());
        checkHumorExistente.setAnonimo(checkHumorDTO.getAnonimo());

        CheckHumor checkHumorAtualizado = checkHumorRepository.save(checkHumorExistente);
        return convertToDTO(checkHumorAtualizado);
    }

    /**
     * Remove registro
     */
    public void deletar(Integer id) {
        if (!checkHumorRepository.existsById(id)) {
            throw new EntityNotFoundException(HumorConstants.MSG_CHECK_HUMOR_NAO_ENCONTRADO + " com ID " + id);
        }
        checkHumorRepository.deleteById(id);
    }

    // === VALIDAÇÕES E CONVERSÕES ===

    /**
     * Valida dados do CheckHumor
     */
    private void validarCheckHumor(CheckHumorDTO checkHumorDTO) {
        if (checkHumorDTO.getUsuarioId() == null) {
            throw new IllegalArgumentException(HumorConstants.MSG_USUARIO_ID_OBRIGATORIO);
        }
        
        if (!usuarioRepository.existsById(checkHumorDTO.getUsuarioId())) {
            throw new IllegalArgumentException(HumorConstants.MSG_USUARIO_NAO_ENCONTRADO);
        }
        
        if (checkHumorDTO.getHumor() == null || checkHumorDTO.getHumor().trim().isEmpty()) {
            throw new IllegalArgumentException(HumorConstants.MSG_HUMOR_OBRIGATORIO);
        }
        
        try {
            CheckHumor.Humor.valueOf(checkHumorDTO.getHumor());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Humor inválido. Valores permitidos: CONTENTE, MOTIVADO, CALMO, DESMOTIVADO, ESTRESSADO");
        }
    }

    /**
     * Converte entidade para DTO
     */
    private CheckHumorDTO convertToDTO(CheckHumor checkHumor) {
        return CheckHumorDTO.builder()
                .id(checkHumor.getId())
                .usuarioId(checkHumor.getUsuario() != null ? checkHumor.getUsuario().getId() : null)
                .nomeUsuario(checkHumor.getUsuario() != null ? checkHumor.getUsuario().getNome() : null)
                .dataRegistro(checkHumor.getDataRegistro() != null ? checkHumor.getDataRegistro().toString() : null)
                .humor(checkHumor.getHumor() != null ? checkHumor.getHumor().name() : null)
                .humorDescricao(checkHumor.getHumor() != null ? checkHumor.getHumor().getDescricao() : null)
                .observacao(checkHumor.getObservacao())
                .anonimo(checkHumor.getAnonimo())
                .dataCriacao(checkHumor.getDataCriacao())
                .build();
    }

    /**
     * Converte DTO para entidade
     */
    private CheckHumor convertToEntity(CheckHumorDTO dto) {
        CheckHumor checkHumor = new CheckHumor();
        
        if (dto.getUsuarioId() != null) {
            Usuarios usuarios = usuarioRepository.findById(dto.getUsuarioId())
                    .orElseThrow(() -> new EntityNotFoundException(HumorConstants.MSG_USUARIO_NAO_ENCONTRADO + " com ID " + dto.getUsuarioId()));
            checkHumor.setUsuario(usuarios);
        }
        
        if (dto.getHumor() != null) {
            checkHumor.setHumor(CheckHumor.Humor.valueOf(dto.getHumor()));
        }
        
        checkHumor.setObservacao(dto.getObservacao());
        checkHumor.setAnonimo(dto.getAnonimo() != null ? dto.getAnonimo() : false);
        
        return checkHumor;
    }

    // === MÉTODOS AUXILIARES PARA FUNCIONALIDADES AVANÇADAS ===

    /**
     * Valida dados do check-in avançado
     */
    private void validarCheckIn(CheckHumorDTO dto) {
        if (dto.getUsuarioId() == null) {
            throw new IllegalArgumentException("ID do usuário é obrigatório");
        }
        if (dto.getOpcaoHumor() == null || dto.getOpcaoHumor() < HumorConstants.OPCAO_HUMOR_MIN || 
            dto.getOpcaoHumor() > HumorConstants.OPCAO_HUMOR_MAX) {
            throw new IllegalArgumentException("Opção de humor deve ser entre " + 
                HumorConstants.OPCAO_HUMOR_MIN + " e " + HumorConstants.OPCAO_HUMOR_MAX);
        }
    }

    /**
     * Constrói entidade CheckHumor a partir do DTO (versão avançada)
     */
    private CheckHumor construirCheckInEntity(CheckHumorDTO dto, Usuarios usuarios) {
        CheckHumor checkIn = new CheckHumor();
        checkIn.setUsuario(usuarios);
        checkIn.setHumor(CheckHumor.Humor.fromOpcao(dto.getOpcaoHumor()));
        checkIn.setDataRegistro(LocalDate.now());
        checkIn.setObservacao(dto.getObservacao());
        checkIn.setConfirmado(dto.getConfirmado() != null ? dto.getConfirmado() : false);
        checkIn.setAnonimo(dto.getAnonimo() != null ? dto.getAnonimo() : false);

        return checkIn;
    }

    /**
     * Converte entidade para DTO (versão avançada)
     */
    private CheckHumorDTO convertToDTOAvancado(CheckHumor entity) {
        String sexoUsuario = null; // Campo sexo não disponível no modelo atual

        return CheckHumorDTO.builder()
                .id(entity.getId())
                .usuarioId(entity.getUsuario().getId())
                .nomeUsuario(entity.getUsuario().getNome())
                .sexoUsuario(sexoUsuario)
                .opcaoHumor(entity.getHumor().getOpcao())
                .humor(entity.getHumor().name())
                .humorDescricao(entity.getHumor().getDescricaoPorGenero(sexoUsuario))
                .bemEstarPontos(entity.getBemEstarPontos())
                .dataRegistroDate(entity.getDataRegistro())
                .dataRegistro(entity.getDataRegistro() != null ? entity.getDataRegistro().toString() : null)
                .dataCriacao(entity.getDataCriacao())
                .confirmado(entity.getConfirmado())
                .observacao(entity.getObservacao())
                .anonimo(entity.getAnonimo())
                .podeResponder(true)
                .tempoRestante("Pode realizar check-in")
                .mensagemMenu(construirMenuEmocoes(sexoUsuario))
                .build();
    }

    /**
     * Atualiza o método convertToDTO existente para incluir novos campos
     */
    private CheckHumorDTO convertToDTOCompleto(CheckHumor checkHumor) {
        CheckHumorDTO dto = convertToDTO(checkHumor);
        
        // Adicionar campos específicos da versão avançada se existirem
        if (checkHumor.getBemEstarPontos() != null) {
            dto.setBemEstarPontos(checkHumor.getBemEstarPontos());
        }
        if (checkHumor.getConfirmado() != null) {
            dto.setConfirmado(checkHumor.getConfirmado());
        }
        if (checkHumor.getHumor() != null) {
            dto.setOpcaoHumor(checkHumor.getHumor().getOpcao());
            // Campo sexo não disponível no modelo atual, usando null
            dto.setHumorDescricao(checkHumor.getHumor().getDescricaoPorGenero(null));
            dto.setSexoUsuario(null);
        }
        
        return dto;
    }

    /**
     * Constrói menu de emoções personalizado por gênero
     */
    private String construirMenuEmocoes(String sexoUsuario) {
        StringBuilder menu = new StringBuilder();

        String boasVindas = switch (sexoUsuario != null ? sexoUsuario.toUpperCase() : "N") {
            case "M" -> "Bem-vindo ao check-in de humor diário!";
            case "F" -> "Bem-vinda ao check-in de humor diário!";
            default -> "Bem-vinde ao check-in de humor diário!";
        };

        menu.append("\n-----------------------------------------\n");
        menu.append(boasVindas).append("\n");
        menu.append("Selecione uma das emoções a seguir!\n");
        menu.append("--------------------------------------\n");

        for (CheckHumor.Humor humor : CheckHumor.Humor.values()) {
            menu.append(humor.getOpcao())
                    .append(" - ")
                    .append(humor.getDescricaoPorGenero(sexoUsuario))
                    .append("\n");
        }

        menu.append("-----------------------------------------");

        return menu.toString();
    }

    /**
     * Record para resultado de validação
     */
    public record ValidacaoResult(boolean podeRealizar, String mensagem) {}
} 

