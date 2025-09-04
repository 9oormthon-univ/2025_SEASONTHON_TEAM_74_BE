package com.example.demo.user.service;

import com.example.demo.user.dto.res.KakaoUserInfoResponseDto;
import com.example.demo.user.dto.res.UserRes;
import com.example.demo.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {

    User kakaoSignup(KakaoUserInfoResponseDto userInfo);

    UserRes.UserLoginRes kakaoLoginWeb(HttpServletRequest request, HttpServletResponse response, User user);

}
