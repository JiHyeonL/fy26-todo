package com.fy26.todo.dto.todoshare;

import com.fy26.todo.dto.todo.TodoSimpleResponse;

public record TodoShareSimpleGetResponse(long id, long ownerId, TodoSimpleResponse todo) {
}
