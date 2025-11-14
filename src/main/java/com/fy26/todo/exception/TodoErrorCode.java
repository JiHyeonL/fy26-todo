package com.fy26.todo.exception;

import com.fy26.todo.exception.common.CustomErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties.Http;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TodoErrorCode implements CustomErrorCode {

    TODO_NOT_FOUND(HttpStatus.NOT_FOUND, "투두를 찾을 수 없습니다."),
    TODO_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "투두를 생성한 사용자가 아닙니다."),
    INVALID_DUE_DATE(HttpStatus.BAD_REQUEST, "마감일은 현재 시각 이후여야 합니다.");

    private final HttpStatus status;
    private final String message;
}
