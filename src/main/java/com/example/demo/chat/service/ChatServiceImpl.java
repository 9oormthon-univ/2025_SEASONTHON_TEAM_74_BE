package com.example.demo.chat.service;

import com.example.demo.chat.dto.ChatReq;
import com.example.demo.chat.dto.ChatRes;
import com.example.demo.chat.entity.ChatRoom;
import com.example.demo.chat.entity.Message;
import com.example.demo.chat.repository.ChatRoomRepository;
import com.example.demo.chat.repository.MessageRepository;
import com.example.demo.room.entity.TeamMember;
import com.example.demo.room.repository.TeamMemberRepository;
import com.example.demo.room.service.TeamService;
import com.example.demo.user.entity.User;
import com.example.demo.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final MessageRepository messageRepository;
    private final TeamService teamService;


    @Override
    public ChatRes.ChatMessageDtoRes createChat(Long userId, ChatReq.ChatMessageDtoReq chatMessageDto, Long chatRoomId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("ChatRoom not found with id: " + chatRoomId));

        //이후에 활성화 시켜야함!!
//      TeamMember teamMember = teamMemberRepository.findByUserIdAndTeamId(userId, chatRoom.getTeam().getId())
//                .orElseThrow(() -> new IllegalArgumentException("User is not a member of the team for this chat room"));

        Message message = messageRepository.save(Message.builder()
                .content(chatMessageDto.getMessage())
                .user(user)
                .chatRoom(chatRoom)
                .build());

        return new ChatRes.ChatMessageDtoRes(
                chatRoomId,
                message.getId(),
                message.getContent(),
                user.getNickName(),
                message.getCreatedAt()
        );

    }

    @Override
    public List<ChatRes.ChatMessageDtoRes> getChat(Long userId, Long chatRoomId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("ChatRoom not found with id: " + chatRoomId));

        TeamMember teamMember = teamMemberRepository.findByUserIdAndTeamId(userId, chatRoom.getTeam().getId())
                .orElseThrow(() -> new IllegalArgumentException("User is not a member of the team for this chat room"));

        List<Message> messageList = messageRepository.findTop30ByChatRoomIdOrderByCreatedAtDesc(chatRoomId);

        return messageList.stream().map(
                        m -> new ChatRes.ChatMessageDtoRes(
                                chatRoomId,
                                m.getId(),
                                m.getContent(),
                                m.getUser().getNickName(),
                                m.getCreatedAt())
                        ).toList();
    }
}
