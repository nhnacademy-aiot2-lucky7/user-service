package com.nhnacademy.user.repository;

import com.nhnacademy.common.exception.NotFoundException;
import com.nhnacademy.department.domain.Department;
import com.nhnacademy.department.repository.DepartmentRepository;
import com.nhnacademy.eventlevel.domain.EventLevel;
import com.nhnacademy.eventlevel.repository.EventLevelRepository;
import com.nhnacademy.role.domain.Role;
import com.nhnacademy.role.repository.RoleRepository;
import com.nhnacademy.user.domain.User;
import com.nhnacademy.user.dto.UserResponse;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;


/**
 * 사용자 관련 커스텀 레포지토리 테스트 클래스
 */
@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    EntityManager entityManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    EventLevelRepository eventLevelRepository;

    /**
     * 테스트를 위한 사용자 등록 메서드
     *
     * @return 저장된 User 엔티티
     */
    User settingUser() {
        Department department = new Department("MCS-234", "인사과");
        User user = User.ofNewMember(
                "user",
                "user@email.com",
                "userPassword",
                "010-1234-5678",
                department
        );

        departmentRepository.save(department);
        roleRepository.save(user.getRole());
        eventLevelRepository.save(user.getEventLevel());

        return userRepository.save(user);
    }

    /**
     * userEmail을 기반으로 UserResponse 객체를 반환하는 테스트
     * <p>정상적으로 조회되는지 확인</p>
     */
    @Test
    @DisplayName("userEmail로 userResponse 객체 반환")
    void findUserResponseByUserEmail() {

        User user = settingUser();
        entityManager.clear();

        UserResponse response = userRepository.findUserResponseByUserEmail(user.getUserEmail())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 유저"));

        Assertions.assertNotNull(response);

        Assertions.assertAll(
                () -> {
                    Assertions.assertEquals("ROLE_MEMBER", response.getUserRole());
                    Assertions.assertEquals("user@email.com", response.getUserEmail());
                    Assertions.assertEquals("user", response.getUserName());
                    Assertions.assertEquals(user.getUserNo(), response.getUserNo());
                    Assertions.assertEquals("MCS-234", response.getDepartment().getDepartmentId());
                }
        );
    }

    @Test
    @DisplayName("모든 사용자 조회")
    void findAllUserResponse() {
        Department department = new Department("DEP-001", "개발부");
        Role role = new Role("ROLE_MEMBER", "멤버");
        EventLevel eventLevel = new EventLevel("INFO", "일반 정보", 1);

        roleRepository.save(role);
        departmentRepository.save(department);
        eventLevelRepository.save(eventLevel);

        // 사용자 10명 생성
        IntStream.range(1, 11).forEach(i -> {
            User user = User.ofNewMember(
                    "user" + i,
                    "user" + i + "@email.com",
                    "password" + i,
                    "010-0000-000" + i,
                    department
            );
            userRepository.save(user);
        });

        entityManager.clear();

        Pageable pageable = PageRequest.of(0, 10);
        List<UserResponse> allUsers = userRepository.findAllUserResponse(pageable)
                .orElse(List.of());
        Assertions.assertEquals(10, allUsers.size());

        allUsers.forEach(user -> {
            System.out.println(user.getUserName() + " - " + user.getUserEmail());
        });
    }

    @Test
    @DisplayName("이메일 사용자 존재 여부 확인")
    void existsByUserEmailAndWithdrawalAtIsNull() {
        Department department = new Department("DEP-001", "개발부");
        Role role = new Role("ROLE_MEMBER", "멤버");
        EventLevel eventLevel = new EventLevel("INFO", "일반 정보", 1);

        roleRepository.save(role);
        departmentRepository.save(department);
        eventLevelRepository.save(eventLevel);

        // 사용자 10명 생성
        IntStream.range(1, 11).forEach(i -> {
            User user = User.ofNewMember(
                    "user" + i,
                    "user" + i + "@email.com",
                    "password" + i,
                    "010-0000-000" + i,
                    department
            );
            if (i == 5 || i == 8) {
                user.updateWithdrawalAt();
            }

            userRepository.save(user);
        });

        entityManager.clear();

        boolean withdrawnUser = userRepository.existsByUserEmailAndWithdrawalAtIsNull("user8@email.com");
        boolean activeUser = userRepository.existsByUserEmailAndWithdrawalAtIsNull("user3@email.com");

        Assertions.assertFalse(withdrawnUser);
        Assertions.assertTrue(activeUser);
    }

    @Test
    @DisplayName("탈퇴하지 않은 이메일 사용자 조회")
    void findByUserEmailAndWithdrawalAtIsNull() {
        Department department = new Department("DEP-001", "개발부");
        Role role = new Role("ROLE_MEMBER", "멤버");
        EventLevel eventLevel = new EventLevel("INFO", "일반 정보", 1);

        roleRepository.save(role);
        departmentRepository.save(department);
        eventLevelRepository.save(eventLevel);

        // 사용자 10명 생성
        IntStream.range(1, 11).forEach(i -> {
            User user = User.ofNewMember(
                    "user" + i,
                    "user" + i + "@email.com",
                    "password" + i,
                    "010-0000-000" + i,
                    department
            );
            if (i == 5 || i == 8) {
                user.updateWithdrawalAt();
            }

            userRepository.save(user);
        });

        entityManager.clear();

        Optional<User> withdrawnUser = userRepository.findByUserEmailAndWithdrawalAtIsNull("user8@email.com");
        Optional<User> activeUser = userRepository.findByUserEmailAndWithdrawalAtIsNull("user3@email.com");

        Assertions.assertFalse(withdrawnUser.isPresent());
        Assertions.assertTrue(activeUser.isPresent());
    }

}