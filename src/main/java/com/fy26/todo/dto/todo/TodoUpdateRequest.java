package com.fy26.todo.dto.todo;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record TodoUpdateRequest(
        @Nullable
        @Size(max = 255, message = "투두 내용은 최대 255자까지 입력 가능합니다.")
        String content,

        @Nullable
        @FutureOrPresent(message = "마감일은 현재 시간보다 이후여야 합니다.")
        LocalDateTime dueDate
) {
}
