package com.example.demo.room.service;

import com.example.demo.room.dto.req.RoomReq;
import com.example.demo.room.dto.res.RoomRes;

public interface RoomService {


    RoomRes.InviteCode createInviteCode();

    RoomRes.CreateRoom createRoom(RoomReq.CreateRoom request, Long user);

    RoomRes.JoinRoom joinRoom(RoomReq.JoinRoom request, Long user);

    void leaveRoom(Long roomId, Long userId);

    void removeRoom(Long roomId, Long userId);

    RoomRes.JoinRoom getLobbyInfo(Long roomId, Long userId);

    RoomRes.TeamInfo getTeamLobbyInfo(Long roomId, Long teamId, Long userId);

    RoomRes.TeamInfo changeLeader(Long roomId, Long userId, Long teamId, Boolean isLeader);
}
