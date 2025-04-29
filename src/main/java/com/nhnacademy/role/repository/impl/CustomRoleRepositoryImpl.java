package com.nhnacademy.role.repository.impl;

import com.nhnacademy.role.domain.QRole;
import com.nhnacademy.role.domain.Role;
import com.nhnacademy.role.dto.RoleResponse;
import com.nhnacademy.role.repository.CustomRoleRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.Optional;

public class CustomRoleRepositoryImpl extends QuerydslRepositorySupport implements CustomRoleRepository {

    public CustomRoleRepositoryImpl() {
        super(Role.class);
    }

    @Override
    public Optional<List<RoleResponse>> findAllRole() {
        JPAQuery<RoleResponse> query = new JPAQuery<>(getEntityManager());
        QRole qRole = QRole.role;

        return Optional.of(query
                .select(Projections.constructor(
                        RoleResponse.class,
                        qRole.roleId,
                        qRole.roleName
                ))
                .from(qRole)
                .fetch());
    }

    @Override
    public Optional<RoleResponse> findRoleByRoleId(String roleId) {
        JPAQuery<RoleResponse> query = new JPAQuery<>(getEntityManager());
        QRole qRole = QRole.role;

        return Optional.ofNullable(query
                .select(Projections.constructor(
                        RoleResponse.class,
                        qRole.roleId,
                        qRole.roleName
                ))
                .from(qRole)
                .where(qRole.roleId.eq(roleId))
                .fetchOne());
    }
}
