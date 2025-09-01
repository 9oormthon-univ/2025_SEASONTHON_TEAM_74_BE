package com.example.demo.common.security;

import com.example.demo.apiPayload.ApiResponse;
import com.example.demo.apiPayload.code.status.ErrorStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String token = jwtTokenProvider.resolveAccessToken();

        if (token != null && !token.isBlank()) {
            // 민감정보 전체 로그 금지 (프리뷰만)
            String tokenPreview = token.length() > 10 ? token.substring(0, 10) + "..." : token;
            log.debug("JwtAuthenticationFilter 토큰 프리뷰: {}", tokenPreview);

            // 여기서 '던지는' 메서드를 호출.
            //     - 유효하지 않거나 블랙리스트면 ExpiredJwtException/JwtException/IllegalArgumentException 등이 던져짐
            //     - 이 예외는 SecurityConfig에서 JwtAuthenticationFilter 앞에 둔 ExceptionHandlerFilter가 catch해서 통일 응답 작성
            jwtTokenProvider.processTokenAndSetAuthContextOrThrow(token);
        }

        // token == null (혹은 공백): 그냥 다음 체인으로 넘김
        // - 보호 리소스 접근 시에는 스프링 시큐리티가 AuthenticationEntryPoint로 흐름을 보냄
        chain.doFilter(request, response);
    }

    // 아래 유틸은 다른 핸들러(EntryPoint/AccessDenied/ExceptionHandlerFilter)에서 재사용하므로 그대로 둔다.
    public static void setErrorResponse(HttpServletResponse response, ErrorStatus errorCode) throws IOException {
        setErrorResponse(response, errorCode, null);
    }

    private static void setErrorResponse(HttpServletResponse response, ErrorStatus errorCode, Object data) throws IOException {
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");

        ApiResponse<Object> failure = ApiResponse.onFailure(
                errorCode.getCode(),
                errorCode.getMessage(),
                data
        );

        new ObjectMapper().writeValue(response.getWriter(), failure);
    }
}
