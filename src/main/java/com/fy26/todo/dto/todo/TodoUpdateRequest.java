package com.fy26.todo.dto.todo;

import java.time.LocalDateTime;

public record TodoUpdateRequest(String content, LocalDateTime dueDate) {
}
