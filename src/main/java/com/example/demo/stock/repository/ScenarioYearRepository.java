package com.example.demo.stock.repository;

import com.example.demo.stock.entity.ScenarioYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScenarioYearRepository extends JpaRepository<ScenarioYear,Long> {
}
