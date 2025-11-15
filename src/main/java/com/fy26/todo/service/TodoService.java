package com.fy26.todo.service;

import com.fy26.todo.domain.Status;
import com.fy26.todo.domain.TodoPosition;
import com.fy26.todo.domain.entity.Member;
import com.fy26.todo.domain.entity.Tag;
import com.fy26.todo.domain.entity.Todo;
import com.fy26.todo.dto.tag.TagCreateRequest;
import com.fy26.todo.dto.tag.TagCreateResponse;
import com.fy26.todo.dto.todo.TodoCreateRequest;
import com.fy26.todo.dto.todo.TodoCreateResponse;
import com.fy26.todo.dto.todo.TodoGetResponse;
import com.fy26.todo.dto.todo.TodoOrderContext;
import com.fy26.todo.dto.todo.TodoOrderUpdateRequest;
import com.fy26.todo.dto.todo.TodoUpdateRequest;
import com.fy26.todo.dto.todoshare.TodoShareCreateResponse;
import com.fy26.todo.dto.todoshare.TodoShareDetailGetResponse;
import com.fy26.todo.dto.todoshare.TodoShareSimpleGetResponse;
import com.fy26.todo.exception.TodoErrorCode;
import com.fy26.todo.exception.TodoException;
import com.fy26.todo.repository.TodoRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
    private final TodoShareService todoShareService;

    @Transactional
    public TodoCreateResponse createTodo(final TodoCreateRequest request, final Member member) {
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

        final List<String> normalizedTagNames = normalizeTagNames(request.tagNames());
        final List<Tag> existingTags = tagService.getExistingTags(normalizedTagNames, member);
        final Set<String> existingTagNames = existingTags.stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());
        final List<String> newTagNames = getNewTagNames(normalizedTagNames, existingTagNames);
        final List<Tag> newTags = tagService.createAndBindNewTags(savedTodo, newTagNames);
        tagService.bindExistingTags(savedTodo, existingTags);

        final List<Tag> createdTags = Stream.of(existingTags, newTags)
                .flatMap(Collection::stream)
                .toList();
        return TodoCreateResponse.of(savedTodo, createdTags);
    }

    private List<String> normalizeTagNames(final List<String> tagNames) {
        return tagNames.stream()
                .map(String::trim)
                .distinct()
                .toList();
    }

    private List<String> getNewTagNames(final List<String> tagNames, final Set<String> existingTagNames) {
        return tagNames.stream()
                .filter(name -> !existingTagNames.contains(name))
                .toList();
    }

    @Transactional
    public List<TagCreateResponse> addTagsFromTodo(final Long id, final TagCreateRequest request, final Member member) {
        final Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new TodoException(TodoErrorCode.TODO_NOT_FOUND, Map.of("id", id)));
        validateTodoOwner(todo, member);

        final List<String> normalizedTagNames = normalizeTagNames(request.tagNames());
        final List<Tag> existingTags = tagService.getExistingTags(normalizedTagNames, member);
        final Set<String> existingTagNames = existingTags.stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());
        final List<String> newTagNames = getNewTagNames(normalizedTagNames, existingTagNames);
        final List<Tag> newTags = tagService.createAndBindNewTags(todo, newTagNames);
        tagService.bindExistingTags(todo, existingTags);

        return Stream.of(existingTags, newTags)
                .flatMap(Collection::stream)
                .map(tag -> new TagCreateResponse(tag.getId(), tag.getName()))
                .toList();
    }

    public List<TodoGetResponse> getTodos(final Member member) {
        final List<Todo> todos = todoRepository.findAllByMemberOrderByOrderIndexAsc(member);
        return todos.stream()
                .map(todo -> {
                    final List<Tag> tags = tagService.getTagsForTodo(todo.getId());
                    return TodoGetResponse.of(todo, tags);
                })
                .toList();
    }

    public List<TodoShareDetailGetResponse> getSharedTodos(final Member member) {
        final long memberId = member.getId();
        final List<TodoShareSimpleGetResponse> allSharedTodo = todoShareService.getAllSharedTodoId(memberId);
        return allSharedTodo.stream()
                .map(sharedTodo -> {
                    final List<Tag> tags = tagService.getTagsForTodo(sharedTodo.todo().id());
                    return TodoShareDetailGetResponse.of(sharedTodo, tags);
                })
                .toList();
    }

    public TodoGetResponse getTodo(final Long id) {
        final Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new TodoException(TodoErrorCode.TODO_NOT_FOUND, Map.of("id", id)));
        final List<Tag> tags = tagService.getTagsForTodo(todo.getId());
        return TodoGetResponse.of(todo, tags);
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

    @Transactional
    public void updateTodo(final Long id, final TodoUpdateRequest request, final Member member) {
        final Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new TodoException(TodoErrorCode.TODO_NOT_FOUND, Map.of("id", id)));
        validateTodoOwner(todo, member);
        if (request.content() != null && !request.content().isBlank()) {
            todo.setContent(request.content());
        }
        if (request.dueDate() != null) {
            if (request.dueDate().isBefore(LocalDateTime.now())) {
                throw new TodoException(TodoErrorCode.INVALID_DUE_DATE, Map.of("dueDate", request.dueDate()));
            }
            todo.setDueDate(request.dueDate());
        }
    }

    @Transactional
    public void removeTagFromTodo(final Long todoId, final Long tagId, final Member member) {
        final Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new TodoException(TodoErrorCode.TODO_NOT_FOUND, Map.of("id", todoId)));
        validateTodoOwner(todo, member);
        tagService.unbindTagFromTodo(todoId, tagId);
    }

    @Transactional
    public void deleteTodo(final Long id, final Member member) {
        final Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new TodoException(TodoErrorCode.TODO_NOT_FOUND, Map.of("id", id)));
        validateTodoOwner(todo, member);
        todo.setStatus(Status.DELETED);
    }

    @Transactional
    public void deleteTag(final Long tagId, final Member member) {
        tagService.deleteTag(tagId, member);
    }

    public List<TodoGetResponse> filterTodos(final Boolean completed, final List<String> tagNames, final Member member) {
        return todoRepository.findFilteredTodos(member, completed, tagNames)
                .stream()
                .map(todo -> {
                    final List<Tag> tags = tagService.getTagsForTodo(todo.getId());
                    return TodoGetResponse.of(todo, tags);
                })
                .toList();
    }

    @Transactional
    public TodoShareCreateResponse shareTodo(final Long todoId, final Long targetMemberId, final Member member) {
        final Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new TodoException(TodoErrorCode.TODO_NOT_FOUND, Map.of("id", todoId)));
        validateTodoOwner(todo, member);
        return todoShareService.shareTodo(todo, targetMemberId);
    }
}

