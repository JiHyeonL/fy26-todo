package com.fy26.todo.dto.todo;

import com.fy26.todo.domain.Tag;
import com.fy26.todo.domain.Todo;
import com.fy26.todo.dto.tag.TagCreateResponse;
import java.time.LocalDateTime;
import java.util.List;

public record TodoCreateResponse(long id, long orderIndex, String content, List<TagCreateResponse> tags, boolean completed, LocalDateTime dueDate) {

    public static TodoCreateResponse of(final Todo todo, final List<Tag> tags) {
        return new TodoCreateResponse(
                todo.getId(),
                todo.getOrderIndex(),
                todo.getContent(),
                tags.stream()
                        .map(tag -> new TagCreateResponse(tag.getId(), tag.getName()))
                        .toList(),
                todo.isCompleted(),
                todo.getDueDate()
        );
    }
}
