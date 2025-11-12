package com.fy26.todo.service;

import com.fy26.todo.domain.Member;
import com.fy26.todo.domain.Status;
import com.fy26.todo.domain.Todo;
import com.fy26.todo.dto.TodoCreateRequest;
import com.fy26.todo.repository.TodoRepository;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TodoService {

    private final TodoRepository todoRepository;

    @Transactional
    public Todo createTodo(final TodoCreateRequest request, final Member member) {
        final Optional<Todo> lastTodo = todoRepository.findByMemberAndNextTodoIdIsNull(member);
        Long lastTodoId = null;
        if (lastTodo.isPresent()) {
            lastTodoId = lastTodo.get()
                    .getId();
        }
        final Todo todo = Todo.builder()
                .member(member)
                .content(request.content())
                .prevTodoId(lastTodoId)
                .nextTodoId(null)
                .completed(false)
                .dueDate(request.dueDate())
                .status(Status.ACTIVE)
                .build();
        return todoRepository.save(todo);
    }
}
