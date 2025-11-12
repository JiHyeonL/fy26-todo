package com.fy26.todo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Todo extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "content", length = 255)
    private String content;

    @Column(name = "prev_todo_id", nullable = true)
    private Long prevTodoId;

    @Column(name = "next_todo_id", nullable = true)
    private Long nextTodoId;

    @Column(name = "completed")
    private boolean completed;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "status")
    private Status status;

    @Builder
    public Todo(Member member, String content, Long prevTodoId, Long nextTodoId, boolean completed,
                LocalDateTime dueDate,
                Status status) {
        this.member = member;
        this.content = content;
        this.prevTodoId = prevTodoId;
        this.nextTodoId = nextTodoId;
        this.completed = completed;
        this.dueDate = dueDate;
        this.status = status;
    }
}
