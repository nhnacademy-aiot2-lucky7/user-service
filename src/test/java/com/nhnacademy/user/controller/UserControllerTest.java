package com.nhnacademy.user.controller;

import com.common.AESUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.user.dto.*;
import com.nhnacademy.user.service.UserService;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AESUtil aesUtil;

    @Test
    @DisplayName("회원가입 요청 - 201 반환")
    void signUp_201() throws Exception {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest(
                "testUser",
                "test@email.com",
                "P@ss0rd",
                "010-1234-5678",
                "DEP-001"
        );

        mockMvc.perform(post("/users/auth/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userRegisterRequest)))
                .andExpect(status().isCreated());

        verify(userService, times(1)).createUser(any(UserRegisterRequest.class));
    }

    @Test
    @DisplayName("회원가입 요청 - validate 검증으로 인한 400 반환")
    void signUp_400() throws Exception {
        UserRegisterRequest request = new UserRegisterRequest(
                "testUser",
                "test@email.com",
                "Pass0rd",
                "010-1234-5678",
                "DEP-001"
        );

        mockMvc.perform(post("/users/auth/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(UserRegisterRequest.class));
    }

    @Test
    @DisplayName("로그인 요청 - 200 반환")
    void signIn_200() throws Exception {
        UserLoginRequest userLoginRequest = new UserLoginRequest(
                "test@email.com",
                "P@ssw0rd"
        );

        mockMvc.perform(post("/users/auth/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userLoginRequest)))
                .andExpect(status().isOk());

        verify(userService, times(1)).loginUser(any(UserLoginRequest.class));
    }

    @Test
    @DisplayName("로그인 요청 - validate 검증으로 인한 400 반환")
    void signIn_400() throws Exception {
        UserLoginRequest userLoginRequest = new UserLoginRequest(
                "test@email.com",
                "Passw0rd"
        );

        mockMvc.perform(post("/users/auth/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userLoginRequest)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).loginUser(any(UserLoginRequest.class));
    }

    @Test
    @DisplayName("사용자 정보 조회 - 200 반환")
    void getMyInfo_200() throws Exception {
        UserResponse userResponse = new UserResponse(
                "ROLE_MEMBER",
                1L,
                "testUser",
                "test@email.com",
                "010-1234-5678",
                "DEP-001"
        );

        when(aesUtil.decrypt(anyString())).thenReturn("test@eamil.com");
        when(userService.getUser(anyString())).thenReturn(userResponse);

        mockMvc.perform(get("/users/me")
                        .header("X-User-Id", "test@email.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userEmail").value("test@email.com"))
                .andExpect(jsonPath("$.userName").value("testUser"));

        verify(userService, times(1)).getUser(anyString());
    }

    @Test
    @DisplayName("사용자 정보 수정 - 204 반환")
    void updateMyInfo_204() throws Exception {
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest(
                "testUser",
                "010-1234-5678",
                "DEP-001"
        );

        when(aesUtil.decrypt(anyString())).thenReturn("test@eamil.com");

        mockMvc.perform(put("/users/me")
                        .header("X-User-Id", "test@email.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userUpdateRequest)))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).updateUser(anyString(), any(UserUpdateRequest.class));
    }

    @Test
    @DisplayName("사용자 정보 수정 - validate 검증으로 인한 400 반환")
    void updateMyInfo_400() throws Exception {
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest(
                "testUser",
                "0101234-5678",
                "DEP-001"
        );

        when(aesUtil.decrypt(anyString())).thenReturn("test@eamil.com");

        mockMvc.perform(put("/users/me")
                        .header("X-User-Id", "test@email.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userUpdateRequest)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).updateUser(anyString(), any(UserUpdateRequest.class));
    }

    @Test
    @DisplayName("사용자 비밀번호 수정 - 204 반환")
    void changePassword_204() throws Exception {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(
                "wrongP@ssw0rd",
                "newP@ssw0rd",
                "newP@ssw0rd"
        );

        when(aesUtil.decrypt(anyString())).thenReturn("test@eamil.com");

        mockMvc.perform(put("/users/me/password")
                        .header("X-User-Id", "test@email.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(changePasswordRequest)))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).changePassword(anyString(), any(ChangePasswordRequest.class));
    }

    @Test
    @DisplayName("사용자 비밀번호 수정 - validate 검증으로 인한 400 반환")
    void changePassword_400_case1() throws Exception {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(
                "wrongPassw0rd",
                "newP@ssw0rd",
                "newP@ssw0rd"
        );

        when(aesUtil.decrypt(anyString())).thenReturn("test@eamil.com");

        mockMvc.perform(put("/users/me/password")
                        .header("X-User-Id", "test@email.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(changePasswordRequest)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).changePassword(anyString(), any(ChangePasswordRequest.class));
    }

    @Test
    @DisplayName("사용자 비밀번호 수정 - 확인 비밀번호 불일치로 인한 400 반환")
    void changePassword_400_case2() throws Exception {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(
                "wrongP@ssw0rd",
                "newP@ssw0rd",
                "newPassw0rd"
        );

        when(aesUtil.decrypt(anyString())).thenReturn("test@eamil.com");

        mockMvc.perform(put("/users/me/password")
                        .header("X-User-Id", "test@email.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(changePasswordRequest)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).changePassword(anyString(), any(ChangePasswordRequest.class));
    }

    @Test
    @DisplayName("회원 탈퇴 - 204 반환")
    void deleteMyAccount_204() throws Exception {
        when(aesUtil.decrypt(anyString())).thenReturn("test@email.com");

        mockMvc.perform(delete("/users/me")
                        .header("X-User-Id", "test@email.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(anyString());
    }
}
