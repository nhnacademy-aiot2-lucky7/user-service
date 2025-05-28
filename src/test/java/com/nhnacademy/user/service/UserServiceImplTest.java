package com.nhnacademy.user.service;

import com.nhnacademy.common.exception.ConflictException;
import com.nhnacademy.common.exception.NotFoundException;
import com.nhnacademy.common.exception.UnauthorizedException;
import com.nhnacademy.department.domain.Department;
import com.nhnacademy.department.dto.DepartmentResponse;
import com.nhnacademy.department.repository.DepartmentRepository;
import com.nhnacademy.eventlevel.dto.EventLevelResponse;
import com.nhnacademy.eventlevel.repository.EventLevelRepository;
import com.nhnacademy.role.domain.Role;
import com.nhnacademy.role.repository.RoleRepository;
import com.nhnacademy.user.domain.User;
import com.nhnacademy.user.dto.*;
import com.nhnacademy.user.repository.UserRepository;
import com.nhnacademy.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@ExtendWith(SpringExtension.class)
class UserServiceImplTest {
    @Spy
    BCryptPasswordEncoder passwordEncoder;

    @Mock
    DepartmentRepository departmentRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    EventLevelRepository eventLevelRepository;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    @DisplayName("유저가입 - 새로운 사용자 등록")
    void createUser() {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest(
                "testUser",
                "test@email.com",
                "testPassword",
                "010-1234-5678",
                "DEP-001"
        );

        Mockito.when(userRepository.existsByUserEmailAndWithdrawalAtIsNull(Mockito.anyString())).thenReturn(false);
        Mockito.when(departmentRepository.existsById(Mockito.anyString())).thenReturn(true);

        userService.createUser(userRegisterRequest, false);

        Mockito.verify(userRepository, Mockito.times(1)).existsByUserEmailAndWithdrawalAtIsNull(Mockito.anyString());
        Mockito.verify(passwordEncoder, Mockito.times(1)).encode(Mockito.anyString());
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
    }

