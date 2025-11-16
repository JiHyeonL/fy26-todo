package com.fy26.todo.repository;

import com.fy26.todo.domain.entity.Tag;
import com.fy26.todo.domain.entity.TodoTag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TodoTagRepository extends JpaRepository<TodoTag, Long> {

    @Query("""
        SELECT tt.tag
        FROM TodoTag tt
        WHERE tt.todo.id = :todoId
    """)
    List<Tag> findAllTagByTodoId(@Param("todoId") Long todoId);

    void deleteAllByTagId(Long tagId);

    void deleteByTodoIdAndTagId(Long todoId, Long tagId);
}
