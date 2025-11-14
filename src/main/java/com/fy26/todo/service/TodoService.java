package com.fy26.todo.service;

import com.fy26.todo.domain.Member;
import com.fy26.todo.domain.Status;
import com.fy26.todo.domain.Tag;
import com.fy26.todo.domain.Todo;
import com.fy26.todo.domain.TodoPosition;
import com.fy26.todo.dto.todo.TodoCreateRequest;
import com.fy26.todo.dto.todo.TodoGetResponse;
import com.fy26.todo.dto.todo.TodoOrderContext;
import com.fy26.todo.dto.todo.TodoOrderUpdateRequest;
import com.fy26.todo.exception.TodoErrorCode;
import com.fy26.todo.exception.TodoException;
import com.fy26.todo.repository.TodoRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TodoService {

    public static final long GAP_ORDER_INDEX = 100_000L;
    public static final long INITIAL_ORDER_INDEX = 0L;
    public static final long SAFETY_MARGIN = 1_000_000L;

    private final TodoRepository todoRepository;
    private final TagService tagService;

    @Transactional
    public Todo createTodo(final TodoCreateRequest request, final Member member) {
        final Long lastOrderIndex = todoRepository.findMaxOrderIndexByMember(member)
                .map(max -> max + GAP_ORDER_INDEX)
                .orElse(INITIAL_ORDER_INDEX);
        final Todo todo = Todo.builder()
                .member(member)
                .content(request.content())
                .orderIndex(lastOrderIndex)
                .completed(false)
                .dueDate(request.dueDate())
                .status(Status.ACTIVE)
                .build();
        final Todo savedTodo = todoRepository.save(todo);
        tagService.createTagsForTodo(savedTodo, request.tagNames());
        return savedTodo;
    }

    public List<TodoGetResponse> getTodos(final Member member) {
        final List<Todo> todos = todoRepository.findAllByMemberOrderByOrderIndexAsc(member);
        return todos.stream()
                .map(todo -> new TodoGetResponse(
                        todo.getId(),
                        todo.getOrderIndex(),
                        todo.getContent(),
                        tagService.getTagsForTodo(todo.getId())
                                .stream()
                                .map(Tag::getName)
                                .toList(),
                        todo.isCompleted(),
                        todo.getDueDate(),
                        ChronoUnit.DAYS.between(LocalDate.now(), todo.getDueDate().toLocalDate())
                ))
                .toList();
    }

    public TodoGetResponse getTodo(final Long id) {
        final Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new TodoException(TodoErrorCode.TODO_NOT_FOUND, Map.of("id", id)));
        return new TodoGetResponse(
                todo.getId(),
                todo.getOrderIndex(),
                todo.getContent(),
                tagService.getTagsForTodo(todo.getId())
                        .stream()
                        .map(Tag::getName)
                        .toList(),
                todo.isCompleted(),
                todo.getDueDate(),
                ChronoUnit.DAYS.between(LocalDate.now(), todo.getDueDate().toLocalDate())
        );
    }

    @Transactional
    public void updateTodoOrder(final Long id, final TodoOrderUpdateRequest request, final Member member) {
        final Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new TodoException(TodoErrorCode.TODO_NOT_FOUND, Map.of("id", id)));
        validateTodoOwner(todo, member);
        TodoOrderContext newTodoContext = calculateNewOrderIndex(
                request.position(),
                request.previousTodoId(),
                request.nextTodoId()
        );
        if (needsRebalancing(newTodoContext)) {
            rebalanceOrder(member);
            newTodoContext = calculateNewOrderIndex(
                    request.position(),
                    request.previousTodoId(),
                    request.nextTodoId()
            );
        }
        todo.setOrderIndex(newTodoContext.orderIndex());
    }
    
    private void validateTodoOwner(final Todo todo, final Member member) {
        final long todoOwnerId = todo.getMember()
                .getId();
        final long requesterId = member.getId();
        if (todoOwnerId != requesterId) {
            throw new TodoException(TodoErrorCode.TODO_UNAUTHORIZED, Map.of("id", todo.getId()));
        }
    }

    private TodoOrderContext calculateNewOrderIndex(final TodoPosition position, final Long previousTodoId, final Long nextTodoId) {
        if (previousTodoId == null && nextTodoId == null) {
            return new TodoOrderContext(INITIAL_ORDER_INDEX, null, null);
        }
        if (position == TodoPosition.TOP) {
            long topOrderIndex = todoRepository.findOrderIndexById(nextTodoId)
                    .orElseThrow(() -> new TodoException(TodoErrorCode.TODO_NOT_FOUND, Map.of("nextTodoId", nextTodoId)));

            return new TodoOrderContext(topOrderIndex - GAP_ORDER_INDEX, topOrderIndex, null);
        }
        if (position == TodoPosition.BOTTOM) {
            long bottomOrderIndex = todoRepository.findOrderIndexById(previousTodoId)
                    .orElseThrow(() -> new TodoException(TodoErrorCode.TODO_NOT_FOUND, Map.of("previousTodoId", previousTodoId)));
            return new TodoOrderContext(bottomOrderIndex + GAP_ORDER_INDEX, null, bottomOrderIndex);
        }
        final long previousTodoOrderIndex = todoRepository.findOrderIndexById(previousTodoId)
                .orElseThrow(() -> new TodoException(TodoErrorCode.TODO_NOT_FOUND, Map.of("previousTodoId", previousTodoId)));
        final Long nextTodoOrderIndex = todoRepository.findOrderIndexById(nextTodoId)
                .orElseThrow(() -> new TodoException(TodoErrorCode.TODO_NOT_FOUND, Map.of("nextTodoId", nextTodoId)));
        return new TodoOrderContext(previousTodoOrderIndex + (nextTodoOrderIndex - previousTodoOrderIndex) / 2,
                previousTodoOrderIndex, nextTodoOrderIndex
        );
    }

    private boolean needsRebalancing(final TodoOrderContext todoContext) {
        if (todoContext.previousOrderIndex() != null && todoContext.nextOrderIndex() != null) {
            final long gap = todoContext.nextOrderIndex() - todoContext.previousOrderIndex();
            if (gap <= 1) {
                return true;
            }
        }
        return todoContext.orderIndex() >= Long.MAX_VALUE - SAFETY_MARGIN ||
                todoContext.orderIndex() <= Long.MIN_VALUE + SAFETY_MARGIN;
    }

    private void rebalanceOrder(final Member member) {
        final List<Todo> todos = todoRepository.findAllByMemberOrderByOrderIndexAsc(member);
        long orderIndex = INITIAL_ORDER_INDEX;
        for (final Todo todo : todos) {
            todo.setOrderIndex(orderIndex);
            orderIndex += GAP_ORDER_INDEX;
        }
    }

    @Transactional
    public void updateComplete(final Long id, final Member member) {
        final Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new TodoException(TodoErrorCode.TODO_NOT_FOUND, Map.of("id", id)));
        validateTodoOwner(todo, member);
        todo.setCompleted(!todo.isCompleted());
    }
}

