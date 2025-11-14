package com.fy26.todo.service;

import static com.fy26.todo.service.TodoService.GAP_ORDER_INDEX;
import static com.fy26.todo.service.TodoService.INITIAL_ORDER_INDEX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fy26.todo.domain.Member;
import com.fy26.todo.domain.Role;
import com.fy26.todo.domain.Status;
import com.fy26.todo.domain.Todo;
import com.fy26.todo.domain.TodoPosition;
import com.fy26.todo.dto.tag.TagCreateRequest;
import com.fy26.todo.dto.tag.TagCreateResponse;
import com.fy26.todo.dto.todo.TodoCreateRequest;
import com.fy26.todo.dto.todo.TodoGetResponse;
import com.fy26.todo.dto.todo.TodoOrderUpdateRequest;
import com.fy26.todo.dto.todo.TodoUpdateRequest;
import com.fy26.todo.exception.TodoException;
import com.fy26.todo.repository.MemberRepository;
import com.fy26.todo.repository.TagRepository;
import com.fy26.todo.repository.TodoRepository;
import com.fy26.todo.repository.TodoTagRepository;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TodoServiceTest {

    @Autowired
    private TodoService todoService;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TodoTagRepository todoTagRepository;

    @DisplayName("첫 번째 todo를 생성한다.")
    @Test
    void create_todo() {
        // given
        final TodoCreateRequest request = new TodoCreateRequest("첫 번째 할 일", Collections.emptyList(), LocalDateTime.now());
        final Member member = new Member(Role.USER, "아이디", "비번");
        memberRepository.save(member);

        // when
        final Todo actual = todoService.createTodo(request, member);

        // then
        assertThat(actual.getOrderIndex()).isEqualTo(INITIAL_ORDER_INDEX);
    }

    @DisplayName("todo 생성 시 이미 존재하는 태그 이름이 있다면 제외하고 태그를 생성한다.")
    @Test
    void create_todo_with_existing_tags() {
        // given
        final Member member = new Member(Role.USER, "아이디", "비번");
        memberRepository.save(member);
        final List<String> tags = List.of("태그1", "태그2", "태그3");
        final TodoCreateRequest firstRequest = new TodoCreateRequest("첫 번째 할 일", List.of(tags.get(0), tags.get(1)), LocalDateTime.now());
        final Todo firstTodo = todoService.createTodo(firstRequest, member);
        final TodoCreateRequest secondRequest = new TodoCreateRequest("두 번째 할 일", List.of(tags.get(1), tags.get(2)), LocalDateTime.now());

        // when
        final Todo secondTodo = todoService.createTodo(secondRequest, member);

        // then
        assertThat(tagRepository.findAllByMemberAndNameIn(member, tags)).hasSize(tags.size());
        assertThat(todoTagRepository.findAllByTodoId(firstTodo.getId())).hasSize(firstRequest.tagNames().size());
        assertThat(todoTagRepository.findAllByTodoId(secondTodo.getId())).hasSize(secondRequest.tagNames().size());
    }

    @DisplayName("todo를 두 개 생성한다.")
    @Test
    void create_two_todo() {
        // given
        final TodoCreateRequest request = new TodoCreateRequest("첫 번째 할 일", Collections.emptyList(), LocalDateTime.now());
        final Member member = new Member(Role.USER, "아이디", "비번");
        memberRepository.save(member);

        // when
        final Todo firstActual = todoService.createTodo(request, member);
        final Todo secondActual = todoService.createTodo(request, member);

        // then
        assertThat(firstActual.getOrderIndex()).isEqualTo(INITIAL_ORDER_INDEX);
        assertThat(secondActual.getOrderIndex()).isEqualTo(INITIAL_ORDER_INDEX + GAP_ORDER_INDEX);
    }

    @DisplayName("태그 추가 생성 시 사용자가 다르면 중복 태그 이름을 허용한다.")
    @Test
    void create_tags_with_same_name_for_different_members() {
        // given
        final Member member1 = new Member(Role.USER, "아이디1", "비번1");
        final Member member2 = new Member(Role.USER, "아이디2", "비번2");
        memberRepository.saveAll(List.of(member1, member2));
        final TodoCreateRequest request = new TodoCreateRequest("첫 번째 할 일", List.of("공통태그"), LocalDateTime.now());

        // when
        todoService.createTodo(request, member1);
        todoService.createTodo(request, member2);

        // then

        assertThat(tagRepository.findAllByMemberAndNameIn(member1, request.tagNames())).hasSize(1);
        assertThat(tagRepository.findAllByMemberAndNameIn(member2, request.tagNames())).hasSize(1);
    }

    @DisplayName("사용자가 생성한 모든 todo를 반환한다.")
    @Test
    void get_todos() {
        // given
        final TodoCreateRequest request = new TodoCreateRequest("첫 번째 할 일", Collections.emptyList(), LocalDateTime.now());
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
        final TodoCreateRequest request = new TodoCreateRequest("첫 번째 할 일", Collections.emptyList(), LocalDateTime.now());
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
        final TodoCreateRequest request = new TodoCreateRequest("첫 번째 할 일", Collections.emptyList(), LocalDateTime.now());
        final Member member = new Member(Role.USER, "아이디", "비번");
        memberRepository.save(member);
        final long invalidId = todoService.createTodo(request, member)
                .getId() + 1;

        // when & then
        assertThatThrownBy(() -> todoService.getTodo(invalidId)).isExactlyInstanceOf(TodoException.class);
    }

    @DisplayName("다른 사용자가 생성한 todo의 순서를 변경할 시 예외가 발생한다.")
    @Test
    void throw_exception_when_update_todo_order_of_another_member() {
        // given
        final TodoCreateRequest request = new TodoCreateRequest("첫 번째 할 일", Collections.emptyList(), LocalDateTime.now());
        final Member invalidMember = new Member(Role.USER, "아이디1", "비번1");
        final Member validMember = new Member(Role.USER, "아이디2", "비번2");
        memberRepository.saveAll(List.of(invalidMember, validMember));
        final Todo savedTodo = todoService.createTodo(request, validMember);

        // when & then
        final long todoId = savedTodo.getId();
        final TodoOrderUpdateRequest todoOrderRequest = new TodoOrderUpdateRequest(TodoPosition.TOP, null, null);
        assertThatThrownBy(() -> todoService.updateTodoOrder(todoId, todoOrderRequest, invalidMember))
                .isExactlyInstanceOf(TodoException.class);
    }

    @DisplayName("두번째 todo를 가장 윗 순서로 변경한다.")
    @Test
    void update_todo_order_to_top() {
        // given
        final TodoCreateRequest request = new TodoCreateRequest("할 일", Collections.emptyList(), LocalDateTime.now());
        final Member member = new Member(Role.USER, "아이디", "비번");
        memberRepository.save(member);
        final Todo firstTodo = todoService.createTodo(request, member);
        final Todo secondTodo = todoService.createTodo(request, member);

        // when
        todoService.updateTodoOrder(secondTodo.getId(),
                new TodoOrderUpdateRequest(TodoPosition.TOP, null, firstTodo.getId()), member);

        // then
        final TodoGetResponse secondTodoInfo = todoService.getTodo(secondTodo.getId());
        assertThat(secondTodoInfo.orderIndex()).isEqualTo(INITIAL_ORDER_INDEX - GAP_ORDER_INDEX);
    }

    @DisplayName("첫번째 todo를 가장 아랫 순서로 변경한다.")
    @Test
    void update_todo_order_to_bottom() {
        // given
        final TodoCreateRequest request = new TodoCreateRequest("할 일", Collections.emptyList(), LocalDateTime.now());
        final Member member = new Member(Role.USER, "아이디", "비번");
        memberRepository.save(member);
        final Todo firstTodo = todoService.createTodo(request, member);
        final Todo secondTodo = todoService.createTodo(request, member);

        // when
        todoService.updateTodoOrder(firstTodo.getId(),
                new TodoOrderUpdateRequest(TodoPosition.BOTTOM, secondTodo.getId(), null), member);

        // then
        final TodoGetResponse firstTodoInfo = todoService.getTodo(firstTodo.getId());
        assertThat(firstTodoInfo.orderIndex()).isEqualTo(secondTodo.getOrderIndex() + GAP_ORDER_INDEX);
    }

    @DisplayName("첫번째 todo 위치를 두번째 todo와 세번째 todo의 사이로 변경한다")
    @Test
    void update_todo_order_between_two_todos() {
        // given
        final TodoCreateRequest request = new TodoCreateRequest("할 일", Collections.emptyList(), LocalDateTime.now());
        final Member member = new Member(Role.USER, "아이디", "비번");
        memberRepository.save(member);
        final Todo firstTodo = todoService.createTodo(request, member);
        final Todo secondTodo = todoService.createTodo(request, member);
        final Todo thirdTodo = todoService.createTodo(request, member);

        // when
        todoService.updateTodoOrder(firstTodo.getId(),
                new TodoOrderUpdateRequest(TodoPosition.MIDDLE, secondTodo.getId(), thirdTodo.getId()), member);

        // then
        final TodoGetResponse firstTodoInfo = todoService.getTodo(firstTodo.getId());
        assertThat(firstTodoInfo.orderIndex()).isEqualTo((secondTodo.getOrderIndex() + thirdTodo.getOrderIndex()) / 2);
    }

    @DisplayName("todo 순서 변경 시 Long 범위를 벗어날 경우 재정렬을 수행한다.")
    @Test
    void rebalance_when_order_index_exceeds_long_range() {
        // given
        final Member member = memberRepository.save(new Member(Role.USER, "아이디", "비번"));
        memberRepository.save(member);
        final Todo firstTodo = new Todo(member, "투두", INITIAL_ORDER_INDEX, false, LocalDateTime.now(), Status.ACTIVE);
        final Todo secondTodo = new Todo(member, "투두", Long.MAX_VALUE, false, LocalDateTime.now(), Status.ACTIVE);
        final Todo savedFirstTodo = todoRepository.save(firstTodo);
        final Todo savedSecondTodo = todoRepository.save(secondTodo);

        // when
        todoService.updateTodoOrder(savedFirstTodo.getId(),
                new TodoOrderUpdateRequest(TodoPosition.BOTTOM, savedSecondTodo.getId(), null), member);

        // then
        final long firstOrderIndex = todoService.getTodo(savedFirstTodo.getId()).orderIndex();
        assertThat(firstOrderIndex).isEqualTo(INITIAL_ORDER_INDEX + GAP_ORDER_INDEX * 2);
        final long secondOrderIndex = todoService.getTodo(savedSecondTodo.getId()).orderIndex();
        assertThat(secondOrderIndex).isEqualTo(INITIAL_ORDER_INDEX + GAP_ORDER_INDEX);
    }

    @DisplayName("todo 순서 변경 시 정렬 인덱스가 같을 경우 재정렬을 수행한다.")
    @Test
    void rebalance_when_order_index_is_same() {
        // given
        final Member member = memberRepository.save(new Member(Role.USER, "아이디", "비번"));
        memberRepository.save(member);
        final Todo firstTodo = new Todo(member, "투두", 10L, false, LocalDateTime.now(), Status.ACTIVE);
        final Todo secondTodo = new Todo(member, "투두", 11L, false, LocalDateTime.now(), Status.ACTIVE);
        final Todo thirdTodo = new Todo(member, "투두", 50L, false, LocalDateTime.now(), Status.ACTIVE);
        final Todo savedFirstTodo = todoRepository.save(firstTodo);
        final Todo savedSecondTodo = todoRepository.save(secondTodo);
        final Todo savedThirdTodo = todoRepository.save(thirdTodo);

        // when
        todoService.updateTodoOrder(savedThirdTodo.getId(),
                new TodoOrderUpdateRequest(TodoPosition.MIDDLE, savedFirstTodo.getId(), savedSecondTodo.getId()), member);

        // then
        final long thirdOrderIndex = todoService.getTodo(savedThirdTodo.getId()).orderIndex();
        assertThat(thirdOrderIndex).isEqualTo((INITIAL_ORDER_INDEX * 2 + GAP_ORDER_INDEX) / 2);
    }

    @DisplayName("todo 완료 상태를 토글한다.")
    @Test
    void toggle_todo_completed() {
        // given
        final TodoCreateRequest request = new TodoCreateRequest("첫 번째 할 일", Collections.emptyList(), LocalDateTime.now());
        final Member member = new Member(Role.USER, "아이디", "비번");
        memberRepository.save(member);
        final Todo savedTodo = todoService.createTodo(request, member);

        // when
        todoService.updateComplete(savedTodo.getId(), member);

        // then
        final TodoGetResponse todoInfo = todoService.getTodo(savedTodo.getId());
        assertThat(todoInfo.completed()).isTrue();
    }

    @DisplayName("todo 내용을 수정한다.")
    @Test
    void update_todo_content() {
        // given
        final TodoCreateRequest request = new TodoCreateRequest("첫 번째 할 일", Collections.emptyList(), LocalDateTime.now());
        final Member member = new Member(Role.USER, "아이디", "비번");
        memberRepository.save(member);
        final Todo savedTodo = todoService.createTodo(request, member);

        // when
        final String expected = "수정된 할 일";
        todoService.updateTodo(savedTodo.getId(),
                new TodoUpdateRequest(expected, null), member);

        // then
        final TodoGetResponse actual = todoService.getTodo(savedTodo.getId());
        assertThat(actual.content()).isEqualTo(expected);
    }

    @DisplayName("투두 마감일을 수정할 때, 현재 날짜보다 이전이면 예외가 발생한다.")
    @Test
    void throw_exception_when_update_todo_due_date_before_now() {
        // given
        final TodoCreateRequest request = new TodoCreateRequest("첫 번째 할 일", Collections.emptyList(), LocalDateTime.now());
        final Member member = new Member(Role.USER, "아이디", "비번");
        memberRepository.save(member);
        final Todo savedTodo = todoService.createTodo(request, member);

        // when & then
        final long todoId = savedTodo.getId();
        final LocalDateTime pastDueDate = LocalDateTime.now().minusDays(1);
        final TodoUpdateRequest updateRequest = new TodoUpdateRequest(null, pastDueDate);
        assertThatThrownBy(() -> todoService.updateTodo(todoId, updateRequest, member))
                .isExactlyInstanceOf(TodoException.class);
    }

    @DisplayName("투두에 태그를 추가한다.")
    @Test
    void add_tags_to_todo() {
        // given
        final TodoCreateRequest request = new TodoCreateRequest("첫 번째 할 일", Collections.emptyList(),
                LocalDateTime.now());
        final Member member = new Member(Role.USER, "아이디", "비번");
        memberRepository.save(member);
        final Todo savedTodo = todoService.createTodo(request, member);
        final TagCreateRequest tagCreateRequest = new TagCreateRequest(List.of("태그1", "태그2"));

        // when
        final List<TagCreateResponse> responses = todoService.addTags(savedTodo.getId(), tagCreateRequest, member);

        // then
        assertThat(responses).hasSize(2);
    }

    @DisplayName("투두를 삭제한다.")
    @Test
    void delete_todo() {
        // given
        final TodoCreateRequest request = new TodoCreateRequest("첫 번째 할 일", Collections.emptyList(),
                LocalDateTime.now());
        final Member member = new Member(Role.USER, "아이디", "비번");
        memberRepository.save(member);
        final Todo savedTodo = todoService.createTodo(request, member);

        // when
        todoService.deleteTodo(savedTodo.getId(), member);

        // then
        final long todoId = savedTodo.getId();
        assertThatThrownBy(() -> todoService.getTodo(todoId))
                .isExactlyInstanceOf(TodoException.class);
    }

    @DisplayName("투두 조회 시 삭제한 투두는 반환하지 않는다.")
    @Test
    void do_not_return_deleted_todo_on_get_todos() {
        // given
        final TodoCreateRequest request = new TodoCreateRequest("첫 번째 할 일", Collections.emptyList(),
                LocalDateTime.now());
        final Member member = new Member(Role.USER, "아이디", "비번");
        memberRepository.save(member);
        final Todo savedTodo = todoService.createTodo(request, member);

        // when
        todoService.deleteTodo(savedTodo.getId(), member);
        final List<TodoGetResponse> todos = todoService.getTodos(member);

        // then
        assertThat(todos).isEmpty();
    }
}
