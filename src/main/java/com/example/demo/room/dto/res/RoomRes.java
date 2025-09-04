package com.example.demo.room.dto.res;

import com.example.demo.room.entity.enums.Mode;
import com.example.demo.room.entity.enums.RoomStatus;

public class RoomRes {

    public record InviteCode(String code) {}

    public record CreateRoom(
            Long roomId, Integer maxMember, String pwd, String inviteCode,
            Integer maxTeam, Integer maxRound, RoomStatus roomStatus, Mode mode,
            String yearSet, Integer seedMoney, String nickName) {}
}
