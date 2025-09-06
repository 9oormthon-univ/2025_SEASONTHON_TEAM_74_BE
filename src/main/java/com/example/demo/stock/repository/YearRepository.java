package com.example.demo.stock.repository;

import com.example.demo.stock.entity.Year;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface YearRepository extends JpaRepository<Year, Long> {

    Optional<Year> findAllByYearId(Integer yearId);

}
