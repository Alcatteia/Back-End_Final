package com.exemplo.bancoalcatteia.repository;

import com.exemplo.bancoalcatteia.model.Feedback;
import com.exemplo.bancoalcatteia.model.Usuarios;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {

    // Buscar por usuário
    List<Feedback> findByUsuario(Usuarios Usuarios);

    Page<Feedback> findByUsuario(Usuarios Usuarios, Pageable pageable);

    // Buscar por usuário e status ativo
    List<Feedback> findByUsuarioAndAtivo(Usuarios Usuarios, Boolean ativo);

    Page<Feedback> findByUsuarioAndAtivo(Usuarios Usuarios, Boolean ativo, Pageable pageable);

    // Buscar por tipo
    List<Feedback> findByTipo(Feedback.TipoFeedback tipo);

    List<Feedback> findByTipoAndAtivo(Feedback.TipoFeedback tipo, Boolean ativo);

    Page<Feedback> findByTipoAndAtivo(Feedback.TipoFeedback tipo, Boolean ativo, Pageable pageable);

    // Buscar por status
    List<Feedback> findByStatus(Feedback.StatusFeedback status);

    List<Feedback> findByStatusAndAtivo(Feedback.StatusFeedback status, Boolean ativo);

    Page<Feedback> findByStatusAndAtivo(Feedback.StatusFeedback status, Boolean ativo, Pageable pageable);

    // Buscar por prioridade
    List<Feedback> findByPrioridade(Feedback.PrioridadeFeedback prioridade);

    List<Feedback> findByPrioridadeAndAtivo(Feedback.PrioridadeFeedback prioridade, Boolean ativo);

    // Buscar feedbacks anônimos
    List<Feedback> findByAnonimo(Boolean anonimo);

    List<Feedback> findByAnonimoAndAtivo(Boolean anonimo, Boolean ativo);

    Page<Feedback> findByAnonimoAndAtivo(Boolean anonimo, Boolean ativo, Pageable pageable);

    // Buscar por respondido por
    List<Feedback> findByRespondidoPor(Usuarios respondidoPor);

    // Buscar feedbacks pendentes
    List<Feedback> findByStatusAndAtivoOrderByDataCriacaoDesc(Feedback.StatusFeedback status, Boolean ativo);

    // Buscar feedbacks em análise
    List<Feedback> findByStatusInAndAtivoOrderByPrioridadeDescDataCriacaoDesc(List<Feedback.StatusFeedback> status, Boolean ativo);

    // Buscar por título (busca parcial)
    List<Feedback> findByTituloContainingIgnoreCaseAndAtivo(String titulo, Boolean ativo);

    Page<Feedback> findByTituloContainingIgnoreCaseAndAtivo(String titulo, Boolean ativo, Pageable pageable);

    // Buscar por conteúdo (busca parcial)
    List<Feedback> findByConteudoContainingIgnoreCaseAndAtivo(String conteudo, Boolean ativo);

    // Buscar por período
    List<Feedback> findByDataCriacaoBetween(LocalDateTime inicio, LocalDateTime fim);

    List<Feedback> findByDataCriacaoBetweenAndAtivo(LocalDateTime inicio, LocalDateTime fim, Boolean ativo);

    // Buscar por usuário e período
    List<Feedback> findByUsuarioAndDataCriacaoBetween(Usuarios Usuarios, LocalDateTime inicio, LocalDateTime fim);

    // Contar por status
    long countByStatus(Feedback.StatusFeedback status);

    long countByStatusAndAtivo(Feedback.StatusFeedback status, Boolean ativo);

    // Contar por tipo
    long countByTipo(Feedback.TipoFeedback tipo);

    long countByTipoAndAtivo(Feedback.TipoFeedback tipo, Boolean ativo);

    // Contar por prioridade
    long countByPrioridade(Feedback.PrioridadeFeedback prioridade);

    long countByPrioridadeAndAtivo(Feedback.PrioridadeFeedback prioridade, Boolean ativo);



    // Contar feedbacks anônimos
    long countByAnonimoAndAtivo(Boolean anonimo, Boolean ativo);

    // Buscar feedbacks por prioridade crítica ou alta
    @Query("SELECT f FROM Feedback f WHERE f.prioridade IN ('CRITICA', 'ALTA') AND f.status IN ('PENDENTE', 'EM_ANALISE') AND f.ativo = true ORDER BY f.prioridade DESC, f.dataCriacao ASC")
    List<Feedback> findFeedbacksPrioritarios();

    // Buscar estatísticas por tipo
    @Query("SELECT f.tipo as tipo, COUNT(f) as total FROM Feedback f WHERE f.ativo = true GROUP BY f.tipo")
    List<Object[]> findEstatisticasPorTipo();

    // Buscar estatísticas por status
    @Query("SELECT f.status as status, COUNT(f) as total FROM Feedback f WHERE f.ativo = true GROUP BY f.status")
    List<Object[]> findEstatisticasPorStatus();

    // Buscar estatísticas por prioridade
    @Query("SELECT f.prioridade as prioridade, COUNT(f) as total FROM Feedback f WHERE f.ativo = true GROUP BY f.prioridade")
    List<Object[]> findEstatisticasPorPrioridade();

    // Buscar feedbacks recentes (últimos 7 dias)
    @Query("SELECT f FROM Feedback f WHERE f.dataCriacao >= :dataLimite AND f.ativo = true ORDER BY f.dataCriacao DESC")
    List<Feedback> findFeedbacksRecentes(@Param("dataLimite") LocalDateTime dataLimite);

    // Buscar feedbacks não respondidos há mais de X dias
    @Query("SELECT f FROM Feedback f WHERE f.status = 'PENDENTE' AND f.dataCriacao < :dataLimite AND f.ativo = true ORDER BY f.prioridade DESC, f.dataCriacao ASC")
    List<Feedback> findFeedbacksNaoRespondidos(@Param("dataLimite") LocalDateTime dataLimite);

    // Buscar feedbacks do usuário por status
    List<Feedback> findByUsuarioAndStatusAndAtivo(Usuarios Usuarios, Feedback.StatusFeedback status, Boolean ativo);

    // Buscar todos os feedbacks ativos ordenados por prioridade e data
    @Query("SELECT f FROM Feedback f WHERE f.ativo = true ORDER BY f.prioridade DESC, f.dataCriacao DESC")
    List<Feedback> findAllAtivosOrdenadosPorPrioridade();

    Page<Feedback> findByAtivoOrderByPrioridadeDescDataCriacaoDesc(Boolean ativo, Pageable pageable);

    // Buscar feedbacks respondidos por período
    @Query("SELECT f FROM Feedback f WHERE f.status = 'RESPONDIDO' AND f.dataResposta BETWEEN :inicio AND :fim AND f.ativo = true")
    List<Feedback> findFeedbacksRespondidosPorPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    // Buscar por múltiplos tipos
    List<Feedback> findByTipoInAndAtivo(List<Feedback.TipoFeedback> tipos, Boolean ativo);

    // Buscar por múltiplos status
    List<Feedback> findByStatusInAndAtivo(List<Feedback.StatusFeedback> status, Boolean ativo);
} 

