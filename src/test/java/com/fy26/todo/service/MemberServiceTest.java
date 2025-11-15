package com.fy26.todo.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;

import com.fy26.todo.dto.member.SignupRequest;
import com.fy26.todo.exception.MemberException;
import com.fy26.todo.support.Cleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MemberServiceTest {

    @Autowired
    private Cleanup cleanup;

    @Autowired
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        cleanup.all();
    }

    @DisplayName("회원가입에 성공한다.")
    @Test
    void signup() {
        // given
        final SignupRequest request = new SignupRequest("사용자", "비밀번호");

        // when & then
        assertThatCode(() -> memberService.signup(request))
                .doesNotThrowAnyException();
    }

    @DisplayName("같은 유저네임으로 회원가입하면 예외가 발생한다.")
    @Test
    void throw_exception_signup_when_duplicate_username() {
        // given
        final SignupRequest request = new SignupRequest("사용자사용자", "비밀번호");

        // when & then
        memberService.signup(request);
        assertThrows(MemberException.class, () -> memberService.signup(request));
    }
}
