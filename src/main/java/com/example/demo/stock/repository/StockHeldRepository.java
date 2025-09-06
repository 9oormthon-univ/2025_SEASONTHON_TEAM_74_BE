package com.example.demo.stock.repository;

import com.example.demo.stock.entity.StockHeld;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StockHeldRepository extends JpaRepository<StockHeld, Long> {
    
    @Query("SELECT sh FROM StockHeld sh WHERE sh.team.id = :teamId")
    List<StockHeld> findByTeamId(@Param("teamId") Long teamId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
      select sh from StockHeld sh
      join fetch sh.yearInstrument yi
      where sh.team.id = :teamId and yi.id = :yiId
    """)
    Optional<StockHeld> findByTeamIdAndYearInstrumentIdForUpdate(
            @Param("teamId") Long teamId, @Param("yiId") Long yiId);

    @Query("SELECT sh FROM StockHeld sh WHERE sh.team.id = :teamId AND sh.yearInstrument.id = :yearInstrumentId")
    Optional<StockHeld> findByTeamIdAndYearInstrumentId(@Param("teamId") Long teamId, @Param("yearInstrumentId") Long yearInstrumentId);
}
