package com.nhnacademy.user.controller;

import com.common.AESUtil;
import com.nhnacademy.common.exception.UnauthorizedException;
import com.nhnacademy.user.dto.*;
import com.nhnacademy.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 사용자 관련 요청을 처리하는 REST 컨트롤러입니다.
 * <p>
 * 회원가입, 로그인, 사용자 정보 조회 및 수정, 삭제 기능과
 * 관리자의 사용자 관리 기능을 제공합니다.
 * </p>
 */
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
     * @param userRegisterRequest 사용자 등록 요청 정보
     * @return 201 CREATED 응답
     */
    @PostMapping("auth/signUp")
    public ResponseEntity<Void> signUp(@Validated @RequestBody UserRegisterRequest userRegisterRequest){
        userService.createUser(userRegisterRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 로그인 요청을 처리합니다.
     *
     * @param userLoginRequest 사용자 로그인 요청 정보
     * @return 200 OK 응답
     */
    @PostMapping("/auth/signIn")
    public ResponseEntity<Void> signIn(@Validated @RequestBody UserLoginRequest userLoginRequest){
        userService.loginUser(userLoginRequest);
        return ResponseEntity.ok().build();
    }

    /**
     * 자신의 사용자 정보를 조회합니다.
     *
     * @param encryptedEmail AES로 암호화된 이메일
     * @return 사용자 정보
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyInfo(@RequestHeader("X-User-Id") String encryptedEmail) {
        String email = aesUtil.decrypt(encryptedEmail);
        return ResponseEntity.ok(userService.getUser(email));
    }

    /**
     * 자신의 사용자 정보를 수정합니다.
     *
     * @param encryptedEmail AES로 암호화된 이메일
     * @param request 사용자 수정 요청 정보
     * @return 200 OK 응답
     */
    @PutMapping("/me")
    public ResponseEntity<Void> updateMyInfo(@RequestHeader("X-User-Id") String encryptedEmail,
                                             @Validated @RequestBody UserUpdateRequest request) {
        userService.updateUser(aesUtil.decrypt(encryptedEmail), request);
        return ResponseEntity.ok().build();
    }

    /**
     * 비밀번호를 변경합니다.
     *
     * @param encryptedEmail AES로 암호화된 이메일
     * @param request 비밀번호 변경 요청 정보
     * @return 200 OK 응답
     */
    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(@RequestHeader("X-User-Id") String encryptedEmail,
                                               @Validated @RequestBody ChangePasswordRequest request) {
        userService.changePassword(aesUtil.decrypt(encryptedEmail), request);
        return ResponseEntity.ok().build();
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
        return ResponseEntity.noContent().build();
    }

    /**
     * 관리자 - 전체 사용자 목록을 조회합니다.
     *
     * @param encryptedEmail AES로 암호화된 관리자 이메일
     * @return 전체 사용자 목록
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUser(@RequestHeader("X-User-Id") String encryptedEmail) {
        validateAdmin(aesUtil.decrypt(encryptedEmail));
        return ResponseEntity.ok(userService.getAllUser());
    }

    /**
     * 관리자 - 특정 사용자를 조회합니다.
     *
     * @param encryptedEmail AES로 암호화된 관리자 이메일
     * @param userId 조회할 사용자 ID
     * @return 사용자 정보
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@RequestHeader("X-User-Id") String encryptedEmail,
                                                    @PathVariable String userId) {
        validateAdmin(aesUtil.decrypt(encryptedEmail));
        return ResponseEntity.ok(userService.getUser(userId));
    }

    /**
     * 관리자 - 특정 사용자를 삭제합니다.
     *
     * @param encryptedEmail AES로 암호화된 관리자 이메일
     * @param userId 삭제할 사용자 ID
     * @return 204 No Content 응답
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUserByAdmin(@RequestHeader("X-User-Id") String encryptedEmail,
                                                  @PathVariable String userId) {
        validateAdmin(aesUtil.decrypt(encryptedEmail));
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 요청자의 관리자 권한을 검증합니다.
     *
     * @param email 복호화된 사용자 이메일
     * @throws UnauthorizedException 요청자가 관리자가 아닐 경우 발생
     */
    private void validateAdmin(String email) {
        UserResponse requester = userService.getUser(email);

        if (!"ROLE_ADMIN".equals(requester.getUserRole())) {
            throw new UnauthorizedException("어드민이 아닙니다.");
        }
    }
}
