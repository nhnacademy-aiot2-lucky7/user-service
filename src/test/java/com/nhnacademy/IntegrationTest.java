package com.nhnacademy;

import com.common.AESUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.department.domain.Department;
import com.nhnacademy.department.dto.DepartmentRequest;
import com.nhnacademy.department.repository.DepartmentRepository;
import com.nhnacademy.image.domain.Image;
import com.nhnacademy.image.repository.ImageRepository;
import com.nhnacademy.role.domain.Role;
import com.nhnacademy.role.dto.RoleRequest;
import com.nhnacademy.role.repository.RoleRepository;
import com.nhnacademy.user.domain.User;
import com.nhnacademy.user.dto.*;
import com.nhnacademy.user.repository.UserRepository;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class IntegrationTest {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AESUtil aesUtil;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ImageRepository imageRepository;

    @BeforeEach
    void setUp() {
        // Department 10개 무작위 생성
        for (int i = 0; i < 10; i++) {
            departmentRepository.save(new Department(
                    "dept" + i,
                    "부서명" + i
            ));
        }

        // Role 3개 추가
        roleRepository.save(new Role("ROLE_MEMBER", "일반 회원"));
        roleRepository.save(new Role("ROLE_OWNER", "팀장"));
        roleRepository.save(new Role("ROLE_ADMIN", "관리자"));

        // User 20명 생성 (부서, 롤 랜덤 지정)
        List<Department> departments = departmentRepository.findAll();
        List<Role> roles = roleRepository.findAll();

        for (int i = 0; i < 20; i++) {
            Department randomDept = departments.get((int) (Math.random() * departments.size()));
            Role randomRole = roles.get((int) (Math.random() * roles.size()));

            User user = User.ofNewMember(
                    "testUser" + i,
                    "user" + i + "@test.com",
                    passwordEncoder.encode("P@ssw0rd" + i),
                    "010-0000-000" + i,
                    randomDept
            );
            if (i == 1) {
                user.changeRole(new Role("ROLE_MEMBER", "일반 회원"));
            } else {
                user.changeRole(new Role(randomRole.getRoleId(), randomRole.getRoleName()));
            }


            // 몇 명만 프로필 이미지 추가
            if (i % 3 == 0) { // 0, 3, 6, 9, 12, 15, 18
                Image image = new Image("path/to/image" + i + ".jpg");
                imageRepository.save(image);
                user.changeProfileImage(image);
            }

            userRepository.save(user);
        }

        User user = User.ofNewMember(
                "admin",
                "admin@test.com",
                "P@ssw0rd",
                "010-4444-3333",
                departments.get(1)
        );
        user.changeRole(new Role("ROLE_ADMIN", "관리자"));

        userRepository.save(user);
    }

    @Test
    @DisplayName("회원 가입 - 201반환")
    void signUp_201() throws Exception {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest(
                "testUser",
                "test@email.com",
                "P@ssw0rd",
                "010-1234-5678",
                "dept1"
        );

        mockMvc.perform(post("/users/auth/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userRegisterRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("회원 가입 - validate 검증으로 인한 400반환")
    void signUp_400() throws Exception {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest(
                "testUser",
                "test@email.com",
                "Passw0rd",
                "010-1234-5678",
                "dept1"
        );

        mockMvc.perform(post("/users/auth/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userRegisterRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원 가입 - 이메일 중복으로 인한 409반환")
    void signUp_409() throws Exception {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest(
                "testUser",
                "user1@test.com",
                "P@ssw0rd",
                "010-1234-5678",
                "dept1"
        );

        mockMvc.perform(post("/users/auth/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userRegisterRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("회원 가입 - 존재하지 않는 부서 404반환")
    void signUp_404() throws Exception {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest(
                "testUser",
                "test@test.com",
                "P@ssw0rd",
                "010-1234-5678",
                "dept20"
        );

        mockMvc.perform(post("/users/auth/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userRegisterRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("로그인 - 200반환")
    void signIn_200() throws Exception {
        UserLoginRequest userLoginRequest = new UserLoginRequest(
                "user1@test.com",
                "P@ssw0rd1"
        );

        mockMvc.perform(post("/users/auth/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userLoginRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("로그인 - validate검증으로 인한 400반환")
    void signIn_400() throws Exception {
        UserLoginRequest userLoginRequest = new UserLoginRequest(
                "user1@test.com",
                "Passw0rd1"
        );

        mockMvc.perform(post("/users/auth/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userLoginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인 - 존재하지 않는 유저 404반환")
    void signIn_404() throws Exception {
        UserLoginRequest userLoginRequest = new UserLoginRequest(
                "user20@test.com",
                "P@ssw0rd1"
        );

        mockMvc.perform(post("/users/auth/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userLoginRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("로그인 - 비밀번호 불일치 401반환")
    void signIn_401() throws Exception {
        UserLoginRequest userLoginRequest = new UserLoginRequest(
                "user1@test.com",
                "P@ssw0rd10"
        );

        mockMvc.perform(post("/users/auth/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userLoginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("본인 정보 조회 - 200반환")
    void getMyInfo_200() throws Exception {
        mockMvc.perform(get("/users/me")
                        .header("X-User-Id", aesUtil.encrypt("user1@test.com"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userEmail").value("user1@test.com"))
                .andExpect(jsonPath("$.userName").value("testUser1"));
    }

    @Test
    @DisplayName("본인 정보 조회 - 존재하지 않는 유저 404반환")
    void getMyInfo_404() throws Exception {
        mockMvc.perform(get("/users/me")
                        .header("X-User-Id", aesUtil.encrypt("user20@test.com"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("사용자 정보 수정 - 204 반환")
    void updateMyInfo_204() throws Exception {
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest(
                "testUser1",
                "010-1234-5678",
                "dept1"
        );


        mockMvc.perform(put("/users/me")
                        .header("X-User-Id", aesUtil.encrypt("user1@test.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userUpdateRequest)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("사용자 정보 수정 - validate 검증으로 인한 400 반환")
    void updateMyInfo_400() throws Exception {
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest(
                "testUser1",
                "0101234-5678",
                "dept1"
        );


        mockMvc.perform(put("/users/me")
                        .header("X-User-Id", aesUtil.encrypt("user1@test.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userUpdateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("사용자 정보 수정 - 존재하지 않는 유저 404 반환")
    void updateMyInfo_404_user() throws Exception {
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest(
                "testUser1",
                "010-1234-5678",
                "dept1"
        );


        mockMvc.perform(put("/users/me")
                        .header("X-User-Id", aesUtil.encrypt("user20@test.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userUpdateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("사용자 정보 수정 - 존재하지 않는 부서 404 반환")
    void updateMyInfo_404_department() throws Exception {
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest(
                "testUser1",
                "010-1234-5678",
                "dept20"
        );


        mockMvc.perform(put("/users/me")
                        .header("X-User-Id", aesUtil.encrypt("user1@test.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userUpdateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("사용자 비밀번호 수정 - 204 반환")
    void changePassword_204() throws Exception {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(
                "P@ssw0rd1",
                "newP@ssw0rd",
                "newP@ssw0rd"
        );

        mockMvc.perform(put("/users/me/password")
                        .header("X-User-Id", aesUtil.encrypt("user1@test.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(changePasswordRequest)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("사용자 비밀번호 수정 - validate 검증으로 인한 400 반환")
    void changePassword_400_case1() throws Exception {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(
                "P@ssw0rd1",
                "newPassw0rd",
                "newPassw0rd"
        );

        mockMvc.perform(put("/users/me/password")
                        .header("X-User-Id", aesUtil.encrypt("user1@test.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(changePasswordRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("사용자 비밀번호 수정 - 확인 비밀번호 불일치로 인한 400 반환")
    void changePassword_400_case2() throws Exception {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(
                "wrongP@ssw0rd",
                "newP@ssw0rd",
                "newP@ssw0rd1"
        );

        mockMvc.perform(put("/users/me/password")
                        .header("X-User-Id", aesUtil.encrypt("user1@test.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(changePasswordRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("사용자 비밀번호 수정 - 존재하지 않는 회원 404 반환")
    void changePassword_404() throws Exception {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(
                "wrongP@ssw0rd",
                "newP@ssw0rd",
                "newP@ssw0rd"
        );

        mockMvc.perform(put("/users/me/password")
                        .header("X-User-Id", aesUtil.encrypt("user20@test.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(changePasswordRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("사용자 비밀번호 수정 - currentPassword 불일치 401 반환")
    void changePassword_401() throws Exception {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(
                "wrongP@ssw0rd",
                "newP@ssw0rd",
                "newP@ssw0rd"
        );

        mockMvc.perform(put("/users/me/password")
                        .header("X-User-Id", aesUtil.encrypt("user1@test.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(changePasswordRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("회원 탈퇴 - 204 반환")
    void deleteMyAccount_204() throws Exception {
        mockMvc.perform(delete("/users/me")
                        .header("X-User-Id", aesUtil.encrypt("user1@test.com"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("회원 탈퇴 - 존재하지 않는 회원 404 반환")
    void deleteMyAccount_404() throws Exception {
        mockMvc.perform(delete("/users/me")
                        .header("X-User-Id", aesUtil.encrypt("user20@test.com"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // -------------------------admin-------------------------

    @Test
    @DisplayName("모든 사용자 조회 - 200 반환")
    void getAllUser_200() throws Exception {
        mockMvc.perform(get("/admin/users")
                        .header("X-User-Id", aesUtil.encrypt("admin@test.com"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("모든 사용자 조회 - 권한 부족 403 반환")
    void getAllUser_401() throws Exception {
        mockMvc.perform(get("/admin/users")
                        .header("X-User-Id", aesUtil.encrypt("user1@test.com"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @DisplayName("Email 기반 사용자 조회 - 200 반환")
    void getUserById_200() throws Exception {
        mockMvc.perform(get("/admin/users/user2@test.com")
                        .header("X-User-Id", aesUtil.encrypt("admin@test.com"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userEmail").value("user2@test.com"))
                .andExpect(jsonPath("$.userName").value("testUser2"));
    }

    @Test
    @DisplayName("사용자 역할 수정 - 204 반환")
    void updateUserRole_204() throws Exception {
        UserRoleUpdateRequest userRoleUpdateRequest = new UserRoleUpdateRequest(
                "user2@test.com",
                "ROLE_OWNER"
        );

        mockMvc.perform(put("/admin/users/roles")
                        .header("X-User-Id", aesUtil.encrypt("admin@test.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userRoleUpdateRequest)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("사용자 역할 수정 - validate 검증으로 인한 400 반환")
    void updateUserRole_400() throws Exception {
        UserRoleUpdateRequest userRoleUpdateRequest = new UserRoleUpdateRequest(
                "testemail.com",
                "ROLE_OWNER"
        );

        mockMvc.perform(put("/admin/users/roles")
                        .header("X-User-Id", aesUtil.encrypt("admin@test.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userRoleUpdateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("사용자 역할 수정 - 존재하지 않는 사용자 404 반환")
    void updateUserRole_404_case1() throws Exception {
        UserRoleUpdateRequest userRoleUpdateRequest = new UserRoleUpdateRequest(
                "user20@test.com",
                "ROLE_OWNER"
        );

        mockMvc.perform(put("/admin/users/roles")
                        .header("X-User-Id", aesUtil.encrypt("admin@test.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userRoleUpdateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("사용자 역할 수정 - 존재하지 않는 권한ID 404 반환")
    void updateUserRole_404_case2() throws Exception {
        UserRoleUpdateRequest userRoleUpdateRequest = new UserRoleUpdateRequest(
                "user2@test.com",
                "ROLE_TEST"
        );

        mockMvc.perform(put("/admin/users/roles")
                        .header("X-User-Id", aesUtil.encrypt("admin@test.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userRoleUpdateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("특정 사용자 회원 탈퇴 - 204 반환")
    void deleteUserByAdmin_204() throws Exception {
        mockMvc.perform(delete("/admin/users/user2@test.com")
                        .header("X-User-Id", aesUtil.encrypt("admin@test.com"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("특정 사용자 회원 탈퇴 - 존재하지 않는 사용자 404 반환")
    void deleteUserByAdmin_404() throws Exception {
        mockMvc.perform(delete("/admin/users/user20@test.com")
                        .header("X-User-Id", aesUtil.encrypt("admin@test.com"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // -------------------------image-------------------------

    @Test
    @DisplayName("사용자 이메일 경로 조회 - 200 반환")
    void getImage_200() throws Exception {
        mockMvc.perform(get("/images/user0@test.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imagePath").value("path/to/image0.jpg"));
    }

    @Test
    @DisplayName("사용자 이메일 경로 조회 - 존재하지 않는 이메일 404 반환")
    void getImage_404_case1() throws Exception {
        mockMvc.perform(get("/images/user20@test.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("사용자 이메일 경로 조회 - 이미지 미 등록 404 반환")
    void getImage_404_case2() throws Exception {
        mockMvc.perform(get("/images/user2@test.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("사용자 이미지 경로 생성 - 201 반환")
    void createImage_201() throws Exception {
        mockMvc.perform(post("/images")
                        .param("userEmail", "user1@test.com")
                        .param("imagePath", "images/path")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @DisplayName("사용자 이미지 경로 생성 - 존재하지 않는 사용자 404 반환")
    void createImage_404() throws Exception {
        mockMvc.perform(post("/images")
                        .param("userEmail", "user20@test.com")
                        .param("imagePath", "images/path")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("사용자 이미지 경로 수정 - 204 반환")
    void updateImage_204() throws Exception {
        mockMvc.perform(put("/images")
                        .param("userEmail", "user0@test.com")
                        .param("imagePath", "images/path")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    @DisplayName("사용자 이미지 경로 수정 - 존재하지 않는 사용자 404 반환")
    void updateImage_404_case1() throws Exception {
        mockMvc.perform(put("/images")
                        .param("userEmail", "user20@test.com")
                        .param("imagePath", "images/path")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("사용자 이미지 경로 수정 - 이미지 미등록 404 반환")
    void updateImage_404_case2() throws Exception {
        mockMvc.perform(put("/images")
                        .param("userEmail", "user1@test.com")
                        .param("imagePath", "images/path")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("사용자 이미지 경로 삭제 - 204 반환")
    void deleteImage_204() throws Exception {
        mockMvc.perform(delete("/images/{userEmail}", "user0@test.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    @DisplayName("사용자 이미지 경로 삭제 - 존재하지 않는 사용자 404 반환")
    void deleteImage_404_case1() throws Exception {
        mockMvc.perform(delete("/images/{userEmail}", "user20@test.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("사용자 이미지 경로 삭제 - 이미지 미등록 404 반환")
    void deleteImage_404_case2() throws Exception {
        mockMvc.perform(delete("/images/{userEmail}", "user1@test.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    // -------------------------role-------------------------

    @Test
    @DisplayName("모든 권한 조회 - 200 반환")
    void getAllRole_200() throws Exception {
        mockMvc.perform(get("/users/roles")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("권한ID에 따른 조회 - 200 반환")
    void getRoleByRoleId_200() throws Exception {
        mockMvc.perform(get("/users/roles/ROLE_MEMBER")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roleId").value("ROLE_MEMBER"))
                .andExpect(jsonPath("$.roleName").value("일반 회원"));
    }

    @Test
    @DisplayName("권한ID에 따른 조회 - 권한ID 존재하지 않음 404 반환")
    void getRoleByRoleId_404() throws Exception {
        mockMvc.perform(get("/users/roles/ROLE_TEST")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("권한 생성 - 201 반환")
    void createRole_201() throws Exception {
        RoleRequest roleRequest = new RoleRequest("ROLE_TEST", "테스트 권한");

        mockMvc.perform(post("/users/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(roleRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("권한 생성 - 이미 존재하는 roleId 409 반환")
    void createRole_409() throws Exception {
        RoleRequest roleRequest = new RoleRequest("ROLE_MEMBER", "일반 회원");

        mockMvc.perform(post("/users/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(roleRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("권한 수정 - 204 반환")
    void updateRole_204() throws Exception {
        RoleRequest roleRequest = new RoleRequest("ROLE_MEMBER", "테스트 권한");

        mockMvc.perform(put("/users/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(roleRequest)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("권한 수정 - 존재하지 않는 roleId 404 반환")
    void updateRole_404() throws Exception {
        RoleRequest roleRequest = new RoleRequest("ROLE_TEST", "테스트 권한");

        mockMvc.perform(put("/users/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(roleRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("권한 삭제 - 204 반환")
    void deleteRole_204() throws Exception {
        mockMvc.perform(delete("/users/roles/ROLE_MEMBER")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("권한 삭제 - 존재하지 않는 roleId 404 반환")
    void deleteRole_404() throws Exception {
        mockMvc.perform(delete("/users/roles/ROLE_TEST")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // -------------------------department-------------------------

    @Test
    @DisplayName("모든 부서 조회 - 200 반환")
    void getAllDepartment_200() throws Exception {
        mockMvc.perform(get("/users/departments")
                        .accept(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("부서ID에 따른 조회 - 200 반환")
    void getDepartmentByDepartmentId_200() throws Exception {
        mockMvc.perform(get("/users/departments/dept1")
                        .accept(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.departmentId").value("dept1"))
                .andExpect(jsonPath("$.departmentName").value("부서명1"));
    }

    @Test
    @DisplayName("부서ID에 따른 조회 - 존재하지 않는 부서ID 404 반환")
    void getDepartmentByDepartmentId_404() throws Exception {
        mockMvc.perform(get("/users/departments/dept10")
                        .accept(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("부서 생성 - 201 반환")
    void createDepartment_201() throws Exception {
        DepartmentRequest departmentRequest = new DepartmentRequest("testDept", "인사부");

        mockMvc.perform(post("/users/departments")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(departmentRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("부서 생성 - 409 반환")
    void createDepartment_409() throws Exception {
        DepartmentRequest departmentRequest = new DepartmentRequest("dept1", "인사부");

        mockMvc.perform(post("/users/departments")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(departmentRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("부서 수정 - 204 반환")
    void updateDepartment_204() throws Exception {
        DepartmentRequest departmentRequest = new DepartmentRequest("dept1", "총무부");

        mockMvc.perform(put("/users/departments")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(departmentRequest)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("부서 수정 - 존재하지 않는 부서ID 404 반환")
    void updateDepartment_404() throws Exception {
        DepartmentRequest departmentRequest = new DepartmentRequest("testDept", "총무부");

        mockMvc.perform(put("/users/departments")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(departmentRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("부서 삭제 - 204 반환")
    void deleteDepartment_204() throws Exception {
        mockMvc.perform(delete("/users/departments/dept1")
                        .accept(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("부서 삭제 - 존재하지 않는 부서ID 404 반환")
    void deleteDepartment_409() throws Exception {
        mockMvc.perform(delete("/users/departments/testDept")
                        .accept(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
