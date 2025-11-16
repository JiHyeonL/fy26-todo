package com.fy26.todo.dto.todo;

import com.fy26.todo.domain.TodoPosition;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record TodoOrderUpdateRequest(
        @NotNull(message = "투두 위치 상수가 존재하지 않습니다.")
        TodoPosition position,

        @Nullable
        Long previousTodoId,

        @Nullable
        Long nextTodoId
) {
}
