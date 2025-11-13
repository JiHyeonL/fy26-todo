package com.fy26.todo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role")
    private Role role;

    @Column(name = "member_name")
    private String memberName;

    @Column(name = "password")
    private String password;

    public Member(Role role, String memberName, String password) {
        this.role = role;
        this.memberName = memberName;
        this.password = password;
    }

    public Member(Long id, Role role, String memberName, String password) {
        this.id = id;
        this.role = role;
        this.memberName = memberName;
        this.password = password;
    }
}
