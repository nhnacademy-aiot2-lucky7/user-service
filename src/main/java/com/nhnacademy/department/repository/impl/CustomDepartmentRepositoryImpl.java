package com.nhnacademy.department.repository.impl;

import com.nhnacademy.department.domain.Department;
import com.nhnacademy.department.domain.QDepartment;
import com.nhnacademy.department.dto.DepartmentResponse;
import com.nhnacademy.department.repository.CustomDepartmentRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.Optional;

public class CustomDepartmentRepositoryImpl extends QuerydslRepositorySupport implements CustomDepartmentRepository {
    public CustomDepartmentRepositoryImpl() {
        super(Department.class);
    }

    @Override
    public Optional<List<DepartmentResponse>> findAllDepartment() {
        JPAQuery<DepartmentResponse> query = new JPAQuery<>(getEntityManager());
        QDepartment qDepartment = QDepartment.department;

        return Optional.of(query
                .select(Projections.constructor(
                        DepartmentResponse.class,
                        qDepartment.departmentId,
                        qDepartment.departmentName
                ))
                .from(qDepartment)
                .fetch());
    }

    @Override
    public Optional<DepartmentResponse> findDepartmentByDepartmentId(String departmentId) {
        JPAQuery<DepartmentResponse> query = new JPAQuery<>(getEntityManager());
        QDepartment qDepartment = QDepartment.department;

        return Optional.ofNullable(query
                .select(Projections.constructor(
                        DepartmentResponse.class,
                        qDepartment.departmentId,
                        qDepartment.departmentName
                ))
                .from(qDepartment)
                .where(qDepartment.departmentId.eq(departmentId))
                .fetchOne());
    }

}
