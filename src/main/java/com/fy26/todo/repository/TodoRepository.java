package com.fy26.todo.repository;

import com.fy26.todo.domain.entity.Member;
import com.fy26.todo.domain.entity.Todo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    @Query("SELECT MAX(t.orderIndex) FROM Todo t WHERE t.member = :member AND t.status = com.fy26.todo.domain.Status.ACTIVE")
    Optional<Long> findMaxOrderIndexByMember(@Param("member") Member member);

    @Query("SELECT t.orderIndex FROM Todo t WHERE t.id = :id AND t.status = com.fy26.todo.domain.Status.ACTIVE")
    Optional<Long> findOrderIndexById(@Param("id") long id);

    List<Todo> findAllByMemberOrderByOrderIndexAsc(Member member);

    @Query("""
        SELECT DISTINCT t
        FROM Todo t
        JOIN t.member m
        LEFT JOIN TodoTag tt ON t.id = tt.todo.id
        LEFT JOIN Tag tag ON tt.tag.id = tag.id
        WHERE m = :member
          AND (:completed IS NULL OR t.completed = :completed)
          AND (:tagNames IS NULL OR tag.name IN :tagNames)
          AND t.status = com.fy26.todo.domain.Status.ACTIVE
    """)
    List<Todo> findFilteredTodos(
            @Param("member") Member member,
            @Param("completed") Boolean completed,
            @Param("tagNames") List<String> tagNames
    );
}
