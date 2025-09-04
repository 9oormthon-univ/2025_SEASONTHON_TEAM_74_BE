package com.example.demo.user.service;

import com.example.demo.common.security.JwtTokenProvider;
import com.example.demo.global.util.CookieUtil;
import com.example.demo.user.converter.UserConverter;
import com.example.demo.user.dto.res.KakaoUserInfoResponseDto;
import com.example.demo.user.dto.res.UserRes;
import com.example.demo.user.entity.User;
import com.example.demo.user.entity.enums.Provider;
import com.example.demo.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    // 카카오 로그인 시 신규 회원가입 또는 기존 회원 조회
    public User kakaoSignup(KakaoUserInfoResponseDto userInfo) {
        return userRepository.findByEmail(userInfo.getKakaoAccount().getEmail())
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(userInfo.getKakaoAccount().getEmail())
                            .nickName(userInfo.getKakaoAccount().getProfile().getNickName())
                            .provider(Provider.KAKAO)
                            .build();
                    userRepository.save(newUser);
                    return newUser;
                });
    }

    // 카카오 로그인 처리 후 토큰 발급(웹-쿠키 추가)
    public UserRes.UserLoginRes kakaoLoginWeb(HttpServletRequest request, HttpServletResponse response, User user) {
        String accessToken = jwtTokenProvider.createAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        // 리플레시 토큰 쿠키 저장
        CookieUtil.deleteCookie(request, response, "refreshToken");
        CookieUtil.addCookie(response, "refreshToken", refreshToken, JwtTokenProvider.REFRESH_TOKEN_VALID_TIME_IN_COOKIE);

        UserRes.UserLoginRes res =
                UserConverter.signInRes(user, accessToken, refreshToken, user.getNickName());
        res.setRefreshToken(null); // 웹 응답에서 숨김
        return res;

    }
}
