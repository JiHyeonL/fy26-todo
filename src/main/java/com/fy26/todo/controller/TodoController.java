package com.fy26.todo.controller;

import com.fy26.todo.domain.entity.Member;
import com.fy26.todo.dto.tag.TagCreateRequest;
import com.fy26.todo.dto.tag.TagCreateResponse;
import com.fy26.todo.dto.todo.TodoCreateRequest;
import com.fy26.todo.dto.todo.TodoCreateResponse;
import com.fy26.todo.dto.todo.TodoGetResponse;
import com.fy26.todo.dto.todo.TodoOrderUpdateRequest;
import com.fy26.todo.dto.todo.TodoUpdateRequest;
import com.fy26.todo.dto.todoshare.TodoShareCreateResponse;
import com.fy26.todo.dto.todoshare.TodoShareDetailGetResponse;
import com.fy26.todo.service.MemberService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TodoController {

    private final TodoService todoService;
    private final MemberService memberService;

    @PostMapping("/todos")
    public ResponseEntity<TodoCreateResponse> createTodo(final @RequestBody TodoCreateRequest request, final HttpSession session) {
        final Member loginMember = memberService.getLoginMember(session);
        final TodoCreateResponse todo = todoService.createTodo(request, loginMember);
        final URI location = URI.create("/todos/" + todo.id());
        return ResponseEntity.created(location)
                .body(todo);
    }

    @PostMapping("/todos/{id}/tags")
    public ResponseEntity<List<TagCreateResponse>> addTagsToTodo(
            final @PathVariable Long id,
            final @RequestBody TagCreateRequest request,
            final HttpSession session
    ) {
        final Member loginMember = memberService.getLoginMember(session);
        final List<TagCreateResponse> response = todoService.addTagsFromTodo(id, request, loginMember);
        final URI location = URI.create("/todos/" + id + "/tags");
        return ResponseEntity.created(location)
                .body(response);
    }

    @PostMapping("/todos/{id}/shares")
    public ResponseEntity<TodoShareCreateResponse> shareTodo(
            final @PathVariable Long id,
            @RequestParam("memberId") Long memberId,
            final HttpSession session
    ) {
        final Member loginMember = memberService.getLoginMember(session);
        final TodoShareCreateResponse response = todoService.shareTodo(id, memberId, loginMember);
        final URI location = URI.create("/todos/" + id + "/shares/" + memberId);
        return ResponseEntity.created(location)
                .body(response);
    }

    @GetMapping("/todos")
    public ResponseEntity<List<TodoGetResponse>> getTodos(final HttpSession session) {
        final Member loginMember = memberService.getLoginMember(session);
        final List<TodoGetResponse> todos = todoService.getTodos(loginMember);
        return ResponseEntity.ok(todos);
    }

    @GetMapping("/todos/{id}")
    public ResponseEntity<TodoGetResponse> getTodo(final @PathVariable Long id, final HttpSession session) {
        final Member loginMember = memberService.getLoginMember(session);
        final TodoGetResponse todo = todoService.getTodo(id, loginMember);
        return ResponseEntity.ok(todo);
    }

    @GetMapping("/todos/shares")
    public ResponseEntity<List<TodoShareDetailGetResponse>> getSharedTodos(final HttpSession session) {
        final Member loginMember = memberService.getLoginMember(session);
        final List<TodoShareDetailGetResponse> response = todoService.getSharedTodos(loginMember);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/todos/filter")
    public ResponseEntity<List<TodoGetResponse>> filterTodos(
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) List<String> tags,
            final HttpSession session
    ) {
        final Member loginMember = memberService.getLoginMember(session);
        final List<TodoGetResponse> response = todoService.filterTodos(completed, tags, loginMember);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/todos/{id}/order")
    public ResponseEntity<Void> updateTodoOrder(
            final @PathVariable Long id,
            final @RequestBody TodoOrderUpdateRequest request,
            final HttpSession session
    ) {
        final Member loginMember = memberService.getLoginMember(session);
        todoService.updateTodoOrder(id, request, loginMember);
        return ResponseEntity.noContent()
                .build();
    }

    @PatchMapping("/todos/{id}/complete")
    public ResponseEntity<Void> toggleTodoComplete(final @PathVariable Long id, final HttpSession session) {
        final Member loginMember = memberService.getLoginMember(session);
        todoService.updateComplete(id, loginMember);
        return ResponseEntity.noContent()
                .build();
    }

    @PatchMapping("/todos/{id}")
    public ResponseEntity<Void> updateTodo(
            final @PathVariable Long id,
            final @RequestBody TodoUpdateRequest request,
            final HttpSession session
    ) {
        final Member loginMember = memberService.getLoginMember(session);
        todoService.updateTodo(id, request, loginMember);
        return ResponseEntity.noContent()
                .build();
    }

    @PatchMapping("/todos/{todoId}/tags/{tagId}")
    public ResponseEntity<Void> removeTagFromTodo(
            final @PathVariable Long todoId,
            final @PathVariable Long tagId,
            final HttpSession session
    ) {
        final Member loginMember = memberService.getLoginMember(session);
        todoService.removeTagFromTodo(todoId, tagId, loginMember);
        return ResponseEntity.noContent()
                .build();
    }

    @DeleteMapping("/todos/{id}")
    public ResponseEntity<Void> deleteTodo(final @PathVariable Long id, final HttpSession session) {
        final Member loginMember = memberService.getLoginMember(session);
        todoService.deleteTodo(id, loginMember);
        return ResponseEntity.noContent()
                .build();
    }

    @DeleteMapping("/tags/{tagId}")
    public ResponseEntity<Void> deleteTag(final @PathVariable Long tagId, final HttpSession session) {
        final Member loginMember = memberService.getLoginMember(session);
        todoService.deleteTag(tagId, loginMember);
        return ResponseEntity.noContent()
                .build();
    }
}
