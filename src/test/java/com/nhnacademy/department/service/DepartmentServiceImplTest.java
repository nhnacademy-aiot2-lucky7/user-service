package com.nhnacademy.department.service;

import com.nhnacademy.common.exception.ConflictException;
import com.nhnacademy.common.exception.NotFoundException;
import com.nhnacademy.department.domain.Department;
import com.nhnacademy.department.dto.DepartmentRequest;
import com.nhnacademy.department.dto.DepartmentResponse;
import com.nhnacademy.department.repository.DepartmentRepository;
import com.nhnacademy.department.service.impl.DepartmentServiceImpl;
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
class DepartmentServiceImplTest {
    @Mock
    DepartmentRepository departmentRepository;

    @InjectMocks
    DepartmentServiceImpl departmentService;

    @Test
    @DisplayName("모든 부서 조회")
    void getAllDepartment() {
        List<DepartmentResponse> departmentResponses = IntStream.range(1, 11)
                .mapToObj(i -> new DepartmentResponse("D" + i, "부서" + i))
                .toList();

        when(departmentRepository.findAllDepartment()).thenReturn(Optional.of(departmentResponses));

        List<DepartmentResponse> result = departmentService.getAllDepartment();

        verify(departmentRepository, times(1)).findAllDepartment();

        Assertions.assertEquals(10, result.size());
    }

    @Test
    @DisplayName("부서ID에 따른 부서 조회")
    void getDepartmentByDepartmentId() {
        DepartmentResponse departmentResponse = new DepartmentResponse("D001", "인사팀");

        when(departmentRepository.findDepartmentByDepartmentId(anyString())).thenReturn(Optional.of(departmentResponse));

        DepartmentResponse result = departmentService.getDepartmentByDepartmentId("D001");

        verify(departmentRepository, times(1)).findDepartmentByDepartmentId(anyString());

        Assertions.assertEquals("D001", result.getDepartmentId());
        Assertions.assertEquals("인사팀", result.getDepartmentName());
    }

    @Test
    @DisplayName("부서ID에 따른 부서 조회 - 존재하지 않는 부서ID")
    void getDepartmentByDepartmentId_exception() {
        when(departmentRepository.findDepartmentByDepartmentId(anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> departmentService.getDepartmentByDepartmentId("D001"));

        verify(departmentRepository, times(1)).findDepartmentByDepartmentId(anyString());
    }

    @Test
    @DisplayName("부서 생성")
    void createDepartment() {
        when(departmentRepository.existsById(anyString())).thenReturn(false);

        departmentService.createDepartment(new DepartmentRequest("D001", "인사팀"));

        verify(departmentRepository, times(1)).existsById(anyString());
        verify(departmentRepository, times(1)).save(any(Department.class));
    }

    @Test
    @DisplayName("부서 생성 - 이미 존재하는 부서ID")
    void createDepartment_exception() {
        when(departmentRepository.existsById(anyString())).thenReturn(true);

        Assertions.assertThrows(ConflictException.class, () -> departmentService.createDepartment(new DepartmentRequest("D001", "인사팀")));

        verify(departmentRepository, times(1)).existsById(anyString());
        verify(departmentRepository, never()).save(any(Department.class));
    }

    @Test
    @DisplayName("부서명 수정")
    void updateDepartment() {
        Department department = new Department("D001", "구 부서명");

        when(departmentRepository.findById(anyString())).thenReturn(Optional.of(department));

        departmentService.updateDepartment(new DepartmentRequest("D001", "신 부서명"));

        verify(departmentRepository, times(1)).findById(anyString());
        verify(departmentRepository, times(1)).save(any(Department.class));
    }

    @Test
    @DisplayName("부서명 수정 - 존재하지 않는 부서ID")
    void updateDepartment_exception() {
        when(departmentRepository.findById(anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> departmentService.updateDepartment(new DepartmentRequest("D001", "신 부서명")));

        verify(departmentRepository, times(1)).findById(anyString());
        verify(departmentRepository, never()).save(any(Department.class));
    }

    @Test
    @DisplayName("부서 삭제")
    void deleteDepartment() {
        when(departmentRepository.existsById(anyString())).thenReturn(true);

        departmentService.deleteDepartment("D001");

        verify(departmentRepository, times(1)).existsById(anyString());
        verify(departmentRepository, times(1)).deleteById(anyString());
    }

    @Test
    @DisplayName("부서 삭제 - 존재하지 않는 부서ID")
    void deleteDepartment_exception() {
        when(departmentRepository.existsById(anyString())).thenReturn(false);

        Assertions.assertThrows(NotFoundException.class, () -> departmentService.deleteDepartment("D001"));

        verify(departmentRepository, times(1)).existsById(anyString());
        verify(departmentRepository, never()).deleteById(anyString());
    }
}
