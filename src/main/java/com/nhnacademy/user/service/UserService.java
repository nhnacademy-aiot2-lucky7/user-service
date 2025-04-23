package com.nhnacademy.user.service;

import com.nhnacademy.user.dto.*;

import java.util.List;

/**
 * 사용자 관련 기능을 정의하는 서비스 인터페이스입니다.
 * <p>
 * 회원 가입, 조회, 로그인, 삭제 등의 기능을 제공합니다.
 * </p>
 */
public interface UserService {

    /**
     * 새로운 사용자를 등록합니다.
     * <p>
     * 사용자 등록 요청을 처리하고, 등록된 사용자 정보를 반환합니다.
     * </p>
     *
     * @param userRegisterRequest 사용자 등록 요청 DTO
     */
    void createUser(UserRegisterRequest userRegisterRequest);

    /**
     * 사용자 이메일을 기반으로 사용자 정보를 조회합니다.
     * <p>
     * 사용자 이메일을 사용하여 해당 사용자의 정보를 조회합니다.
     * </p>
     *
     * @param userEmail 사용자 이메일(아이디)
     * @return 사용자 정보 DTO
     */
    UserResponse getUser(String userEmail);

    /**
     * 로그인 요청 정보로 사용자 정보를 조회합니다.
     * <p>
     * 로그인 정보에 해당하는 사용자를 조회하고, 유효성 검사를 진행합니다.
     * </p>
     *
     * @param userLoginRequest 로그인 요청 DTO
     */
    void loginUser(UserLoginRequest userLoginRequest);

    /**
     * 사용자의 비밀번호를 변경합니다.
     * <p>
     * 비밀번호 변경 요청을 처리하고, 변경된 비밀번호로 업데이트합니다.
     * </p>
     *
     * @param userEmail              사용자 이메일
     * @param changePasswordRequest  비밀번호 변경 요청 DTO
     */
    void changePassword(String userEmail, ChangePasswordRequest changePasswordRequest);

    /**
     * 사용자를 삭제합니다.
     * <p>
     * 사용자 이메일을 기반으로 해당 사용자의 정보를 삭제합니다.
     * </p>
     *
     * @param userEmail 사용자 이메일
     */
    void deleteUser(String userEmail);

    /**
     * 사용자의 정보를 업데이트합니다.
     * <p>
     * 사용자 이메일을 기반으로 해당 사용자의 정보를 수정합니다.
     * </p>
     *
     * @param userEmail           사용자 이메일
     * @param userUpdateRequest   사용자 정보 업데이트 요청 DTO
     */
    void updateUser(String userEmail, UserUpdateRequest userUpdateRequest);

    /**
     * 모든 사용자 정보를 조회합니다.
     * <p>
     * 시스템에 등록된 모든 사용자의 정보를 조회하여 리스트로 반환합니다.
     * </p>
     *
     * @return 사용자 정보 리스트
     */
    List<UserResponse> getAllUser();
}
