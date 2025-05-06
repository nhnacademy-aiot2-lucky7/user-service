package com.nhnacademy;

import com.common.AESUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.department.domain.Department;
import com.nhnacademy.department.dto.DepartmentRequest;
import com.nhnacademy.department.repository.DepartmentRepository;
import com.nhnacademy.eventlevel.domain.EventLevel;
import com.nhnacademy.eventlevel.dto.EventLevelRequest;
import com.nhnacademy.eventlevel.repository.EventLevelRepository;
import com.nhnacademy.image.domain.Image;
import com.nhnacademy.image.repository.ImageRepository;
import com.nhnacademy.role.domain.Role;
import com.nhnacademy.role.dto.RoleRequest;
import com.nhnacademy.role.repository.RoleRepository;
import com.nhnacademy.user.domain.User;
import com.nhnacademy.user.dto.*;
import com.nhnacademy.user.repository.UserRepository;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@AutoConfigureRestDocs
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
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
    @Autowired
    private EventLevelRepository eventLevelRepository;

    @BeforeAll
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

        // EventLevel 추가
        eventLevelRepository.save(new EventLevel("INFO", "일반 정보"));
        eventLevelRepository.save(new EventLevel("CRITICAL", "치명적 오류"));
        eventLevelRepository.save(new EventLevel("WARNING", "위험한 정보"));

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
    @DisplayName("회원 가입 - 201 반환")
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
                .andExpect(status().isCreated())
                .andDo(document("signup-success-201"));
    }

    @Test
    @DisplayName("회원 가입 - validate 검증으로 인한 400 반환")
    void signUp_400() throws Exception {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest(
                "testUser",
                "test@email.com",
                "Passw0rd",  // 비밀번호 규칙 위반
                "010-1234-5678",
                "dept1"
        );

        mockMvc.perform(post("/users/auth/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userRegisterRequest)))
                .andExpect(status().isBadRequest())
                .andDo(document("signup-fail-validation-400"));
    }

    @Test
    @DisplayName("회원 가입 - 이메일 중복으로 인한 409 반환")
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
                .andExpect(status().isConflict())
                .andDo(document("signup-fail-email-duplicate-409"));
    }

    @Test
    @DisplayName("회원 가입 - 존재하지 않는 부서 404 반환")
    void signUp_404() throws Exception {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest(
                "testUser",
                "test@test.com",
                "P@ssw0rd",
                "010-1234-5678",
                "dept20"  // 존재하지 않는 부서
        );

        mockMvc.perform(post("/users/auth/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userRegisterRequest)))
                .andExpect(status().isNotFound())
                .andDo(document("signup-fail-department-not-found-404"));
    }

    @Test
    @DisplayName("로그인 - 200 반환")
    void signIn_200() throws Exception {
        UserLoginRequest userLoginRequest = new UserLoginRequest(
                "user1@test.com",
                "P@ssw0rd1"
        );

        mockMvc.perform(post("/users/auth/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userLoginRequest)))
                .andExpect(status().isOk())
                .andDo(document("signin-success-200"));
    }

    @Test
    @DisplayName("로그인 - validate 검증으로 인한 400 반환")
    void signIn_400() throws Exception {
        UserLoginRequest userLoginRequest = new UserLoginRequest(
                "user1@test.com",
                "Passw0rd1"  // 비밀번호 규칙 위반
        );

        mockMvc.perform(post("/users/auth/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userLoginRequest)))
                .andExpect(status().isBadRequest())
                .andDo(document("signin-fail-validation-400"));
    }

    @Test
    @DisplayName("로그인 - 존재하지 않는 유저 404 반환")
    void signIn_404() throws Exception {
        UserLoginRequest userLoginRequest = new UserLoginRequest(
                "user20@test.com",
                "P@ssw0rd1"
        );

        mockMvc.perform(post("/users/auth/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userLoginRequest)))
                .andExpect(status().isNotFound())
                .andDo(document("signin-fail-user-not-found-404"));
    }

    @Test
    @DisplayName("로그인 - 비밀번호 불일치 401 반환")
    void signIn_401() throws Exception {
        UserLoginRequest userLoginRequest = new UserLoginRequest(
                "user1@test.com",
                "P@ssw0rd10"  // 비밀번호 불일치
        );

        mockMvc.perform(post("/users/auth/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userLoginRequest)))
                .andExpect(status().isUnauthorized())
                .andDo(document("signin-fail-invalid-password-401"));
    }

    @Test
    @DisplayName("본인 정보 조회 - 200 반환")
    void getMyInfo_200() throws Exception {
        mockMvc.perform(get("/users/me")
                        .header("X-User-Id", aesUtil.encrypt("user1@test.com"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userEmail").value("user1@test.com"))
                .andExpect(jsonPath("$.userName").value("testUser1"))
                .andDo(document("get-my-info-200"));
    }

    @Test
    @DisplayName("본인 정보 조회 - 존재하지 않는 유저 404 반환")
    void getMyInfo_404() throws Exception {
        mockMvc.perform(get("/users/me")
                        .header("X-User-Id", aesUtil.encrypt("user20@test.com"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(document("get-my-info-fail-user-404-not-found"));
    }

    @Test
    @DisplayName("사용자 정보 수정 - 204 반환")
    void updateMyInfo_204() throws Exception {
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest(
                "testUser1",
                "010-1234-5678",
                "dept3",
                "INFO"
        );

        mockMvc.perform(put("/users/me")
                        .header("X-User-Id", aesUtil.encrypt("user1@test.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userUpdateRequest)))
                .andExpect(status().isNoContent())
                .andDo(document("update-my-info-204"));
    }

    @Test
    @DisplayName("사용자 정보 수정 - validate 검증으로 인한 400 반환")
    void updateMyInfo_400() throws Exception {
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest(
                "testUser1",
                "0101234-5678",
                "dept1",
                "info"
        );

        mockMvc.perform(put("/users/me")
                        .header("X-User-Id", aesUtil.encrypt("user1@test.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userUpdateRequest)))
                .andExpect(status().isBadRequest())
                .andDo(document("update-my-info-400"));
    }

    @Test
    @DisplayName("사용자 정보 수정 - 존재하지 않는 유저 404 반환")
    void updateMyInfo_404_user() throws Exception {
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest(
                "testUser1",
                "010-1234-5678",
                "dept1",
                "info"
        );

        mockMvc.perform(put("/users/me")
                        .header("X-User-Id", aesUtil.encrypt("user20@test.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userUpdateRequest)))
                .andExpect(status().isNotFound())
                .andDo(document("update-my-info-404-user"));
    }

    @Test
    @DisplayName("사용자 정보 수정 - 존재하지 않는 부서 404 반환")
    void updateMyInfo_404_department() throws Exception {
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest(
                "testUser1",
                "010-1234-5678",
                "dept20",
                "info"
        );

        mockMvc.perform(put("/users/me")
                        .header("X-User-Id", aesUtil.encrypt("user1@test.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userUpdateRequest)))
                .andExpect(status().isNotFound())
                .andDo(document("update-my-info-404-department"));
    }

    @Test
    @DisplayName("사용자 정보 수정 - 존재하지 않는 이벤트 레벨 404 반환")
    void updateMyInfo_404_eventLevel() throws Exception {
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest(
                "testUser1",
                "010-1234-5678",
                "dept20",
                "test"
        );

        mockMvc.perform(put("/users/me")
                        .header("X-User-Id", aesUtil.encrypt("user1@test.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userUpdateRequest)))
                .andExpect(status().isNotFound())
                .andDo(document("update-my-info-404-eventLevel"));
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
                .andExpect(status().isNoContent())
                .andDo(document("change-password-204"));
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
                .andExpect(status().isBadRequest())
                .andDo(document("change-password-400-case1"));
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
                .andExpect(status().isBadRequest())
                .andDo(document("change-password-400-case2"));
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
                .andExpect(status().isNotFound())
                .andDo(document("change-password-404"));
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
                .andExpect(status().isUnauthorized())
                .andDo(document("change-password-401"));
    }

    @Test
    @DisplayName("회원 탈퇴 - 204 반환")
    void deleteMyAccount_204() throws Exception {
        mockMvc.perform(delete("/users/me")
                        .header("X-User-Id", aesUtil.encrypt("user1@test.com"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(document("delete-my-account-204"));
    }

    @Test
    @DisplayName("회원 탈퇴 - 존재하지 않는 회원 404 반환")
    void deleteMyAccount_404() throws Exception {
        mockMvc.perform(delete("/users/me")
                        .header("X-User-Id", aesUtil.encrypt("user20@test.com"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(document("delete-my-account-404"));
    }

    // -------------------------admin-------------------------

    @Test
    @DisplayName("모든 사용자 조회 - 200 반환")
    void getAllUser_200() throws Exception {
        mockMvc.perform(get("/admin/users")
                        .header("X-User-Id", aesUtil.encrypt("admin@test.com"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("get-all-users-200"));
    }

    @Test
    @DisplayName("모든 사용자 조회 - 권한 부족 403 반환")
    void getAllUser_401() throws Exception {
        mockMvc.perform(get("/admin/users")
                        .header("X-User-Id", aesUtil.encrypt("user1@test.com"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(print())
                .andDo(document("get-all-users-403"));
    }

    @Test
    @DisplayName("Email 기반 사용자 조회 - 200 반환")
    void getUserById_200() throws Exception {
        mockMvc.perform(get("/admin/users/user2@test.com")
                        .header("X-User-Id", aesUtil.encrypt("admin@test.com"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userEmail").value("user2@test.com"))
                .andExpect(jsonPath("$.userName").value("testUser2"))
                .andDo(document("get-user-by-email-200"));
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
                .andExpect(status().isNoContent())
                .andDo(document("update-user-role-204"));
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
                .andExpect(status().isBadRequest())
                .andDo(document("update-user-role-400"));
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
                .andExpect(status().isNotFound())
                .andDo(document("update-user-role-404-user"));
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
                .andExpect(status().isNotFound())
                .andDo(document("update-user-role-404-role"));
    }

    @Test
    @DisplayName("특정 사용자 회원 탈퇴 - 204 반환")
    void deleteUserByAdmin_204() throws Exception {
        mockMvc.perform(delete("/admin/users/user2@test.com")
                        .header("X-User-Id", aesUtil.encrypt("admin@test.com"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(document("delete-user-by-admin-204"));
    }

    @Test
    @DisplayName("특정 사용자 회원 탈퇴 - 존재하지 않는 사용자 404 반환")
    void deleteUserByAdmin_404() throws Exception {
        mockMvc.perform(delete("/admin/users/user20@test.com")
                        .header("X-User-Id", aesUtil.encrypt("admin@test.com"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(document("delete-user-by-admin-404"));
    }

    // -------------------------image-------------------------

    @Test
    @DisplayName("사용자 이메일 경로 조회 - 200 반환")
    void getImage_200() throws Exception {
        mockMvc.perform(get("/images/user0@test.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imagePath").value("path/to/image0.jpg"))
                .andDo(document("get-image-200"));
    }

    @Test
    @DisplayName("사용자 이메일 경로 조회 - 존재하지 않는 이메일 404 반환")
    void getImage_404_case1() throws Exception {
        mockMvc.perform(get("/images/user20@test.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(document("get-image-404-user-not-found"));
    }

    @Test
    @DisplayName("사용자 이메일 경로 조회 - 이미지 미 등록 404 반환")
    void getImage_404_case2() throws Exception {
        mockMvc.perform(get("/images/user2@test.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(document("get-image-404-image-not-found"));
    }

    @Test
    @DisplayName("사용자 이미지 경로 생성 - 201 반환")
    void createImage_201() throws Exception {
        mockMvc.perform(post("/images")
                        .param("userEmail", "user1@test.com")
                        .param("imagePath", "images/path")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print())
                .andDo(document("create-image-201"));
    }

    @Test
    @DisplayName("사용자 이미지 경로 생성 - 존재하지 않는 사용자 404 반환")
    void createImage_404() throws Exception {
        mockMvc.perform(post("/images")
                        .param("userEmail", "user20@test.com")
                        .param("imagePath", "images/path")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andDo(document("create-image-404-user-not-found"));
    }

    @Test
    @DisplayName("사용자 이미지 경로 수정 - 204 반환")
    void updateImage_204() throws Exception {
        mockMvc.perform(put("/images")
                        .param("userEmail", "user0@test.com")
                        .param("imagePath", "images/path")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("update-image-204"));
    }

    @Test
    @DisplayName("사용자 이미지 경로 수정 - 존재하지 않는 사용자 404 반환")
    void updateImage_404_case1() throws Exception {
        mockMvc.perform(put("/images")
                        .param("userEmail", "user20@test.com")
                        .param("imagePath", "images/path")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andDo(document("update-image-404-user-not-found"));
    }

    @Test
    @DisplayName("사용자 이미지 경로 수정 - 이미지 미등록 404 반환")
    void updateImage_404_case2() throws Exception {
        mockMvc.perform(put("/images")
                        .param("userEmail", "user1@test.com")
                        .param("imagePath", "images/path")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andDo(document("update-image-404-image-not-found"));
    }

    @Test
    @DisplayName("사용자 이미지 경로 삭제 - 204 반환")
    void deleteImage_204() throws Exception {
        mockMvc.perform(delete("/images/{userEmail}", "user0@test.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("delete-image-204"));
    }

    @Test
    @DisplayName("사용자 이미지 경로 삭제 - 존재하지 않는 사용자 404 반환")
    void deleteImage_404_case1() throws Exception {
        mockMvc.perform(delete("/images/{userEmail}", "user20@test.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andDo(document("delete-image-404-user-not-found"));
    }

    @Test
    @DisplayName("사용자 이미지 경로 삭제 - 이미지 미등록 404 반환")
    void deleteImage_404_case2() throws Exception {
        mockMvc.perform(delete("/images/{userEmail}", "user1@test.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andDo(document("delete-image-404-image-not-found"));
    }

    // -------------------------role-------------------------

    @Test
    @DisplayName("모든 권한 조회 - 200 반환")
    void getAllRole_200() throws Exception {
        mockMvc.perform(get("/users/roles")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("get-all-role-200"));
    }

    @Test
    @DisplayName("권한ID에 따른 조회 - 200 반환")
    void getRoleByRoleId_200() throws Exception {
        mockMvc.perform(get("/users/roles/ROLE_MEMBER")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roleId").value("ROLE_MEMBER"))
                .andExpect(jsonPath("$.roleName").value("일반 회원"))
                .andDo(document("get-role-by-role-id-200"));
    }

    @Test
    @DisplayName("권한ID에 따른 조회 - 권한ID 존재하지 않음 404 반환")
    void getRoleByRoleId_404() throws Exception {
        mockMvc.perform(get("/users/roles/ROLE_TEST")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(document("get-role-by-role-id-404-not-found"));
    }

    @Test
    @DisplayName("권한 생성 - 201 반환")
    void createRole_201() throws Exception {
        RoleRequest roleRequest = new RoleRequest("ROLE_TEST", "테스트 권한");

        mockMvc.perform(post("/admin/roles")
                        .header("X-User-Id", aesUtil.encrypt("admin@test.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(roleRequest)))
                .andExpect(status().isCreated())
                .andDo(document("create-role-201"));
    }

    @Test
    @DisplayName("권한 생성 - 이미 존재하는 roleId 409 반환")
    void createRole_409() throws Exception {
        RoleRequest roleRequest = new RoleRequest("ROLE_MEMBER", "일반 회원");

        mockMvc.perform(post("/admin/roles")
                        .header("X-User-Id", aesUtil.encrypt("admin@test.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(roleRequest)))
                .andExpect(status().isConflict())
                .andDo(document("create-role-409-conflict"));
    }

    @Test
    @DisplayName("권한 수정 - 204 반환")
    void updateRole_204() throws Exception {
        RoleRequest roleRequest = new RoleRequest("ROLE_MEMBER", "테스트 권한");

        mockMvc.perform(put("/admin/roles")
                        .header("X-User-Id", aesUtil.encrypt("admin@test.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(roleRequest)))
                .andExpect(status().isNoContent())
                .andDo(document("update-role-204"));
    }

    @Test
    @DisplayName("권한 수정 - 존재하지 않는 roleId 404 반환")
    void updateRole_404() throws Exception {
        RoleRequest roleRequest = new RoleRequest("ROLE_TEST", "테스트 권한");

        mockMvc.perform(put("/admin/roles")
                        .header("X-User-Id", aesUtil.encrypt("admin@test.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(roleRequest)))
                .andExpect(status().isNotFound())
                .andDo(document("update-role-404-not-found"));
    }

    @Test
    @DisplayName("권한 삭제 - 204 반환")
    void deleteRole_204() throws Exception {
        mockMvc.perform(delete("/admin/roles/ROLE_MEMBER")
                        .header("X-User-Id", aesUtil.encrypt("admin@test.com"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(document("delete-role-204"));
    }

    @Test
    @DisplayName("권한 삭제 - 존재하지 않는 roleId 404 반환")
    void deleteRole_404() throws Exception {
        mockMvc.perform(delete("/admin/roles/ROLE_TEST")
                        .header("X-User-Id", aesUtil.encrypt("admin@test.com"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(document("delete-role-404-not-found"));
    }

    // -------------------------department-------------------------

    @Test
    @DisplayName("모든 부서 조회 - 200 반환")
    void getAllDepartment_200() throws Exception {
        mockMvc.perform(get("/users/departments")
                        .accept(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("get-all-department-200"));
    }

    @Test
    @DisplayName("부서ID에 따른 조회 - 200 반환")
    void getDepartmentByDepartmentId_200() throws Exception {
        mockMvc.perform(get("/users/departments/dept1")
                        .accept(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.departmentId").value("dept1"))
                .andExpect(jsonPath("$.departmentName").value("부서명1"))
                .andDo(document("get-department-by-department-id-200"));
    }

    @Test
    @DisplayName("부서ID에 따른 조회 - 존재하지 않는 부서ID 404 반환")
    void getDepartmentByDepartmentId_404() throws Exception {
        mockMvc.perform(get("/users/departments/dept10")
                        .accept(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(document("get-department-by-department-id-404-not-found"));
    }

    @Test
    @DisplayName("부서 생성 - 201 반환")
    void createDepartment_201() throws Exception {
        DepartmentRequest departmentRequest = new DepartmentRequest("testDept", "인사부");

        mockMvc.perform(post("/admin/departments")
                        .header("X-User-Id", aesUtil.encrypt("admin@test.com"))
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(departmentRequest)))
                .andExpect(status().isCreated())
                .andDo(document("create-department-201"));
    }

    @Test
    @DisplayName("부서 생성 - 409 반환")
    void createDepartment_409() throws Exception {
        DepartmentRequest departmentRequest = new DepartmentRequest("dept1", "인사부");

        mockMvc.perform(post("/admin/departments")
                        .header("X-User-Id", aesUtil.encrypt("admin@test.com"))
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(departmentRequest)))
                .andExpect(status().isConflict())
                .andDo(document("create-department-409-conflict"));
    }

    @Test
    @DisplayName("부서 수정 - 204 반환")
    void updateDepartment_204() throws Exception {
        DepartmentRequest departmentRequest = new DepartmentRequest("dept1", "총무부");

        mockMvc.perform(put("/admin/departments")
                        .header("X-User-Id", aesUtil.encrypt("admin@test.com"))
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(departmentRequest)))
                .andExpect(status().isNoContent())
                .andDo(document("update-department-204"));
    }

    @Test
    @DisplayName("부서 수정 - 존재하지 않는 부서ID 404 반환")
    void updateDepartment_404() throws Exception {
        DepartmentRequest departmentRequest = new DepartmentRequest("testDept", "총무부");

        mockMvc.perform(put("/admin/departments")
                        .header("X-User-Id", aesUtil.encrypt("admin@test.com"))
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(departmentRequest)))
                .andExpect(status().isNotFound())
                .andDo(document("update-department-404-not-found"));
    }

    @Test
    @DisplayName("부서 삭제 - 204 반환")
    void deleteDepartment_204() throws Exception {
        mockMvc.perform(delete("/admin/departments/dept1")
                        .header("X-User-Id", aesUtil.encrypt("admin@test.com"))
                        .accept(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(document("delete-department-204"));
    }

    @Test
    @DisplayName("부서 삭제 - 존재하지 않는 부서ID 404 반환")
    void deleteDepartment_409() throws Exception {
        mockMvc.perform(delete("/admin/departments/testDept")
                        .header("X-User-Id", aesUtil.encrypt("admin@test.com"))
                        .accept(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(document("delete-department-404-not-found"));
    }

    // -------------------------eventLevel-------------------------

    @Test
    @DisplayName("모든 이벤트 레벨 조회 - 200 반환")
    void getAllEventLevel_200() throws Exception {
        mockMvc.perform(get("/users/event-levels")
                        .accept(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("get-all-event-level-200"));
    }

    @Test
    @DisplayName("이벤트 레벨명으로 조회 - 200 반환")
    void getEventLevelByLevelName_200() throws Exception {
        mockMvc.perform(get("/users/event-levels/CRITICAL")
                        .accept(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.levelName").value("CRITICAL"))
                .andExpect(jsonPath("$.levelDetails").value("치명적 오류"))
                .andDo(document("get-event-level-by-name-200"));
    }

    @Test
    @DisplayName("이벤트 레벨명으로 조회 - 존재하지 않는 이름 404 반환")
    void getEventLevelByLevelName_404() throws Exception {
        mockMvc.perform(get("/users/event-levels/UNKNOWN")
                        .accept(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(document("get-event-level-by-name-404-not-found"));
    }

    @Test
    @DisplayName("이벤트 레벨 생성 - 201 반환")
    void createEventLevel_201() throws Exception {
        EventLevelRequest request = new EventLevelRequest("UNKNOWN", "정보성 메시지");

        mockMvc.perform(post("/admin/event-levels")
                        .header("X-User-Id", aesUtil.encrypt("admin@test.com"))
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andDo(document("create-event-level-201"));
    }

    @Test
    @DisplayName("이벤트 레벨 생성 - 중복으로 인한 409 반환")
    void createEventLevel_409() throws Exception {
        EventLevelRequest request = new EventLevelRequest("CRITICAL", "중복된 레벨");

        mockMvc.perform(post("/admin/event-levels")
                        .header("X-User-Id", aesUtil.encrypt("admin@test.com"))
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andDo(document("create-event-level-409-conflict"));
    }

    @Test
    @DisplayName("이벤트 레벨 수정 - 204 반환")
    void updateEventLevel_204() throws Exception {
        EventLevelRequest request = new EventLevelRequest("INFO", "수정된 정보 메시지");

        mockMvc.perform(put("/admin/event-levels")
                        .header("X-User-Id", aesUtil.encrypt("admin@test.com"))
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isNoContent())
                .andDo(document("update-event-level-204"));
    }

    @Test
    @DisplayName("이벤트 레벨 수정 - 존재하지 않는 레벨 404 반환")
    void updateEventLevel_404() throws Exception {
        EventLevelRequest request = new EventLevelRequest("UNKNOWN", "없는 레벨");

        mockMvc.perform(put("/admin/event-levels")
                        .header("X-User-Id", aesUtil.encrypt("admin@test.com"))
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andDo(document("update-event-level-404-not-found"));
    }

    @Test
    @DisplayName("이벤트 레벨 삭제 - 204 반환")
    void deleteEventLevel_204() throws Exception {
        mockMvc.perform(delete("/admin/event-levels/WARNING")
                        .header("X-User-Id", aesUtil.encrypt("admin@test.com"))
                        .accept(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(document("delete-event-level-204"));
    }

    @Test
    @DisplayName("이벤트 레벨 삭제 - 존재하지 않는 레벨명 404 반환")
    void deleteEventLevel_404() throws Exception {
        mockMvc.perform(delete("/admin/event-levels/UNKNOWN")
                        .header("X-User-Id", aesUtil.encrypt("admin@test.com"))
                        .accept(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(document("delete-event-level-404-not-found"));
    }

}
