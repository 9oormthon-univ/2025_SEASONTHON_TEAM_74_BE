package com.example.demo.chat.dto;

import lombok.Getter;

import java.time.LocalDateTime;

public class ChatRes {

    public record ChatMessageDtoRes(
        Long chatRoomId,
        Long messageId,
        String content,
        String senderNickname,
        LocalDateTime createdTime
    ) {
        public String getChatRoomId() {
            return String.valueOf(chatRoomId);
        }

    }

}
