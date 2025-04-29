package com.nhnacademy.department.controller;

import com.nhnacademy.department.dto.DepartmentRequest;
import com.nhnacademy.department.dto.DepartmentResponse;
import com.nhnacademy.department.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/departments")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;

    @GetMapping
    public ResponseEntity<List<DepartmentResponse>> getAllDepartment() {
        return ResponseEntity
                .ok(departmentService.getAllDepartment());
    }

    @GetMapping("/{departmentId}")
    public ResponseEntity<DepartmentResponse> getDepartment(@PathVariable String departmentId) {
        return ResponseEntity
                .ok(departmentService.getDepartmentByDepartmentId(departmentId));
    }

    @PostMapping
    public ResponseEntity<Void> createDepartment(@Validated @RequestBody DepartmentRequest departmentRequest) {
        departmentService.createDepartment(departmentRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @PutMapping
    public ResponseEntity<Void> updateDepartment(@Validated @RequestBody DepartmentRequest departmentRequest) {
        departmentService.updateDepartment(departmentRequest);

        return ResponseEntity
                .noContent()
                .build();
    }

    @DeleteMapping("/{departmentId}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable String departmentId) {
        departmentService.deleteDepartment(departmentId);

        return ResponseEntity
                .noContent()
                .build();
    }
}
