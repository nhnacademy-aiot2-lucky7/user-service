package com.nhnacademy.role.repository;

import com.nhnacademy.common.exception.NotFoundException;
import com.nhnacademy.role.domain.Role;
import com.nhnacademy.role.dto.RoleResponse;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
class RoleRepositoryTest {
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        roleRepository.save(new Role("ROLE_MEMBER", "일반 회원"));
        roleRepository.save(new Role("ROLE_OWNER", "팀장"));
        roleRepository.save(new Role("ROLE_ADMIN", "관리자"));

        entityManager.flush();

        entityManager.clear();
    }

    @Test
    @DisplayName("모든 권한 조회")
    void findAllRole() {
        List<RoleResponse> roleResponses = roleRepository.findAllRole()
                .orElse(List.of());

        assertEquals(3, roleResponses.size());
    }

    @Test
    @DisplayName("권한ID에 따른 권한 조회")
    void findRoleByRoleId() {
        RoleResponse roleResponse = roleRepository.findRoleByRoleId("ROLE_OWNER")
                .orElseThrow(() -> new NotFoundException("roleId is null"));

        assertEquals("ROLE_OWNER", roleResponse.getRoleId());
        assertEquals("팀장", roleResponse.getRoleName());
    }
}
