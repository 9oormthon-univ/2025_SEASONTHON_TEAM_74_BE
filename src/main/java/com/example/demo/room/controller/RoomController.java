package com.example.demo.room.controller;

import com.example.demo.apiPayload.ApiResponse;
import com.example.demo.room.dto.res.RoomRes;
import com.example.demo.room.service.RoomService;
import com.example.demo.room.service.RoomServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    //초대 코드 발급
    @PostMapping("/invites")
    public ApiResponse<RoomRes.InviteCode> createInvite() {

        return ApiResponse.onSuccess(roomService.createInviteCode());
    }



}
