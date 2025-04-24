package com.nhnacademy.user.controller;

import com.nhnacademy.user.dto.UserResponse;
import com.nhnacademy.user.dto.UserRoleUpdateRequest;
import com.nhnacademy.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AdminAuthorizationFilter에서 url이 admin/으로 시작할 경우 해당 기능을 수행하는 클라이언트가 admin인지 검증을 합니다.
 */
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Slf4j
public class AdminController {
    private final UserService userService;

    /**
     * 모든 사용자 정보를 조회합니다.
     *
     * @return 사용자 목록
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUser() {
        return ResponseEntity
                .ok(userService.getAllUser());
    }

    /**
     * 특정 사용자 정보를 조회합니다.
     *
     * @param userId 조회할 사용자 ID
     * @return 특정 사용자 정보
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String userId) {
        return ResponseEntity
                .ok(userService.getUser(userId));
    }

    /**
     * 관리자가 사용자의 역할을 업데이트합니다.
     * <p>
     * 관리자 권한으로 사용자 역할을 변경하는 기능을 제공합니다.
     * </p>
     *
     * @param roleUpdateRequest 역할 업데이트 요청 정보
     * @return 204 No Content 응답
     */
    @PutMapping("/roles")
    public ResponseEntity<Void> updateUserRole(UserRoleUpdateRequest roleUpdateRequest) {
        userService.updateUserRole(roleUpdateRequest);

        return ResponseEntity
                .noContent()
                .build();
    }

    /**
     * 관리자가 특정 사용자를 삭제합니다.
     *
     * @param userId 삭제할 사용자 ID
     * @return 204 No Content 응답
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUserByAdmin(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity
                .noContent()
                .build();
    }
}

