package com.fy26.todo.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import com.fy26.todo.dto.member.LoginRequest;
import com.fy26.todo.dto.member.SignupRequest;
import com.fy26.todo.dto.member.SignupResponse;
import com.fy26.todo.exception.MemberException;
import com.fy26.todo.support.Cleanup;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MemberServiceTest {

    @Autowired
    private Cleanup cleanup;

    @Autowired
    private MemberService memberService;

    @Mock
    private HttpSession httpSession;

    @BeforeEach
    void setUp() {
        cleanup.all();
    }

    @DisplayName("회원가입에 성공한다.")
    @Test
    void signup_success() {
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

    @DisplayName("로그인에 성공하면 세션에 회원 ID가 저장된다.")
    @Test
    void login_success() {
        // given
        final String username = "user";
        final String password = "1234";
        final SignupRequest signupRequest = new SignupRequest(username, password);
        final SignupResponse signup = memberService.signup(signupRequest);
        final LoginRequest loginRequest = new LoginRequest(username, password);

        // when & then
        assertThatCode(() -> memberService.login(loginRequest, httpSession))
                .doesNotThrowAnyException();
        verify(httpSession).setAttribute(anyString(), eq(signup.id()));
    }

    @DisplayName("아이디가 존재하지 않으면 예외가 발생한다.")
    @Test
    void login_fail_not_found_username() {
        // given
        final String username = "user";
        final String password = "1234";
        final SignupRequest signupRequest = new SignupRequest(username, password);
        memberService.signup(signupRequest);
        final String invalidUsername = "nonouser";
        final LoginRequest loginRequest = new LoginRequest(invalidUsername, password);

        // when & then
        assertThrows(MemberException.class, () -> memberService.login(loginRequest, httpSession));
    }

    @DisplayName("비밀번호가 틀리면 예외가 발생한다.")
    @Test
    void login_fail_invalid_password() {
        // given
        final String username = "user";
        final String password = "1234";
        final SignupRequest signupRequest = new SignupRequest(username, password);
        memberService.signup(signupRequest);
        final String invalidPassword = "nonopw";
        final LoginRequest loginRequest = new LoginRequest(username, invalidPassword);

        // when & then
        assertThrows(MemberException.class, () -> memberService.login(loginRequest, httpSession));
    }
}
