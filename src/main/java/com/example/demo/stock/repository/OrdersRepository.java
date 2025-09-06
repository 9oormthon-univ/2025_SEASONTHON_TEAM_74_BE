package com.example.demo.stock.repository;

import com.example.demo.stock.entity.Order;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrdersRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE o.round.id = :roundId AND o.team.id = :teamId")
    List<Order> findByRoundIdAndTeamId(@Param("roundId") Long roundId, @Param("teamId") Long teamId);

    @Query("SELECT o FROM Order o WHERE o.round.id = :roundId")
    List<Order> findByRoundId(@Param("roundId") Long roundId);
}
