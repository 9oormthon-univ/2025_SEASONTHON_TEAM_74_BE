package com.example.demo.user.dto.res;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoUserInfoResponseDto {

    @JsonProperty("id")
    public Long id; // 카카오 사용자 고유 ID

    @JsonProperty("kakao_account")
    public KakaoAccount kakaoAccount; // 카카오 계정 정보

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class KakaoAccount {

        @JsonProperty("email_needs_agreement")
        public Boolean isEmailAgree; // 이메일 제공 동의 여부

        @JsonProperty("email")
        public String email; // 이메일 정보

        @JsonProperty("profile_nickname_needs_agreement")
        public Boolean isNickNameAgree; // 닉네임 제공 동의 여부

        @JsonProperty("profile")
        public Profile profile;

        @Getter
        @NoArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public class Profile {

            @JsonProperty("nickname")
            public String nickName; // 닉네임 정보
        }
    }
}
