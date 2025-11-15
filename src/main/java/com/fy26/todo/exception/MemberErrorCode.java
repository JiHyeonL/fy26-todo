package com.fy26.todo.exception;

import com.fy26.todo.exception.common.CustomErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements CustomErrorCode {

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    USERNAME_NOT_FOUND(HttpStatus.BAD_REQUEST, "잘못된 사용자 이름입니다."),
    EXIST_USERNAME(HttpStatus.BAD_REQUEST, "이미 존재하는 사용자 이름입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "올바르지 않은 비밀번호입니다.");

    private final HttpStatus status;
    private final String message;
}
