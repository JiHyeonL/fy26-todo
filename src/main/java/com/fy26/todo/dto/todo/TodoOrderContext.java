package com.fy26.todo.dto.todo;

public record TodoOrderContext(long orderIndex, Long previousOrderIndex, Long nextOrderIndex) {
}
