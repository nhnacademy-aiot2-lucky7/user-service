package com.nhnacademy.department.controller;

import com.common.AESUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.department.dto.DepartmentRequest;
import com.nhnacademy.department.dto.DepartmentResponse;
import com.nhnacademy.department.service.DepartmentService;
import com.nhnacademy.user.service.UserService;
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

@WebMvcTest(DepartmentController.class)
@AutoConfigureMockMvc
class DepartmentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DepartmentService departmentService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AESUtil aesUtil;

    @Test
    @DisplayName("모든 부서 조회 - 200 반환")
    void getAllDepartment_200() throws Exception {
        List<DepartmentResponse> departmentResponses = IntStream.range(1, 11)
                .mapToObj(i ->
                        new DepartmentResponse(
                                "departmentId" + i,
                                "departmentName" + i
                        ))
                .toList();

        when(departmentService.getAllDepartment()).thenReturn(departmentResponses);

        mockMvc.perform(get("/users/departments")
                        .accept(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        verify(departmentService, times(1)).getAllDepartment();
    }

    @Test
    @DisplayName("부서ID에 따른 조회 - 200 반환")
    void getDepartmentByDepartmentId_200() throws Exception {
        DepartmentResponse departmentResponse = new DepartmentResponse("DEPT001", "인사부");

        when(departmentService.getDepartmentByDepartmentId(anyString())).thenReturn(departmentResponse);

        mockMvc.perform(get("/users/departments/DEPT001")
                        .accept(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.departmentId").value("DEPT001"))
                .andExpect(jsonPath("$.departmentName").value("인사부"));

        verify(departmentService, times(1)).getDepartmentByDepartmentId(anyString());
    }

    @Test
    @DisplayName("부서 생성 - 201 반환")
    void createDepartment_201() throws Exception {
        DepartmentRequest departmentRequest = new DepartmentRequest("DEPT001", "인사부");

        mockMvc.perform(post("/users/departments")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(departmentRequest)))
                .andExpect(status().isCreated());

        verify(departmentService, times(1)).createDepartment(any(DepartmentRequest.class));
    }

    @Test
    @DisplayName("부서 수정 - 204 반환")
    void updateDepartment_204() throws Exception {
        DepartmentRequest departmentRequest = new DepartmentRequest("DEPT001", "총무부");

        mockMvc.perform(put("/users/departments")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(departmentRequest)))
                .andExpect(status().isNoContent());

        verify(departmentService, times(1)).updateDepartment(any(DepartmentRequest.class));
    }

    @Test
    @DisplayName("부서 삭제 - 204 반환")
    void deleteDepartment_204() throws Exception {

        mockMvc.perform(delete("/users/departments/DEPT001")
                        .accept(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(departmentService, times(1)).deleteDepartment(anyString());
    }
}
