package com.example.demo.chat.controller;

import com.example.demo.apiPayload.ApiResponse;
import com.example.demo.chat.dto.ChatReq;
import com.example.demo.chat.dto.ChatRes;
import com.example.demo.chat.service.ChatService;
import com.example.demo.chat.service.ChatServiceImpl;
import com.example.demo.common.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/chat")
@RestController
public class ChatController {

    private final JwtTokenProvider jwtTokenProvider;
    private final ChatService chatService;

//    //메시지 저장
//    @PostMapping("/{chatRoomId}/message")
//    public ApiResponse<?> createChat(@RequestBody ChatReq.ChatMessageDto chatMessageDto, @PathVariable Long chatRoomId) {
//        Long userId = jwtTokenProvider.getUserIdFromToken();
//
//        return ApiResponse.onSuccess(chatService.createChat(userId, chatMessageDto, chatRoomId));
//    }

    //채팅창 조회
    @GetMapping("/{chatRoomId}/get")
    public ApiResponse<List<ChatRes.ChatMessageDtoRes>> getChat(@PathVariable Long chatRoomId) {
        Long userId = jwtTokenProvider.getUserIdFromToken();
        return ApiResponse.onSuccess(chatService.getChat(userId, chatRoomId));
    }

}
