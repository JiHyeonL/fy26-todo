package com.fy26.todo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fy26.todo.domain.Member;
import com.fy26.todo.domain.Role;
import com.fy26.todo.domain.Todo;
import com.fy26.todo.dto.TodoCreateRequest;
import com.fy26.todo.exception.TodoException;
import com.fy26.todo.repository.MemberRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TodoServiceTest {

    @Autowired
    private TodoService todoService;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("첫 번째 todo를 생성한다.")
    @Test
    void create_todo() {
        // given
        final TodoCreateRequest request = new TodoCreateRequest("첫 번째 할 일", LocalDateTime.now());
        final Member member = new Member(Role.USER, "아이디", "비번");
        memberRepository.save(member);

        // when
        final Todo actual = todoService.createTodo(request, member);

        // then
        assertThat(actual.getOrderIndex()).isEqualTo(100_000L);
    }

    @DisplayName("todo를 두 개 생성한다.")
    @Test
    void create_two_todo() {
        // given
        final TodoCreateRequest request = new TodoCreateRequest("첫 번째 할 일", LocalDateTime.now());
        final Member member = new Member(Role.USER, "아이디", "비번");
        memberRepository.save(member);

        // when
        final Todo firstActual = todoService.createTodo(request, member);
        final Todo secondActual = todoService.createTodo(request, member);

        // then
        assertThat(firstActual.getOrderIndex()).isEqualTo(100_000L);
        assertThat(secondActual.getOrderIndex()).isZero();
    }

    @DisplayName("사용자가 생성한 모든 todo를 반환한다.")
    @Test
    void get_todos() {
        // given
        final TodoCreateRequest request = new TodoCreateRequest("첫 번째 할 일", LocalDateTime.now());
        final Member member = new Member(Role.USER, "아이디", "비번");
        memberRepository.save(member);
        todoService.createTodo(request, member);
        todoService.createTodo(request, member);

        // when
        final var todos = todoService.getTodos(member);

        // then
        assertThat(todos).hasSize(2);
    }

    @DisplayName("id로 todo를 조회한다.")
    @Test
    void get_todo_by_id() {
        // given
        final TodoCreateRequest request = new TodoCreateRequest("첫 번째 할 일", LocalDateTime.now());
        final Member member = new Member(Role.USER, "아이디", "비번");
        memberRepository.save(member);
        final Todo savedTodo = todoService.createTodo(request, member);

        // when & then
        assertThatCode(() -> todoService.getTodo(savedTodo.getId())).doesNotThrowAnyException();
    }

    @DisplayName("존재하지 않는 id로 todo를 조회할 시 예외가 발생한다.")
    @Test
    void throw_exception_when_not_found_todo_id() {
        // given
        final TodoCreateRequest request = new TodoCreateRequest("첫 번째 할 일", LocalDateTime.now());
        final Member member = new Member(Role.USER, "아이디", "비번");
        memberRepository.save(member);
        final long invalidId = todoService.createTodo(request, member)
                .getId() + 1;

        // when & then
        assertThatThrownBy(() -> todoService.getTodo(invalidId)).isExactlyInstanceOf(TodoException.class);
    }
}
