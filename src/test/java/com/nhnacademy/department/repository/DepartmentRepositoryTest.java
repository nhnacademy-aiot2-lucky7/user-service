package com.nhnacademy.department.repository;

import com.nhnacademy.common.exception.NotFoundException;
import com.nhnacademy.department.domain.Department;
import com.nhnacademy.department.dto.DepartmentResponse;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
class DepartmentRepositoryTest {
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private DepartmentRepository departmentRepository;

    @BeforeEach
    void setUp() {
        departmentRepository.save(new Department("D001", "인사부"));
        departmentRepository.save(new Department("D002", "개발부"));
        departmentRepository.save(new Department("D003", "영업부"));

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("모든 부서 조회")
    void findAllDepartment() {
        List<DepartmentResponse> departmentResponses = departmentRepository.findAllDepartment()
                .orElse(List.of());

        assertEquals(3, departmentResponses.size());
    }

    @Test
    @DisplayName("부서ID에 따른 부서 조회")
    void findDepartmentByDepartmentId() {
        DepartmentResponse departmentResponse = departmentRepository.findDepartmentByDepartmentId("D002")
                .orElseThrow(() -> new NotFoundException("departmentId is null"));

        assertEquals("D002", departmentResponse.getDepartmentId());
        assertEquals("개발부", departmentResponse.getDepartmentName());
    }
}
