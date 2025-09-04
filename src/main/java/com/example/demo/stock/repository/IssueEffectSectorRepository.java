package com.example.demo.stock.repository;

import com.example.demo.stock.entity.IssueEffectSector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IssueEffectSectorRepository extends JpaRepository<IssueEffectSector,Long> {
}
