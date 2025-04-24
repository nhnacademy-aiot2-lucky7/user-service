package com.nhnacademy.user.repository.impl;

import com.nhnacademy.user.domain.QUser;
import com.nhnacademy.user.domain.User;
import com.nhnacademy.user.dto.UserResponse;
import com.nhnacademy.user.repository.CustomUserRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.Optional;

public class CustomUserRepositoryImpl extends QuerydslRepositorySupport implements CustomUserRepository {

    /**
     * 기본 생성자 - QuerydslRepositorySupport에 User 엔티티 클래스를 설정합니다.
     */
    public CustomUserRepositoryImpl() {
        super(User.class);
    }

    /**
     * 사용자 이메일(userEmail)을 기준으로 사용자 정보를 조회합니다.
     * <p>
     * 사용자 이메일에 해당하는 정보를 조회하여 {@link UserResponse} 객체로 반환합니다.
     * 사용자가 존재하지 않을 경우 {@link Optional#empty()}를 반환합니다.
     * </p>
     *
     * @param userEmail 사용자 이메일
     * @return 조회된 사용자 정보를 담은 {@link UserResponse}, 존재하지 않을 경우 Optional.empty()
     */
    @Override
    public Optional<UserResponse> findUserResponseByUserEmail(String userEmail) {
        JPAQuery<UserResponse> query = new JPAQuery<>(getEntityManager());
        QUser qUser = QUser.user;

        return Optional.ofNullable(query
                .select(Projections.constructor(
                        UserResponse.class,
                        qUser.role.roleId,
                        qUser.userNo,
                        qUser.userName,
                        qUser.userEmail,
                        qUser.userPhone,
                        qUser.department
                ))
                .from(qUser)
                .where(qUser.userEmail.eq(userEmail)
                        .and(qUser.withdrawalAt.isNull()))
                .fetchOne());
    }

    /**
     * 모든 사용자 정보를 조회합니다.
     * <p>
     * 시스템에 존재하는 모든 사용자 정보를 {@link List} 형태로 반환합니다.
     * 사용자가 존재하지 않을 경우 {@link Optional#empty()}를 반환합니다.
     * </p>
     *
     * @return 모든 사용자 정보를 담은 {@link List<UserResponse>}, 존재하지 않을 경우 Optional.empty()
     */
    @Override
    public Optional<List<UserResponse>> findAllUserResponse() {
        JPAQuery<UserResponse> query = new JPAQuery<>(getEntityManager());
        QUser qUser = QUser.user;

        return Optional.ofNullable(query
                .select(Projections.constructor(
                        UserResponse.class,
                        qUser.role.roleId,
                        qUser.userNo,
                        qUser.userName,
                        qUser.userEmail,
                        qUser.userPhone,
                        qUser.department
                ))
                .from(qUser)
                .where(qUser.withdrawalAt.isNull())
                .fetch());
    }

}
