package com.fy26.todo.exception;

import com.fy26.todo.exception.common.CustomErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TodoShareErrorCode implements CustomErrorCode {

    CANNOT_SHARE_TO_SELF(HttpStatus.BAD_REQUEST, "자기 자신에게 공유할 수 없습니다."),
    ALREADY_SHARED_TODO(HttpStatus.BAD_REQUEST, "이미 공유한 사용자입니다.");

    private final HttpStatus status;
    private final String message;
}
