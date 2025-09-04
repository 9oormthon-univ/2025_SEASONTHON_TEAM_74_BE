package com.example.demo.room.repository;

import com.example.demo.room.entity.Room;
import com.example.demo.stock.entity.Industry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room,Long> {
}
