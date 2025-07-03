package com.exemplo.bancoalcatteia.repository;

import com.exemplo.bancoalcatteia.model.ParticipanteTarefa;
import com.exemplo.bancoalcatteia.model.Tarefa;
import com.exemplo.bancoalcatteia.model.Usuarios;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipanteTarefaRepository extends JpaRepository<ParticipanteTarefa, Integer> {

    // Buscar por tarefa
    List<ParticipanteTarefa> findByTarefa(Tarefa tarefa);

    List<ParticipanteTarefa> findByTarefaAndAtivo(Tarefa tarefa, Boolean ativo);

    // Buscar por usuário
    List<ParticipanteTarefa> findByUsuario(Usuarios usuario);

    List<ParticipanteTarefa> findByUsuarioAndAtivo(Usuarios usuario, Boolean ativo);

    Page<ParticipanteTarefa> findByUsuarioAndAtivo(Usuarios usuario, Boolean ativo, Pageable pageable);

    // Buscar por tarefa e usuário
    Optional<ParticipanteTarefa> findByTarefaAndUsuario(Tarefa tarefa, Usuarios usuario);

    Optional<ParticipanteTarefa> findByTarefaAndUsuarioAndAtivo(Tarefa tarefa, Usuarios usuario, Boolean ativo);

    // Buscar por status de participação
    List<ParticipanteTarefa> findByStatusParticipacao(ParticipanteTarefa.StatusParticipacao status);

    List<ParticipanteTarefa> findByStatusParticipacaoAndAtivo(ParticipanteTarefa.StatusParticipacao status, Boolean ativo);

    // Buscar por tarefa e status
    List<ParticipanteTarefa> findByTarefaAndStatusParticipacao(Tarefa tarefa, ParticipanteTarefa.StatusParticipacao status);

    List<ParticipanteTarefa> findByTarefaAndStatusParticipacaoAndAtivo(Tarefa tarefa, ParticipanteTarefa.StatusParticipacao status, Boolean ativo);

    // Buscar por usuário e status
    List<ParticipanteTarefa> findByUsuarioAndStatusParticipacao(Usuarios usuario, ParticipanteTarefa.StatusParticipacao status);

    List<ParticipanteTarefa> findByUsuarioAndStatusParticipacaoAndAtivo(Usuarios usuario, ParticipanteTarefa.StatusParticipacao status, Boolean ativo);

    // Contar participantes por tarefa
    long countByTarefa(Tarefa tarefa);

    long countByTarefaAndAtivo(Tarefa tarefa, Boolean ativo);

    // Contar por tarefa e status
    long countByTarefaAndStatusParticipacao(Tarefa tarefa, ParticipanteTarefa.StatusParticipacao status);

    long countByTarefaAndStatusParticipacaoAndAtivo(Tarefa tarefa, ParticipanteTarefa.StatusParticipacao status, Boolean ativo);

    // Contar por usuário e status
    long countByUsuarioAndStatusParticipacao(Usuarios usuario, ParticipanteTarefa.StatusParticipacao status);

    long countByUsuarioAndStatusParticipacaoAndAtivo(Usuarios usuario, ParticipanteTarefa.StatusParticipacao status, Boolean ativo);

    // Verificar se usuário já é participante da tarefa
    boolean existsByTarefaAndUsuario(Tarefa tarefa, Usuarios usuario);

    boolean existsByTarefaAndUsuarioAndAtivo(Tarefa tarefa, Usuarios usuario, Boolean ativo);

    // Buscar participações pendentes do usuário
    @Query("SELECT p FROM ParticipanteTarefa p WHERE p.usuario = :usuario AND p.statusParticipacao = 'PENDENTE' AND p.ativo = true ORDER BY p.dataConvite DESC")
    List<ParticipanteTarefa> findParticipacoesPendentesDoUsuario(@Param("usuario") Usuarios usuario);

    // Buscar participações aceitas do usuário
    @Query("SELECT p FROM ParticipanteTarefa p WHERE p.usuario = :usuario AND p.statusParticipacao = 'ACEITO' AND p.ativo = true ORDER BY p.dataResposta DESC")
    List<ParticipanteTarefa> findParticipacaoesAceitasDoUsuario(@Param("usuario") Usuarios usuario);

    // Buscar estatísticas de participação
    @Query("SELECT p.statusParticipacao as status, COUNT(p) as total FROM ParticipanteTarefa p WHERE p.ativo = true GROUP BY p.statusParticipacao")
    List<Object[]> findEstatisticasParticipacao();

    @Query("SELECT u.nome as usuario, COUNT(p) as total FROM ParticipanteTarefa p JOIN p.usuario u WHERE p.ativo = true GROUP BY u.nome")
    List<Object[]> findEstatisticasPorUsuario();

    // Buscar participantes ativos de uma tarefa com informações do usuário
    @Query("SELECT p FROM ParticipanteTarefa p JOIN FETCH p.usuario WHERE p.tarefa = :tarefa AND p.ativo = true ORDER BY p.dataConvite")
    List<ParticipanteTarefa> findParticipantesAtivosDaTarefa(@Param("tarefa") Tarefa tarefa);

    // Buscar tarefas onde usuário tem participação pendente
    @Query("SELECT p.tarefa FROM ParticipanteTarefa p WHERE p.usuario = :usuario AND p.statusParticipacao = 'PENDENTE' AND p.ativo = true")
    List<Tarefa> findTarefasComParticipacaoPendente(@Param("usuario") Usuarios usuario);

    // Remover participação (soft delete)
    @Query("UPDATE ParticipanteTarefa p SET p.ativo = false, p.statusParticipacao = 'REMOVIDO' WHERE p.tarefa = :tarefa AND p.usuario = :usuario")
    void removerParticipacao(@Param("tarefa") Tarefa tarefa, @Param("usuario") Usuarios usuario);
} 

