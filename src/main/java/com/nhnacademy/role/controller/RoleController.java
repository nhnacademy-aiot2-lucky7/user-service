package com.nhnacademy.role.controller;

import com.nhnacademy.role.dto.RoleRequest;
import com.nhnacademy.role.dto.RoleResponse;
import com.nhnacademy.role.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<List<RoleResponse>> getAllRole() {

        return ResponseEntity
                .ok(roleService.getAllRole());
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<RoleResponse> getRoleByRoleId(@PathVariable String roleId) {

        return ResponseEntity
                .ok(roleService.getRoleByRoleId(roleId));
    }

    @PostMapping
    public ResponseEntity<Void> createRole(@Validated @RequestBody RoleRequest roleRequest) {
        roleService.createRole(roleRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @PutMapping
    public ResponseEntity<Void> updateRole(@Validated @RequestBody RoleRequest roleRequest) {
        roleService.updateRole(roleRequest);

        return ResponseEntity
                .noContent()
                .build();
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<Void> deleteRoleByRoleId(@PathVariable String roleId) {
        roleService.deleteRole(roleId);

        return ResponseEntity
                .noContent()
                .build();
    }
}
