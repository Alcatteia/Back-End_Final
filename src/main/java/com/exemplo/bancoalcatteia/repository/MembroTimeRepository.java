package com.exemplo.bancoalcatteia.repository;

import com.exemplo.bancoalcatteia.model.MembroTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembroTimeRepository extends JpaRepository<MembroTime, Integer> {
    
    /**
     * Buscar membros ativos de um time
     */
    List<MembroTime> findByTimeIdAndAtivoTrueOrderByTipoMembroAscDataEntradaAsc(Long timeId);
    
    /**
     * Buscar times de um usuário
     */
    List<MembroTime> findByUsuarioIdAndAtivoTrueOrderByDataEntradaDesc(Long usuarioId);
    
    /**
     * Verificar se usuário é membro de um time
     */
    boolean existsByUsuarioIdAndTimeIdAndAtivoTrue(Long usuarioId, Long timeId);
    
    /**
     * Buscar membro específico por usuário e time
     */
    Optional<MembroTime> findByUsuarioIdAndTimeIdAndAtivoTrue(Long usuarioId, Long timeId);
    
    /**
     * Buscar líderes de um time
     */
    @Query("SELECT m FROM MembroTime m WHERE m.time.id = :timeId AND m.ativo = true AND m.tipoMembro = 'LIDER'")
    List<MembroTime> findLideresByTimeId(@Param("timeId") Long timeId);
    
    /**
     * Contar membros ativos por time
     */
    long countByTimeIdAndAtivoTrue(Long timeId);
    
    /**
     * Buscar membros por tipo
     */
    List<MembroTime> findByTimeIdAndTipoMembroAndAtivoTrue(Long timeId, MembroTime.TipoMembro tipoMembro);
} 