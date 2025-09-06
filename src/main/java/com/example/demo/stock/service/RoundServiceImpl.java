package com.example.demo.stock.service;

import com.example.demo.room.entity.Team;
import com.example.demo.room.repository.TeamRepository;
import com.example.demo.stock.dto.res.RoundRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class RoundServiceImpl implements RoundService {


    private final TeamRepository teamRepository;

    @Override
    public RoundRes.RoundLockRes lockRound(Long roomId, Long roundId) {
        //1. roomId, roundId로 room, round 찾기
        //2. round 상태를 잠금으로 변경
        //3. teamId, teamName, maximumStockName, maximumStockMoney, status 반환
        //4.

        List<Team> teamList = teamRepository.findAllByRoomId(roomId);
        for (Team team : teamList) {
            RoundRes.RoundLockRes roundLockRes = new RoundRes.RoundLockRes(
                    team.getId(),
                    team.getName(),
                    "최대주식이름",
                    10000,
                    "LOCKED"
            );
            return roundLockRes;
        }


        return
    }
}
