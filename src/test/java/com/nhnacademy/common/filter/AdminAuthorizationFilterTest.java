package com.nhnacademy.common.filter;

import com.common.AESUtil;
import com.nhnacademy.user.dto.UserResponse;
import com.nhnacademy.user.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminAuthorizationFilterTest {

    @Mock
    AESUtil aesUtil;

    @Mock
    UserService userService;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    FilterChain chain;

    @InjectMocks
    AdminAuthorizationFilter filter;

    @BeforeEach
    void setUp() {
        aesUtil = mock(AESUtil.class);
        userService = mock(UserService.class);
        filter = new AdminAuthorizationFilter(aesUtil, userService);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        chain = mock(FilterChain.class);
    }

    @Test
    @DisplayName("정상적인 관리자 접근 - 필터 통과")
    void adminAccess_withValidAdminRole_passesThroughFilter() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/admin/dashboard");
        when(request.getHeader("X-User-Id")).thenReturn("encryptedEmail");
        when(aesUtil.decrypt("encryptedEmail")).thenReturn("admin@example.com");

        UserResponse userResponse = mock(UserResponse.class);
        when(userResponse.getUserRole()).thenReturn("ROLE_ADMIN");
        when(userService.getUser("admin@example.com")).thenReturn(userResponse);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(response, never()).sendError(anyInt(), anyString());
    }

    @Test
    @DisplayName("관리자 헤더 누락 - 401 반환")
    void adminAccess_withoutHeader_returnsUnauthorized() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/admin/dashboard");
        when(request.getHeader("X-User-Id")).thenReturn(null);

        filter.doFilter(request, response, chain);

        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "관리자 인증 정보 누락");
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("관리자 아님 - 403 반환")
    void adminAccess_withNonAdminRole_returnsForbidden() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/admin/dashboard");
        when(request.getHeader("X-User-Id")).thenReturn("encryptedEmail");
        when(aesUtil.decrypt("encryptedEmail")).thenReturn("user@example.com");

        UserResponse userResponse = mock(UserResponse.class);
        when(userResponse.getUserRole()).thenReturn("ROLE_USER");
        when(userService.getUser(anyString())).thenReturn(userResponse);

        filter.doFilter(request, response, chain);

        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "관리자 권한 없음");
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("일반 URI 접근 - 필터 체크 없이 통과")
    void nonAdminUri_shouldPassThroughWithoutChecking() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/user/profile");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(response, never()).sendError(anyInt(), anyString());
    }
}
