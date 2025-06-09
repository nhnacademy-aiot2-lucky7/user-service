package com.nhnacademy.department.service.impl;

import com.nhnacademy.common.exception.ConflictException;
import com.nhnacademy.common.exception.NotFoundException;
import com.nhnacademy.department.domain.Department;
import com.nhnacademy.department.dto.DepartmentDashboardDTO;
import com.nhnacademy.department.dto.DepartmentRequest;
import com.nhnacademy.department.dto.DepartmentResponse;
import com.nhnacademy.department.repository.DepartmentRepository;
import com.nhnacademy.department.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository departmentRepository;

    @Transactional(readOnly = true)
    @Override
    public List<DepartmentResponse> getAllDepartment() {
        return departmentRepository
                .findAllDepartment()
                .orElse(List.of());
    }

    @Transactional(readOnly = true)
    @Override
    public DepartmentResponse getDepartmentByDepartmentId(String departmentId) {
        return departmentRepository.findDepartmentByDepartmentId(departmentId)
                .orElseThrow(() -> new NotFoundException("departmentId에 해당 하는 department는 존재하지 않습니다."));
    }

    @Override
    public void createDepartment(DepartmentRequest departmentRequest) {
        if (departmentRepository.existsById(departmentRequest.getDepartmentId())) {
            throw new ConflictException("이미 존재하는 departmentId입니다.");
        }

        Department department = new Department(departmentRequest.getDepartmentId(), departmentRequest.getDepartmentName(), null, null);

        departmentRepository.save(department);
    }

    @Override
    public void updateDepartment(DepartmentRequest departmentRequest) {
        Department department = departmentRepository.findById(departmentRequest.getDepartmentId())
                .orElseThrow(() -> new NotFoundException("departmentId에 해당 하는 department는 존재하지 않습니다."));

        department.updateDepartmentName(departmentRequest.getDepartmentName());

        departmentRepository.save(department);
    }

    @Override
    public void deleteDepartment(String departmentId) {
        if (!departmentRepository.existsById(departmentId)) {
            throw new NotFoundException("존재하는 않는 departmentId입니다.");
        }

        departmentRepository.deleteById(departmentId);
    }

    @Override
    public DepartmentDashboardDTO getDepartmentDashboard(String departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new NotFoundException("departmentId에 해당 하는 department는 존재하지 않습니다."));

        return new DepartmentDashboardDTO(department.getMainDashboardUid(), department.getMainDashboardTitle(), departmentId);
    }

    @Override
    public void updateMainDashboard(DepartmentDashboardDTO departmentDashboardDTO) {
        Department department = departmentRepository.findById(departmentDashboardDTO.getDepartmentId())
                .orElseThrow(() -> new NotFoundException("departmentId에 해당 하는 department는 존재하지 않습니다."));

        department.updateMainDashboard(departmentDashboardDTO.getDashboardUid(), departmentDashboardDTO.getDashboardTitle());
    }
}
