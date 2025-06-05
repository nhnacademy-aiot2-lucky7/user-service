package com.nhnacademy.user.repository.impl;

import com.nhnacademy.department.dto.DepartmentResponse;
import com.nhnacademy.eventlevel.dto.EventLevelResponse;
import com.nhnacademy.user.domain.QUser;
import com.nhnacademy.user.domain.User;
import com.nhnacademy.user.dto.UserResponse;
import com.nhnacademy.user.repository.CustomUserRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class CustomUserRepositoryImpl extends QuerydslRepositorySupport implements CustomUserRepository {

    public CustomUserRepositoryImpl() {
        super(User.class);
    }

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Optional<UserResponse> findUserResponseByUserEmail(String userEmail) {
        JPAQuery<UserResponse> query = new JPAQuery<>(entityManager);
        QUser qUser = QUser.user;

        return Optional.ofNullable(query
                .select(Projections.constructor(
                        UserResponse.class,
                        qUser.role.roleId,
                        qUser.userNo,
                        qUser.userName,
                        qUser.userEmail,
                        qUser.userPhone,
                        Projections.constructor(DepartmentResponse.class,
                                qUser.department.departmentId,
                                qUser.department.departmentName
                        ),
                        Projections.constructor(EventLevelResponse.class,
                                qUser.eventLevel.eventLevelName,
                                qUser.eventLevel.eventLevelDetails,
                                qUser.eventLevel.priority
                        )
                ))
                .from(qUser)
                .where(qUser.userEmail.eq(userEmail)
                        .and(qUser.withdrawalAt.isNull()))
                .fetchOne());
    }

    @Override
    public Optional<List<UserResponse>> findAllUserResponse(Pageable pageable) {
        QUser qUser = QUser.user;

        return Optional.of(new JPAQuery<UserResponse>(getEntityManager())
                .select(Projections.constructor(
                        UserResponse.class,
                        qUser.role.roleId,
                        qUser.userNo,
                        qUser.userName,
                        qUser.userEmail,
                        qUser.userPhone,
                        Projections.constructor(DepartmentResponse.class,
                                qUser.department.departmentId,
                                qUser.department.departmentName
                        ),
                        Projections.constructor(EventLevelResponse.class,
                                qUser.eventLevel.eventLevelName,
                                qUser.eventLevel.eventLevelDetails,
                                qUser.eventLevel.priority
                        )
                ))
                .from(qUser)
                .where(qUser.withdrawalAt.isNull())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch());
    }

    @Override
    public Optional<List<UserResponse>> findUsersByDepartmentId(String departmentId, Pageable pageable) {
        QUser qUser = QUser.user;

        return Optional.of(new JPAQuery<UserResponse>(getEntityManager())
                .select(Projections.constructor(
                        UserResponse.class,
                        qUser.role.roleId,
                        qUser.userNo,
                        qUser.userName,
                        qUser.userEmail,
                        qUser.userPhone,
                        Projections.constructor(
                                DepartmentResponse.class,
                                qUser.department.departmentId,
                                qUser.department.departmentName
                        ),
                        Projections.constructor(
                                EventLevelResponse.class,
                                qUser.eventLevel.eventLevelName,
                                qUser.eventLevel.eventLevelDetails,
                                qUser.eventLevel.priority
                        )
                ))
                .from(qUser)
                .where(qUser.department.departmentId.eq(departmentId)
                        .and(qUser.withdrawalAt.isNull()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch());
    }
}
