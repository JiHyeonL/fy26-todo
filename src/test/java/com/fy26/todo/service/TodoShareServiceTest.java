package com.fy26.todo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fy26.todo.domain.Role;
import com.fy26.todo.domain.entity.Member;
import com.fy26.todo.domain.entity.Todo;
import com.fy26.todo.dto.todo.TodoCreateRequest;
import com.fy26.todo.dto.todo.TodoCreateResponse;
import com.fy26.todo.dto.todoshare.TodoShareSimpleGetResponse;
import com.fy26.todo.exception.TodoShareException;
import com.fy26.todo.repository.MemberRepository;
import com.fy26.todo.repository.TodoRepository;
import com.fy26.todo.support.Cleanup;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TodoShareServiceTest {

    @Autowired
    private Cleanup cleanup;

    @Autowired
    private TodoService todoService;

    @Autowired
    private TodoShareService todoShareService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TodoRepository todoRepository;

    @BeforeEach
    void setUp() {
        cleanup.all();
    }

    @DisplayName("다른 사용자에게 todo를 공유한다.")
    @Test
    void share_todo_to_another_member() {
        // given
        final TodoCreateRequest request = new TodoCreateRequest("첫 번째 할 일", List.of("태그1"), LocalDateTime.now());
        final Member owner = memberRepository.save(new Member(Role.USER, "공유한 사람", "비번"));
        final TodoCreateResponse todoResponse = todoService.createTodo(request, owner);
        final Todo todo = todoRepository.findById(todoResponse.id()).orElseThrow();
        final Member sharedMember = memberRepository.save(new Member(Role.USER, "공유 받는 사람", "비번"));

        // when & then
        assertThatCode(() -> todoShareService.shareTodo(todo, sharedMember.getId()))
                .doesNotThrowAnyException();
    }

    @DisplayName("자기 자신에게 todo를 공유하면 예외가 발생한다.")
    @Test
    void share_todo_to_self_throws_exception() {
        // given
        final TodoCreateRequest request = new TodoCreateRequest("첫 번째 할 일", List.of("태그1"), LocalDateTime.now());
        final Member owner = memberRepository.save(new Member(Role.USER, "공유한 사람", "비번"));
        final TodoCreateResponse todoResponse = todoService.createTodo(request, owner);
        final Todo todo = todoRepository.findById(todoResponse.id()).orElseThrow();
        final Long ownerId = owner.getId();

        // when & then
        assertThrows(TodoShareException.class, () -> todoShareService.shareTodo(todo, ownerId));
    }

    @DisplayName("같은 사용자에게 중복으로 todo를 공유하면 예외가 발생한다.")
    @Test
    void share_todo_to_same_member_throws_exception() {
        // given
        final TodoCreateRequest request = new TodoCreateRequest("첫 번째 할 일", List.of("태그1"), LocalDateTime.now());
        final Member owner = memberRepository.save(new Member(Role.USER, "공유한 사람", "비번"));
        final TodoCreateResponse todoResponse = todoService.createTodo(request, owner);
        final Todo todo = todoRepository.findById(todoResponse.id()).orElseThrow();
        final Member sharedMember = memberRepository.save(new Member(Role.USER, "공유 받는 사람", "비번"));
        final Long sharedMemberId = sharedMember.getId();
        
        // when & then
        todoShareService.shareTodo(todo, sharedMemberId);
        assertThrows(TodoShareException.class, () -> todoShareService.shareTodo(todo, sharedMemberId));
    }

    @DisplayName("하나의 todo를 여러명에게 공유할 수 있다.")
    @Test
    void share_todo_to_multiple_members() {
        // given
        final TodoCreateRequest request = new TodoCreateRequest("첫 번째 할 일", List.of("태그1"), LocalDateTime.now());
        final Member owner = memberRepository.save(new Member(Role.USER, "공유한 사람", "비번"));
        final TodoCreateResponse todoResponse = todoService.createTodo(request, owner);
        final Todo todo = todoRepository.findById(todoResponse.id()).orElseThrow();
        final Member sharedMember1 = memberRepository.save(new Member(Role.USER, "공유 받는 사람1", "비번"));
        final Member sharedMember2 = memberRepository.save(new Member(Role.USER, "공유 받는 사람2", "비번"));

        // when & then
        assertThatCode(() -> {
            todoShareService.shareTodo(todo, sharedMember1.getId());
            todoShareService.shareTodo(todo, sharedMember2.getId());
        }).doesNotThrowAnyException();
    }

    @DisplayName("한 사용자는 여러 todo를 공유받을 수 있다.")
    @Test
    void member_can_receive_multiple_shared_todos() {
        // given
        final Member owner = memberRepository.save(new Member(Role.USER, "공유한 사람", "비번"));
        final TodoCreateRequest request1 = new TodoCreateRequest("첫 번째 할 일", List.of("태그1"), LocalDateTime.now());
        final TodoCreateRequest request2 = new TodoCreateRequest("두 번째 할 일", List.of("태그2"),
                LocalDateTime.now().plusDays(1));
        final TodoCreateResponse todoResponse1 = todoService.createTodo(request1, owner);
        final TodoCreateResponse todoResponse2 = todoService.createTodo(request2, owner);
        final Todo todo1 = todoRepository.findById(todoResponse1.id()).orElseThrow();
        final Todo todo2 = todoRepository.findById(todoResponse2.id()).orElseThrow();
        final Member sharedMember = memberRepository.save(new Member(Role.USER, "공유 받는 사람", "비번"));

        // when & then
        assertThatCode(() -> {
            todoShareService.shareTodo(todo1, sharedMember.getId());
            todoShareService.shareTodo(todo2, sharedMember.getId());
        }).doesNotThrowAnyException();
    }

    @DisplayName("여러 사용자에게 공유 받은 todo를 전부 조회한다.")
    @Test
    void get_all_shared_todo_all_member() {
        // given
        final Member owner1 = memberRepository.save(new Member(Role.USER, "공유한 사람1", "비번"));
        final Member owner2 = memberRepository.save(new Member(Role.USER, "공유한 사람2", "비번"));
        final TodoCreateRequest request1 = new TodoCreateRequest("첫 번째 할 일", List.of("태그1"), LocalDateTime.now());
        final TodoCreateRequest request2 = new TodoCreateRequest("두 번째 할 일", List.of("태그2"),
                LocalDateTime.now().plusDays(1));
        final TodoCreateResponse todoResponse1 = todoService.createTodo(request1, owner1);
        final TodoCreateResponse todoResponse2 = todoService.createTodo(request2, owner2);
        final Todo todo1 = todoRepository.findById(todoResponse1.id()).orElseThrow();
        final Todo todo2 = todoRepository.findById(todoResponse2.id()).orElseThrow();
        final Member sharedMember = memberRepository.save(new Member(Role.USER, "공유 받는 사람", "비번"));
        todoShareService.shareTodo(todo1, owner2.getId());

        // when
        todoShareService.shareTodo(todo1, sharedMember.getId());
        todoShareService.shareTodo(todo2, sharedMember.getId());
        final List<TodoShareSimpleGetResponse> actual = todoShareService.getAllSharedTodoId(sharedMember.getId());

        // then
        assertThat(actual).hasSize(2);
    }

    @DisplayName("한 사용자에게 공유 받은 todo를 모두 조회한다.")
    @Test
    void get_all_shared_todo() {
        // given
        final TodoCreateRequest request = new TodoCreateRequest("첫 번째 할 일", List.of("태그1"), LocalDateTime.now());
        final Member owner = memberRepository.save(new Member(Role.USER, "공유한 사람", "비번"));
        final TodoCreateResponse todoResponse1 = todoService.createTodo(request, owner);
        final TodoCreateResponse todoResponse2 = todoService.createTodo(request, owner);
        final Todo todo1 = todoRepository.findById(todoResponse1.id()).orElseThrow();
        final Todo todo2 = todoRepository.findById(todoResponse2.id()).orElseThrow();
        final Member sharedMember = memberRepository.save(new Member(Role.USER, "공유 받는 사람1", "비번"));

        // when
        todoShareService.shareTodo(todo1, sharedMember.getId());
        todoShareService.shareTodo(todo2, sharedMember.getId());
        final List<TodoShareSimpleGetResponse> actual = todoShareService.getAllSharedTodoId(sharedMember.getId());

        // then
        assertThat(actual).hasSize(2);
    }
}
