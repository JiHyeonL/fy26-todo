package com.fy26.todo.dto.member;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "사용자 이름이 존재하지 않습니다.")
        String username,

        @NotBlank(message = "비밀번호가 존재하지 않습니다.")
        String password
) {
}
