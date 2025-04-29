package com.nhnacademy.role.service;

import com.nhnacademy.role.dto.RoleRequest;
import com.nhnacademy.role.dto.RoleResponse;

import java.util.List;

public interface RoleService {
    List<RoleResponse> getAllRole();

    RoleResponse getRoleByRoleId(String roleId);

    void createRole(RoleRequest roleRequest);

    void updateRole(RoleRequest roleRequest);

    void deleteRole(String roleId);
}
