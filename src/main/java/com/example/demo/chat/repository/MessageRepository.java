package com.example.demo.chat.repository;

import com.example.demo.chat.entity.ChatRoom;
import com.example.demo.chat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message,Long> {
    List<Message> findAllByChatRoomIdOrderByCreatedAt(Long chatRoomId);

    List<Message> findTop30ByChatRoomIdOrderByCreatedAtDesc(Long chatRoomId);
}
