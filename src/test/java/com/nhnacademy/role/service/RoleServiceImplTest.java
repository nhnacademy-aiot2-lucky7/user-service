package com.nhnacademy.role.service;

import com.nhnacademy.common.exception.ConflictException;
import com.nhnacademy.common.exception.NotFoundException;
import com.nhnacademy.role.domain.Role;
import com.nhnacademy.role.dto.RoleRequest;
import com.nhnacademy.role.dto.RoleResponse;
import com.nhnacademy.role.repository.RoleRepository;
import com.nhnacademy.role.service.impl.RoleServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class RoleServiceImplTest {
    @Mock
    RoleRepository roleRepository;

    @InjectMocks
    RoleServiceImpl roleService;

    @Test
    @DisplayName("모든 권한 조회")
    void getAllRole() {
        List<RoleResponse> roleResponses = IntStream.range(1, 11)
                .mapToObj(i ->
                        new RoleResponse(
                                "roleId" + i,
                                "roleName" + i
                        ))
                .toList();

        when(roleRepository.findAllRole()).thenReturn(Optional.of(roleResponses));

        List<RoleResponse> roleResponses1 = roleService.getAllRole();

        verify(roleRepository, times(1)).findAllRole();

        Assertions.assertEquals(10, roleResponses1.size());
    }

    @Test
    @DisplayName("RoleId에 따른 권한 조회")
    void getRoleByRoleId() {
        RoleResponse roleResponse = new RoleResponse("ROLE_MEMBER", "일반 회원");

        when(roleRepository.findRoleByRoleId(anyString())).thenReturn(Optional.of(roleResponse));

        RoleResponse findRoleResponse = roleService.getRoleByRoleId("ROLE_MEMBER");

        verify(roleRepository, times(1)).findRoleByRoleId(anyString());

        Assertions.assertEquals("ROLE_MEMBER", findRoleResponse.getRoleId());
        Assertions.assertEquals("일반 회원", findRoleResponse.getRoleName());
    }

    @Test
    @DisplayName("RoleId에 따른 권한 조회 - 존재하지 않는 권한ID")
    void getRoleByRoleId_exception1() {
        when(roleRepository.findRoleByRoleId(anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> roleService.getRoleByRoleId("ROLE_MEMBER"));

        verify(roleRepository, times(1)).findRoleByRoleId(anyString());
    }

    @Test
    @DisplayName("권한 생성")
    void createRole() {
        when(roleRepository.existsById(anyString())).thenReturn(false);

        roleService.createRole(new RoleRequest("ROLE_MEMBER", "일반 회원"));

        verify(roleRepository, times(1)).existsById(anyString());
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    @DisplayName("권한 생성 - 이미 존재하는 RoleId")
    void createRole_exception1() {
        when(roleRepository.existsById(anyString())).thenReturn(true);

        Assertions.assertThrows(ConflictException.class, () -> roleService.createRole(new RoleRequest("ROLE_MEMBER", "일반 회원")));

        verify(roleRepository, times(1)).existsById(anyString());
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    @DisplayName("RoleName 수정")
    void updateRole() {
        Role role = new Role("ROLE_MEMBER", "회원");

        when(roleRepository.findById(anyString())).thenReturn(Optional.of(role));

        roleService.updateRole(new RoleRequest("ROLE_MEMBER", "회원"));

        verify(roleRepository, times(1)).findById(anyString());
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    @DisplayName("RoleName 수정 - 존재하지 않는 RoleId")
    void updateRole_exception1() {
        when(roleRepository.findById(anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> roleService.updateRole(new RoleRequest("ROLE_MEMBER", "회원")));

        verify(roleRepository, times(1)).findById(anyString());
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    @DisplayName("권한 삭제")
    void deleteRole() {
        when(roleRepository.existsById(anyString())).thenReturn(true);

        roleService.deleteRole("ROLE_MEMBER");

        verify(roleRepository, times(1)).existsById(anyString());
        verify(roleRepository, times(1)).deleteById(anyString());
    }

    @Test
    @DisplayName("권한 삭제 - 존재하지 않는 RoleId")
    void deleteRole_exception1() {
        when(roleRepository.existsById(anyString())).thenReturn(false);


        Assertions.assertThrows(NotFoundException.class, () -> roleService.deleteRole("ROLE_MEMBER"));

        verify(roleRepository, times(1)).existsById(anyString());
        verify(roleRepository, never()).deleteById(anyString());
    }
}
