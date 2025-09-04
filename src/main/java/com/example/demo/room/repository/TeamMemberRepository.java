package com.example.demo.room.repository;

import com.example.demo.room.entity.Room;
import com.example.demo.room.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember,Long> {
}
