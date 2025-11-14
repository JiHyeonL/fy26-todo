package com.fy26.todo.repository;

import com.fy26.todo.domain.Member;
import com.fy26.todo.domain.Tag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TagRepository extends JpaRepository<Tag, Long> {

    @Query("""
        SELECT t
        FROM Tag t
        WHERE t.member = :member
          AND t.name IN :names
    """)
    List<Tag> findAllByMemberAndNameIn(@Param("member") Member member, @Param("names") List<String> names);
}
