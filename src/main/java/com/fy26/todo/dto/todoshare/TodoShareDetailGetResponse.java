package com.fy26.todo.dto.todoshare;

import com.fy26.todo.domain.entity.Tag;
import com.fy26.todo.dto.todo.TodoGetResponse;
import com.fy26.todo.dto.todo.TodoSimpleResponse;
import java.util.List;

public record TodoShareDetailGetResponse(long id, long ownerId, TodoGetResponse todoShare) {

    public static TodoShareDetailGetResponse of(final TodoShareSimpleGetResponse todoSimpleGetResponse, final List<Tag> tags) {
        final TodoSimpleResponse todoSimple = todoSimpleGetResponse.todo();
        return new TodoShareDetailGetResponse(
                todoSimpleGetResponse.id(),
                todoSimpleGetResponse.ownerId(),
                TodoGetResponse.of(todoSimple, tags)
        );
    }
}
