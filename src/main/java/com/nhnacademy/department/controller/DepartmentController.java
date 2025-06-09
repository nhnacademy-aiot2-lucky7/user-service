package com.nhnacademy.department.controller;

import com.nhnacademy.department.dto.DepartmentDashboardDTO;
import com.nhnacademy.department.dto.DepartmentResponse;
import com.nhnacademy.department.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;

    @GetMapping("/departments/all")
    public ResponseEntity<List<DepartmentResponse>> getAllDepartment() {
        return ResponseEntity
                .ok(departmentService.getAllDepartment());
    }

    @GetMapping("/departments/{departmentId}")
    public ResponseEntity<DepartmentResponse> getDepartment(@PathVariable String departmentId) {
        return ResponseEntity
                .ok(departmentService.getDepartmentByDepartmentId(departmentId));
    }

    @GetMapping("/main/dashboard/{department-id}")
    public ResponseEntity<DepartmentDashboardDTO> getDepartmentByDashboardUid(@PathVariable("department-id") String departmentid) {
        return ResponseEntity
                .ok(departmentService.getDepartmentDashboard(departmentid));
    }

    @PostMapping("/main/dashboard")
    public ResponseEntity<Void> updateMainDashboard(@RequestBody DepartmentDashboardDTO departmentDashboardDTO) {
        departmentService.updateMainDashboard(departmentDashboardDTO);

        return ResponseEntity.ok().build();
    }
}
