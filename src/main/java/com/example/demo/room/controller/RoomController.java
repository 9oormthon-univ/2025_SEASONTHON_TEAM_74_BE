package com.example.demo.room.controller;

import com.example.demo.apiPayload.ApiResponse;
import com.example.demo.common.security.JwtTokenProvider;
import com.example.demo.room.dto.req.RoomReq;
import com.example.demo.room.dto.res.RoomRes;
import com.example.demo.room.service.RoomService;
import com.example.demo.room.service.RoomServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/join")
    public ApiResponse<RoomRes.JoinRoom> joinRoom(@Valid @RequestBody RoomReq.JoinRoom request) {
        Long user = jwtTokenProvider.getUserIdFromToken();
        return ApiResponse.onSuccess(roomService.joinRoom(request, user));
    }

    //방 나가기
    @DeleteMapping("/{roomId}/participants/me")
    public ApiResponse<String> leaveRoom(@PathVariable Long roomId) {
        Long userId = jwtTokenProvider.getUserIdFromToken();
        roomService.leaveRoom(roomId, userId);
        return ApiResponse.onSuccess("방 나가기 성공");
    }

    //방 제거하기
    @DeleteMapping("/{roomId}/remove")
    public ApiResponse<String> removeRoom(@PathVariable Long roomId) {
        Long userId = jwtTokenProvider.getUserIdFromToken();
        roomService.removeRoom(roomId, userId);
        return ApiResponse.onSuccess("방 제거 성공");
    }

    //로비 조회
    @GetMapping("/{roomId}/lobby")
    public ApiResponse<RoomRes.JoinRoom> getLobbyInfo(@PathVariable Long roomId) {
        //로비 정보 조회 기능 구현 필요
        Long userId = jwtTokenProvider.getUserIdFromToken();

        return ApiResponse.onSuccess(roomService.getLobbyInfo(roomId, userId));
    }

    //팀 대기실 조회
    @GetMapping("/{roomId}/{teamId}")
    public ApiResponse<RoomRes.TeamInfo> getTeamLobbyInfo(@PathVariable Long roomId, @PathVariable Long teamId) {
        Long userId = jwtTokenProvider.getUserIdFromToken();

        return ApiResponse.onSuccess(roomService.getTeamLobbyInfo(roomId, teamId, userId));
    }

    //팀장하기
    @PatchMapping("/{roomId}/{teamId}/leader")
    public ApiResponse<RoomRes.TeamInfo> changeLeader(@PathVariable Long roomId, @PathVariable Long teamId) {
        Long userId = jwtTokenProvider.getUserIdFromToken();

        return ApiResponse.onSuccess(roomService.changeRole(roomId, userId, teamId, true));
    }

    //팀원하기
    @PatchMapping("/{roomId}/{teamId}/member")
    public ApiResponse<RoomRes.TeamInfo> changeMember(@PathVariable Long roomId, @PathVariable Long teamId) {
        Long userId = jwtTokenProvider.getUserIdFromToken();

        return ApiResponse.onSuccess(roomService.changeRole(roomId, userId, teamId, false));
    }

    //팀원 준비
    @PatchMapping("/{roomId}/me/ready")
    public ApiResponse<String> changeReady(@PathVariable Long roomId) {
        Long userId = jwtTokenProvider.getUserIdFromToken();
        String message = roomService.readyTeamMember(roomId, userId);
        return ApiResponse.onSuccess(message);
    }

}
