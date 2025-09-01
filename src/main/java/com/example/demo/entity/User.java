package com.example.demo.entity;

import com.example.demo.entity.enums.Provider;
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
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;//카카오 유저는 비번 없음

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider; //회원가입 플랫폼 : KAKAO , LOCAL

}
