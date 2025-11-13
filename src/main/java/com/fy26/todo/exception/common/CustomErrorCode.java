package com.fy26.todo.exception.common;

import org.springframework.http.HttpStatus;

public interface CustomErrorCode {
    HttpStatus getStatus();
    String getMessage();
    String name();
}
