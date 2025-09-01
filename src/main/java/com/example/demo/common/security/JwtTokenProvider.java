package com.example.demo.common.security;

import com.example.demo.apiPayload.code.exception.GeneralException;
import com.example.demo.apiPayload.code.status.ErrorStatus;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    private final RedisTemplate redisTemplate;
    @Value("${jwt.secret}")
    private String stringSecretKey;

    private Key secretKey;

    public static final long TOKEN_VALID_TIME = 1000L * 60 * 60 * 24;  // 액세스 토큰 24시간
    public static final long REFRESH_TOKEN_VALID_TIME = 1000L * 60 * 60 * 24 * 7; // 리프레시 토큰 1주일
    public static final int REFRESH_TOKEN_VALID_TIME_IN_COOKIE = 60 * 60 * 24 * 7; // 리프레시  토큰 일주일(초) -쿠키

    private final CustomUserDetailsService userDetailService;

//    @Qualifier("jwtRedisTemplate")
//    private final RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    protected void init() {
        byte[] keyBytes = Base64.getDecoder().decode(stringSecretKey.getBytes(StandardCharsets.UTF_8));
        secretKey = new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    // 액세스 토큰 생성
    public String createAccessToken(Long userId) {
        return Jwts.builder()
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALID_TIME))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // 리프레시 토큰 생성 후 Redis에 저장
    public String createRefreshToken(Long userId) {
        String refreshToken = Jwts.builder()
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALID_TIME))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        redisTemplate.opsForValue().set(
                "RT:" + userId,
                refreshToken,
                REFRESH_TOKEN_VALID_TIME,
                TimeUnit.MILLISECONDS
        );
        return refreshToken;
    }

    // 저장된 리프레시 토큰을 Redis에서 가져오기
    public String getStoredRefreshToken(Long userId) {
        return (String) redisTemplate.opsForValue().get("RT:" + userId);
    }

    // 리프레시 토큰 삭제 (Redis에서 제거)
    public void deleteRefreshToken(Long userId) {
        redisTemplate.delete("RT:" + userId);
    }

    // JWT 토큰의 유효성을 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    // 던지는 검증 메서드 추가

    public void assertValidTokenOrThrow(String token) {
        if (token == null || token.isBlank()) {
            throw new GeneralException(ErrorStatus.JWT_EMPTY);
        }
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .setAllowedClockSkewSeconds(60)// 시계 오차 허용
                    .build()
                    .parseClaimsJws(token); // 유효성 검사(여기서 ExpiredJwtException, MalformedJwtException, SignatureException 등 그대로 던짐)
        } catch (ExpiredJwtException e) {
            throw new GeneralException(ErrorStatus.JWT_EXPIRED);
        } catch (MalformedJwtException | io.jsonwebtoken.security.SignatureException | UnsupportedJwtException e) {
            throw new GeneralException(ErrorStatus.JWT_INVALID);
        } catch (IllegalArgumentException e) {
            throw new GeneralException(ErrorStatus.JWT_EMPTY);
        }

        if (isTokenInvalidated(token)) {
            throw new GeneralException(ErrorStatus.JWT_INVALID); // 블랙리스트
        }
    }

    // 리프레시 토큰 유효성 검증 (Redis에 저장된 토큰과 비교)
    public boolean validateRefreshToken(String token, Long userId) {
        String storedToken = getStoredRefreshToken(userId);
        return token.equals(storedToken) && validateToken(token);
    }

    // 로그아웃 시 토큰을 블랙리스트에 추가하여 무효화
    public void invalidateToken(String token) {
        long expiration = getTokenExpiration(token);
        redisTemplate.opsForValue().set("BL:" + token, "logout", expiration, TimeUnit.MILLISECONDS);
    }

    // 토큰이 블랙리스트에 있는지 확인
    public boolean isTokenInvalidated(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("BL:" + token));
    }

    // 토큰의 남은 만료 시간을 계산
    private long getTokenExpiration(String token) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
            return claims.getExpiration().getTime() - System.currentTimeMillis();
        } catch (ExpiredJwtException e) {
            return 0;
        }
    }

    // 액세스 토큰에서 사용자 ID 추출
    public Long getUserIdFromToken() {
        String accessToken = resolveAccessToken();
        return getUserIdInToken(accessToken);
    }

    // JWT 토큰에서 사용자 ID를 직접 추출
    public Long getUserIdInToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
        return claims.get("userId", Long.class);
    }

    // HTTP 요청에서 액세스 토큰 추출 (Bearer 접두사 제거)
    public String resolveAccessToken() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization");
        return (token != null && token.startsWith("Bearer ")) ? token.substring(7) : token;
    }

    // HTTP 요청에서 리프레시 토큰 추출 (Bearer 접두사 제거)
    public String resolveRefreshToken() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        // 헤더 우선
        String token = request.getHeader("Refresh-Token");
        if (token != null && token.startsWith("Bearer ")) token = token.substring(7);
        if (token != null && !token.isBlank()) return token;

        // 쿠키 fallback
        if (request.getCookies() != null) {
            for (var c : request.getCookies()) {
                if ("refreshToken".equals(c.getName())) {
                    return c.getValue();
                }
            }
        }
        return null;
    }

    // 토큰에서 인증 정보 추출하여 Authentication 객체 생성
    public Authentication getAuthentication(String token) {
        String userId = String.valueOf(getUserIdInToken(token));
        UserDetails userDetails = userDetailService.loadUserByUsername(userId);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // JwtAuthenticationFilter 에 적용: 필터 내에서 유효성 검증 후 AuthenticationContext에 저장
    public boolean processTokenAndSetAuthContext(String token) {
        if (token != null && validateToken(token) && !isTokenInvalidated(token)) {
            Authentication authentication = getAuthentication(token);
            org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(authentication);
            return true;
        }
        return false;
    }

    public void processTokenAndSetAuthContextOrThrow(String token) {
        assertValidTokenOrThrow(token); // 유효성 + 블랙리스트 체크 중 예외 던짐
        Authentication authentication = getAuthentication(token);
        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // UserServiceImpl 에서 로그아웃 처리 시 사용: 블랙리스트 및 리프레시 토큰 삭제
    public void handleLogout(String accessToken, String refreshToken) {
        if (accessToken != null) {
            invalidateToken(accessToken);
        }
        if (refreshToken != null) {
            Long userId = getUserIdInToken(refreshToken);
            deleteRefreshToken(userId);
        }
    }
    /**
     * Spring Security의 Authentication 객체에서 사용자 ID(userId)를 추출합니다.
     *
     * JWT 인증이 완료된 사용자의 Authentication.getPrincipal()에는 보통 userId가 문자열 형태로 저장됩니다.
     * 예) Authentication.getPrincipal() → "42" (userId = 42)
     *
     * 인증 방식에 따라 principal 타입이 다를 수 있기 때문에,
     * String 또는 UserDetails 형태에 모두 대응하도록 처리합니다.
     *
     * @param authentication 인증 객체
     * @return userId (Long) 또는 인증되지 않았을 경우 null
     */
    public Long getUserIdInAuthentication(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return null;

        Object principal = authentication.getPrincipal();

        if (principal instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
            // 기본 UserDetails 구현을 사용하는 경우
            return Long.parseLong(userDetails.getUsername()); // 보통 userId를 username에 넣는 경우
        } else if (principal instanceof String principalString) {
            // JWT 인증 시 principal을 userId 문자열로 넣어둔 경우
            return Long.parseLong(principalString);
        }

        return null;
    }

}
