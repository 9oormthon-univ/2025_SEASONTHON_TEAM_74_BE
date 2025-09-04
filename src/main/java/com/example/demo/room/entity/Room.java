package com.example.demo.room.entity;

import com.example.demo.global.BaseEntity;
import com.example.demo.room.entity.enums.Mode;
import com.example.demo.room.entity.enums.RoomStatus;
import com.example.demo.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "room")
public class Room extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invite_code" , unique = true)
    private String inviteCode;

    @Column
    private String pwd;

    @Column(name = "seed_money")
    private Integer seedMoney;

    @Column(name = "year_set")
    private String yearSet;

    @Column(name = "max_round")
    private Integer maxRound;

    @Column(name = "max_member")
    private Integer maxMember;

    @Column(name = "max_team")
    private Integer maxTeam;

    @Column
    @Enumerated(EnumType.STRING)
    private Mode mode;

    @Column
    @Enumerated(EnumType.STRING)
    private RoomStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // 방장

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Team> teams = new ArrayList<>();
}
