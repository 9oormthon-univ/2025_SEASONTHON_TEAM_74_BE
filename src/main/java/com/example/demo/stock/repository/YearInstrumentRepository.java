package com.example.demo.stock.repository;

import com.example.demo.stock.entity.YearInstrument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface YearInstrumentRepository extends JpaRepository<YearInstrument, Long> {
    
    @Query("SELECT yi FROM YearInstrument yi WHERE yi.year.id = :yearId")
    List<YearInstrument> findByYearId(@Param("yearId") Long yearId);
    
    @Query("SELECT yi FROM YearInstrument yi WHERE yi.year.id = :yearId AND yi.instrument.id = :instrumentId")
    Optional<YearInstrument> findByYearIdAndInstrumentId(@Param("yearId") Long yearId, @Param("instrumentId") Long instrumentId);
}
