package com.fy26.todo.service;

import com.fy26.todo.domain.Role;
import com.fy26.todo.domain.entity.Member;
import com.fy26.todo.dto.member.LoginRequest;
import com.fy26.todo.dto.member.LoginResponse;
import com.fy26.todo.dto.member.SignupRequest;
import com.fy26.todo.dto.member.SignupResponse;
import com.fy26.todo.exception.MemberErrorCode;
import com.fy26.todo.exception.MemberException;
import com.fy26.todo.repository.MemberRepository;
import com.fy26.todo.util.JwtProvider;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberService {

    private static final String LOGIN_SESSION_NAME = "LOGIN_MEMBER_ID";

    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    @Transactional
    public SignupResponse signup(final SignupRequest request) {
        validateDuplicateUsername(request.username());
        final String encodedPassword = passwordEncoder.encode(request.password());
        final Member member = new Member(Role.USER, request.username(), encodedPassword);
        final Member savedMember = memberRepository.save(member);
        final String token = jwtProvider.generateToken(savedMember.getId());
        return new SignupResponse(savedMember.getId(), savedMember.getUsername(), token);
    }

    private void validateDuplicateUsername(final String username) {
        if (memberRepository.existsByUsername(username)) {
            throw new MemberException(MemberErrorCode.EXIST_USERNAME, Map.of("username", username));
        }
    }

    @Transactional
    public LoginResponse login(final LoginRequest request, final HttpSession httpSession) {
        final Member member = memberRepository.findByUsername(request.username())
                .orElseThrow(() -> new MemberException(MemberErrorCode.USERNAME_NOT_FOUND,
                        Map.of("username", request.username())));

        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new MemberException(MemberErrorCode.INVALID_PASSWORD, Map.of("password", request.password()));
        }
        httpSession.setAttribute(LOGIN_SESSION_NAME, member.getId());
        return new LoginResponse(member.getUsername());
    }

    public Member getLoginMember(final HttpSession httpSession) {
        final Long memberId = (Long) httpSession.getAttribute(LOGIN_SESSION_NAME);
        if (memberId == null) {
            throw new MemberException(MemberErrorCode.MEMBER_UNAUTHORIZED);
        }
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(
                        MemberErrorCode.MEMBER_NOT_FOUND,
                        Map.of("memberId", memberId))
                );
    }
}