    @Test
    @DisplayName("유저가입 - 존재하는 이메일")
    void createUser_exception1() {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest(
                "testUser",
                "test@email.com",
                "testPassword",
                "010-1234-5678",
                "DEP-001"
        );

        Mockito.when(userRepository.existsByUserEmailAndWithdrawalAtIsNull(Mockito.anyString())).thenReturn(true);

        Assertions.assertThrows(ConflictException.class, () -> userService.createUser(userRegisterRequest, false));

        Mockito.verify(userRepository, Mockito.times(1)).existsByUserEmailAndWithdrawalAtIsNull(Mockito.anyString());
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    @DisplayName("유저가입 - 존재하지 않는 부서")
    void createUser_exception2() {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest(
                "testUser",
                "test@email.com",
                "testPassword",
                "010-1234-5678",
                "DEP-001"
        );

        Mockito.when(userRepository.existsByUserEmailAndWithdrawalAtIsNull(Mockito.anyString())).thenReturn(false);
        Mockito.when(departmentRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> userService.createUser(userRegisterRequest, false));

        Mockito.verify(userRepository, Mockito.times(1)).existsByUserEmailAndWithdrawalAtIsNull(Mockito.anyString());
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    @DisplayName("이메일 기반 사용자 조회")
    void getUser() {
        String userEmail = "test@email.com";
        UserResponse userResponse = new UserResponse(
                "ROLE_MEMBER",
                1L,
                "testUser",
                "test@email.com",
                "010-1234-5678",
                new DepartmentResponse("DEP-001", "개발부"),
                new EventLevelResponse("error", "에러", 4)
        );

        Mockito.when(userRepository.findUserResponseByUserEmail(Mockito.anyString())).thenReturn(Optional.of(userResponse));

        UserResponse getUser = userService.getUser(userEmail);

        Mockito.verify(userRepository, Mockito.times(1)).findUserResponseByUserEmail(Mockito.anyString());

        Assertions.assertAll(
                () -> {
                    Assertions.assertEquals("ROLE_MEMBER", getUser.getUserRole());
                    Assertions.assertEquals(1L, getUser.getUserNo());
                    Assertions.assertEquals("testUser", getUser.getUserName());
                    Assertions.assertEquals("test@email.com", getUser.getUserEmail());
                    Assertions.assertEquals("010-1234-5678", getUser.getUserPhone());
                    Assertions.assertEquals("DEP-001", getUser.getDepartment().getDepartmentId());
                }
        );
    }

    @Test
    @DisplayName("이메일 기반 사용자 조회 - 존재하지 않는 유저")
    void getUser_exception1() {
        String userEmail = "test@email.com";

        Mockito.when(userRepository.findUserResponseByUserEmail(Mockito.anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> userService.getUser(userEmail));

        Mockito.verify(userRepository, Mockito.times(1)).findUserResponseByUserEmail(Mockito.anyString());
    }

    @Test
    @DisplayName("서비스에서 페이징된 사용자 목록 가져오기")
    void getAllUser_withPaging() {
        Pageable pageable = PageRequest.of(0, 10);

        List<UserResponse> userResponses = IntStream.range(1, 11)
                .mapToObj(i -> new UserResponse(
                        "ROLE_MEMBER",
                        (long) i,
                        "testUser" + i,
                        "test" + i + "@email.com",
                        "010-1234-567" + i,
                        new DepartmentResponse("DEP-001", "개발부"),
                        new EventLevelResponse("error", "에러", 4)
                ))
                .toList();

        Mockito.when(userRepository.findAllUserResponse(pageable))
                .thenReturn(Optional.of(userResponses));

        List<UserResponse> result = userService.getAllUser(pageable);

        Mockito.verify(userRepository, Mockito.times(1)).findAllUserResponse(pageable);
        Assertions.assertEquals(10, result.size());
    }

    @Test
    @DisplayName("로그인")
    void loginUser() {
        UserLoginRequest userLoginRequest = new UserLoginRequest(
                "user@email.com",
                "P@ssw0rd"
        );

        User user = User.ofNewMember(
                "testUser",
                "test@email.com",
                passwordEncoder.encode("P@ssw0rd"),
                "010-1234-5678",
                new Department("DEP-001", "개발팀")
        );

        Mockito.when(userRepository.findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString())).thenReturn(Optional.of(user));

        userService.loginUser(userLoginRequest);

        Mockito.verify(userRepository, Mockito.times(1)).findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString());
        Mockito.verify(passwordEncoder, Mockito.times(1)).matches(Mockito.any(CharSequence.class), Mockito.anyString());
    }

    @Test
    @DisplayName("로그인 - 존재하지 않는 유저")
    void loginUser_exception1() {
        UserLoginRequest userLoginRequest = new UserLoginRequest(
                "user@email.com",
                "P@ssw0rd"
        );

        Mockito.when(userRepository.findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> userService.loginUser(userLoginRequest));

        Mockito.verify(userRepository, Mockito.times(1)).findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString());
        Mockito.verify(passwordEncoder, Mockito.never()).matches(Mockito.any(CharSequence.class), Mockito.anyString());
    }

    @Test
    @DisplayName("로그인 - 비밀번호 불일치")
    void loginUser_exception2() {
        UserLoginRequest userLoginRequest = new UserLoginRequest(
                "user@email.com",
                "wrongP@ssw0rd"
        );

        User user = User.ofNewMember(
                "testUser",
                "test@email.com",
                passwordEncoder.encode("P@ssw0rd"),
                "010-1234-5678",
                new Department("DEP-001", "개발팀")
        );

        Mockito.when(userRepository.findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString())).thenReturn(Optional.of(user));

        Assertions.assertThrows(UnauthorizedException.class, () -> userService.loginUser(userLoginRequest));

