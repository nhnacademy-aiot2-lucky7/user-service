package com.nhnacademy.user.repository;

import com.nhnacademy.user.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.DialectOverride;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

/**
 * {@link UserRepository}의 기능 중 일부를 검증하는 테스트 클래스입니다.
 */
@Slf4j
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    TestEntityManager testEntityManager;

    /**
     * 사용자의 이메일이 존재하는지 확인하는 테스트
     *
     * 실제 데이터베이스에 사용자 정보를 저장하고,
     * 이메일 기준으로 조회되는지 확인합니다.
     *
     */
    @Test
    @DisplayName("User 이메일이 존재하는지 확인")
    void existsByUserEmail() {
        User user = User.ofNewUser(
                "user",
                "user@email.com",
                "userPassword"
        );
        testEntityManager.persist(user);

        User dbUser = testEntityManager.find(User.class, user.getUserNo());


        Assertions.assertNotNull(dbUser);
        Assertions.assertEquals("user@email.com", dbUser.getUserEmail());
    }
}