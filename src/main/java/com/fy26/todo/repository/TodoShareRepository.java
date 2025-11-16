package com.fy26.todo.repository;

import com.fy26.todo.domain.entity.Member;
import com.fy26.todo.domain.entity.TodoShare;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoShareRepository extends JpaRepository<TodoShare, Long> {

    boolean existsByTodoIdAndSharedMemberId(long todoId, long memberId);

    @Query("""
        SELECT ts
        FROM TodoShare ts
        JOIN FETCH ts.todo t
        JOIN FETCH t.member
        WHERE ts.sharedMember = :member
    """)
    List<TodoShare> findAllBySharedMember(@Param("member") Member member);

    @Query("""
        SELECT ts.todo.id
        FROM TodoShare ts
        WHERE ts.todo.member.id = :ownerId
          AND ts.sharedMember.id = :sharedMemberId
    """)
    List<Long> findTodoIdSharedByOwnerToMember(
            @Param("ownerId") Long ownerId,
            @Param("sharedMemberId") Long sharedMemberId
    );
}
