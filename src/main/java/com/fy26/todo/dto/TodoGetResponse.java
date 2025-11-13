package com.fy26.todo.dto;

import java.time.LocalDateTime;

public record TodoGetResponse(long id, String content, boolean completed, LocalDateTime dueDate, long dDay) {
}
