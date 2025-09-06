package com.example.demo.stock.repository;

import com.example.demo.stock.entity.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstrumentRepository extends JpaRepository<Instrument,Long> {
}
