package com.example.demo.stock.repository;

import com.example.demo.stock.entity.StockHeld;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockHeldRepository extends JpaRepository<StockHeld, Long> {
}
