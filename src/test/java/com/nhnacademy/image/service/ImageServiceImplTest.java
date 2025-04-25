package com.nhnacademy.image.service;

import com.nhnacademy.common.exception.NotFoundException;
import com.nhnacademy.department.domain.Department;
import com.nhnacademy.image.domain.Image;
import com.nhnacademy.image.dto.ImageResponse;
import com.nhnacademy.image.repository.ImageRepository;
import com.nhnacademy.image.service.impl.ImageServiceImpl;
import com.nhnacademy.user.domain.User;
import com.nhnacademy.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.Field;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
class ImageServiceImplTest {
    @Mock
    ImageRepository imageRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    ImageServiceImpl imageService;

    @Test
    @DisplayName("이미지 경로 조회")
    void getImage() {
        String userEmail = "test@email.com";
        User user = User.ofNewMember(
                "testUser",
                "test@email.com",
                "P@ssw0rd",
                "010-1234-5678",
                new Department("DEP-001", "개발팀")
        );
        user.changeProfileImage(new Image("image/path"));

        Mockito.when(userRepository.findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString())).thenReturn(Optional.of(user));

        ImageResponse imageResponse = imageService.getImage(userEmail);

        Mockito.verify(userRepository, Mockito.times(1)).findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString());

        Assertions.assertEquals("image/path", imageResponse.getImagePath());
    }

    @Test
    @DisplayName("이미지 경로 조회 - 존재하지 않는 유저")
    void getImage_exception1() {
        String userEmail = "test@email.com";

        Mockito.when(userRepository.findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> imageService.getImage(userEmail));

        Mockito.verify(userRepository, Mockito.times(1)).findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString());
    }

    @Test
    @DisplayName("이미지 경로 조회 - 프로필 이미지 미등록")
    void getImage_exception2() {
        String userEmail = "test@email.com";
        User user = User.ofNewMember(
                "testUser",
                "test@email.com",
                "P@ssw0rd",
                "010-1234-5678",
                new Department("DEP-001", "개발팀")
        );

        Mockito.when(userRepository.findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString())).thenReturn(Optional.of(user));

        Assertions.assertThrows(NotFoundException.class, () -> imageService.getImage(userEmail));

        Mockito.verify(userRepository, Mockito.times(1)).findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString());
    }

    @Test
    @DisplayName("이미지 경로 삭제")
    void deleteImage() throws NoSuchFieldException, IllegalAccessException {
        String userEmail = "test@email.com";
        User user = User.ofNewMember(
                "testUser",
                "test@email.com",
                "P@ssw0rd",
                "010-1234-5678",
                new Department("DEP-001", "개발팀")
        );
        user.changeProfileImage(new Image("image/path"));

        Field field = Image.class.getDeclaredField("imageNo");
        field.setAccessible(true);
        field.set(user.getProfileImage(), 1L);

        Mockito.when(userRepository.findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString())).thenReturn(Optional.of(user));

        imageService.deleteImage(userEmail);

        Mockito.verify(userRepository, Mockito.times(1)).findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString());
    }

    @Test
    @DisplayName("이미지 경로 삭제 - 존재하지 않는 유저")
    void deleteImage_exception1() {
        String userEmail = "test@email.com";

        Mockito.when(userRepository.findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> imageService.deleteImage(userEmail));

        Mockito.verify(userRepository, Mockito.times(1)).findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString());
    }

    @Test
    @DisplayName("이미지 경로 삭제 - 프로필 이미지 미등록")
    void deleteImage_exception2() {
        String userEmail = "test@email.com";
        User user = User.ofNewMember(
                "testUser",
                "test@email.com",
                "P@ssw0rd",
                "010-1234-5678",
                new Department("DEP-001", "개발팀")
        );

        Mockito.when(userRepository.findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString())).thenReturn(Optional.of(user));

        Assertions.assertThrows(NotFoundException.class, () -> imageService.deleteImage(userEmail));

        Mockito.verify(userRepository, Mockito.times(1)).findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString());
    }

    @Test
    @DisplayName("이미지 경로 생성")
    void createImage() {
        String userEmail = "test@email.com";
        String imagePath = "image/path";
        User user = User.ofNewMember(
                "testUser",
                "test@email.com",
                "P@ssw0rd",
                "010-1234-5678",
                new Department("DEP-001", "개발팀")
        );

        Mockito.when(userRepository.findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString())).thenReturn(Optional.of(user));
        Mockito.doAnswer(invocation -> {
            Image image = invocation.getArgument(0);
            Field field = Image.class.getDeclaredField("imageNo");
            field.setAccessible(true);
            field.set(image, 1L);

            return null;
        }).when(imageRepository).save(Mockito.any(Image.class));

        imageService.createImage(userEmail, imagePath);

        Mockito.verify(userRepository, Mockito.times(1)).findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString());

        Assertions.assertEquals(1L, user.getProfileImage().getImageNo());
    }

    @Test
    @DisplayName("이미지 경로 생성 - 존재하지 않는 유저")
    void createImage_exception1() {
        String userEmail = "test@email.com";
        String imagePath = "image/path";

        Mockito.when(userRepository.findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> imageService.createImage(userEmail, imagePath));

        Mockito.verify(userRepository, Mockito.times(1)).findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString());
    }

    @Test
    @DisplayName("이미지 경로 수정")
    void updateImage() {
        String userEmail = "test@email.com";
        String imagePath = "image/";
        User user = User.ofNewMember(
                "testUser",
                "test@email.com",
                "P@ssw0rd",
                "010-1234-5678",
                new Department("DEP-001", "개발팀")
        );
        user.changeProfileImage(new Image("image/path"));

        Mockito.when(userRepository.findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString())).thenReturn(Optional.of(user));

        imageService.updateImage(userEmail, imagePath);

        Mockito.verify(userRepository, Mockito.times(1)).findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString());

        Assertions.assertEquals("image/", user.getProfileImage().getImagePath());
    }

    @Test
    @DisplayName("이미지 경로 수정 - 존재하지 않는 유저")
    void updateImage_exception1() {
        String userEmail = "test@email.com";
        String imagePath = "image/";

        Mockito.when(userRepository.findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> imageService.updateImage(userEmail, imagePath));

        Mockito.verify(userRepository, Mockito.times(1)).findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString());
    }

    @Test
    @DisplayName("이미지 경로 수정 - 프로필 이미지 미 설정")
    void updateImage_exception2() {
        String userEmail = "test@email.com";
        String imagePath = "image/";
        User user = User.ofNewMember(
                "testUser",
                "test@email.com",
                "P@ssw0rd",
                "010-1234-5678",
                new Department("DEP-001", "개발팀")
        );

        Mockito.when(userRepository.findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString())).thenReturn(Optional.of(user));

        Assertions.assertThrows(NotFoundException.class, () -> imageService.updateImage(userEmail, imagePath));

        Mockito.verify(userRepository, Mockito.times(1)).findByUserEmailAndWithdrawalAtIsNull(Mockito.anyString());
    }
}