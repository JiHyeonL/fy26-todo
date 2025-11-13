package com.fy26.todo.controller;

import com.fy26.todo.domain.Member;
import com.fy26.todo.domain.Role;
import com.fy26.todo.domain.Todo;
import com.fy26.todo.dto.TodoCreateRequest;
import com.fy26.todo.dto.TodoGetResponse;
import com.fy26.todo.service.TodoService;
import jakarta.servlet.http.HttpSession;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TodoController {

    private final TodoService todoService;

    @PostMapping("/todos")
    public ResponseEntity<Long> createTodo(final @RequestBody TodoCreateRequest request, final HttpSession session) {
        // todo: 세션에서 회원 정보 가져오기
        final Todo todo = todoService.createTodo(request, new Member(Role.USER, "아이디", "비번"));
        return ResponseEntity.created(URI.create("/todo/" + todo.getId()))
                .body(todo.getId());
    }

    @GetMapping("/todos")
    public ResponseEntity<List<TodoGetResponse>> getTodos(final HttpSession session) {
        // todo: 세션에서 회원 정보 가져오기
        final List<TodoGetResponse> todos = todoService.getTodos(new Member(Role.USER, "아이디", "비번"));
        return ResponseEntity.ok(todos);
    }

    @GetMapping("/todos/{id}")
    public ResponseEntity<TodoGetResponse> getTodo(final @PathVariable Long id, final HttpSession session) {
        // todo: 세션에서 회원 정보 가져오기
        final TodoGetResponse todo = todoService.getTodo(id);
        return ResponseEntity.ok(todo);
    }
}
