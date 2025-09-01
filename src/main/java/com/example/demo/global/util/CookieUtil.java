package com.example.demo.global.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {
    // 요청 값 (이름, 값, 만료기간)을 바탕으로 쿠키 추가
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        cookie.setSecure(true); // HTTPS만
        response.addCookie(cookie);
    }

    // 쿠키의 이름을 입력받아 쿠키 삭제
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name){
        Cookie[] cookies = request.getCookies();
        if (cookies == null){
            return;
        }

        for (Cookie cookie : cookies){
            if (name.equals(cookie.getName())){
                cookie.setValue("");
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
    }
//
//    // 객체를 직렬화해 쿠키의 값으로 반환
//    public static String serialize(Object obj){
//        return Base64.getUrlEncoder()
//                .encodeToString(SerializationUtils.serialize(obj));
//    }
//
//    // 쿠키를 역질렬화해 객체로 변환
//    public static <T> T deserialize(Cookie cookie, Class<T> cls){
//        return cls.cast(
//                SerializationUtils.deserialize(
//                        Base64.getUrlDecoder().decode(cookie.getValue())
//                )
//        );
//    }
}