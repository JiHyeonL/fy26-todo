package com.fy26.todo.util;

import static org.assertj.core.api.Assertions.assertThat;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class JwtProviderTest {

    private static final String SECRET_KEY = "F9t@X3r#8pQ2!Zm5K7b$H1vT0sL9wE6c";

    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider();
        ReflectionTestUtils.setField(jwtProvider, "secretKeyString", SECRET_KEY);
        ReflectionTestUtils.setField(jwtProvider, "expirationMs", 1000 * 60 * 10L);
        jwtProvider.init();
    }

    @DisplayName("토큰이 정상적으로 생성된다.")
    @Test
    void generate_token_success() {
        // when
        final String token = jwtProvider.generateToken(1L);

        // then
        assertThat(token).isNotNull()
                .contains(".");
    }

    @DisplayName("생성된 토큰으로 회원 id를 조회할 수 있다.")
    @Test
    void get_memberId_success() {
        // given
        final Long expected = 123L;
        final String token = jwtProvider.generateToken(expected);

        // when
        final Long actual = jwtProvider.getMemberId(token);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("유효한 토큰이면 true를 반환한다.")
    @Test
    void validate_token_valid() {
        // given
        final String token = jwtProvider.generateToken(99L);

        // when
        final boolean actual = jwtProvider.validateToken(token);

        // then
        assertThat(actual).isTrue();
    }

    @DisplayName("서명이 잘못된 토큰이면 검증에 실패한다.")
    @Test
    void validate_token_invalid_signature() {
        // given
        final String anotherKey = SECRET_KEY + "invalid";
        final SecretKey invalidKey = Keys.hmacShaKeyFor(anotherKey.getBytes());
        final String invalidToken = Jwts.builder()
                .subject("5")
                .signWith(invalidKey, Jwts.SIG.HS256)
                .compact();

        // when
        final boolean actual = jwtProvider.validateToken(invalidToken);

        // then
        assertThat(actual).isFalse();
    }

    @DisplayName("만료된 토큰이면 검증에 실패한다.")
    @Test
    void validate_token_expired() {
        // given
        final Date past = new Date(System.currentTimeMillis() - 10000);
        final Date issued = new Date(System.currentTimeMillis() - 20000);
        final String expiredToken = Jwts.builder()
                .subject("1")
                .issuedAt(issued)
                .expiration(past)
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()), Jwts.SIG.HS256)
                .compact();

        // when
        final boolean actual = jwtProvider.validateToken(expiredToken);

        // then
        assertThat(actual).isFalse();
    }
}
