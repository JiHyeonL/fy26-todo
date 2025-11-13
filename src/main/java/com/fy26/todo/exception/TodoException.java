package com.fy26.todo.exception;

import com.fy26.todo.exception.common.CustomErrorCode;
import com.fy26.todo.exception.common.CustomException;
import java.util.Map;

public class TodoException extends CustomException {

    public TodoException(CustomErrorCode errorCode, Map<String, Object> invalidData) {
        super(errorCode, invalidData, null);
    }
}