        Mockito.verify(userRepository, Mockito.times(1)).findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString());
        Mockito.verify(passwordEncoder, Mockito.times(1)).matches(Mockito.any(CharSequence.class), Mockito.anyString());
    }

    @Test
    @DisplayName("비밀번호 변경")
    void changePassword() {
        String userEmail = "test@email.com";
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(
                "P@ssw0rd",
                "newP@ssw0rd",
                "newP@ssw0rd"
        );

        User user = User.ofNewMember(
                "testUser",
                "test@email.com",
                passwordEncoder.encode("P@ssw0rd"),
                "010-1234-5678",
                new Department("DEP-001", "개발팀")
        );

        Mockito.when(userRepository.findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString())).thenReturn(Optional.of(user));

        userService.changePassword(userEmail, changePasswordRequest);

        Mockito.verify(userRepository, Mockito.times(1)).findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString());
        Mockito.verify(passwordEncoder, Mockito.times(1)).matches(Mockito.any(CharSequence.class), Mockito.anyString());

        Assertions.assertTrue(passwordEncoder.matches(changePasswordRequest.getNewPassword(), user.getUserPassword()));
    }

    @Test
    @DisplayName("비밀번호 변경 - 존재하지 않는 유저")
    void changePassword_exception1() {
        String userEmail = "test@email.com";
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(
                "P@ssw0rd",
                "newP@ssw0rd",
                "newP@ssw0rd"
        );

        Mockito.when(userRepository.findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> userService.changePassword(userEmail, changePasswordRequest));

        Mockito.verify(userRepository, Mockito.times(1)).findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString());
        Mockito.verify(passwordEncoder, Mockito.never()).matches(Mockito.any(CharSequence.class), Mockito.anyString());
    }

    @Test
    @DisplayName("비밀번호 변경 - 비밀번호 불일치")
    void changePassword_exception2() {
        String userEmail = "test@email.com";
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(
                "wrongP@ssw0rd",
                "newP@ssw0rd",
                "newP@ssw0rd"
        );

        User user = User.ofNewMember(
                "testUser",
                "test@email.com",
                passwordEncoder.encode("P@ssw0rd"),
                "010-1234-5678",
                new Department("DEP-001", "개발팀")
        );

        Mockito.when(userRepository.findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString())).thenReturn(Optional.of(user));

        Assertions.assertThrows(UnauthorizedException.class, () -> userService.changePassword(userEmail, changePasswordRequest));

        Mockito.verify(userRepository, Mockito.times(1)).findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString());
        Mockito.verify(passwordEncoder, Mockito.times(1)).matches(Mockito.any(CharSequence.class), Mockito.anyString());
    }

    @Test
    @DisplayName("사용자 정보 수정")
    void updateUser() {
        String userEmail = "test@email.com";
        Department department = new Department("DEP-001", "개발부");

        UserUpdateRequest userUpdateRequest = new UserUpdateRequest(
                "testUser",
                "010-1234-5678",
                department.getDepartmentId(),
                "info"
        );

        User user = User.ofNewMember(
                "testUser",
                "test@email.com",
                passwordEncoder.encode("P@ssw0rd"),
                "010-1234-5678",
                department
        );

        Mockito.when(userRepository.findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString())).thenReturn(Optional.of(user));
        Mockito.when(departmentRepository.existsById(Mockito.anyString())).thenReturn(true);
        Mockito.when(eventLevelRepository.existsById(Mockito.anyString())).thenReturn(true);

        userService.updateUser(userEmail, userUpdateRequest);

        Mockito.verify(userRepository, Mockito.times(1)).findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString());
        Mockito.verify(departmentRepository, Mockito.times(1)).existsById(Mockito.anyString());
        Mockito.verify(eventLevelRepository, Mockito.times(1)).existsById(Mockito.anyString());
    }

    @Test
    @DisplayName("사용자 정보 수정 - 존재하지 않는 유저")
    void updateUser_exception1() {
        String userEmail = "test@email.com";
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest(
                "testUser",
                "010-1234-5678",
                "DEP-001",
                "info"
        );

        Mockito.when(userRepository.findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> userService.updateUser(userEmail, userUpdateRequest));

        Mockito.verify(userRepository, Mockito.times(1)).findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString());
        Mockito.verify(departmentRepository, Mockito.never()).findById(Mockito.anyString());
    }

    @Test
    @DisplayName("사용자 정보 수정 - 존재하지 않는 부서")
    void updateUser_exception2() {
        String userEmail = "test@email.com";
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest("testUser", "010-1234-5678", "DEP-001", "info");
        User user = Mockito.mock(User.class);

        Mockito.when(userRepository.findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString())).thenReturn(Optional.of(user));
        Mockito.when(departmentRepository.existsById(Mockito.anyString())).thenReturn(false);


        Assertions.assertThrows(NotFoundException.class, () -> userService.updateUser(userEmail, userUpdateRequest));

        Mockito.verify(userRepository, Mockito.times(1)).findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString());
        Mockito.verify(departmentRepository, Mockito.times(1)).existsById(Mockito.anyString());
    }

    @Test
    @DisplayName("사용자 정보 수정 - 존재하지 않는 이벤트 레벨")
    void updateUser_exception3() {
        String userEmail = "test@email.com";
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest("testUser", "010-1234-5678", "DEP-001", "info");
        User user = Mockito.mock(User.class);

        Mockito.when(userRepository.findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString())).thenReturn(Optional.of(user));
        Mockito.when(departmentRepository.existsById(Mockito.anyString())).thenReturn(true);
        Mockito.when(eventLevelRepository.existsById(Mockito.anyString())).thenReturn(false);


        Assertions.assertThrows(NotFoundException.class, () -> userService.updateUser(userEmail, userUpdateRequest));

        Mockito.verify(userRepository, Mockito.times(1)).findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString());
        Mockito.verify(departmentRepository, Mockito.times(1)).existsById(Mockito.anyString());
        Mockito.verify(eventLevelRepository, Mockito.times(1)).existsById(Mockito.anyString());
    }

    @Test
    @DisplayName("사용자 권한 수정")
    void updateUserRole() {
        UserRoleUpdateRequest userRoleUpdateRequest = new UserRoleUpdateRequest(
                "user@email.com",
                "ROLE_OWNER"
        );

        User user = User.ofNewMember(
                "testUser",
                "test@email.com",
                passwordEncoder.encode("P@ssw0rd"),
                "010-1234-5678",
                new Department("DEP-001", "개발부")
        );

        Mockito.when(userRepository.findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString())).thenReturn(Optional.of(user));
        Mockito.when(roleRepository.existsById(Mockito.anyString())).thenReturn(true);
        Mockito.when(roleRepository.getReferenceById(Mockito.anyString())).thenReturn(new Role("ROLE_OWNER", "팀장"));

        userService.updateUserRole(userRoleUpdateRequest);

        Mockito.verify(userRepository, Mockito.times(1)).findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString());
        Mockito.verify(roleRepository, Mockito.times(1)).existsById(Mockito.anyString());

        Assertions.assertEquals("ROLE_OWNER", user.getRole().getRoleId());
    }

    @Test
    @DisplayName("사용자 권한 수정 - 존재하지 않는 유저")
    void updateUserRole_exception1() {
        UserRoleUpdateRequest request = new UserRoleUpdateRequest(
                "test@email.com",
                "ROLE_ADMIN"
        );

        Mockito.when(userRepository.findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> userService.updateUserRole(request));

        Mockito.verify(userRepository, Mockito.times(1)).findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString());
        Mockito.verify(roleRepository, Mockito.never()).findById(Mockito.anyString());
    }

    @Test
    @DisplayName("사용자 권한 수정 - 존재하지 않는 권한")
    void updateUserRole_exception2() {
        UserRoleUpdateRequest request = new UserRoleUpdateRequest(
                "test@email.com",
                "ROLE_ADMIN"
        );
        User user = User.ofNewMember(
                "testUser",
                "test@email.com",
                passwordEncoder.encode("P@ssw0rd"),
                "010-1234-5678",
                new Department("DEP-001", "개발부")
        );

        Mockito.when(userRepository.findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString())).thenReturn(Optional.of(user));
        Mockito.when(roleRepository.existsById(Mockito.anyString())).thenReturn(false);

        Assertions.assertThrows(NotFoundException.class, () -> userService.updateUserRole(request));

        Mockito.verify(userRepository, Mockito.times(1)).findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString());
        Mockito.verify(roleRepository, Mockito.times(1)).existsById(Mockito.anyString());
    }

    @Test
    @DisplayName("회원 탈퇴")
    void deleteUser() {
        String userEmail = "test@email.com";
        User user = User.ofNewMember(
                "testUser",
                "test@email.com",
                passwordEncoder.encode("P@ssw0rd"),
                "010-1234-5678",
                new Department("DEP-001", "개발부")
        );

        Mockito.when(userRepository.findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString())).thenReturn(Optional.of(user));

        userService.deleteUser(userEmail);

        Mockito.verify(userRepository, Mockito.times(1)).findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString());

        Assertions.assertNotNull(user.getWithdrawalAt());
    }

    @Test
    @DisplayName("회원 탈퇴 - 존재하지 않는 유저")
    void deleteUser_exception1() {
        Mockito.when(userRepository.findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> userService.deleteUser("test@email.com"));

        Mockito.verify(userRepository, Mockito.times(1)).findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString());
    }
}