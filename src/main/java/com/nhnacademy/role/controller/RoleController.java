package com.nhnacademy.role.controller;

import com.nhnacademy.role.dto.RoleResponse;
import com.nhnacademy.role.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
