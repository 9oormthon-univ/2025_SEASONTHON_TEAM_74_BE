package com.example.demo.chat.service;

import com.example.demo.chat.dto.ChatReq;
import com.example.demo.chat.dto.ChatRes;

import java.util.List;


public interface ChatService {

    ChatRes.ChatMessageDtoRes createChat(Long userId, ChatReq.ChatMessageDtoReq chatMessageDto, Long chatRoomId);

    List<ChatRes.ChatMessageDtoRes> getChat(Long userId, Long chatRoomId);
}
