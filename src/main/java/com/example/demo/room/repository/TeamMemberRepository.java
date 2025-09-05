package com.example.demo.room.repository;

import com.example.demo.room.entity.Room;
import com.example.demo.room.entity.Team;
import com.example.demo.room.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember,Long> {
    List<TeamMember> findAllByTeamId(Long id);

    <T> Optional<T> findByTeamIdAndIsLeader(Long id, boolean b);

    Integer countByRoomId(Long id);

    void deleteByRoomIdAndUserId(Long roomId, Long userId);

    void deleteAllByRoomId(Long roomId);

    Boolean existsByRoomIdAndUserId(Long roomId, Long userId);

    List<TeamMember> findAllByTeamIdAndRoomId(Long teamId, Long roomId);

    Optional<TeamMember> findByUserIdAndRoomId(Long userId, Long roomId);


    Boolean existsByTeamIdAndRoomIdAndIsLeader(Long teamId, Long roomId, boolean b);

    Optional<TeamMember> findByRoomIdAndTeamId(Long roomId, Long teamId);

    Optional<TeamMember> findByRoomIdAndUserId(Long roomId, Long userId);

    Integer countByRoomIdAndTeamId(Long roomId, Long teamId);

    Optional<TeamMember> findByUserIdAndRoomIdAndTeamId(Long userId, Long roomId, Long teamId);
}
