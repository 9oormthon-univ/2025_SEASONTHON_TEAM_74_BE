package com.example.demo.stock.repository;

import com.example.demo.stock.entity.YearInstrument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface YearInstrumentRepository extends JpaRepository<YearInstrument, Long> {
}
