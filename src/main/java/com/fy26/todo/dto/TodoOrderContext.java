package com.fy26.todo.dto;

public record TodoOrderContext(long orderIndex, Long previousOrderIndex, Long nextOrderIndex) {
}
