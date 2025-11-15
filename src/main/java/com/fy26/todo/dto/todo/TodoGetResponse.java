package com.fy26.todo.dto.todo;

import com.fy26.todo.domain.entity.Tag;
import com.fy26.todo.domain.entity.Todo;
import com.fy26.todo.dto.tag.TagGetResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public record TodoGetResponse(long id, long orderIndex, String content, List<TagGetResponse> tags, boolean completed, LocalDateTime dueDate, long dDay) {

    public static TodoGetResponse of(final Todo todo, final List<Tag> tags) {
        final long dDay = calculateDday(todo.getDueDate());
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

    public static TodoGetResponse of(final TodoSimpleResponse simpleTodo, final List<Tag> tags) {
        final long dDay = calculateDday(simpleTodo.dueDate());
        return new TodoGetResponse(
                simpleTodo.id(),
                simpleTodo.orderIndex(),
                simpleTodo.content(),
                tags.stream()
                        .map(tag -> new TagGetResponse(tag.getId(), tag.getName()))
                        .toList(),
                simpleTodo.completed(),
                simpleTodo.dueDate(),
                dDay
        );
    }

    private static long calculateDday(final LocalDateTime dueDate) {
        return ChronoUnit.DAYS.between(LocalDate.now(), dueDate.toLocalDate());
    }
}
