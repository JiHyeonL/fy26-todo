package com.fy26.todo.dto.todo;

import com.fy26.todo.domain.entity.Todo;
import java.time.LocalDateTime;

public record TodoSimpleResponse(long id, long orderIndex, String content, boolean completed, LocalDateTime dueDate) {

    public static TodoSimpleResponse of(final Todo todo) {
        return new TodoSimpleResponse(
                todo.getId(),
                todo.getOrderIndex(),
                todo.getContent(),
                todo.isCompleted(),
                todo.getDueDate()
        );
    }
}
