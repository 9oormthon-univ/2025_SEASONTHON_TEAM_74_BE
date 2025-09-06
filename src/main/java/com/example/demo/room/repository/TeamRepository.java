package com.example.demo.room.repository;

import com.example.demo.room.entity.Team;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team,Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Team> findAllByRoomId(Long id);

    void deleteAllByRoomId(Long roomId);

    Optional<Team> findByIdAndRoomId(Long teamId, Long roomId);
}
