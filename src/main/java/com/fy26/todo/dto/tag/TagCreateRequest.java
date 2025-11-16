package com.fy26.todo.dto.tag;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record TagCreateRequest(
        @NotNull
        List<String> tagNames
) {
}
