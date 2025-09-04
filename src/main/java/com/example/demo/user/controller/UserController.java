package com.example.demo.user.controller;

import com.example.demo.apiPayload.ApiResponse;
import com.example.demo.common.security.JwtTokenProvider;
import com.example.demo.user.dto.res.KakaoUserInfoResponseDto;
import com.example.demo.user.dto.res.UserRes;
import com.example.demo.user.service.KakaoService;
import com.example.demo.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final KakaoService kakaoService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/callback")
    public ApiResponse<UserRes.UserLoginRes> callback(@RequestParam("code") String code, HttpServletRequest request, HttpServletResponse response) {
        String accessToken = kakaoService.getAccessTokenFromKakao(code);
        KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(accessToken);

        //회원가입, 로그인 동시진행
        return ApiResponse.onSuccess(userService.kakaoLoginWeb(request,response, userService.kakaoSignup(userInfo)));
    }

    //jwt 발급용 api
    @GetMapping("/jwt")
    public String getJwtToken() {
        return jwtTokenProvider.createAccessToken(1L);
    }



}
