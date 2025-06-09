package com.nhnacademy.department.service;

import com.nhnacademy.department.dto.DepartmentDashboardDTO;
import com.nhnacademy.department.dto.DepartmentRequest;
import com.nhnacademy.department.dto.DepartmentResponse;

import java.util.List;

public interface DepartmentService {
    List<DepartmentResponse> getAllDepartment();

    DepartmentResponse getDepartmentByDepartmentId(String departmentId);

    void createDepartment(DepartmentRequest departmentRequest);

    void updateDepartment(DepartmentRequest departmentRequest);

    void deleteDepartment(String departmentId);

    DepartmentDashboardDTO getDepartmentDashboard(String mainDashboardUid);

    void updateMainDashboard(DepartmentDashboardDTO departmentDashboardDTO);
}
