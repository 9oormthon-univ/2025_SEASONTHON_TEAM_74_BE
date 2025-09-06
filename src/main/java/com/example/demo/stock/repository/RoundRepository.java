package com.example.demo.stock.repository;

import com.example.demo.stock.entity.Round;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoundRepository extends JpaRepository<Round, Long> {
    
    @Query("SELECT r FROM Round r WHERE r.room.id = :roomId AND r.id = :roundId")
    Optional<Round> findByRoomIdAndRoundId(@Param("roomId") Long roomId, @Param("roundId") Long roundId);
    
    @Query("SELECT r FROM Round r WHERE r.room.id = :roomId ORDER BY r.roundNumber DESC LIMIT 1")
    Optional<Round> findCurrentRoundByRoomId(@Param("roomId") Long roomId);
}
