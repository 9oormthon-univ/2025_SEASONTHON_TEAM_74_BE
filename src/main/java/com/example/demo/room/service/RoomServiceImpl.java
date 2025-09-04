package com.example.demo.room.service;

import com.example.demo.global.util.InviteCodeGenerator;
import com.example.demo.room.converter.RoomConverter;
import com.example.demo.room.dto.req.RoomReq;
import com.example.demo.room.dto.res.RoomRes;
import com.example.demo.room.dto.res.TeamRes;
import com.example.demo.room.entity.Room;
import com.example.demo.room.entity.Team;
import com.example.demo.room.entity.TeamMember;
import com.example.demo.room.repository.RoomRepository;
import com.example.demo.room.repository.TeamMemberRepository;
import com.example.demo.room.repository.TeamRepository;
import com.example.demo.user.entity.User;
import com.example.demo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;

    @Override
    public RoomRes.InviteCode createInviteCode() {

        String inviteCode = InviteCodeGenerator.generateInviteCode(8);
        while(roomRepository.existsByInviteCode(inviteCode)){
            inviteCode = InviteCodeGenerator.generateInviteCode(8);
        }

        return new RoomRes.InviteCode(inviteCode);
    }

    @Override
    public RoomRes.CreateRoom createRoom(RoomReq.CreateRoom request, Long userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        //방장 닉네임 수정
        user.setNickName(request.getNickname());
        userRepository.save(user);

        Room room = roomRepository.save(RoomConverter.toRoomEntity(request, user));

        return new RoomRes.CreateRoom(room.getId(), room.getMaxMember(), room.getPwd(), room.getInviteCode(),
                room.getMaxTeam(), room.getMaxRound(), room.getStatus(), room.getMode(),
                room.getYearSet(), room.getSeedMoney(), user.getNickName());
    }

    @Override
    public RoomRes.JoinRoom joinRoom(RoomReq.JoinRoom request, Long userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        //닉네임 수정
        user.setNickName(request.getNickname());
        userRepository.save(user);

        //초대 코드로 방 찾기
        Room room = roomRepository.findByInviteCode(request.getInviteCode()).orElseThrow(() -> new RuntimeException("Room not found"));

        //비밀번호가 있는 방인지 확인
        if(room.getPwd() != null){
            if(!room.getPwd().equals(request.getPwd())){
                throw new RuntimeException("비밀번호가 다릅니다.");
            }
        }

        //팀 멤버에 본인 등록
        TeamMember teamMember = TeamMember.builder()
                .room(room)
                .user(user)
                .isLeader(false)
                .build();

        teamMemberRepository.save(teamMember);

        List<Team> teams = teamRepository.findAllByRoomId(room.getId());

        List<TeamRes.TeamDetail> teamDetails = teams.stream()
                .map(team -> {

                    // 3. 각 팀의 멤버들을 조회합니다. (N회 쿼리 발생)
                    List<TeamMember> teamMembers = teamMemberRepository.findAllByTeamId(team.getId());

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


        return new RoomRes.JoinRoom(room.getId(), user.getNickName(), teamDetails);
    }
}
