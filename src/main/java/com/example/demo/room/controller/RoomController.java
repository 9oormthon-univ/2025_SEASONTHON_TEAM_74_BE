package com.example.demo.room.controller;

import com.example.demo.apiPayload.ApiResponse;
import com.example.demo.common.security.JwtTokenProvider;
import com.example.demo.room.dto.req.RoomReq;
import com.example.demo.room.dto.res.RoomRes;
import com.example.demo.room.service.RoomService;
import com.example.demo.room.service.RoomServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final JwtTokenProvider jwtTokenProvider;

    //초대 코드 발급
    @PostMapping("/invites")
    public ApiResponse<RoomRes.InviteCode> createInvite() {

        return ApiResponse.onSuccess(roomService.createInviteCode());
    }

    //방 만들기
    @PostMapping("")
    public ApiResponse<RoomRes.CreateRoom> createRoom(@Valid @RequestBody RoomReq.CreateRoom request) {
        Long user = jwtTokenProvider.getUserIdFromToken();
        return ApiResponse.onSuccess(roomService.createRoom(request, user));
    }

    //방 참가하기
//    @PostMapping("")
//    public ApiResponse<RoomRes.CreateRoom> createRoom(@Valid @RequestBody RoomReq.CreateRoom request) {
//        Long user = jwtTokenProvider.getUserIdFromToken();
//        return ApiResponse.onSuccess(roomService.createRoom(request, user));
//    }



}
