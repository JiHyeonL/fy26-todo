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

    @Column(name = "order_index")
    private Long orderIndex;

    @Column(name = "completed")
    private boolean completed;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "status")
    private Status status;

    @Builder
    public Todo(Member member, String content, Long orderIndex, boolean completed, LocalDateTime dueDate,
                Status status) {
        this.member = member;
        this.content = content;
        this.orderIndex = orderIndex;
        this.completed = completed;
        this.dueDate = dueDate;
        this.status = status;
    }

    public void setOrderIndex(final long newOrderIndex) {
        this.orderIndex = newOrderIndex;
    }
}
