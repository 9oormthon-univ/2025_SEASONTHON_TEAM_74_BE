package com.example.demo.stock.repository;

import com.example.demo.stock.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
    
    @Query("SELECT o FROM Orders o WHERE o.round.id = :roundId AND o.team.id = :teamId")
    List<Orders> findByRoundIdAndTeamId(@Param("roundId") Long roundId, @Param("teamId") Long teamId);
    
    @Query("SELECT o FROM Orders o WHERE o.round.id = :roundId")
    List<Orders> findByRoundId(@Param("roundId") Long roundId);
}
