package com.nhnacademy.user.service.impl;

import com.nhnacademy.common.exception.NotFoundException;
import com.nhnacademy.common.exception.UnauthorizedException;
import com.nhnacademy.user.domain.User;
import com.nhnacademy.user.dto.UserLoginRequest;
import com.nhnacademy.user.dto.UserRegisterRequest;
import com.nhnacademy.user.dto.UserResponse;
import com.nhnacademy.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.Field;
import java.util.Optional;


@Slf4j
@ExtendWith(SpringExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    private User testUser;

    @Spy
    BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        testUser = User.ofNewMember(
                "user1",
                "user1@email.com",
                "user12345?",
                "010-1234-5678",
                "인사과"
        );
    }

    @Test
    @DisplayName("회원가입 - user 등록")
    void createUser() {

        UserRegisterRequest registerUserRequest = new UserRegisterRequest(
                testUser.getUserName(),
                testUser.getUserEmail(),
                testUser.getUserPassword(),
                testUser.getUserPhone(),
                testUser.getUserDepartment()
        );

        UserResponse fakeResponse = new UserResponse(
                User.Role.MEMBER,
                1l,
                "user1",
                "user1@email.com",
                "010-1234-5678",
                "인사과"
        );

        Mockito.when(userRepository.existsByUserEmail(Mockito.anyString())).thenReturn(false);
        Mockito.when(userRepository.findUserResponseByUserNo(Mockito.anyLong()))
                .thenReturn(Optional.of(fakeResponse));
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(testUser));

        // userRepository.save를 호출하면 userNo가 생성됩니다.
        Mockito.doAnswer(invocation -> {
            User paramUser = invocation.getArgument(0);
            Field field = User.class.getDeclaredField("userNo");
            field.setAccessible(true);
            field.set(paramUser, 1L);
            log.debug("paramUser: {}", paramUser);
            return null;
        }).when(userRepository).save(Mockito.any(User.class));

        userService.createUser(registerUserRequest);
    }

    @Test
    @DisplayName("user조회 - 성공")
    void getUser_success() {
        UserResponse fakeResponse = new UserResponse(
                User.Role.MEMBER,
                1l,
                "user1",
                "user1@email.com",
                "010-1234-5678",
                "인사과"
        );
        Mockito.when(userRepository.findUserResponseByUserEmail(Mockito.anyString())).thenReturn(Optional.of(fakeResponse));

        UserResponse userResponse = userService.getUser("user1@email.com");

        Mockito.verify(userRepository, Mockito.times(1)).findUserResponseByUserEmail(Mockito.anyString());
        Assertions.assertAll(
                () -> {
                    Assertions.assertNotNull(userResponse.getUserNo());
                    Assertions.assertEquals(User.Role.MEMBER, userResponse.getUserRole());
                    Assertions.assertEquals("user1", userResponse.getUserName());
                    Assertions.assertEquals("user1@email.com", userResponse.getUserEmail());
                }
        );

    }

    @Test
    @DisplayName("user조회 - 실패")
    void getUser_fail() {

        // 빈 Optional을 주면 에러 발생
        Mockito.when(userRepository.findUserResponseByUserNo(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        // 존재하지 않는 userNo 호출시 에러 발생
        Assertions.assertThrows(NotFoundException.class, () -> {
            userService.getUser("wrong@email.com");
        });

        Mockito.verify(userRepository, Mockito.times(1)).findUserResponseByUserEmail(Mockito.anyString());
    }

    @Test
    @DisplayName("로그인 성공")
    void loginUser_success() {
        User fakeUser = User.ofNewMember(
                "testUser",
                "user1@email.com",
                passwordEncoder.encode("user12345?"),
                "010-1234-5678",
                "인사과"
        );
        UserLoginRequest loginRequest = new UserLoginRequest(
                "user1@email.com",
                "user12345?"
        );

        Mockito.when(userRepository.findByUserEmail(Mockito.anyString())).thenReturn(Optional.of(fakeUser));
        userService.loginUser(loginRequest);

        Mockito.verify(userRepository, Mockito.times(1)).findByUserEmail(Mockito.anyString());
    }

    @Test
    @DisplayName("로그인 실패 - 이메일에 맞는 유저가 없음")
    void loginUser_exception1() {

        UserLoginRequest loginRequest = new UserLoginRequest(
                "",
                "user12345?"
        );

        Mockito.when(userRepository.findUserResponseByUserEmail(Mockito.anyString()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> {
            userService.loginUser(loginRequest);
        });

        Mockito.verify(userRepository, Mockito.never()).findUserResponseByUserEmail(Mockito.anyString());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void loginUser_exception2() {
        User fakeUser = User.ofNewMember(
                "testUser",
                "user1@email.com",
                passwordEncoder.encode("testUser12345?"),
                "010-1234-5678",
                "인사과"
        );
        UserLoginRequest loginRequest = new UserLoginRequest(
                "user1@email.com",
                "user12345?"
        );

        Mockito.when(userRepository.findByUserEmail(Mockito.anyString())).thenReturn(Optional.of(fakeUser));
        Assertions.assertThrows(UnauthorizedException.class, () -> userService.loginUser(loginRequest));

        Mockito.verify(userRepository, Mockito.times(1)).findByUserEmail(Mockito.anyString());
    }
}