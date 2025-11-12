package com.fy26.todo.dto;

import java.time.LocalDateTime;

public record TodoCreateRequest(String content, LocalDateTime dueDate) {
}
