package com.nhnacademy.department.controller;

import com.nhnacademy.department.dto.DepartmentResponse;
import com.nhnacademy.department.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/departments")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;

    @GetMapping("/all")
    public ResponseEntity<List<DepartmentResponse>> getAllDepartment() {
        return ResponseEntity
                .ok(departmentService.getAllDepartment());
    }

    @GetMapping("/{departmentId}")
    public ResponseEntity<DepartmentResponse> getDepartment(@PathVariable String departmentId) {
        return ResponseEntity
                .ok(departmentService.getDepartmentByDepartmentId(departmentId));
    }
}
