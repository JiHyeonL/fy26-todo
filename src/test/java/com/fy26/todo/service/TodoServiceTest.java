package com.fy26.todo.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.fy26.todo.domain.Member;
import com.fy26.todo.domain.Role;
import com.fy26.todo.domain.Todo;
import com.fy26.todo.dto.TodoCreateRequest;
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
        assertThat(actual.getPrevTodoId()).isNull();
        assertThat(actual.getNextTodoId()).isNull();
    }
}
