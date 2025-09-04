package com.example.demo.room.service;

import com.example.demo.global.util.InviteCodeGenerator;
import com.example.demo.room.converter.RoomConverter;
import com.example.demo.room.dto.req.RoomReq;
import com.example.demo.room.dto.res.RoomRes;
import com.example.demo.room.entity.Room;
import com.example.demo.room.repository.RoomRepository;
import com.example.demo.user.entity.User;
import com.example.demo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

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
}
