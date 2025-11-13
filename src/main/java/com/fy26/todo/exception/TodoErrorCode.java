package com.fy26.todo.exception;

import com.fy26.todo.exception.common.CustomErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TodoErrorCode implements CustomErrorCode {

    TODO_NOT_FOUND(HttpStatus.NOT_FOUND, "투두를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;
}
