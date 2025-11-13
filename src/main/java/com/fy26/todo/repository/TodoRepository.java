package com.fy26.todo.repository;

import com.fy26.todo.domain.Member;
import com.fy26.todo.domain.Todo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    @Query("SELECT MIN(t.orderIndex) FROM Todo t WHERE t.member = :member")
    Optional<Long> findMinOrderIndexByMember(@Param("member") Member member);

    List<Todo> findAllByMemberOrderByOrderIndexAsc(Member member);
}
