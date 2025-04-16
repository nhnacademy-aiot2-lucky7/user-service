package com.nhnacademy.user.repository;

import com.nhnacademy.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("이메일 존재 여부 확인 - 존재할 경우")
    void testExistsByUserEmail_true() {
        // given
        User user = User.ofNewMember(
                "user",
                "user@email.com",
                "userPassword",
                "010-1234-5678",
                "인사과"
        );
        userRepository.save(user);

        // when
        boolean exists = userRepository.existsByUserEmail("user@email.com");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("이메일 존재 여부 확인 - 존재하지 않을 경우")
    void testExistsByUserEmail_false() {
        // when
        boolean exists = userRepository.existsByUserEmail("nope@nhn.com");

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("이메일로 사용자 조회")
    void testFindByUserEmail() {
        // given
        User user = User.ofNewMember(
                "user",
                "user@email.com",
                "userPassword",
                "010-1234-5678",
                "인사과"
        );
        userRepository.save(user);

        // when
        Optional<User> result = userRepository.findByUserEmail("user@email.com");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getUserName()).isEqualTo("user");
    }
}
