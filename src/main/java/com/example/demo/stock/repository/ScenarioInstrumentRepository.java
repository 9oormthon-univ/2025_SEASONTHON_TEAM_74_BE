package com.example.demo.stock.repository;

import com.example.demo.stock.entity.ScenarioInstrument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScenarioInstrumentRepository extends JpaRepository<ScenarioInstrument,Long> {
}
