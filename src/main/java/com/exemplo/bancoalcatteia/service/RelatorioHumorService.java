package com.exemplo.bancoalcatteia.service;

import com.exemplo.bancoalcatteia.dto.CheckHumorDTO;
import com.exemplo.bancoalcatteia.dto.RelatorioHumorDTO;
import com.exemplo.bancoalcatteia.model.CheckHumor;
import com.exemplo.bancoalcatteia.model.Usuarios;
import com.exemplo.bancoalcatteia.repository.CheckHumorRepository;
import com.exemplo.bancoalcatteia.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service responsável por geração de relatórios de humor
 * Implementa Single Responsibility Principle para relatórios
 */
@Service
public class RelatorioHumorService {

    private final CheckHumorRepository checkHumorRepository;
    private final UsuarioRepository usuarioRepository;
    private final HumorStatisticsService statisticsService;
    private final TendenciaAnalysisService tendenciaService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public RelatorioHumorService(CheckHumorRepository checkHumorRepository, 
                                UsuarioRepository usuarioRepository,
                                HumorStatisticsService statisticsService,
                                TendenciaAnalysisService tendenciaService) {
        this.checkHumorRepository = checkHumorRepository;
        this.usuarioRepository = usuarioRepository;
        this.statisticsService = statisticsService;
        this.tendenciaService = tendenciaService;
    }

    /**
     * Gera relatório quinzenal completo
     */
    public RelatorioHumorDTO gerarRelatorioQuinzenal(LocalDate dataInicio, LocalDate dataFim) {
        // Buscar todos os registros do período
        List<CheckHumor> registros = checkHumorRepository.findByDataRegistroBetween(dataInicio, dataFim);
        
        if (registros.isEmpty()) {
            return criarRelatorioVazio(dataInicio, dataFim);
        }

        // Calcular estatísticas básicas
        Map<String, Long> estatisticas = statisticsService.calcularEstatisticasHumor(registros);
        Map<String, Double> percentuais = statisticsService.calcularPercentuais(estatisticas, registros.size());
        Double mediaGeral = statisticsService.calcularMediaGeral(registros);
        String humorPredominante = statisticsService.encontrarHumorPredominante(estatisticas);
        
        // Buscar comentários
        List<CheckHumorDTO> comentarios = buscarComentarios(dataInicio, dataFim, false);
        List<CheckHumorDTO> comentariosAnonimos = buscarComentarios(dataInicio, dataFim, true);
        
        // Análise de tendência
        String tendencia = tendenciaService.analisarTendencia(dataInicio, dataFim);
        Double variacao = statisticsService.calcularVariacao(dataInicio, dataFim);
        
        // Detalhes por usuário
        List<RelatorioHumorDTO.UsuarioHumorDTO> detalhePorUsuario = gerarDetalhePorUsuario(registros);
        
        // Comparação com período anterior
        RelatorioHumorDTO.ComparacaoHumorDTO comparacao = gerarComparacao(dataInicio, dataFim);
        
        // Alertas e recomendações
        List<String> alertas = tendenciaService.gerarAlertas(estatisticas, registros.size());
        List<String> recomendacoes = tendenciaService.gerarRecomendacoes(estatisticas, mediaGeral);

        return RelatorioHumorDTO.builder()
                .dataInicio(dataInicio)
                .dataFim(dataFim)
                .periodo(dataInicio.format(formatter) + " - " + dataFim.format(formatter))
                .estatisticasHumor(estatisticas)
                .percentuaisHumor(percentuais)
                .totalRegistros((long) registros.size())
                .totalUsuarios(statisticsService.contarUsuariosUnicos(registros))
                .mediaGeral(mediaGeral)
                .humorPredominante(humorPredominante)
                .tendencia(tendencia)
                .variacao(variacao)
                .comentarios(comentarios)
                .comentariosAnonimos(comentariosAnonimos)
                .totalComentarios((long) (comentarios.size() + comentariosAnonimos.size()))
                .detalhePorUsuario(detalhePorUsuario)
                .comparacao(comparacao)
                .alertas(alertas)
                .recomendacoes(recomendacoes)
                .build();
    }

