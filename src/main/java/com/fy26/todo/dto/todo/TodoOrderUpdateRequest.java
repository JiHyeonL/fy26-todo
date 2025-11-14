package com.fy26.todo.dto.todo;

import com.fy26.todo.domain.TodoPosition;

public record TodoOrderUpdateRequest(TodoPosition position, Long previousTodoId, Long nextTodoId) {
}
