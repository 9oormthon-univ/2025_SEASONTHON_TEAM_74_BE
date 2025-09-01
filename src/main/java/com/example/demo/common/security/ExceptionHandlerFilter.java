package com.example.demo.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.demo.apiPayload.ApiResponse;
import com.example.demo.apiPayload.code.ErrorReasonDTO;
import com.example.demo.apiPayload.code.exception.GeneralException;
import com.example.demo.apiPayload.code.status.ErrorStatus;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);

            // 도메인 예외를 최우선으로 처리 (ErrorStatus 기반 통일 응답)
        } catch (GeneralException ex) {
            ErrorReasonDTO reason = ex.getErrorReasonHttpStatus(); // 상태/코드/메시지 포함
            log.warn("[GENERAL/JWT] code={}, msg={}", reason.getCode(), reason.getMessage());
            writeError(response, reason.getHttpStatus().value(), reason.getCode(), reason.getMessage(), null);

            // 혹시 Provider에서 GeneralException으로 래핑하지 못한 JWT 예외 방어 처리
        } catch (ExpiredJwtException e) {
            log.warn("[JWT] expired: {}", e.getMessage());
            writeError(response,
                    ErrorStatus.JWT_EXPIRED.getHttpStatus().value(),
                    ErrorStatus.JWT_EXPIRED.getCode(),
                    ErrorStatus.JWT_EXPIRED.getMessage(),
                    null);

        } catch (JwtException | IllegalArgumentException e) {
            log.warn("[JWT] invalid: {}", e.getMessage());
            writeError(response,
                    ErrorStatus.JWT_INVALID.getHttpStatus().value(),
                    ErrorStatus.JWT_INVALID.getCode(),
                    ErrorStatus.JWT_INVALID.getMessage(),
                    null);
        }
    }

    private void writeError(HttpServletResponse response, int httpStatus, String code,
                            String message, Object data) throws IOException {
        response.setStatus(httpStatus);
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");

        ApiResponse<Object> body = ApiResponse.onFailure(code, message, data);
        new ObjectMapper().writeValue(response.getWriter(), body);
    }
}
