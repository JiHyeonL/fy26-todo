package com.fy26.todo.controller;

import com.fy26.todo.domain.Member;
import com.fy26.todo.domain.Role;
import com.fy26.todo.dto.tag.TagCreateRequest;
import com.fy26.todo.dto.tag.TagCreateResponse;
import com.fy26.todo.dto.todo.TodoCreateRequest;
import com.fy26.todo.dto.todo.TodoCreateResponse;
import com.fy26.todo.dto.todo.TodoGetResponse;
import com.fy26.todo.dto.todo.TodoOrderUpdateRequest;
import com.fy26.todo.dto.todo.TodoUpdateRequest;
import com.fy26.todo.service.TodoService;
import jakarta.servlet.http.HttpSession;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
        final TodoCreateResponse todo = todoService.createTodo(request, new Member(Role.USER, "아이디", "비번"));
        final URI location = URI.create("/todo/" + todo.id());
        return ResponseEntity.created(location)
                .body(todo.id());
    }

    @PostMapping("/todos/{id}/tags")
    public ResponseEntity<List<TagCreateResponse>> addTagsToTodo(
            final @PathVariable Long id,
            final @RequestBody TagCreateRequest request,
            final HttpSession session
    ) {
        // todo: 세션에서 회원 정보 가져오기
        final List<TagCreateResponse> response = todoService.addTags(id, request, new Member(Role.USER, "아이디", "비번"));
        final URI location = URI.create("/todo/" + id + "/tags");
        return ResponseEntity.created(location)
                .body(response);
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

    @PatchMapping("/todos/{id}/order")
    public ResponseEntity<Void> updateTodoOrder(
            final @PathVariable Long id,
            final @RequestBody TodoOrderUpdateRequest request,
            final HttpSession session
    ) {
        // todo: 세션에서 회원 정보 가져오기
        todoService.updateTodoOrder(id, request, new Member(Role.USER, "아이디", "비번"));
        return ResponseEntity.noContent()
                .build();
    }

    @PatchMapping("/todos/{id}/complete")
    public ResponseEntity<Void> toggleTodoComplete(final @PathVariable Long id, final HttpSession session) {
        // todo: 세션에서 회원 정보 가져오기
        todoService.updateComplete(id, new Member(Role.USER, "아이디", "비번"));
        return ResponseEntity.noContent()
                .build();
    }

    @PatchMapping("/todos/{id}")
    public ResponseEntity<Void> updateTodo(
            final @PathVariable Long id,
            final @RequestBody TodoUpdateRequest request,
            final HttpSession session
    ) {
        // todo: 세션에서 회원 정보 가져오기
        todoService.updateTodo(id, request, new Member(Role.USER, "아이디", "비번"));
        return ResponseEntity.noContent()
                .build();
    }

    @DeleteMapping("/todos/{id}")
    public ResponseEntity<Void> deleteTodo(final @PathVariable Long id, final HttpSession session) {
        // todo: 세션에서 회원 정보 가져오기
        todoService.deleteTodo(id, new Member(Role.USER, "아이디", "비번"));
        return ResponseEntity.noContent()
                .build();
    }
}
