package com.example.demo.room.service;

import com.example.demo.room.dto.req.RoomReq;
import com.example.demo.room.dto.res.RoomRes;

public interface RoomService {


    RoomRes.InviteCode createInviteCode();

    RoomRes.CreateRoom createRoom(RoomReq.CreateRoom request, Long user);
}
