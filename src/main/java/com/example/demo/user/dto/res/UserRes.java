package com.example.demo.user.dto.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public class UserRes {

    @Data
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL) // null 필드는 응답에서 제외
    public static class UserLoginRes {
        private Long id;
        private String email;
        private String accessToken;
        private String refreshToken; // 웹에선 null로 세팅해서 숨김
        private String nickname;
    }
}