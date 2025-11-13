package com.fy26.todo.exception.common;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;

public record ExceptionResponse(
        LocalDateTime timestamp,
        HttpStatus status,
        int errorCode,
        String message,
        String path,
        String method
) {

    public static ExceptionResponse fromCustom(final CustomException e, final HttpServletRequest request) {
        return new ExceptionResponse(
                LocalDateTime.now(),
                e.getStatus(),
                e.getStatus().value(),
                e.getMessage(),
                request.getRequestURI(),
                request.getMethod()
        );
    }

    public static ExceptionResponse fromUnHandled(final HttpServletRequest request) {
        return new ExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "서버 내부 오류가 발생했습니다.",
                request.getRequestURI(),
                request.getMethod()
        );
    }
}
