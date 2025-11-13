package com.fy26.todo.service;

import com.fy26.todo.domain.Member;
import com.fy26.todo.domain.Status;
import com.fy26.todo.domain.Todo;
import com.fy26.todo.dto.TodoCreateRequest;
import com.fy26.todo.repository.TodoRepository;
import jakarta.transaction.Transactional;
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
}
