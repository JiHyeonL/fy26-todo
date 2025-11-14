package com.fy26.todo.exception;

import com.fy26.todo.exception.common.CustomErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TagErrorCode implements CustomErrorCode {

    TAG_NOT_FOUND(HttpStatus.NOT_FOUND, "태그를 찾을 수 없습니다."),
    TAG_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "태그를 생성한 사용자가 아닙니다.");

    private final HttpStatus status;
    private final String message;
}
