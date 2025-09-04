package com.example.demo.room.dto.res;

import com.example.demo.room.entity.enums.Mode;
import com.example.demo.room.entity.enums.RoomStatus;

import java.util.List;
import java.util.Map;

public class RoomRes {

    public record InviteCode(String code) {}

    public record CreateRoom(
            Long roomId, Integer maxMember, String pwd, String inviteCode,
            Integer maxTeam, Integer maxRound, RoomStatus roomStatus, Mode mode,
            String yearSet, Integer seedMoney, String nickName) {}

    public record JoinRoom(Long roomId, String nickName, List<TeamRes.TeamDetail> teamDetail) {}

    public record TeamInfo(Long teamId, String teamName, Map<String, Boolean> leader, List<Map<String, Boolean>> members) {}
}
