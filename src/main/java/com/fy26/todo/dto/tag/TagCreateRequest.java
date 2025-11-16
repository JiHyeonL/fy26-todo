package com.fy26.todo.dto.tag;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record TagCreateRequest(
        @NotNull(message = "태그 리스트는 null일 수 없습니다.")
        List<String> tagNames
) {
}
