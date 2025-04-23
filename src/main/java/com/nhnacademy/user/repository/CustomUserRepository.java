package com.nhnacademy.user.repository;

import com.nhnacademy.user.dto.UserResponse;

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
     * 사용자 번호(userNo)를 기반으로 사용자 정보를 조회합니다.
     * <p>
     * 사용자 번호에 해당하는 정보를 조회하여 {@link UserResponse} 객체로 반환합니다.
     * 사용자가 존재하지 않을 경우 {@link Optional#empty()}를 반환합니다.
     * </p>
     *
     * @param userNo 사용자 번호
     * @return 조회된 사용자 정보를 담은 {@link UserResponse}, 존재하지 않을 경우 Optional.empty()
     */
    Optional<UserResponse> findUserResponseByUserNo(Long userNo);

    /**
     * 사용자 이메일(userEmail)을 기반으로 사용자 정보를 조회합니다.
     * <p>
     * 사용자 이메일에 해당하는 정보를 조회하여 {@link UserResponse} 객체로 반환합니다.
     * 사용자가 존재하지 않을 경우 {@link Optional#empty()}를 반환합니다.
     * </p>
     *
     * @param userEmail 사용자 이메일
     * @return 조회된 사용자 정보를 담은 {@link UserResponse}, 존재하지 않을 경우 Optional.empty()
     */
    Optional<UserResponse> findUserResponseByUserEmail(String userEmail);

    /**
     * 모든 사용자 정보를 조회합니다.
     * <p>
     * 시스템에 존재하는 모든 사용자 정보를 {@link List} 형태로 반환합니다.
     * 사용자가 존재하지 않을 경우 {@link Optional#empty()}를 반환합니다.
     * </p>
     *
     * @return 모든 사용자 정보를 담은 {@link List<UserResponse>}, 존재하지 않을 경우 Optional.empty()
     */
    Optional<List<UserResponse>> findAllUserResponse();
}
