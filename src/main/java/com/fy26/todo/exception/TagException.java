package com.fy26.todo.exception;

import com.fy26.todo.exception.common.CustomErrorCode;
import com.fy26.todo.exception.common.CustomException;
import java.util.Map;

public class TagException extends CustomException {

    public TagException(CustomErrorCode errorCode, Map<String, Object> invalidData) {
        super(errorCode, invalidData, null);
    }
}
