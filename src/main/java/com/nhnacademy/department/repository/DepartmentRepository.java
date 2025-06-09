package com.nhnacademy.department.repository;

import com.nhnacademy.department.domain.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, String>, CustomDepartmentRepository {
    Optional<Department> findByMainDashboardUid(String mainDashboardUid);
}
