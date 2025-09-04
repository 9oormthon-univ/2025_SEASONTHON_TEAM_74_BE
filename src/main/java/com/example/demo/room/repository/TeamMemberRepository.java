package com.example.demo.room.repository;

import com.example.demo.room.entity.Room;
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
}
