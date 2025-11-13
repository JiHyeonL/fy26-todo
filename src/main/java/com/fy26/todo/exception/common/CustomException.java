package com.fy26.todo.exception.common;

import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public class CustomException extends RuntimeException {

    private final HttpStatus status;
    private final String message;
    private final transient Map<String, Object> invalidData;
    private final Throwable causeException;
}
