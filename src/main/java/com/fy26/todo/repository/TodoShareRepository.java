package com.fy26.todo.repository;

import com.fy26.todo.domain.entity.TodoShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoShareRepository extends JpaRepository<TodoShare, Long> {

    boolean existsByTodoIdAndSharedMemberId(long todoId, long memberId);
}
