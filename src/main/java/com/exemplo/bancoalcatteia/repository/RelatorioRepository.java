package com.exemplo.bancoalcatteia.repository;

import com.exemplo.bancoalcatteia.model.Relatorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RelatorioRepository extends JpaRepository<Relatorio, Integer> {}

