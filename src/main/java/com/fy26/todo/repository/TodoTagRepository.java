package com.fy26.todo.repository;

import com.fy26.todo.domain.entity.TodoTag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoTagRepository extends JpaRepository<TodoTag, Long> {

    List<TodoTag> findAllByTodoId(Long todoId);

    void deleteAllByTagId(Long tagId);

    void deleteByTodoIdAndTagId(Long todoId, Long tagId);
}
