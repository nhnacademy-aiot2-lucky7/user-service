package com.nhnacademy.user.controller;

import com.common.AESUtil;
import com.nhnacademy.common.exception.BadRequestException;
import com.nhnacademy.user.dto.*;
import com.nhnacademy.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final AESUtil aesUtil;
    private final UserService userService;

    /**
     * 회원가입 요청을 처리합니다.
     *
     * @param userRegisterRequest 사용자 등록 정보 (이메일, 비밀번호 등)
     * @return 201 CREATED 응답
     */
    @PostMapping("/auth/signUp")
    public ResponseEntity<Void> signUp(@Validated @RequestBody UserRegisterRequest userRegisterRequest) {
        boolean isSocialed = false;

        userService.createUser(userRegisterRequest, isSocialed);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @PostMapping("/auth/social/signUp")
    public ResponseEntity<Void> socialSignUp(@Validated @RequestBody SocialUserRegisterRequest socialUserRegisterRequest) {
        boolean isSocialed = true;

        UserRegisterRequest userRegisterRequest = new UserRegisterRequest(
                socialUserRegisterRequest.getUserName(),
                socialUserRegisterRequest.getUserEmail(),
                socialUserRegisterRequest.getUserPassword(),
                socialUserRegisterRequest.getUserPhone(),
                socialUserRegisterRequest.getUserDepartment()
        );

        userService.createUser(userRegisterRequest, isSocialed);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    /**
     * 로그인 요청을 처리합니다.
     *
     * @param userLoginRequest 사용자 로그인 정보 (이메일, 비밀번호 등)
     * @return 200 OK 응답
     */
    @PostMapping("/auth/signIn")
    public ResponseEntity<Void> signIn(@Validated @RequestBody UserLoginRequest userLoginRequest) {
        userService.loginUser(userLoginRequest);

        return ResponseEntity
                .ok()
                .build();
    }

    @GetMapping("/{userEmail}")
    public Boolean existsByEmail(@PathVariable String userEmail) {
        return userService.existsByUserEmail(userEmail);
    }

    /**
     * 자신의 사용자 정보를 조회합니다.
     *
     * @param encryptedEmail AES로 암호화된 이메일
     * @return 현재 사용자 정보 (이메일, 이름 등)
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyInfo(@RequestHeader("X-User-Id") String encryptedEmail) {
        String email = aesUtil.decrypt(encryptedEmail);

        return ResponseEntity
                .ok(userService.getUser(email));
    }

    /**
     * 자신의 사용자 정보를 수정합니다.
     *
     * @param encryptedEmail    AES로 암호화된 이메일
     * @param userUpdateRequest 사용자 수정 정보 (이름, 주소 등)
     * @return 204 No Content 응답
     */
    @PutMapping("/me")
    public ResponseEntity<Void> updateMyInfo(@RequestHeader("X-User-Id") String encryptedEmail,
                                             @Validated @RequestBody UserUpdateRequest userUpdateRequest) {
        userService.updateUser(aesUtil.decrypt(encryptedEmail), userUpdateRequest);

        return ResponseEntity
                .noContent()
                .build();
    }

    /**
     * 비밀번호를 변경합니다.
     *
     * @param encryptedEmail        AES로 암호화된 이메일
     * @param changePasswordRequest 비밀번호 변경 요청 정보 (현재 비밀번호, 새 비밀번호)
     * @return 204 No Content 응답
     */
    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(@RequestHeader("X-User-Id") String encryptedEmail,
                                               @Validated @RequestBody ChangePasswordRequest changePasswordRequest) {
        if (!changePasswordRequest.isPasswordConfirmed()) {
            throw new BadRequestException("확인 패스워드 불일치");
        }

        userService.changePassword(aesUtil.decrypt(encryptedEmail), changePasswordRequest);

        return ResponseEntity
                .noContent()
                .build();
    }

    /**
     * 자신의 계정을 삭제합니다.
     *
     * @param encryptedEmail AES로 암호화된 이메일
     * @return 204 No Content 응답
     */
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyAccount(@RequestHeader("X-User-Id") String encryptedEmail) {
        userService.deleteUser(aesUtil.decrypt(encryptedEmail));

        return ResponseEntity
                .noContent()
                .build();
    }
}
