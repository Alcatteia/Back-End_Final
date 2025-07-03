package com.exemplo.bancoalcatteia.repository;

import com.exemplo.bancoalcatteia.model.Notificacao;
import com.exemplo.bancoalcatteia.model.Tarefa;
import com.exemplo.bancoalcatteia.model.Usuarios;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificacaoRepository extends JpaRepository<Notificacao, Integer> {

    // Buscar por usuário
    List<Notificacao> findByUsuarioNotificado(Usuarios Usuarios);

    Page<Notificacao> findByUsuarioNotificado(Usuarios Usuarios, Pageable pageable);

    // Buscar por usuário ordenado por data
    List<Notificacao> findByUsuarioNotificadoOrderByDataCriacaoDesc(Usuarios Usuarios);

    Page<Notificacao> findByUsuarioNotificadoOrderByDataCriacaoDesc(Usuarios Usuarios, Pageable pageable);

    // Buscar por tipo de notificação
    List<Notificacao> findByTipoNotificacao(Notificacao.TipoNotificacao tipo);

    // Buscar por tarefa
    List<Notificacao> findByTarefa(Tarefa tarefa);

    // Buscar por status de leitura
    List<Notificacao> findByConfirmacaoLeitura(Boolean lida);

    // Buscar notificações não lidas do usuário
    List<Notificacao> findByUsuarioNotificadoAndConfirmacaoLeituraFalse(Usuarios Usuarios);

    Page<Notificacao> findByUsuarioNotificadoAndConfirmacaoLeituraFalse(Usuarios Usuarios, Pageable pageable);

    // Buscar notificações não lidas do usuário ordenadas por data
    List<Notificacao> findByUsuarioNotificadoAndConfirmacaoLeituraFalseOrderByDataCriacaoDesc(Usuarios Usuarios);

    // Buscar notificações lidas do usuário
    List<Notificacao> findByUsuarioNotificadoAndConfirmacaoLeituraTrue(Usuarios Usuarios);

    Page<Notificacao> findByUsuarioNotificadoAndConfirmacaoLeituraTrue(Usuarios Usuarios, Pageable pageable);

    // Buscar por usuário e tipo
    List<Notificacao> findByUsuarioNotificadoAndTipoNotificacao(Usuarios Usuarios, Notificacao.TipoNotificacao tipo);

    // Buscar por usuário, tipo e status de leitura
    List<Notificacao> findByUsuarioNotificadoAndTipoNotificacaoAndConfirmacaoLeitura(Usuarios Usuarios, Notificacao.TipoNotificacao tipo, Boolean lida);

    // Buscar por período
    List<Notificacao> findByDataCriacaoBetween(LocalDateTime inicio, LocalDateTime fim);

    // Buscar por usuário e período
    List<Notificacao> findByUsuarioNotificadoAndDataCriacaoBetween(Usuarios Usuarios, LocalDateTime inicio, LocalDateTime fim);

    // Contar notificações não lidas do usuário
    long countByUsuarioNotificadoAndConfirmacaoLeituraFalse(Usuarios Usuarios);

    // Contar notificações por tipo
    long countByTipoNotificacao(Notificacao.TipoNotificacao tipo);

    // Contar notificações por usuário
    long countByUsuarioNotificado(Usuarios Usuarios);

    // Marcar todas as notificações do usuário como lidas
    @Modifying
    @Query("UPDATE Notificacao n SET n.confirmacaoLeitura = true WHERE n.usuarioNotificado = :Usuarios AND n.confirmacaoLeitura = false")
    void marcarTodasComoLidasParaUsuario(@Param("Usuarios") Usuarios Usuarios);

    // Marcar notificações específicas como lidas
    @Modifying
    @Query("UPDATE Notificacao n SET n.confirmacaoLeitura = true WHERE n.id IN :ids")
    void marcarComoLidas(@Param("ids") List<Long> ids);

    // Buscar notificações recentes (últimas 24 horas)
    @Query("SELECT n FROM Notificacao n WHERE n.usuarioNotificado = :Usuarios AND n.dataCriacao >= :dataLimite ORDER BY n.dataCriacao DESC")
    List<Notificacao> findNotificacoesRecentes(@Param("Usuarios") Usuarios Usuarios, @Param("dataLimite") LocalDateTime dataLimite);

    // Buscar estatísticas de notificações
    @Query("SELECT n.tipoNotificacao as tipo, COUNT(n) as total FROM Notificacao n GROUP BY n.tipoNotificacao")
    List<Object[]> findEstatisticasPorTipo();

    @Query("SELECT DATE(n.dataCriacao) as data, COUNT(n) as total FROM Notificacao n WHERE n.dataCriacao >= :dataInicio GROUP BY DATE(n.dataCriacao) ORDER BY DATE(n.dataCriacao)")
    List<Object[]> findEstatisticasPorDia(@Param("dataInicio") LocalDateTime dataInicio);

    // Buscar notificações relacionadas a tarefas atrasadas
    @Query("SELECT n FROM Notificacao n WHERE n.tipoNotificacao = 'TAREFA_ATRASADA' AND n.dataCriacao >= :dataLimite")
    List<Notificacao> findNotificacoesTarefasAtrasadas(@Param("dataLimite") LocalDateTime dataLimite);

    // Buscar notificações relacionadas a prazos próximos
    @Query("SELECT n FROM Notificacao n WHERE n.tipoNotificacao = 'PRAZO_CONCLUSAO' AND n.dataCriacao >= :dataLimite")
    List<Notificacao> findNotificacoesPrazos(@Param("dataLimite") LocalDateTime dataLimite);

    // Buscar últimas notificações do usuário (limitado)
    @Query("SELECT n FROM Notificacao n WHERE n.usuarioNotificado = :Usuarios ORDER BY n.dataCriacao DESC LIMIT :limite")
    List<Notificacao> findUltimasNotificacoes(@Param("Usuarios") Usuarios Usuarios, @Param("limite") int limite);

    // Remover notificações antigas
    @Modifying
    @Query("DELETE FROM Notificacao n WHERE n.dataCriacao < :dataLimite")
    void removerNotificacoesAntigas(@Param("dataLimite") LocalDateTime dataLimite);

    // Buscar notificações por tarefa e tipo
    List<Notificacao> findByTarefaAndTipoNotificacao(Tarefa tarefa, Notificacao.TipoNotificacao tipo);
} 

