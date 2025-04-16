package com.nhnacademy.user.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User {

    public enum Role {
        ADMIN,
        OWNER,
        MEMBER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_no", nullable = false, unique = true, updatable = false)
    @Comment("사용자-번호")
    private Long userNo;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false, updatable = false)
    @Comment("사용자-역할")
    private Role userRole;

    @NotNull
    @Column(name = "user_name", nullable = false, length = 45)
    @Comment("사용자-이름")
    private String userName;

    @Column(name = "user_email", unique = true, nullable = false, length = 45)
    @Comment("사용자-이메일")
    private String userEmail;

    @Column(name = "user_password", nullable = false, length = 45)
    @Comment("사용자-비밀번호")
    private String userPassword;

    @Column(name = "user_phone", length = 20)
    @Comment("사용자-전화번호")
    private String userPhone;

    @Column(name = "user_department", length = 45)
    @Comment("사용자-부서명")
    private String userDepartment;

    @Column(nullable = false, updatable = false)
    @Comment("가입일자")
    private LocalDateTime createdAt;

    @Comment("수정일자")
    private LocalDateTime updatedAt;

    @Comment("탈퇴일자")
    private LocalDateTime withdrawalAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void withdrawalAt() {
        this.withdrawalAt = LocalDateTime.now();
    }

    private User(Role userRole, String userName, String userEmail, String userPassword, String userPhone, String userDepartment) {
        this.userRole = userRole;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.userPhone = userPhone;
        this.userDepartment = userDepartment;
    }

    public static User ofNewMember(String userName, String userEmail, String userPassword, String userPhone, String userDepartment) {
        return new User(Role.MEMBER, userName, userEmail, userPassword, userPhone, userDepartment);
    }

    public void update(String userName, String userEmail, String userPhone, String userDepartment) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.userDepartment = userDepartment;
    }

    public void updateRole(Role updateUserRole) {
        this.userRole = updateUserRole;
    }

    public void changePassword(String userPassword) {
        this.userPassword = userPassword;
    }
}
