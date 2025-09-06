package com.example.demo.stock.repository;

import com.example.demo.stock.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<Order, Long> {
}
