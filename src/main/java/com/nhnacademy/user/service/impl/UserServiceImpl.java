package com.nhnacademy.user.service.impl;

import com.nhnacademy.common.exception.ConflictException;
import com.nhnacademy.common.exception.NotFoundException;
import com.nhnacademy.common.exception.UnauthorizedException;
import com.nhnacademy.user.domain.User;
import com.nhnacademy.user.dto.ChangePasswordRequest;
import com.nhnacademy.user.dto.UserLoginRequest;
import com.nhnacademy.user.dto.UserRegisterRequest;
import com.nhnacademy.user.dto.UserResponse;
import com.nhnacademy.user.repository.UserRepository;
import com.nhnacademy.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 관련 비즈니스 로직을 처리하는 서비스 구현체입니다.
 * <p>
 * 회원가입, 조회, 로그인 등의 기능을 제공합니다.
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    /**
     * 새로운 사용자를 등록합니다.
     * <p>
     * 이메일 중복 여부를 확인하고, 중복될 경우 {@link ConflictException}을 발생시킵니다.
     *
     * @param registerUserRequest 사용자 등록 요청 DTO
     * @return 등록된 사용자 정보
     * @throws ConflictException 이미 등록된 이메일인 경우
     * @throws NotFoundException 등록 후 사용자 조회 실패 시
     */
    @Override
    public void createUser(UserRegisterRequest registerUserRequest) {

        log.debug("회원가입 시작! 회원 정보: {}", registerUserRequest);

        boolean isExistsEmail = userRepository.existsByUserEmail(registerUserRequest.getUserEmail());
        if (isExistsEmail) {
            throw new ConflictException("이미 존재하는 이메일입니다. 이메일: " + registerUserRequest.getUserEmail());
        }

        String encodePassword = passwordEncoder.encode(registerUserRequest.getUserPassword());

        User user = User.ofNewMember(
                registerUserRequest.getUserName(),
                registerUserRequest.getUserEmail(),
                encodePassword,
                registerUserRequest.getUserPhone(),
                registerUserRequest.getUserDepartment()
        );

        userRepository.save(user);
    }

    /**
     * 사용자 번호로 사용자 정보를 조회합니다.
     *
     * @param userEmail 사용자 이메일
     * @return 사용자 응답 DTO
     * @throws NotFoundException 사용자 정보가 없을 경우
     */
    @Override
    public UserResponse getUser(String userEmail) {
        log.debug("회원조회 시작! 회원 이메일 : {}", userEmail);

        return userRepository.findUserResponseByUserEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("해당 userEmail에 해당하는 유저를 찾을 수 없습니다."));
    }

    /**
     * 로그인 시 이메일을 통해 사용자 정보를 조회합니다.
     *
     * @param userLoginRequest 로그인 요청 DTO
     * @return 사용자 응답 DTO
     * @throws NotFoundException 이메일에 해당하는 사용자가 없을 경우
     */
    @Override
    public void loginUser(UserLoginRequest userLoginRequest) {
        log.debug("로그인 시작! 회원 이메일: {}", userLoginRequest.getUserEmail());
        User getUser = userRepository.findByUserEmail(userLoginRequest.getUserEmail())
                .orElseThrow(() -> new NotFoundException("해당 userEmail에 해당하는 유저를 찾을 수 없습니다."));

        if(!passwordEncoder.matches(userLoginRequest.getUserPassword(), getUser.getUserPassword())) {
            throw new UnauthorizedException("비밀번호 불일치");
        }
    }

    @Override
    public void changePassword(String userEmail, ChangePasswordRequest changePasswordRequest) {
        User getUser = userRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("해당 userEmail에 해당하는 유저를 찾을 수 없습니다."));

        if(!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), getUser.getUserPassword())) {
            throw new UnauthorizedException("비밀번호 불일치");
        }

        if(!changePasswordRequest.isPasswordConfirmed()) {
            throw new UnauthorizedException("확인 패스워드 불일치");
        }

        User user = userRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 유저"));

        user.changePassword(changePasswordRequest.getNewPassword());
    }

}