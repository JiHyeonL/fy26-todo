package com.fy26.todo.dto.todo;

import java.time.LocalDateTime;
import java.util.List;

public record TodoGetResponse(long id, long orderIndex, String content, List<String> tags, boolean completed, LocalDateTime dueDate, long dDay) {
}
