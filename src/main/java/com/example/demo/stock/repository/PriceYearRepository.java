package com.example.demo.stock.repository;

import com.example.demo.stock.entity.PriceYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceYearRepository extends JpaRepository<PriceYear,Long> {
}
