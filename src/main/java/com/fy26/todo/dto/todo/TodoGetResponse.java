package com.fy26.todo.dto.todo;

import com.fy26.todo.dto.tag.TagGetResponse;
import java.time.LocalDateTime;
import java.util.List;

public record TodoGetResponse(long id, long orderIndex, String content, List<TagGetResponse> tags, boolean completed, LocalDateTime dueDate, long dDay) {
}