    /**
     * Busca comentários do período (anônimos ou não)
     */
    private List<CheckHumorDTO> buscarComentarios(LocalDate inicio, LocalDate fim, boolean anonimos) {
        List<CheckHumor> registros;
        
        if (anonimos) {
            registros = checkHumorRepository.findByDataRegistroBetween(inicio, fim)
                    .stream()
                    .filter(r -> Boolean.TRUE.equals(r.getAnonimo()) && 
                               r.getObservacao() != null && 
                               !r.getObservacao().trim().isEmpty())
                    .collect(Collectors.toList());
        } else {
            registros = checkHumorRepository.findByAnonimoFalseAndObservacaoIsNotNullAndDataRegistroBetweenOrderByDataCriacaoDesc(inicio, fim);
        }
        
        return registros.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Gera detalhamento por usuário
     */
    private List<RelatorioHumorDTO.UsuarioHumorDTO> gerarDetalhePorUsuario(List<CheckHumor> registros) {
        Map<Integer, List<CheckHumor>> porUsuario = registros.stream()
                .collect(Collectors.groupingBy(r -> r.getUsuario().getId()));
        
        return porUsuario.entrySet().stream()
                .map(entry -> {
                    Integer usuarioId = entry.getKey();
                    List<CheckHumor> humoresUsuario = entry.getValue();
                    
                    Optional<Usuarios> usuario = usuarioRepository.findById(usuarioId);
                    String nomeUsuario = usuario.map(Usuarios::getNome).orElse("Usuário não encontrado");
                    
                    Double mediaHumor = statisticsService.calcularMediaGeral(humoresUsuario);
                    String humorPredominante = statisticsService.encontrarHumorPredominante(
                        statisticsService.calcularEstatisticasHumor(humoresUsuario));
                    
                    CheckHumor ultimoRegistro = humoresUsuario.stream()
                            .max(Comparator.comparing(CheckHumor::getDataRegistro))
                            .orElse(null);
                    
                    return RelatorioHumorDTO.UsuarioHumorDTO.builder()
                            .usuarioId(usuarioId)
                            .nomeUsuario(nomeUsuario)
                            .totalRegistros((long) humoresUsuario.size())
                            .mediaHumor(Math.round(mediaHumor * 100.0) / 100.0)
                            .humorPredominante(humorPredominante)
                            .ultimoHumor(ultimoRegistro != null ? ultimoRegistro.getHumor().name() : null)
                            .ultimoRegistro(ultimoRegistro != null ? ultimoRegistro.getDataRegistro() : null)
                            .precisaAtencao(tendenciaService.precisaAtencao(mediaHumor))
                            .build();
                })
                .sorted(Comparator.comparing(RelatorioHumorDTO.UsuarioHumorDTO::getNomeUsuario))
                .collect(Collectors.toList());
    }

    /**
     * Gera comparação com período anterior
     */
    private RelatorioHumorDTO.ComparacaoHumorDTO gerarComparacao(LocalDate inicio, LocalDate fim) {
        long dias = fim.toEpochDay() - inicio.toEpochDay() + 1;
        LocalDate inicioAnterior = inicio.minusDays(dias);
        LocalDate fimAnterior = inicio.minusDays(1);
        
        Double mediaAtual = checkHumorRepository.findAverageHumorValue(inicio, fim);
        Double mediaAnterior = checkHumorRepository.findAverageHumorValue(inicioAnterior, fimAnterior);
        Long totalAtual = checkHumorRepository.countByDataRegistroBetween(inicio, fim);
        Long totalAnterior = checkHumorRepository.countByDataRegistroBetween(inicioAnterior, fimAnterior);
        
        String tendenciaGeral = tendenciaService.analisarTendencia(inicio, fim);
        
        return RelatorioHumorDTO.ComparacaoHumorDTO.builder()
                .mediaAtual(mediaAtual)
                .mediaPeriodoAnterior(mediaAnterior)
                .tendenciaGeral(tendenciaGeral)
                .totalRegistrosAtual(totalAtual)
                .totalRegistrosAnterior(totalAnterior)
                .build();
    }

    /**
     * Cria relatório vazio para períodos sem dados
     */
    private RelatorioHumorDTO criarRelatorioVazio(LocalDate dataInicio, LocalDate dataFim) {
        return RelatorioHumorDTO.builder()
                .dataInicio(dataInicio)
                .dataFim(dataFim)
                .periodo(dataInicio.format(formatter) + " - " + dataFim.format(formatter))
                .totalRegistros(0L)
                .estatisticasHumor(Collections.emptyMap())
                .comentarios(Collections.emptyList())
                .build();
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
} 

