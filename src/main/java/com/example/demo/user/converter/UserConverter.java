package com.example.demo.user.converter;


import com.example.demo.user.dto.res.UserRes;
import com.example.demo.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {
    public static UserRes.UserLoginRes signInRes(User user, String accessToken, String refreshToken, String name) {
        return UserRes.UserLoginRes.builder()
                .id(user.getId())
                .email(user.getEmail())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .nickname(name)
                .build();
    }
}
