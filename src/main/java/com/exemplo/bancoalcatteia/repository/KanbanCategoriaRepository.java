package com.exemplo.bancoalcatteia.repository;

import com.exemplo.bancoalcatteia.model.KanbanCategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KanbanCategoriaRepository extends JpaRepository<KanbanCategoria, Integer> {
    
    /**
     * Buscar categorias ativas ordenadas por ordem
     */
    List<KanbanCategoria> findByAtivoTrueOrderByOrdemAscNomeAsc();
    
    /**
     * Buscar categorias por usuário
     */
    List<KanbanCategoria> findByCriadoPorIdAndAtivoTrueOrderByOrdemAscNomeAsc(Integer criadoPorId);
    
    /**
     * Buscar categorias por time
     */
    List<KanbanCategoria> findByTimeIdAndAtivoTrueOrderByOrdemAscNomeAsc(Integer timeId);
    
    /**
     * Buscar categorias por usuário ou time
     */
    @Query("SELECT k FROM KanbanCategoria k WHERE k.ativo = true AND " +
           "(k.criadoPor.id = :usuarioId OR k.time.id = :timeId) " +
           "ORDER BY k.ordem ASC, k.nome ASC")
    List<KanbanCategoria> findByUsuarioOrTime(@Param("usuarioId") Integer usuarioId, 
                                             @Param("timeId") Integer timeId);
    
    /**
     * Verificar se existe categoria com mesmo nome para usuário
     */
    boolean existsByNomeAndCriadoPorIdAndAtivoTrue(String nome, Integer criadoPorId);
    
    /**
     * Verificar se existe categoria com mesmo nome para time
     */
    boolean existsByNomeAndTimeIdAndAtivoTrue(String nome, Integer timeId);
    
    /**
     * Buscar próxima ordem disponível para usuário
     */
    @Query("SELECT COALESCE(MAX(k.ordem), 0) + 1 FROM KanbanCategoria k " +
           "WHERE k.criadoPor.id = :usuarioId AND k.ativo = true")
    Integer findNextOrdemByUsuario(@Param("usuarioId") Integer usuarioId);
    
    /**
     * Buscar próxima ordem disponível para time
     */
    @Query("SELECT COALESCE(MAX(k.ordem), 0) + 1 FROM KanbanCategoria k " +
           "WHERE k.time.id = :timeId AND k.ativo = true")
    Integer findNextOrdemByTime(@Param("timeId") Integer timeId);
    
    /**
     * Contar tarefas por categoria
     */
    @Query("SELECT k.id, COUNT(t) FROM KanbanCategoria k " +
           "LEFT JOIN KanbanTarefa t ON t.categoria.id = k.id " +
           "WHERE k.ativo = true " +
           "GROUP BY k.id")
    List<Object[]> countTarefasByCategoria();
    
    /**
     * Contar tarefas ativas por categoria
     */
    @Query("SELECT k.id, COUNT(t) FROM KanbanCategoria k " +
           "LEFT JOIN KanbanTarefa t ON t.categoria.id = k.id AND t.status IN ('ATIVA', 'PAUSADA') " +
           "WHERE k.ativo = true " +
           "GROUP BY k.id")
    List<Object[]> countTarefasAtivasByCategoria();
}

