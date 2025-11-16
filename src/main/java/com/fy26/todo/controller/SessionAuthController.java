package com.fy26.todo.controller;

import com.fy26.todo.dto.member.LoginRequest;
import com.fy26.todo.dto.member.LoginResponse;
import com.fy26.todo.dto.member.SignupRequest;
import com.fy26.todo.dto.member.SignupResponse;
import com.fy26.todo.service.MemberService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SessionAuthController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(final @Valid @RequestBody SignupRequest request) {
        final SignupResponse response = memberService.signup(request);
        final URI location = URI.create("/members/" + response.id());
        return ResponseEntity.created(location)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            final @Valid @RequestBody LoginRequest request,
            final HttpSession httpSession
    ) {
        final LoginResponse response = memberService.login(request, httpSession);
        return ResponseEntity.ok(response);
    }
}
