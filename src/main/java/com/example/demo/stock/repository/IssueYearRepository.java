package com.example.demo.stock.repository;

import com.example.demo.stock.entity.IssueYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IssueYearRepository extends JpaRepository<IssueYear,Long> {
}
