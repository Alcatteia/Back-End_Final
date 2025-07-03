package com.exemplo.bancoalcatteia.repository;

import com.exemplo.bancoalcatteia.model.KanbanTarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface KanbanTarefaRepository extends JpaRepository<KanbanTarefa, Integer> {
    
    /**
     * Buscar tarefas por categoria ordenadas por ordem e prioridade
     */
    List<KanbanTarefa> findByCategoriaIdOrderByOrdemAscPrioridadeDescDataCriacaoAsc(Integer categoriaId);
    
    /**
     * Buscar tarefas por status
     */
    List<KanbanTarefa> findByStatusOrderByPrioridadeDescDataCriacaoAsc(KanbanTarefa.Status status);
    
    /**
     * Buscar tarefas por responsável
     */
    List<KanbanTarefa> findByResponsavelIdOrderByPrioridadeDescDataCriacaoAsc(Integer responsavelId);
    
    /**
     * Buscar tarefas por criador
     */
    List<KanbanTarefa> findByCriadoPorIdOrderByDataCriacaoDesc(Integer criadoPorId);
    
    /**
     * Buscar tarefas atrasadas
     */
    @Query("SELECT t FROM KanbanTarefa t WHERE t.dataEntrega < :hoje AND t.status != 'CONCLUIDA'")
    List<KanbanTarefa> findTarefasAtrasadas(@Param("hoje") LocalDate hoje);
    
    /**
     * Buscar tarefas com entrega próxima (próximos N dias)
     */
    @Query("SELECT t FROM KanbanTarefa t WHERE t.dataEntrega BETWEEN :hoje AND :dataLimite " +
           "AND t.status IN ('ATIVA', 'PAUSADA') ORDER BY t.dataEntrega ASC")
    List<KanbanTarefa> findTarefasComEntregaProxima(@Param("hoje") LocalDate hoje, 
                                                   @Param("dataLimite") LocalDate dataLimite);
    
    /**
     * Buscar tarefas por filtros múltiplos
     */
    @Query("SELECT t FROM KanbanTarefa t WHERE " +
           "(:categoriaId IS NULL OR t.categoria.id = :categoriaId) AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:responsavelId IS NULL OR t.responsavel.id = :responsavelId) AND " +
           "(:prioridade IS NULL OR t.prioridade = :prioridade) " +
           "ORDER BY t.ordem ASC, t.prioridade DESC, t.dataCriacao ASC")
    List<KanbanTarefa> findByFiltros(@Param("categoriaId") Integer categoriaId,
                                    @Param("status") KanbanTarefa.Status status,
                                    @Param("responsavelId") Integer responsavelId,
                                    @Param("prioridade") KanbanTarefa.Prioridade prioridade);
    
    /**
     * Buscar próxima ordem disponível na categoria
     */
    @Query("SELECT COALESCE(MAX(t.ordem), 0) + 1 FROM KanbanTarefa t WHERE t.categoria.id = :categoriaId")
    Integer findNextOrdemByCategoria(@Param("categoriaId") Integer categoriaId);
    
    /**
     * Contar tarefas por status
     */
    @Query("SELECT t.status, COUNT(t) FROM KanbanTarefa t GROUP BY t.status")
    List<Object[]> countByStatus();
    
    /**
     * Contar tarefas por prioridade
     */
    @Query("SELECT t.prioridade, COUNT(t) FROM KanbanTarefa t GROUP BY t.prioridade")
    List<Object[]> countByPrioridade();
    
    /**
     * Buscar tarefas do Kanban completo (por usuário ou time)
     */
    @Query("SELECT t FROM KanbanTarefa t JOIN t.categoria c WHERE " +
           "(c.criadoPor.id = :usuarioId OR c.time.id = :timeId) " +
           "ORDER BY c.ordem ASC, t.ordem ASC, t.prioridade DESC")
    List<KanbanTarefa> findKanbanByUsuarioOrTime(@Param("usuarioId") Integer usuarioId, 
                                                @Param("timeId") Integer timeId);
    
    /**
     * Relatório de produtividade por responsável
     */
    @Query("SELECT t.responsavel.id, t.responsavel.nome, " +
           "COUNT(t), " +
           "SUM(CASE WHEN t.status = 'CONCLUIDA' THEN 1 ELSE 0 END), " +
           "AVG(t.horasTrabalhadas) " +
           "FROM KanbanTarefa t WHERE t.responsavel IS NOT NULL " +
           "GROUP BY t.responsavel.id, t.responsavel.nome")
    List<Object[]> relatorioProdutiviidade();
    
    /**
     * Estatísticas de tempo médio por categoria (em dias)
     * TEMPORARIAMENTE COMENTADO - problema de sintaxe DATEDIFF no H2/Hibernate
     */
    // @Query("SELECT c.nome, AVG(DATEDIFF('DAY', t.dataCriacao, t.dataConclusao)) " +
    //        "FROM KanbanTarefa t JOIN t.categoria c " +
    //        "WHERE t.status = 'CONCLUIDA' AND t.dataConclusao IS NOT NULL " +
    //        "GROUP BY c.id, c.nome")
    // List<Object[]> tempoMedioPorCategoria();
}

