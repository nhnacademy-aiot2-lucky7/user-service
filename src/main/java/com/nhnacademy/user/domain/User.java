package com.nhnacademy.user.domain;

import com.nhnacademy.department.domain.Department;
import com.nhnacademy.eventlevel.domain.EventLevel;
import com.nhnacademy.image.domain.Image;
import com.nhnacademy.role.domain.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_no")
    @Comment("사용자 번호")
    private Long userNo;

    @Column(name = "user_name", length = 50, nullable = false)
    @Comment("사용자 이름")
    private String userName;

    @Column(name = "user_email", length = 100, nullable = false, unique = true)
    @Comment("사용자 이메일")
    private String userEmail;

    @Column(name = "user_password", length = 200)
    @Comment("사용자 비밀번호")
    private String userPassword;

    @Column(name = "user_phone", length = 30, nullable = false)
    @Comment("사용자 연락처")
    private String userPhone;

    @Column(name = "is_socialed", nullable = false)
    @Comment("소셜 로그인 여부")
    private Boolean isSocialed;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "withdrawal_at")
    private LocalDateTime withdrawalAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_name", nullable = false)
    private EventLevel eventLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_no")
    private Image profileImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    private User(String userName, String userEmail, String userPassword,
                 String userPhone, Boolean isSocialed, Department department, Role role, EventLevel eventLevel) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.userPhone = userPhone;
        this.isSocialed = isSocialed;
        this.department = department;
        this.role = role;
        this.eventLevel = eventLevel;
    }

    public static User ofNewMember(String userName, String userEmail, String userPassword,
                                   String userPhone, Department department) {
        return new User(
                userName,
                userEmail,
                userPassword,
                userPhone,
                false,
                department,
                new Role("ROLE_MEMBER", "멤버"),
                new EventLevel("INFO", "일반 정보", 1)
        );
    }

    public static User ofNewSocialMember(String userName, String userEmail, String userPassword,
                                         String userPhone, Department department) {
        return new User(
                userName,
                userEmail,
                userPassword,
                userPhone,
                true,
                department,
                new Role("ROLE_MEMBER", "멤버"),
                new EventLevel("INFO", "일반 정보", 1)
        );
    }

    // 비밀번호 변경 메서드
    public void changePassword(String newPassword) {
        this.userPassword = newPassword;
    }

    // 권한 변경 메서드
    public void changeRole(Role userRole) {
        this.role = userRole;
    }

    public void updateUser(String userName, String userPhone, Department department, EventLevel eventLevel) {
        this.userName = userName;
        this.userPhone = userPhone;
        this.department = department;
        this.eventLevel = eventLevel;
    }

    // 사용자 프로필 이미지 변경
    public void changeProfileImage(Image profileImage) {
        this.profileImage = profileImage;
    }

    public void updateWithdrawalAt() {
        this.withdrawalAt = LocalDateTime.now();
    }

    // JPA 생명주기 이벤트 - 엔티티 저장 전 호출
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // JPA 생명주기 이벤트 - 엔티티 업데이트 전 호출
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
