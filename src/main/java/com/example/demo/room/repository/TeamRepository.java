package com.example.demo.room.repository;

import com.example.demo.room.entity.Team;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team,Long> {
    List<Team> findAllByRoomId(Long id);

    void deleteAllByRoomId(Long roomId);

    Optional<Team> findByIdAndRoomId(Long teamId, Long roomId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from Team t where t.id = :teamId")
    Optional<Team> findByIdForUpdate(@Param("teamId") Long teamId);
}
