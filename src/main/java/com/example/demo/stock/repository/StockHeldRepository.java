package com.example.demo.stock.repository;

import com.example.demo.stock.entity.StockHeld;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StockHeldRepository extends JpaRepository<StockHeld, Long> {
    
    @Query("SELECT sh FROM StockHeld sh WHERE sh.team.id = :teamId")
    List<StockHeld> findByTeamId(@Param("teamId") Long teamId);

    @Query("SELECT sh FROM StockHeld sh WHERE sh.team.id = :teamId AND sh.yearInstrument.id = :yearInstrumentId")
    Optional<StockHeld> findByTeamIdAndYearInstrumentId(@Param("teamId") Long teamId, @Param("yearInstrumentId") Long yearInstrumentId);
}
