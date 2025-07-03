package com.exemplo.bancoalcatteia.repository;

import com.exemplo.bancoalcatteia.model.Job;
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
public interface JobRepository extends JpaRepository<Job, Integer> {

    // Buscar por tipo
    List<Job> findByTipo(Job.TipoJob tipo);

    List<Job> findByTipoAndAtivo(Job.TipoJob tipo, Boolean ativo);

    Page<Job> findByTipoAndAtivo(Job.TipoJob tipo, Boolean ativo, Pageable pageable);

    // Buscar por status
    List<Job> findByStatus(Job.StatusJob status);

    List<Job> findByStatusAndAtivo(Job.StatusJob status, Boolean ativo);

    Page<Job> findByStatusAndAtivo(Job.StatusJob status, Boolean ativo, Pageable pageable);

    // Buscar por criador
    List<Job> findByCriadoPor(Usuarios criadoPor);

    List<Job> findByCriadoPorAndAtivo(Usuarios criadoPor, Boolean ativo);

    Page<Job> findByCriadoPorAndAtivo(Usuarios criadoPor, Boolean ativo, Pageable pageable);

    // Buscar jobs agendados prontos para execução
    @Query("SELECT j FROM Job j WHERE j.status = 'AGENDADO' AND j.dataAgendamento <= :agora AND j.ativo = true ORDER BY j.dataAgendamento ASC")
    List<Job> findJobsProntosParaExecucao(@Param("agora") LocalDateTime agora);

    // Buscar jobs executando
    List<Job> findByStatusAndAtivoOrderByDataExecucaoAsc(Job.StatusJob status, Boolean ativo);

    // Buscar jobs com erro que podem ser retentados
    @Query("SELECT j FROM Job j WHERE j.status = 'ERRO' AND j.tentativas < j.maxTentativas AND j.ativo = true ORDER BY j.dataConclusao ASC")
    List<Job> findJobsParaRetentar();

    // Buscar por data de agendamento
    List<Job> findByDataAgendamentoBetween(LocalDateTime inicio, LocalDateTime fim);

    List<Job> findByDataAgendamentoBetweenAndAtivo(LocalDateTime inicio, LocalDateTime fim, Boolean ativo);

    // Buscar por data de execução
    List<Job> findByDataExecucaoBetween(LocalDateTime inicio, LocalDateTime fim);

    List<Job> findByDataExecucaoBetweenAndAtivo(LocalDateTime inicio, LocalDateTime fim, Boolean ativo);

    // Buscar por data de conclusão
    List<Job> findByDataConclusaoBetween(LocalDateTime inicio, LocalDateTime fim);

    List<Job> findByDataConclusaoBetweenAndAtivo(LocalDateTime inicio, LocalDateTime fim, Boolean ativo);

    // Buscar jobs recorrentes (com cron expression)
    @Query("SELECT j FROM Job j WHERE j.cronExpression IS NOT NULL AND j.cronExpression != '' AND j.ativo = true")
    List<Job> findJobsRecorrentes();

    // Buscar por nome (busca parcial)
    List<Job> findByNomeContainingIgnoreCaseAndAtivo(String nome, Boolean ativo);

    Page<Job> findByNomeContainingIgnoreCaseAndAtivo(String nome, Boolean ativo, Pageable pageable);

    // Contar por status
    long countByStatus(Job.StatusJob status);

    long countByStatusAndAtivo(Job.StatusJob status, Boolean ativo);

    // Contar por tipo
    long countByTipo(Job.TipoJob tipo);

    long countByTipoAndAtivo(Job.TipoJob tipo, Boolean ativo);

    // Contar jobs agendados
    long countByStatusAndDataAgendamentoBeforeAndAtivo(Job.StatusJob status, LocalDateTime data, Boolean ativo);



    // Buscar estatísticas por status
    @Query("SELECT j.status as status, COUNT(j) as total FROM Job j WHERE j.ativo = true GROUP BY j.status")
    List<Object[]> findEstatisticasPorStatus();

    // Buscar estatísticas por tipo
    @Query("SELECT j.tipo as tipo, COUNT(j) as total FROM Job j WHERE j.ativo = true GROUP BY j.tipo")
    List<Object[]> findEstatisticasPorTipo();

    // Buscar jobs executados hoje
    @Query("SELECT j FROM Job j WHERE DATE(j.dataExecucao) = CURRENT_DATE AND j.ativo = true ORDER BY j.dataExecucao DESC")
    List<Job> findJobsExecutadosHoje();

    // Buscar jobs com falha recente
    @Query("SELECT j FROM Job j WHERE j.status = 'ERRO' AND j.dataConclusao >= :dataLimite AND j.ativo = true ORDER BY j.dataConclusao DESC")
    List<Job> findJobsComFalhaRecente(@Param("dataLimite") LocalDateTime dataLimite);

    // Buscar jobs concluídos por período
    @Query("SELECT j FROM Job j WHERE j.status = 'CONCLUIDO' AND j.dataConclusao BETWEEN :inicio AND :fim AND j.ativo = true")
    List<Job> findJobsConcluidosPorPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    // Buscar jobs atrasados (agendados para execução no passado mas ainda agendados)
    @Query("SELECT j FROM Job j WHERE j.status = 'AGENDADO' AND j.dataAgendamento < :agora AND j.ativo = true ORDER BY j.dataAgendamento ASC")
    List<Job> findJobsAtrasados(@Param("agora") LocalDateTime agora);

    // Buscar jobs executando há muito tempo
    @Query("SELECT j FROM Job j WHERE j.status = 'EXECUTANDO' AND j.dataExecucao < :tempoLimite AND j.ativo = true")
    List<Job> findJobsExecutandoMuitoTempo(@Param("tempoLimite") LocalDateTime tempoLimite);

    // Buscar jobs por múltiplos status
    List<Job> findByStatusInAndAtivo(List<Job.StatusJob> status, Boolean ativo);

    // Buscar jobs por múltiplos tipos
    List<Job> findByTipoInAndAtivo(List<Job.TipoJob> tipos, Boolean ativo);

    // Buscar próximos jobs a serem executados
    @Query("SELECT j FROM Job j WHERE j.status = 'AGENDADO' AND j.ativo = true ORDER BY j.dataAgendamento ASC LIMIT :limite")
    List<Job> findProximosJobs(@Param("limite") int limite);

    // Buscar últimos jobs executados
    @Query("SELECT j FROM Job j WHERE j.status IN ('CONCLUIDO', 'ERRO') AND j.ativo = true ORDER BY j.dataConclusao DESC LIMIT :limite")
    List<Job> findUltimosJobsExecutados(@Param("limite") int limite);

    // Buscar jobs do usuário ordenados por data
    List<Job> findByCriadoPorAndAtivoOrderByDataCriacaoDesc(Usuarios criadoPor, Boolean ativo);

    // Buscar jobs ativos ordenados por data de agendamento
    List<Job> findByAtivoOrderByDataAgendamentoAsc(Boolean ativo);

    Page<Job> findByAtivoOrderByDataAgendamentoAsc(Boolean ativo, Pageable pageable);

    // Buscar por período de criação
    List<Job> findByDataCriacaoBetweenAndAtivo(LocalDateTime inicio, LocalDateTime fim, Boolean ativo);
} 

