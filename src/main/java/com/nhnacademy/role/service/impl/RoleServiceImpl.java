package com.nhnacademy.role.service.impl;

import com.nhnacademy.common.exception.ConflictException;
import com.nhnacademy.common.exception.NotFoundException;
import com.nhnacademy.role.domain.Role;
import com.nhnacademy.role.dto.RoleRequest;
import com.nhnacademy.role.dto.RoleResponse;
import com.nhnacademy.role.repository.RoleRepository;
import com.nhnacademy.role.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Transactional(readOnly = true)
    @Override
    public List<RoleResponse> getAllRole() {
        return roleRepository.findAllRole()
                .orElse(List.of());
    }

    @Transactional(readOnly = true)
    @Override
    public RoleResponse getRoleByRoleId(String roleId) {
        return roleRepository.findRoleByRoleId(roleId)
                .orElseThrow(() -> new NotFoundException("RoleId에 해당하는 Role은 존재하지 않습니다."));
    }

    @Override
    public void createRole(RoleRequest roleRequest) {
        if (roleRepository.existsById(roleRequest.getRoleId())) {
            throw new ConflictException("이미 존재하는 RoldId입니다.");
        }

        Role role = new Role(roleRequest.getRoleId(), roleRequest.getRoleName());

        roleRepository.save(role);
    }

    @Override
    public void updateRole(RoleRequest roleRequest) {
        Role role = roleRepository.findById(roleRequest.getRoleId())
                .orElseThrow(() -> new NotFoundException("RoleId에 해당하는 Role은 존재하지 않습니다."));

        role.updateRoleName(roleRequest.getRoleName());

        roleRepository.save(role);
    }

    @Override
    public void deleteRole(String roleId) {
        if (!roleRepository.existsById(roleId)) {
            throw new NotFoundException("존재하지 않는 RoldId입니다.");
        }

        roleRepository.deleteById(roleId);
    }
}
