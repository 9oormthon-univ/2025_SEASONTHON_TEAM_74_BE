package com.example.demo.common.security.handler;

import com.example.demo.apiPayload.code.status.ErrorStatus;
import com.example.demo.common.security.JwtAuthenticationFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        String authz = request.getHeader("Authorization");
        boolean hasBearer = (authz != null && authz.startsWith("Bearer "));

        log.warn("[UNAUTHORIZED] uri={}, hasBearer={}, ex={}",
                request.getRequestURI(), hasBearer, authException.getMessage());

        if (!hasBearer) {
            // 토큰 자체가 없음
            JwtAuthenticationFilter.setErrorResponse(response, ErrorStatus.JWT_EMPTY);
        } else {
            // 토큰은 있는데 인증 실패(서명오류/만료 등)로 미인증 상태가 된 케이스
            JwtAuthenticationFilter.setErrorResponse(response, ErrorStatus.JWT_INVALID);
        }
    }
}

