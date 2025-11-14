package com.fy26.todo.service;

import com.fy26.todo.domain.entity.Member;
import com.fy26.todo.domain.entity.Todo;
import com.fy26.todo.domain.entity.TodoShare;
import com.fy26.todo.dto.todoshare.TodoShareCreateResponse;
import com.fy26.todo.exception.MemberErrorCode;
import com.fy26.todo.exception.MemberException;
import com.fy26.todo.exception.TodoShareErrorCode;
import com.fy26.todo.exception.TodoShareException;
import com.fy26.todo.repository.MemberRepository;
import com.fy26.todo.repository.TodoRepository;
import com.fy26.todo.repository.TodoShareRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TodoShareService {

    private final TodoShareRepository todoShareRepository;
    private final MemberRepository memberRepository;
    private final TodoRepository todoRepository;

    public TodoShareCreateResponse shareTodo(final Todo todo, final Long sharedMemberId) {
        final long ownerId = todo.getMember()
                .getId();
        if (sharedMemberId.equals(ownerId)) {
            throw new TodoShareException(
                    TodoShareErrorCode.CANNOT_SHARE_TO_SELF, Map.of("ownerId", ownerId));
        }
        final Member sharedMember = memberRepository.findById(sharedMemberId)
                .orElseThrow(() -> new MemberException(
                        MemberErrorCode.MEMBER_NOT_FOUND,
                        Map.of("sharedMemberId", sharedMemberId))
                );
        if (todoShareRepository.existsByTodoIdAndSharedMemberId(todo.getId(), sharedMemberId)) {
            throw new TodoShareException(TodoShareErrorCode.ALREADY_SHARED_TODO, Map.of("sharedMemberId", sharedMemberId));
        }
        final TodoShare savedTodoShare = todoShareRepository.save(new TodoShare(todo, sharedMember));
        return new TodoShareCreateResponse(savedTodoShare.getId(), todo.getId(), sharedMemberId);
    }
}
