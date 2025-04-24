package com.nhnacademy.image.service.impl;

import com.nhnacademy.common.exception.NotFoundException;
import com.nhnacademy.image.domain.Image;
import com.nhnacademy.image.dto.ImageResponse;
import com.nhnacademy.image.repository.ImageRepository;
import com.nhnacademy.image.service.ImageService;
import com.nhnacademy.user.domain.User;
import com.nhnacademy.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * 이미지 관련 비즈니스 로직을 처리하는 서비스 구현 클래스입니다.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final UserRepository userRepository;

    /**
     * 사용자의 프로필 이미지를 조회합니다.
     *
     * @param userEmail 사용자 이메일
     * @return 이미지 경로를 포함한 응답 DTO
     * @throws NotFoundException 사용자가 없거나 이미지가 없는 경우
     */
    @Transactional(readOnly = true)
    @Override
    public ImageResponse getImage(String userEmail) {
        User user = userRepository.findByUserEmailAndWithdrawalAtIsNull(userEmail)
                .orElseThrow(() -> new NotFoundException("해당 userEmail에 해당하는 유저를 찾을 수 없습니다."));

        Image profileImage = user.getProfileImage();
        if (Objects.isNull(profileImage)) {
            throw new NotFoundException("해당 유저의 프로필 이미지가 등록되어 있지 않습니다.");
        }

        return new ImageResponse(profileImage.getImagePath());
    }

    /**
     * 사용자의 프로필 이미지를 삭제합니다.
     *
     * @param userEmail 사용자 이메일
     * @throws NotFoundException 사용자가 없거나 이미지가 없는 경우
     */
    @Override
    public void deleteImage(String userEmail) {
        User user = userRepository.findByUserEmailAndWithdrawalAtIsNull(userEmail)
                .orElseThrow(() -> new NotFoundException("해당 userEmail에 해당하는 유저를 찾을 수 없습니다."));

        Image profileImage = user.getProfileImage();
        if (Objects.isNull(profileImage)) {
            throw new NotFoundException("해당 유저의 프로필 이미지가 등록되어 있지 않습니다.");
        }

        imageRepository.deleteById(profileImage.getImageNo());
        user.changeProfileImage(null); // 유저 객체 내 이미지 참조 해제
    }

    /**
     * 사용자의 프로필 이미지를 새로 생성하고 연결합니다.
     *
     * @param userEmail 사용자 이메일
     * @param imagePath 이미지 경로
     * @throws NotFoundException 사용자가 존재하지 않는 경우
     */
    @Override
    public void createImage(String userEmail, String imagePath) {
        User user = userRepository.findByUserEmailAndWithdrawalAtIsNull(userEmail)
                .orElseThrow(() -> new NotFoundException("해당 userEmail에 해당하는 유저를 찾을 수 없습니다."));

        Image newImage = new Image(imagePath);
        imageRepository.save(newImage); // 이미지 저장

        user.changeProfileImage(newImage); // 유저와 연결
        userRepository.save(user); // 유저 변경사항 저장
    }

    /**
     * 사용자의 기존 프로필 이미지 경로를 수정합니다.
     *
     * @param userEmail 사용자 이메일
     * @param imagePath 새 이미지 경로
     * @throws NotFoundException 유저나 기존 이미지가 없는 경우
     */
    @Override
    public void updateImage(String userEmail, String imagePath) {
        User user = userRepository.findByUserEmailAndWithdrawalAtIsNull(userEmail)
                .orElseThrow(() -> new NotFoundException("해당 userEmail에 해당하는 유저를 찾을 수 없습니다."));

        Image currentImage = user.getProfileImage();
        if (Objects.isNull(currentImage)) {
            throw new NotFoundException("등록된 프로필 이미지가 없습니다. 이미지를 먼저 설정해 주세요.");
        }

        currentImage.updateImagePath(imagePath); // 이미지 경로만 업데이트
    }
}
