package com.nhnacademy.user.repository;

import com.nhnacademy.user.dto.UserResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 사용자에 대한 커스텀 조회 기능을 정의한 인터페이스입니다.
 * <p>
 * 일반 JPA Repository로는 처리할 수 없는 복잡한 쿼리를 정의합니다.
 * </p>
 */
public interface CustomUserRepository {

    /**
     * 사용자 이메일을 기반으로 사용자 정보를 조회합니다.
     * <p>
     * 주어진 사용자 이메일에 해당하는 정보를 조회하여 {@link UserResponse} 객체로 반환합니다.
     * 사용자가 존재하지 않으면 {@link Optional#empty()}를 반환합니다.
     * </p>
     *
     * @param userEmail 조회할 사용자 이메일
     * @return 사용자 이메일에 해당하는 {@link UserResponse} 객체를 {@link Optional}로 반환,
     * 사용자가 존재하지 않으면 {@link Optional#empty()}를 반환
     */
    Optional<UserResponse> findUserResponseByUserEmail(String userEmail);

    /**
     * 시스템에 존재하는 모든 사용자 정보를 조회합니다.
     * <p>
     * 모든 사용자 정보를 조회하여 {@link List<UserResponse>} 형태로 반환합니다.
     * 사용자가 존재하지 않으면 {@link Optional#empty()}를 반환합니다.
     * </p>
     *
     * @return 시스템에 존재하는 모든 사용자 정보가 담긴 {@link List<UserResponse>}를 {@link Optional}로 반환,
     * 사용자가 존재하지 않으면 {@link Optional#empty()}를 반환
     */
    Optional<List<UserResponse>> findAllUserResponse(Pageable pageable);

    Optional<List<UserResponse>> findUsersByDepartmentId(String departmentId, Pageable pageable);
}
