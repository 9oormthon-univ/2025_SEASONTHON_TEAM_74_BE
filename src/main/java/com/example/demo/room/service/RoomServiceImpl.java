package com.example.demo.room.service;

import com.example.demo.global.util.InviteCodeGenerator;
import com.example.demo.room.dto.res.RoomRes;
import com.example.demo.room.repository.RoomRepository;
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

    @Override
    public RoomRes.InviteCode createInviteCode() {

        String inviteCode = InviteCodeGenerator.generateInviteCode(8);
        while(roomRepository.existsByCode(inviteCode)){
            inviteCode = InviteCodeGenerator.generateInviteCode(8);
        }

        return new RoomRes.InviteCode(inviteCode);
    }
}
