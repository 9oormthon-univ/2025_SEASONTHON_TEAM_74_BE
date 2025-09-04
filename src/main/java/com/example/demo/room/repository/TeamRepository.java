package com.example.demo.room.repository;

import com.example.demo.room.entity.Room;
import com.example.demo.room.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team,Long> {
    List<Team> findAllByRoomId(Long id);
}
