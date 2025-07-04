package com.exemplo.bancoalcatteia.repository;

import com.exemplo.bancoalcatteia.model.CheckHumor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CheckHumorRepository extends JpaRepository<CheckHumor, Integer> {
    
    /**
     * Verificar se usuário já registrou humor hoje
     */
    Optional<CheckHumor> findByUsuarioIdAndDataRegistro(Integer usuarioId, LocalDate data);
    
    /**
     * Verificar se existe registro para usuário em uma data específica
     */
    boolean existsByUsuarioIdAndDataRegistro(Integer usuarioId, LocalDate data);
    
    /**
     * Buscar humores por período (para relatório quinzenal)
     */
    List<CheckHumor> findByDataRegistroBetween(LocalDate inicio, LocalDate fim);
    
    /**
     * Buscar humores por usuário e período
     */
    List<CheckHumor> findByUsuarioIdAndDataRegistroBetween(Integer usuarioId, LocalDate inicio, LocalDate fim);
    
    /**
     * Buscar comentários não anônimos para exibição em cards
     */
    List<CheckHumor> findByAnonimoFalseAndObservacaoIsNotNullAndDataRegistroBetweenOrderByDataCriacaoDesc(
        LocalDate inicio, LocalDate fim);
    
    /**
     * Buscar humores por usuário ordenados por data
     */
    List<CheckHumor> findByUsuarioIdOrderByDataRegistroDesc(Integer usuarioId);
    
    /**
     * Estatísticas de humor por período
     */
    @Query("SELECT h.humor, COUNT(h) FROM CheckHumor h WHERE h.dataRegistro BETWEEN :inicio AND :fim GROUP BY h.humor")
    List<Object[]> findHumorStatistics(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);
    
    /**
     * Média de humor por período (valor numérico)
     */
    @Query("SELECT AVG(CASE " +
           "WHEN h.humor = 'CONTENTE' THEN 5 " +
           "WHEN h.humor = 'MOTIVADO' THEN 4 " +
           "WHEN h.humor = 'CALMO' THEN 3 " +
           "WHEN h.humor = 'NEUTRO' THEN 3 " +
           "WHEN h.humor = 'DESMOTIVADO' THEN 2 " +
           "WHEN h.humor = 'ESTRESSADO' THEN 1 " +
           "ELSE 0 END) FROM CheckHumor h WHERE h.dataRegistro BETWEEN :inicio AND :fim")
    Double findAverageHumorValue(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);
    
    /**
     * Contar registros por período
     */
    long countByDataRegistroBetween(LocalDate inicio, LocalDate fim);
    
    /**
     * Buscar últimos registros por usuário (para análise de tendência)
     */
    @Query("SELECT h FROM CheckHumor h WHERE h.usuario.id = :usuarioId " +
           "ORDER BY h.dataRegistro DESC LIMIT 7")
    List<CheckHumor> findTop7ByUsuarioIdOrderByDataRegistroDesc(@Param("usuarioId") Integer usuarioId);
    
    /**
     * Buscar humores por time (através dos membros do time)
     */
    @Query("SELECT h FROM CheckHumor h " +
           "JOIN h.usuario u " +
           "JOIN MembroTime mt ON mt.usuario.id = u.id " +
           "WHERE mt.time.id = :timeId AND h.dataRegistro BETWEEN :inicio AND :fim")
    List<CheckHumor> findByTimeIdAndPeriodo(@Param("timeId") Integer timeId, 
                                          @Param("inicio") LocalDate inicio, 
                                          @Param("fim") LocalDate fim);
    
    /**
     * Contar registros de hoje
     */
    @Query("SELECT COUNT(h) FROM CheckHumor h WHERE h.dataRegistro = CURRENT_DATE")
    long countRegistrosHoje();
    
    // === NOVAS QUERIES PARA FUNCIONALIDADES AVANÇADAS ===
    
    /**
     * Buscar último check-in do usuário (para validação de intervalo)
     */
    Optional<CheckHumor> findTopByUsuarioIdOrderByDataCriacaoDesc(Integer usuarioId);
    
    /**
     * Calcular soma de pontos de bem-estar por usuário e período
     */
    @Query("SELECT COALESCE(SUM(h.bemEstarPontos), 0) FROM CheckHumor h " +
           "WHERE h.usuario.id = :usuarioId AND h.dataRegistro BETWEEN :inicio AND :fim")
    Integer sumBemEstarPontosByUsuarioAndPeriod(@Param("usuarioId") Integer usuarioId, 
                                                @Param("inicio") LocalDate inicio, 
                                                @Param("fim") LocalDate fim);
    
    /**
     * Buscar check-ins não confirmados do usuário
     */
    List<CheckHumor> findByUsuarioIdAndConfirmadoFalseOrderByDataCriacaoDesc(Integer usuarioId);
    
    /**
     * Buscar registros confirmados por período
     */
    List<CheckHumor> findByConfirmadoTrueAndDataRegistroBetweenOrderByDataCriacaoDesc(
        LocalDate inicio, LocalDate fim);
    
    /**
     * Estatísticas de bem-estar por período
     */
    @Query("SELECT h.humor, AVG(h.bemEstarPontos), COUNT(h) FROM CheckHumor h " +
           "WHERE h.dataRegistro BETWEEN :inicio AND :fim AND h.confirmado = true " +
           "GROUP BY h.humor ORDER BY AVG(h.bemEstarPontos) DESC")
    List<Object[]> findBemEstarStatisticsByPeriod(@Param("inicio") LocalDate inicio, 
                                                 @Param("fim") LocalDate fim);
    
    /**
     * Buscar registros para análise de tendência (últimos 30 dias)
     */
    @Query("SELECT h FROM CheckHumor h WHERE h.usuario.id = :usuarioId " +
           "AND h.dataRegistro >= :dataLimite AND h.confirmado = true " +
           "ORDER BY h.dataRegistro DESC")
    List<CheckHumor> findTendenciaByUsuario(@Param("usuarioId") Integer usuarioId, 
                                           @Param("dataLimite") LocalDate dataLimite);
    
    /**
     * Contar check-ins por usuário em período específico
     */
    @Query("SELECT COUNT(h) FROM CheckHumor h WHERE h.usuario.id = :usuarioId " +
           "AND h.dataRegistro BETWEEN :inicio AND :fim")
    long countCheckInsByUsuarioAndPeriod(@Param("usuarioId") Integer usuarioId,
                                        @Param("inicio") LocalDate inicio,
                                        @Param("fim") LocalDate fim);
}

