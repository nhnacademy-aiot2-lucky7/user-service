package com.nhnacademy.user.service.impl;

import com.nhnacademy.common.exception.ConflictException;
import com.nhnacademy.common.exception.NotFoundException;
import com.nhnacademy.common.exception.UnauthorizedException;
import com.nhnacademy.department.domain.Department;
import com.nhnacademy.department.repository.DepartmentRepository;
import com.nhnacademy.role.domain.Role;
import com.nhnacademy.role.repository.RoleRepository;
import com.nhnacademy.user.domain.User;
import com.nhnacademy.user.dto.*;
import com.nhnacademy.user.repository.UserRepository;
import com.nhnacademy.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * 사용자 관련 비즈니스 로직을 처리하는 서비스 구현체입니다.
 * <p>
 * 회원가입, 조회, 로그인, 비밀번호 변경, 사용자 정보 수정 및 삭제 등의 기능을 제공합니다.
 * </p>
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;

    /**
     * 새로운 사용자를 등록합니다.
     * <p>
     * 이메일 중복 여부를 확인하고, 중복될 경우 {@link ConflictException}을 발생시킵니다.
     * </p>
     *
     * @param registerUserRequest 사용자 등록 요청 DTO
     * @throws ConflictException 이미 등록된 이메일인 경우
     * @throws NotFoundException 등록 후 사용자 조회 실패 시
     */
    @Override
    public void createUser(UserRegisterRequest registerUserRequest) {
        log.debug("회원가입 시작! 회원 정보: {}", registerUserRequest);

        boolean isExistsEmail = userRepository.existsByUserEmailAndWithdrawalAtIsNull(registerUserRequest.getUserEmail());
        if (isExistsEmail) {
            throw new ConflictException("이미 존재하는 이메일입니다.");
        }

        Department department = departmentRepository.findById(registerUserRequest.getUserDepartment())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 부서입니다."));

        String encodePassword = passwordEncoder.encode(registerUserRequest.getUserPassword());

        User user = User.ofNewMember(
                registerUserRequest.getUserName(),
                registerUserRequest.getUserEmail(),
                encodePassword,
                registerUserRequest.getUserPhone(),
                department
        );

        if (!roleRepository.existsById(user.getRole().getRoleId())) {
            throw new NotFoundException("존재하지 않는 권한입니다.");
        }

        userRepository.save(user);
    }

    /**
     * 사용자 이메일을 기반으로 사용자 정보를 조회합니다.
     * <p>
     * 사용자가 존재하지 않을 경우 {@link NotFoundException}을 발생시킵니다.
     * </p>
     *
     * @param userEmail 조회할 사용자 이메일
     * @return 사용자 정보 (UserResponse DTO)
     * @throws NotFoundException 사용자 정보가 없을 경우
     */
    @Transactional(readOnly = true)
    @Override
    public UserResponse getUser(String userEmail) {
        log.debug("회원조회 시작! 회원 이메일 : {}", userEmail);

        return userRepository.findUserResponseByUserEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("해당 userEmail에 해당하는 유저를 찾을 수 없습니다."));
    }

    /**
     * 모든 사용자 정보를 조회합니다.
     * <p>
     * 사용자 정보가 없을 경우 빈 목록을 반환합니다.
     * </p>
     *
     * @return 사용자 목록 (List<UserResponse>)
     */
    @Transactional(readOnly = true)
    @Override
    public List<UserResponse> getAllUser() {
        return userRepository.findAllUserResponse()
                .orElse(Collections.emptyList());
    }

    /**
     * 로그인 시 이메일을 통해 사용자 정보를 조회합니다.
     * <p>
     * 비밀번호가 일치하지 않으면 {@link UnauthorizedException}을 발생시킵니다.
     * </p>
     *
     * @param userLoginRequest 로그인 요청 DTO
     * @throws NotFoundException     이메일에 해당하는 사용자가 없을 경우
     * @throws UnauthorizedException 비밀번호 불일치 시
     */
    @Transactional(readOnly = true)
    @Override
    public void loginUser(UserLoginRequest userLoginRequest) {
        log.debug("로그인 시작! 회원 이메일: {}", userLoginRequest.getUserEmail());
        User getUser = userRepository.findByUserEmailAndWithdrawalAtIsNull(userLoginRequest.getUserEmail())
                .orElseThrow(() -> new NotFoundException("해당 userEmail에 해당하는 유저를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(userLoginRequest.getUserPassword(), getUser.getUserPassword())) {
            throw new UnauthorizedException("비밀번호 불일치");
        }
    }

    /**
     * 사용자의 비밀번호를 변경합니다.
     * <p>
     * 비밀번호가 일치하지 않으면 {@link UnauthorizedException}을 발생시킵니다.
     * </p>
     *
     * @param userEmail             사용자 이메일
     * @param changePasswordRequest 비밀번호 변경 요청 DTO
     * @throws NotFoundException     사용자가 존재하지 않을 경우
     * @throws UnauthorizedException 비밀번호 불일치 또는 확인 비밀번호 불일치 시
     */
    @Override
    public void changePassword(String userEmail, ChangePasswordRequest changePasswordRequest) {
        User getUser = userRepository.findByUserEmailAndWithdrawalAtIsNull(userEmail)
                .orElseThrow(() -> new NotFoundException("해당 userEmail에 해당하는 유저를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), getUser.getUserPassword())) {
            throw new UnauthorizedException("비밀번호 불일치");
        }

        getUser.changePassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(getUser);
    }

    /**
     * 사용자의 정보를 수정합니다.
     * <p>
     * 존재하지 않는 부서인 경우 {@link NotFoundException}을 발생시킵니다.
     * </p>
     *
     * @param userEmail         사용자 이메일
     * @param userUpdateRequest 사용자 정보 수정 요청 DTO
     * @throws NotFoundException 사용자가 존재하지 않거나 부서가 존재하지 않을 경우
     */
    @Override
    public void updateUser(String userEmail, UserUpdateRequest userUpdateRequest) {
        User getUser = userRepository.findByUserEmailAndWithdrawalAtIsNull(userEmail)
                .orElseThrow(() -> new NotFoundException("해당 userEmail에 해당하는 유저를 찾을 수 없습니다."));

        Department department = departmentRepository.findById(userUpdateRequest.getUserDepartmentId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 부서입니다."));

        getUser.updateUser(
                userUpdateRequest.getUserName(),
                userUpdateRequest.getUserPhone(),
                department
        );

        userRepository.save(getUser);
    }

    /**
     * 사용자의 권한을 업데이트합니다.
     * <p>
     * 권한이 존재하지 않으면 {@link NotFoundException}을 발생시킵니다.
     * </p>
     *
     * @param userRoleUpdateRequest 권한 업데이트 요청 DTO
     * @throws NotFoundException 권한이 존재하지 않을 경우
     */
    @Override
    public void updateUserRole(UserRoleUpdateRequest userRoleUpdateRequest) {
        User getUser = userRepository.findByUserEmailAndWithdrawalAtIsNull(userRoleUpdateRequest.getUserId())
                .orElseThrow(() -> new NotFoundException("해당 userEmail에 해당하는 유저를 찾을 수 없습니다."));

        Role role = roleRepository.findById(userRoleUpdateRequest.getRoleId())
                .orElseThrow(() -> new NotFoundException("해당 RoleId의 권한 찾을 수 없습니다."));

        getUser.changeRole(role);

        userRepository.save(getUser);
    }

    /**
     * 사용자를 삭제합니다.
     * <p>
     * 사용자 삭제는 실제 삭제가 아니라 탈퇴 처리로, {@code withdrawalAt} 필드를 업데이트합니다.
     * </p>
     *
     * @param userEmail 삭제할 사용자 이메일
     * @throws NotFoundException 사용자가 존재하지 않을 경우
     */
    @Override
    public void deleteUser(String userEmail) {
        User getUser = userRepository.findByUserEmailAndWithdrawalAtIsNull(userEmail)
                .orElseThrow(() -> new NotFoundException("해당 userEmail에 해당하는 유저를 찾을 수 없습니다."));

        getUser.updateWithdrawalAt();
        userRepository.save(getUser);
    }
}
