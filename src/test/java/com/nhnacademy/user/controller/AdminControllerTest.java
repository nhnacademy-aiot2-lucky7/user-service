package com.nhnacademy.user.controller;

import com.common.AESUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.department.dto.DepartmentRequest;
import com.nhnacademy.department.dto.DepartmentResponse;
import com.nhnacademy.department.service.DepartmentService;
import com.nhnacademy.eventlevel.dto.EventLevelRequest;
import com.nhnacademy.eventlevel.dto.EventLevelResponse;
import com.nhnacademy.eventlevel.service.EventLevelService;
import com.nhnacademy.role.dto.RoleRequest;
import com.nhnacademy.role.service.RoleService;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private EventLevelService eventLevelService;
    @MockitoBean
    private DepartmentService departmentService;
    @MockitoBean
    private RoleService roleService;
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
                new DepartmentResponse("DEP-001", "개발부"),
                new EventLevelResponse("error", "에러", 4)
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
                                new DepartmentResponse("DEP-001", "개발부"),
                                new EventLevelResponse("error", "에러", 4)
                        ))
                .toList();

        Pageable pageable = PageRequest.of(0, 10);
        when(userService.getAllUser(pageable)).thenReturn(userResponses);

        mockMvc.perform(get("/admin/users")
                        .header("X-User-Id", "encryptEmail")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        verify(userService, times(1)).getAllUser(any(Pageable.class));
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
                new DepartmentResponse("DEP-001", "개발부"),
                new EventLevelResponse("error", "에러", 4)
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

    @Test
    @DisplayName("이벤트 레벨 생성 - 201 반환")
    void createEventLevel_201() throws Exception {
        EventLevelRequest request = new EventLevelRequest("INFO", "정보성 메시지", 1);

        mockMvc.perform(post("/admin/event-levels")
                        .header("X-User-Id", "encryptEmail")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(eventLevelService, times(1)).createEventLevel(any(EventLevelRequest.class));
    }

    @Test
    @DisplayName("이벤트 레벨 수정 - 204 반환")
    void updateEventLevel_204() throws Exception {
        EventLevelRequest request = new EventLevelRequest("INFO", "수정된 정보 메시지", 1);

        mockMvc.perform(put("/admin/event-levels")
                        .header("X-User-Id", "encryptEmail")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(eventLevelService, times(1)).updateEventLevel(any(EventLevelRequest.class));
    }

    @Test
    @DisplayName("이벤트 레벨 삭제 - 204 반환")
    void deleteEventLevel_204() throws Exception {

        mockMvc.perform(delete("/admin/event-levels/WARNING")
                        .header("X-User-Id", "encryptEmail")
                        .accept(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(eventLevelService, times(1)).deleteEventLevel("WARNING");
    }

    @Test
    @DisplayName("부서 생성 - 201 반환")
    void createDepartment_201() throws Exception {
        DepartmentRequest departmentRequest = new DepartmentRequest("DEPT001", "인사부");

        mockMvc.perform(post("/admin/departments")
                        .header("X-User-Id", "encryptEmail")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(departmentRequest)))
                .andExpect(status().isCreated());

        verify(departmentService, times(1)).createDepartment(any(DepartmentRequest.class));
    }

    @Test
    @DisplayName("부서 수정 - 204 반환")
    void updateDepartment_204() throws Exception {
        DepartmentRequest departmentRequest = new DepartmentRequest("DEPT001", "총무부");

        mockMvc.perform(put("/admin/departments")
                        .header("X-User-Id", "encryptEmail")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(departmentRequest)))
                .andExpect(status().isNoContent());

        verify(departmentService, times(1)).updateDepartment(any(DepartmentRequest.class));
    }

    @Test
    @DisplayName("부서 삭제 - 204 반환")
    void deleteDepartment_204() throws Exception {

        mockMvc.perform(delete("/admin/departments/DEPT001")
                        .header("X-User-Id", "encryptEmail")
                        .accept(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(departmentService, times(1)).deleteDepartment(anyString());
    }

    @Test
    @DisplayName("권한 생성 - 201 반환")
    void createRole_201() throws Exception {
        RoleRequest roleRequest = new RoleRequest("ROLE_MEMBER", "일반 회원");

        mockMvc.perform(post("/admin/roles")
                        .header("X-User-Id", "encryptEmail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(roleRequest)))
                .andExpect(status().isCreated());

        verify(roleService, times(1)).createRole(any(RoleRequest.class));
    }

    @Test
    @DisplayName("권한 수정 - 204 반환")
    void updateRole_204() throws Exception {
        RoleRequest roleRequest = new RoleRequest("ROLE_MEMBER", "일반 회원");

        mockMvc.perform(put("/admin/roles")
                        .header("X-User-Id", "encryptEmail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(roleRequest)))
                .andExpect(status().isNoContent());

        verify(roleService, times(1)).updateRole(any(RoleRequest.class));
    }

    @Test
    @DisplayName("권한 삭제 - 204 반환")
    void deleteRole_204() throws Exception {

        mockMvc.perform(delete("/admin/roles/ROLE_MEMBER")
                        .header("X-User-Id", "encryptEmail")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(roleService, times(1)).deleteRole(anyString());
    }
}