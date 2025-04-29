package com.nhnacademy.department.repository;

import com.nhnacademy.department.dto.DepartmentResponse;

import java.util.List;
import java.util.Optional;

public interface CustomDepartmentRepository {
    Optional<List<DepartmentResponse>> findAllDepartment();

    Optional<DepartmentResponse> findDepartmentByDepartmentId(String departmentId);
}
