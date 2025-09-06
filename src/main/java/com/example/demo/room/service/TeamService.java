package com.example.demo.room.service;

import com.example.demo.chat.dto.ChatRes;
import com.example.demo.room.dto.res.RoomRes;
import com.example.demo.room.dto.res.TeamRes;
import com.example.demo.room.entity.Team;
import com.example.demo.room.entity.TeamMember;
import com.example.demo.room.repository.TeamMemberRepository;
import com.example.demo.room.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class TeamService {

    private final SimpMessageSendingOperations messagingTemplate;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    //로비 전체 조회
    public void sendLobbyUpdate(Long roomId, String nickName) {
        String destination = "/topic/room/" + roomId + "/lobby"; // roomId를 사용하여 토픽 경로를 동적으로 생성


        List<Team> teams = teamRepository.findAllByRoomId(roomId);
        List<TeamRes.TeamDetail> teamDetails = teams.stream()
                .map(team -> {

                    // 3. 각 팀의 멤버들을 조회합니다. (N회 쿼리 발생)
                    List<TeamMember> teamMembers = teamMemberRepository.findAllByTeamIdAndRoomId(team.getId(), roomId);

                    Map<Boolean, List<String>> partitionedNicknames = teamMembers.stream()
                            .collect(Collectors.partitioningBy(
                                    TeamMember::getIsLeader,
                                    Collectors.mapping(
                                            tm -> tm.getUser().getNickName(),
                                            Collectors.toList()
                                    )
                            ));

                    List<String> memberNicknames = partitionedNicknames.get(false); // isLeader가 false인 사람들
                    String leaderNickname = partitionedNicknames.get(true).stream()
                            .findFirst()
                            .orElse(null); // isLeader가 true인 사람 (단 한 명이라고 가정)

                    // 4. TeamDetail 레코드를 생성하여 반환합니다.
                    return new TeamRes.TeamDetail(
                            team.getId(),
                            team.getTeamName(),
                            team.getIsReady(),
                            leaderNickname,
                            memberNicknames
                    );
                })
                .toList(); // 최종적으로 List<TeamDetail>을 반환

        messagingTemplate.convertAndSend(destination, new RoomRes.JoinRoom(roomId, nickName, teamDetails));
        log.info("{} 전체 로비 조회:", roomId);
    }

    public void sendTeamUpdate(Long roomId, Long teamId) {
        String destination = "/topic/room/" + roomId + "/team/" + teamId; // roomId를 사용하여 토픽 경로를 동적으로 생성

        Team team = teamRepository.findById(teamId).orElseThrow();
        List<TeamMember> teamMembers = teamMemberRepository.findAllByTeamIdAndRoomId(teamId, roomId);

        Map<Boolean, List<String>> partitionedNicknames = teamMembers.stream()
                .collect(Collectors.partitioningBy(
                        TeamMember::getIsLeader,
                        Collectors.mapping(
                                tm -> tm.getUser().getNickName(),
                                Collectors.toList()
                        )
                ));

        List<Map<String, Boolean>> members = partitionedNicknames.get(false).stream()
                .map(nickName -> Map.of(nickName, false))
                .collect(Collectors.toList());

        Map<String, Boolean> leader = partitionedNicknames.get(true).stream()
                .findFirst()
                .map(nickName -> Map.of(nickName, true))
                .orElse(Map.of()); // isLeader가 true인 사람 (단 한 명이라고 가정)

        RoomRes.TeamInfo teamInfo = new RoomRes.TeamInfo(
                team.getId(),
                team.getTeamName(),
                leader,
                members
        );

        messagingTemplate.convertAndSend(destination, teamInfo);
        log.info("{} {} 팀 조회:", roomId, teamId);
    }

//    public void sendMessageToTeam(Long chatRoomId, ChatRes.ChatMessageDtoRes message) {
//        String destination = "/topic/chat/" + chatRoomId; // roomId를 사용하여 토픽 경로를 동적으로 생성
//
//        messagingTemplate.convertAndSend(destination, message);
//        log.info("{} {} 팀 메시지 전송: {}", destination, chatRoomId, message);
//    }

}
