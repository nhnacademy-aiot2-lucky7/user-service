package com.nhnacademy.controller;

import com.nhnacademy.user.dto.ChangePasswordRequest;
import com.nhnacademy.user.dto.UserLoginRequest;
import com.nhnacademy.user.dto.UserRegisterRequest;
import com.nhnacademy.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자 관련 요청을 처리하는 REST 컨트롤러입니다.
 * 회원가입, 로그인, 사용자 조회 기능을 제공합니다.
 */

@RestController
@RequestMapping(value = {"/users"})
@Slf4j
public class UserController {

    /**
     * 사용자 서비스 객체입니다. 사용자 생성, 조회, 로그인 등의 로직을 처리합니다.
     */
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 회원가입 요청을 처리합니다.
     *
     * @param userRegisterRequest 회원가입 요청 정보 (이름, 이메일, 비밀번호 등)
     * @return 생성된 사용자 정보를 포함한 응답 (HTTP 201 Created)
     */
    @PostMapping("/signUp")
    public ResponseEntity<Void> createAction(@Validated @RequestBody UserRegisterRequest userRegisterRequest){
        userService.createUser(userRegisterRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    /**
     * 로그인 요청을 처리합니다.
     *
     * @param userLoginRequest 로그인 요청 정보 (이메일, 비밀번호 등)
     * @return 로그인된 사용자 정보 응답 (HTTP 200 OK)
     */
    @PostMapping("/signIn")
    public ResponseEntity<Void> loginAction(@Validated @RequestBody UserLoginRequest userLoginRequest){
        userService.loginUser(userLoginRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @PutMapping("/changePassword")
    public ResponseEntity<Void> changePassword(@RequestHeader("X-User-Id") String userEmail, @Validated @RequestBody ChangePasswordRequest changePasswordRequest) {
        userService.changePassword(userEmail, changePasswordRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}
