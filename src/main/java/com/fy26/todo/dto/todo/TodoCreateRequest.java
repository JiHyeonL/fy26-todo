package com.fy26.todo.dto.todo;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

public record TodoCreateRequest(
        @Size(max = 255, message = "투두 내용은 최대 255자까지 입력 가능합니다.")
        @NotBlank(message = "투두 내용이 존재하지 않습니다.")
        String content,

        @NotNull(message = "태그 리스트는 null일 수 없습니다.")
        List<String> tagNames,

        @NotNull(message = "마감일이 존재하지 않습니다.")
        @FutureOrPresent(message = "마감일은 현재 시간보다 이후여야 합니다.")
        LocalDateTime dueDate
) {
}
