package com.exemplo.bancoalcatteia.repository;

import com.exemplo.bancoalcatteia.model.Dashboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DashboardRepository extends JpaRepository<Dashboard, Integer> {}

