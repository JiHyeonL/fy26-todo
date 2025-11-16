package com.fy26.todo.dto.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @NotBlank
        @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하로 입력해주세요.")
        String username,

        @NotBlank
        @Size(min = 8, max = 20, message = "비밀번호 길이는 8 이상 20 이하여야 합니다.")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*()_+]{8,20}$",
                message = "비밀번호는 영어와 숫자를 무조건 포함해야 합니다."
        )
        String password
) {
}
