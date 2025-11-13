package com.fy26.todo.controller;

import com.fy26.todo.exception.common.CustomException;
import com.fy26.todo.exception.common.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String HANDLED_LOG_MESSAGE = "[{}] {} (path: {}, method: {}) cause: {}, invalidData: {}";
    private static final String UNHANDLED_LOG_MESSAGE = "[UNHANDLED] {} (path: {}, method: {}) cause: {}";

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ExceptionResponse> handleCustomException(final CustomException e, final HttpServletRequest request) {
        log.warn(HANDLED_LOG_MESSAGE,
                e.getStatus(),
                e.getMessage(),
                request.getRequestURI(),
                request.getMethod(),
                e.getCauseException(),
                e.getInvalidData()
        );
        return ResponseEntity.status(e.getStatus())
                .body(ExceptionResponse.fromCustom(e, request));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidException(final MethodArgumentNotValidException e, final HttpServletRequest request) {
        final Map<String, Object> invalidFields = e.getBindingResult().getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> String.format("값: %s, 메시지: %s",
                                fieldError.getRejectedValue(),
                                fieldError.getDefaultMessage())
                ));
        log.warn(HANDLED_LOG_MESSAGE,
                HttpStatus.BAD_REQUEST,
                e.getMessage(),
                request.getRequestURI(),
                request.getMethod(),
                e.getCause(),
                invalidFields
        );
        final CustomException customException = new CustomException(
                HttpStatus.BAD_REQUEST,
                "요청 데이터 검증에 실패했습니다.",
                invalidFields,
                e
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.fromCustom(customException, request));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(final Exception e, final HttpServletRequest request) {
        log.error(UNHANDLED_LOG_MESSAGE,
                e.getClass().getSimpleName(),
                request.getRequestURI(),
                request.getMethod(),
                e.getMessage(),
                e
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ExceptionResponse.fromUnHandled(request));
    }
}
