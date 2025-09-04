package com.example.demo.global.util;

import java.security.SecureRandom;

public class InviteCodeGenerator {

    // 초대 코드에 사용할 문자셋 정의
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 지정된 길이의 난수 초대 코드를 생성합니다.
     * @param length 생성할 코드의 길이
     * @return 생성된 초대 코드 문자열
     */
    public static String generateInviteCode(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("길이는 1보다 커야 합니다.");
        }

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            // 문자셋에서 무작위로 한 문자를 선택하여 추가
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}