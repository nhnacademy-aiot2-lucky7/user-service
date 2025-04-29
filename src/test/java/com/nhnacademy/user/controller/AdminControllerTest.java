package com.nhnacademy.user.controller;

import com.common.AESUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.user.dto.UserResponse;
import com.nhnacademy.user.dto.UserRoleUpdateRequest;
import com.nhnacademy.user.service.UserService;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.BeforeEach;
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

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc
class AdminControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private AESUtil aesUtil;

    @BeforeEach
    void setUp() {
        UserResponse adminUserResponse = new UserResponse(
                "ROLE_ADMIN",
                1L,
                "testUser",
                "test@email.com",
                "010-1234-5678",
                "DEP-001"
        );

        when(aesUtil.decrypt(anyString())).thenReturn("admin@email.com");
        when(userService.getUser("admin@email.com")).thenReturn(adminUserResponse);
    }

    @Test
    @DisplayName("모든 사용자 조회 - 200 반환")
    void getAllUser_200() throws Exception {
        List<UserResponse> userResponses = IntStream.range(1, 11)
                .mapToObj(i ->
                        new UserResponse(
                                "ROLE_MEMBER",
                                (long) i,
                                "testUser" + i,
                                "test" + i + "@email.com",
                                "010-1234-567" + i,
                                "DEP-001"
                        ))
                .toList();

        when(userService.getAllUser()).thenReturn(userResponses);

        mockMvc.perform(get("/admin/users")
                        .header("X-User-Id", "encryptEmail")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        verify(userService, times(1)).getAllUser();
    }

    @Test
    @DisplayName("Email 기반 사용자 조회 - 200 반환")
    void getUserById_200() throws Exception {
        UserResponse userResponse = new UserResponse(
                "ROLE_MEMBER",
                1L,
                "testUser",
                "test@email.com",
                "010-1234-5678",
                "DEP-001"
        );

        when(userService.getUser("test@email.com")).thenReturn(userResponse);

        mockMvc.perform(get("/admin/users/test@email.com")
                        .header("X-User-Id", "encryptEmail")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userEmail").value("test@email.com"))
                .andExpect(jsonPath("$.userName").value("testUser"));

        verify(userService, times(2)).getUser(anyString());
    }

    @Test
    @DisplayName("사용자 역할 수정 - 204 반환")
    void updateUserRole_204() throws Exception {
        UserRoleUpdateRequest userRoleUpdateRequest = new UserRoleUpdateRequest(
                "test@email.com",
                "ROLE_OWNER"
        );

        mockMvc.perform(put("/admin/users/roles")
                        .header("X-User-Id", "encryptEmail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userRoleUpdateRequest)))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).updateUserRole(any(UserRoleUpdateRequest.class));
    }

    @Test
    @DisplayName("사용자 역할 수정 - validate 검증으로 인한 400 반환")
    void updateUserRole_400() throws Exception {
        UserRoleUpdateRequest userRoleUpdateRequest = new UserRoleUpdateRequest(
                "testemail.com",
                "ROLE_OWNER"
        );

        mockMvc.perform(put("/admin/users/roles")
                        .header("X-User-Id", "encryptEmail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userRoleUpdateRequest)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).updateUserRole(any(UserRoleUpdateRequest.class));
    }

    @Test
    @DisplayName("특정 사용자 회원 탈퇴 - 204 반환")
    void deleteUserByAdmin_204() throws Exception {
        mockMvc.perform(delete("/admin/users/test@email.com")
                        .header("X-User-Id", "encryptEmail")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(anyString());
    }
}