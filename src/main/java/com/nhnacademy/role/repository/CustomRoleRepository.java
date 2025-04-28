package com.nhnacademy.role.repository;

import com.nhnacademy.role.dto.RoleResponse;

import java.util.List;
import java.util.Optional;

public interface CustomRoleRepository {
    Optional<List<RoleResponse>> findAllRole();

    Optional<RoleResponse> findRoleByRoleId(String roleId);
}
