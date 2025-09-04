package com.example.demo.user.service;

import com.example.demo.user.dto.res.KakaoTokenResponseDto;
import com.example.demo.user.dto.res.KakaoUserInfoResponseDto;
import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class KakaoService {

    private String clientId;
    private final String KAUTH_TOKEN_URL_HOST;
    private final String KAUTH_USER_URL_HOST;
    @Value("${kakao.redirect_uri}")
    private String redirectUri;

    @Autowired
    public KakaoService(@Value("${kakao.client_id}") String clientId) {
        this.clientId = clientId;
        KAUTH_TOKEN_URL_HOST = "https://kauth.kakao.com";
        KAUTH_USER_URL_HOST = "https://kapi.kakao.com";
    }

    //인가 토큰을 보내야함
    //https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#request-token
    public String getAccessTokenFromKakao(String code) {
        KakaoTokenResponseDto kakaoTokenResponseDto = WebClient.create(KAUTH_TOKEN_URL_HOST).post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/oauth/token")
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", clientId)
                        .queryParam("redirect_uri", redirectUri)
                        .queryParam("code", code)
                        .build(true))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .retrieve()
                //TODO : Custom Exception
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .bodyToMono(KakaoTokenResponseDto.class)
                .block();

//        log.info(" [Kakao Service] Access Token ------> {}", kakaoTokenResponseDto.getAccessToken());
//        log.info(" [Kakao Service] Refresh Token ------> {}", kakaoTokenResponseDto.getRefreshToken());
//        log.info(" [Kakao Service] Scope ------> {}", kakaoTokenResponseDto.getScope());
        return kakaoTokenResponseDto.getAccessToken();
    }
    public KakaoUserInfoResponseDto getUserInfo(String accessToken) {

        KakaoUserInfoResponseDto userInfo = WebClient.create(KAUTH_USER_URL_HOST)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/v2/user/me")
                        .build(true))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken) // access token 인가
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                // TODO: Custom Exception
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .bodyToMono(KakaoUserInfoResponseDto.class)
                .block();

//        log.info("[ Kakao Service ] Auth ID ---> {}", userInfo.getId());
//        // 닉네임 정보 로깅 (동의 여부 확인 후 로깅)
//        if (userInfo.getKakaoAccount().getIsNickNameAgree() != null && userInfo.getKakaoAccount().getIsNickNameAgree()
//                && userInfo.getKakaoAccount().getProfile() != null && userInfo.getKakaoAccount().getProfile().getNickName() != null) {
//            log.info("[ Kakao Service ] NickName ---> {}", userInfo.getKakaoAccount().getProfile().getNickName());
//        } else {
//            log.info("[ Kakao Service ] NickName ---> Not provided (User did not agree or data unavailable)");
//        }
//        // 이메일 정보 로깅 (동의 여부 확인 후 로깅)
//        if (userInfo.getKakaoAccount().getIsEmailAgree() != null && userInfo.getKakaoAccount().getIsEmailAgree()
//                && userInfo.getKakaoAccount().getEmail() != null) {
//            log.info("[ Kakao Service ] Email ---> {}", userInfo.getKakaoAccount().getEmail());
//        } else {
//            log.info("[ Kakao Service ] Email ---> Not provided (User did not agree or data unavailable)");
//        }

        return userInfo;
    }
}
