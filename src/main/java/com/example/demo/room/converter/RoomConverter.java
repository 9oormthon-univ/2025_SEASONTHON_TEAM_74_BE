package com.example.demo.room.converter;

import com.example.demo.room.dto.req.RoomReq;
import com.example.demo.room.entity.Room;
import com.example.demo.room.entity.enums.RoomStatus;
import com.example.demo.user.entity.User;

public class RoomConverter {

    public static Room toRoomEntity(RoomReq.CreateRoom req, User user) {
        return Room.builder()
                .inviteCode(req.getInviteCode())
                .pwd(req.getPwd())
                .seedMoney(req.getSeedMoney())
                .yearSet(req.getYearSet())
                .maxRound(req.getMaxRound())
                .maxTeam(req.getMaxTeam())
                .maxMember(req.getMaxMember())
                .mode(req.getMode())
                .status(RoomStatus.OPEN)
                .user(user)
                .build();
    }

}
