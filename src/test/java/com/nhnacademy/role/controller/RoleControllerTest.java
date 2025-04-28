package com.nhnacademy.role.controller;

import com.common.AESUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.role.dto.RoleRequest;
import com.nhnacademy.role.dto.RoleResponse;
import com.nhnacademy.role.service.RoleService;
import com.nhnacademy.user.service.UserService;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.IntStream;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoleController.class)
@AutoConfigureMockMvc
class RoleControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoleService roleService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AESUtil aesUtil;

    @Test
    @DisplayName("모든 권한 조회 - 200 반환")
    void getAllRole_200() throws Exception {
        List<RoleResponse> roleResponses = IntStream.range(1, 11)
                .mapToObj(i ->
                        new RoleResponse(
                                "roleId" + i,
                                "roleName" + i
                        ))
                .toList();

        when(roleService.getAllRole()).thenReturn(roleResponses);

        mockMvc.perform(get("/users/roles")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        verify(roleService, times(1)).getAllRole();
    }

    @Test
    @DisplayName("권한ID에 따른 조회 - 200 반환")
    void getRoleByRoleId_200() throws Exception {
        RoleResponse roleResponse = new RoleResponse("ROLE_MEMBER", "일반 회원");

        when(roleService.getRoleByRoleId(anyString())).thenReturn(roleResponse);

        mockMvc.perform(get("/users/roles/ROLE_MEMBER")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roleId").value("ROLE_MEMBER"))
                .andExpect(jsonPath("$.roleName").value("일반 회원"));

        verify(roleService, times(1)).getRoleByRoleId(anyString());
    }

    @Test
    @DisplayName("권한 생성 - 201 반환")
    void createRole_201() throws Exception {
        RoleRequest roleRequest = new RoleRequest("ROLE_MEMBER", "일반 회원");

        mockMvc.perform(post("/users/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(roleRequest)))
                .andExpect(status().isCreated());

        verify(roleService, times(1)).createRole(any(RoleRequest.class));
    }

    @Test
    @DisplayName("권한 수정 - 204 반환")
    void updateRole_204() throws Exception {
        RoleRequest roleRequest = new RoleRequest("ROLE_MEMBER", "일반 회원");

        mockMvc.perform(put("/users/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(roleRequest)))
                .andExpect(status().isNoContent());

        verify(roleService, times(1)).updateRole(any(RoleRequest.class));
    }

    @Test
    @DisplayName("권한 삭제 - 204 반환")
    void deleteRole_204() throws Exception {

        mockMvc.perform(delete("/users/roles/ROLE_MEMBER")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(roleService, times(1)).deleteRole(anyString());
    }
}
