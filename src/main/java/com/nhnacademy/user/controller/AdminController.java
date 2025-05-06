package com.nhnacademy.user.controller;

import com.nhnacademy.department.dto.DepartmentRequest;
import com.nhnacademy.department.service.DepartmentService;
import com.nhnacademy.eventlevel.dto.EventLevelRequest;
import com.nhnacademy.eventlevel.service.EventLevelService;
import com.nhnacademy.role.dto.RoleRequest;
import com.nhnacademy.role.service.RoleService;
import com.nhnacademy.user.dto.UserResponse;
import com.nhnacademy.user.dto.UserRoleUpdateRequest;
import com.nhnacademy.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AdminAuthorizationFilter에서 url이 admin/으로 시작할 경우 해당 기능을 수행하는 클라이언트가 admin인지 검증을 합니다.
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {
    private final UserService userService;
    private final EventLevelService eventLevelService;
    private final DepartmentService departmentService;
    private final RoleService roleService;

    /**
     * 모든 사용자 정보를 조회합니다.
     *
     * @return 사용자 목록
     */
    @GetMapping("/users")
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
    @GetMapping("/users/{userId}")
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
    @PutMapping("/users/roles")
    public ResponseEntity<Void> updateUserRole(@Validated @RequestBody UserRoleUpdateRequest roleUpdateRequest) {
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
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUserByAdmin(@PathVariable String userId) {
        userService.deleteUser(userId);

        return ResponseEntity
                .noContent()
                .build();
    }

    // EventLevel
    @PostMapping("/event-levels")
    public ResponseEntity<Void> createEventLevel(@Validated @RequestBody EventLevelRequest eventLevelRequest) {
        eventLevelService.createEventLevel(eventLevelRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @PutMapping("/event-levels")
    public ResponseEntity<Void> updateEventLevel(@Validated @RequestBody EventLevelRequest eventLevelRequest) {
        eventLevelService.updateEventLevel(eventLevelRequest);

        return ResponseEntity
                .noContent()
                .build();
    }

    @DeleteMapping("/event-levels/{levelName}")
    public ResponseEntity<Void> deleteEventLevel(@PathVariable String levelName) {
        eventLevelService.deleteEventLevel(levelName);

        return ResponseEntity
                .noContent()
                .build();
    }

    // Department
    @PostMapping("/departments")
    public ResponseEntity<Void> createDepartment(@Validated @RequestBody DepartmentRequest departmentRequest) {
        departmentService.createDepartment(departmentRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @PutMapping("/departments")
    public ResponseEntity<Void> updateDepartment(@Validated @RequestBody DepartmentRequest departmentRequest) {
        departmentService.updateDepartment(departmentRequest);

        return ResponseEntity
                .noContent()
                .build();
    }

    @DeleteMapping("/departments/{departmentId}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable String departmentId) {
        departmentService.deleteDepartment(departmentId);

        return ResponseEntity
                .noContent()
                .build();
    }

    // Role
    @PostMapping("/roles")
    public ResponseEntity<Void> createRole(@Validated @RequestBody RoleRequest roleRequest) {
        roleService.createRole(roleRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @PutMapping("/roles")
    public ResponseEntity<Void> updateRole(@Validated @RequestBody RoleRequest roleRequest) {
        roleService.updateRole(roleRequest);

        return ResponseEntity
                .noContent()
                .build();
    }

    @DeleteMapping("/roles/{roleId}")
    public ResponseEntity<Void> deleteRoleByRoleId(@PathVariable String roleId) {
        roleService.deleteRole(roleId);

        return ResponseEntity
                .noContent()
                .build();
    }
}

