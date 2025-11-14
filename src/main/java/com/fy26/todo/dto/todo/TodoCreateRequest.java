package com.fy26.todo.dto.todo;

import java.time.LocalDateTime;
import java.util.List;

public record TodoCreateRequest(String content, List<String> tagNames, LocalDateTime dueDate) {
}
