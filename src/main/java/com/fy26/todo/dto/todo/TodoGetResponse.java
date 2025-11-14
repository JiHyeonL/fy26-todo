package com.fy26.todo.dto.todo;

import com.fy26.todo.domain.Tag;
import com.fy26.todo.domain.Todo;
import com.fy26.todo.dto.tag.TagGetResponse;
import java.time.LocalDateTime;
import java.util.List;

public record TodoGetResponse(long id, long orderIndex, String content, List<TagGetResponse> tags, boolean completed, LocalDateTime dueDate, long dDay) {

    public static TodoGetResponse of(final Todo todo, final long dDay, final List<Tag> tags) {
        return new TodoGetResponse(
                todo.getId(),
                todo.getOrderIndex(),
                todo.getContent(),
                tags.stream()
                    .map(tag -> new TagGetResponse(tag.getId(), tag.getName()))
                    .toList(),
                todo.isCompleted(),
                todo.getDueDate(),
                dDay
        );
    }
}
