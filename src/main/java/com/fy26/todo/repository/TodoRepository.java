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

    @Query("SELECT MAX(t.orderIndex) FROM Todo t WHERE t.member = :member")
    Optional<Long> findMaxOrderIndexByMember(@Param("member") Member member);

    @Query("SELECT t.orderIndex FROM Todo t WHERE t.id = :id")
    Optional<Long> findOrderIndexById(@Param("id") long id);

    @Query("""
        SELECT MAX(t.orderIndex) FROM Todo t
        WHERE t.member = :member AND t.orderIndex < :currentOrderIndex
    """)
    Optional<Long> findPrevOrderIndex(@Param("member") Member member, @Param("currentOrderIndex") long currentOrderIndex);

    List<Todo> findAllByMemberOrderByOrderIndexAsc(Member member);
}
