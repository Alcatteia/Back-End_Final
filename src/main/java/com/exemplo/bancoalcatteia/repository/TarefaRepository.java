package com.exemplo.bancoalcatteia.repository;

import com.exemplo.bancoalcatteia.model.Tarefa;
import com.exemplo.bancoalcatteia.model.Usuarios;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Integer> {

    // Buscar por responsável
    List<Tarefa> findByResponsavel(Usuarios responsavel);

    Page<Tarefa> findByResponsavel(Usuarios responsavel, Pageable pageable);

    // Buscar por criador
    List<Tarefa> findByCriadoPor(Usuarios criadoPor);

    Page<Tarefa> findByCriadoPor(Usuarios criadoPor, Pageable pageable);

    // Buscar por status
    List<Tarefa> findByStatus(Tarefa.StatusTarefa status);

    Page<Tarefa> findByStatus(Tarefa.StatusTarefa status, Pageable pageable);

    // Buscar por prioridade
    List<Tarefa> findByPrioridade(Tarefa.Prioridade prioridade);

    Page<Tarefa> findByPrioridade(Tarefa.Prioridade prioridade, Pageable pageable);

    // Buscar por data de entrega
    List<Tarefa> findByDataEntrega(LocalDate dataEntrega);

    List<Tarefa> findByDataEntregaBefore(LocalDate dataEntrega);

    List<Tarefa> findByDataEntregaAfter(LocalDate dataEntrega);

    List<Tarefa> findByDataEntregaBetween(LocalDate inicio, LocalDate fim);

    // Buscar tarefas atrasadas (com prazo vencido e status DOING)
    @Query("SELECT t FROM Tarefa t WHERE t.dataEntrega < :hoje AND t.status = 'DOING'")
    List<Tarefa> findTarefasAtrasadas(@Param("hoje") LocalDate hoje);

    // Buscar tarefas com prazo vencido (independente do status, exceto DONE)
    @Query("SELECT t FROM Tarefa t WHERE t.dataEntrega < :hoje AND t.status != 'DONE'")
    List<Tarefa> findTarefasComPrazoVencido(@Param("hoje") LocalDate hoje);

    // Buscar tarefas próximas ao vencimento
    @Query("SELECT t FROM Tarefa t WHERE t.dataEntrega BETWEEN :hoje AND :prazoLimite AND t.status != 'DONE'")
    List<Tarefa> findTarefasProximasVencimento(@Param("hoje") LocalDate hoje, @Param("prazoLimite") LocalDate prazoLimite);

    // Buscar por título (busca parcial)
    List<Tarefa> findByTituloContainingIgnoreCase(String titulo);

    Page<Tarefa> findByTituloContainingIgnoreCase(String titulo, Pageable pageable);

    // Buscar por responsável e status
    List<Tarefa> findByResponsavelAndStatus(Usuarios responsavel, Tarefa.StatusTarefa status);

    // Buscar por responsável e prioridade
    List<Tarefa> findByResponsavelAndPrioridade(Usuarios responsavel, Tarefa.Prioridade prioridade);

    // Contar tarefas por status
    long countByStatus(Tarefa.StatusTarefa status);

    // Contar tarefas por responsável
    long countByResponsavel(Usuarios responsavel);

    // Contar tarefas por responsável e status
    long countByResponsavelAndStatus(Usuarios responsavel, Tarefa.StatusTarefa status);

    // Query para estatísticas
    @Query("SELECT t.status as status, COUNT(t) as total FROM Tarefa t GROUP BY t.status")
    List<Object[]> findEstatisticasPorStatus();

    @Query("SELECT t.prioridade as prioridade, COUNT(t) as total FROM Tarefa t GROUP BY t.prioridade")
    List<Object[]> findEstatisticasPorPrioridade();

    @Query("SELECT u.nome as responsavel, COUNT(t) as total FROM Tarefa t JOIN t.responsavel u GROUP BY u.nome")
    List<Object[]> findEstatisticasPorResponsavel();

    // Buscar tarefas por período de criação
    @Query("SELECT t FROM Tarefa t WHERE DATE(t.dataCriacao) BETWEEN :inicio AND :fim")
    List<Tarefa> findByPeriodoCriacao(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    // Buscar tarefas concluídas por período
    @Query("SELECT t FROM Tarefa t WHERE t.status = 'DONE' AND DATE(t.dataConclusao) BETWEEN :inicio AND :fim")
    List<Tarefa> findTarefasConcluidasPorPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    // Buscar tarefas com participantes
    @Query("SELECT DISTINCT t FROM Tarefa t LEFT JOIN FETCH t.participantes p WHERE p.ativo = true")
    List<Tarefa> findTarefasComParticipantes();

    // Buscar tarefas onde o usuário é participante
    @Query("SELECT DISTINCT t FROM Tarefa t JOIN t.participantes p WHERE p.usuario = :usuario AND p.ativo = true")
    List<Tarefa> findTarefasOndeUsuarioEParticipante(@Param("usuario") Usuarios usuario);

    // Buscar tarefas onde o usuário é participante aceito
    @Query("SELECT DISTINCT t FROM Tarefa t JOIN t.participantes p WHERE p.usuario = :usuario AND p.statusParticipacao = 'ACEITO' AND p.ativo = true")
    List<Tarefa> findTarefasOndeUsuarioEParticipanteAceito(@Param("usuario") Usuarios usuario);
} 

