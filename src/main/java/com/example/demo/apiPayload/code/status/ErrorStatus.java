package com.example.demo.apiPayload.code.status;

import com.example.demo.apiPayload.code.BaseErrorCode;
import com.example.demo.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {
    // 에러 예시
    FAIL_OOOOO(HttpStatus.BAD_REQUEST, "FAIL", "실패하였습니다."),

    // 토큰 관련 에러
    JWT_FORBIDDEN(HttpStatus.FORBIDDEN, "JWT4000", "권한이 없습니다."),
    JWT_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "JWT4001", "인증이 필요합니다."),
    JWT_INVALID(HttpStatus.UNAUTHORIZED, "JWT4002", "유효하지 않은 토큰입니다."),
    JWT_EMPTY(HttpStatus.UNAUTHORIZED, "JWT4003", "JWT 토큰을 넣어주세요."),
    JWT_EXPIRED(HttpStatus.UNAUTHORIZED, "JWT4004", "만료된 토큰입니다."),
    JWT_REFRESHTOKEN_NOT_MATCHED(HttpStatus.UNAUTHORIZED, "JWT4005", "RefreshToken이 일치하지 않습니다."),
    JWT_REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "JWT4031", "리프레시 토큰이 존재하지 않거나 만료되었습니다."),
    JWT_REFRESH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "JWT4032", "유효하지 않은 리프레시 토큰입니다."),

    // 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    //User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER4004", "사용자를 찾을 수 없습니다."),
    USER_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, "USERINFO4004", "저장된 사용자 정보가 없습니다."),
    UNAUTHORIZED(HttpStatus.FORBIDDEN, "USERINFO4001", "정보 수정 권한이 없습니다."),

    //로그인 관련
    EMAIL_REGISTERED_WITH_KAKAO(HttpStatus.BAD_REQUEST, "AUTH4006", "해당 이메일은 카카오 계정으로 가입되어 있습니다."),
    EMAIL_REGISTERED_WITH_LOCAL(HttpStatus.BAD_REQUEST, "AUTH4007", "이미 로컬 계정으로 가입되어 있습니다."),
    LOCAL_LOGIN_FOR_KAKAO_EMAIL(HttpStatus.UNAUTHORIZED, "AUTH4008", "카카오 계정으로 로그인해주세요."),

    //Image
    FILE_TOO_LARGE(HttpStatus.BAD_REQUEST, "FILE4000", "이미지 용량은 5MB이하로 해주세요"),
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST,"FILE4001","이미지 파일만 업로드해주세요" ),
    INVALID_IMAGE_URL(HttpStatus.NOT_FOUND,"FILE4002","이미지 URL을 찾을 수 없습니다"),
    FILE_UPLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "FILE4003", "파일 업로드에 실패했습니다."),
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "FILE4004", "해당 이미지를 찾을 수 없습니다."),
    INVALID_VIDEO_FILE_TYPE(HttpStatus.BAD_REQUEST,"FILE4005","영상 파일만 업로드해주세요" ),
    FILE_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_SAVE_FAILED", "임시 파일 저장 중 오류가 발생했습니다."),


    //Video
    DAILY_VIDEO_NOT_FOUND(HttpStatus.NOT_FOUND,"VIDEO4000", "영상을 찾을 수 없습니다."),

    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "STORE404", "가게를 찾을 수 없습니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "CATEGORY404", "카테고리를 찾을 수 없습니다."),

    SHORTS_NOT_FOUND(HttpStatus.NOT_FOUND, "SHORTS_NOT_FOUND", "쇼츠 정보를 찾을 수 없습니다."),
    SHORTS_NOT_READY(HttpStatus.BAD_REQUEST, "SHORTS_NOT_READY", "쇼츠가 아직 처리 중입니다."),
    SHORTS_IMAGE_EMPTY(HttpStatus.BAD_REQUEST, "SHORTS_IMAGE_EMPTY", "최소 1개의 이미지가 필요합니다."),
    SHORTS_IMAGE_COUNT_MISMATCH(HttpStatus.BAD_REQUEST, "SHORTS_IMAGE_COUNT_MISMATCH", "이미지 개수와 설명 개수가 일치하지 않습니다."),

    PYTHON_BAD_REQUEST(HttpStatus.BAD_GATEWAY, "PYTHON_BAD_REQUEST", "Python 서버에서 오류가 반환되었습니다."),
    PYTHON_TIMEOUT(HttpStatus.BAD_GATEWAY, "PYTHON_TIMEOUT", "Python 서버 응답이 지연되었습니다."),

    SHORTS_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "S004", "요청한 이미지가 존재하지 않거나 가게 소유가 아닙니다."),
    FILE_DOWNLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S005", "이미지 파일 다운로드에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}
