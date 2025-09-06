package com.example.demo.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ChatReq {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMessageDto {
        private String message;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChatMessageDtoReq {
        private Long chatRoomId;
        private String senderNickname;
        private String message;
    }

}
