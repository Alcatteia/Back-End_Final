package com.exemplo.bancoalcatteia.repository;

import com.exemplo.bancoalcatteia.model.Time;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeRepository extends JpaRepository<Time, Integer> {
    
    List<Time> findByAtivoTrueOrderByNomeTimeAsc();
    
    List<Time> findByLiderIdAndAtivoTrueOrderByNomeTimeAsc(Integer liderId);
    
    boolean existsByNomeTimeAndAtivoTrue(String nomeTime);
    
    @Query("SELECT t FROM Time t WHERE t.lider.id = :liderId OR t.criadoPor.id = :usuarioId AND t.ativo = true ORDER BY t.nomeTime")
    List<Time> findTimesByUsuario(@Param("liderId") Long liderId, @Param("usuarioId") Long usuarioId);
    
    @Query("SELECT COUNT(kc) FROM KanbanCategoria kc WHERE kc.time.id = :timeId AND kc.ativo = true")
    Long countCategoriasByTime(@Param("timeId") Long timeId);
} 

