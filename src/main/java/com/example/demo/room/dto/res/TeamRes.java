package com.example.demo.room.dto.res;

import java.util.List;

public class TeamRes {

    public record TeamDetail(
            Long teamId,
            String teamName,
            Boolean isReady,
            String leaderNickName,
            List<String> memberNickNames
    ) {}
}
