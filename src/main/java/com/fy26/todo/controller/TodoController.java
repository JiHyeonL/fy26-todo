package com.fy26.todo.controller;

import com.fy26.todo.domain.Member;
import com.fy26.todo.domain.Role;
import com.fy26.todo.domain.Todo;
import com.fy26.todo.dto.TodoCreateRequest;
import com.fy26.todo.service.TodoService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TodoController {

    private final TodoService todoService;

    @PostMapping("/todo")
    public ResponseEntity<Long> createTodo(final @RequestBody TodoCreateRequest request, final HttpSession session) {
        // todo: 세션에서 회원 정보 가져오기
        final Todo todo = todoService.createTodo(request, new Member(Role.USER, "아이디", "비번"));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(todo.getId());
    }
}
