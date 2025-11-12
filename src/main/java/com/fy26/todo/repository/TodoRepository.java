package com.fy26.todo.repository;

import com.fy26.todo.domain.Member;
import com.fy26.todo.domain.Todo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    Optional<Todo> findByMemberAndNextTodoIdIsNull(Member member);
}
