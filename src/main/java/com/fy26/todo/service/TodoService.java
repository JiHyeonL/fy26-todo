package com.fy26.todo.service;

import com.fy26.todo.domain.Member;
import com.fy26.todo.domain.Status;
import com.fy26.todo.domain.Todo;
import com.fy26.todo.dto.TodoCreateRequest;
import com.fy26.todo.dto.TodoGetResponse;
import com.fy26.todo.exception.TodoErrorCode;
import com.fy26.todo.exception.TodoException;
import com.fy26.todo.repository.TodoRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TodoService {

    private static final long GAP_ORDER_INDEX = 100_000L;
    private static final long INITIAL_ORDER_INDEX = 100_000L;

    private final TodoRepository todoRepository;

    @Transactional
    public Todo createTodo(final TodoCreateRequest request, final Member member) {
        final Long firstOrderIndex = todoRepository.findMinOrderIndexByMember(member)
                .map(min -> min - GAP_ORDER_INDEX)
                .orElse(INITIAL_ORDER_INDEX);
        final Todo todo = Todo.builder()
                .member(member)
                .content(request.content())
                .orderIndex(firstOrderIndex)
                .completed(false)
                .dueDate(request.dueDate())
                .status(Status.ACTIVE)
                .build();
        return todoRepository.save(todo);
    }

    public List<TodoGetResponse> getTodos(final Member member) {
        final List<Todo> todos = todoRepository.findAllByMemberOrderByOrderIndexAsc(member);
        return todos.stream()
                .map(todo -> new TodoGetResponse(
                        todo.getId(),
                        todo.getContent(),
                        todo.isCompleted(),
                        todo.getDueDate(),
                        ChronoUnit.DAYS.between(LocalDate.now(), todo.getDueDate().toLocalDate())
                ))
                .toList();
    }

    public TodoGetResponse getTodo(final Long id) {
        final Optional<Todo> todoOrNull = todoRepository.findById(id);
        return todoOrNull
                .map(todo -> new TodoGetResponse(
                        todo.getId(),
                        todo.getContent(),
                        todo.isCompleted(),
                        todo.getDueDate(),
                        ChronoUnit.DAYS.between(LocalDate.now(), todo.getDueDate().toLocalDate())
                ))
                .orElseThrow(() -> new TodoException(TodoErrorCode.TODO_NOT_FOUND, Map.of("id", id)));
    }
}
