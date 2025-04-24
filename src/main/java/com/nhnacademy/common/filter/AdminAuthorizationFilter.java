package com.nhnacademy.common.filter;

import com.common.AESUtil;
import com.nhnacademy.user.service.UserService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@Order(1)
public class AdminAuthorizationFilter implements Filter {

    private final AESUtil aesUtil;
    private final UserService userService;

    public AdminAuthorizationFilter(AESUtil aesUtil, UserService userService) {
        this.aesUtil = aesUtil;
        this.userService = userService;
    }

    /**
     * 필터가 요청을 처리하는 메서드입니다.
     * <p>
     * /admin으로 시작하는 요청에 대해 관리자 인증 정보를 확인하고,
     * 인증이 되지 않거나 권한이 없는 경우 적절한 HTTP 상태 코드를 반환합니다.
     * </p>
     *
     * @param request  클라이언트의 요청
     * @param response 서버의 응답
     * @param chain    필터 체인
     * @throws IOException      입출력 예외
     * @throws ServletException 서블릿 예외
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String uri = httpRequest.getRequestURI();

        // /admin으로 시작하면 관리자 권한 체크
        if (uri.startsWith("/admin")) {
            String encryptedEmail = httpRequest.getHeader("X-User-Id");

            // 관리자 인증 정보가 없는 경우
            if (encryptedEmail == null) {
                log.debug("관리자 인증 정보 누락");
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "관리자 인증 정보 누락");
                return;
            }

            // AES 복호화하여 이메일 추출 후 사용자 역할 확인
            String email = aesUtil.decrypt(encryptedEmail);
            String role = userService.getUser(email).getUserRole();

            // 관리자 권한이 없는 경우
            if (!"ROLE_ADMIN".equals(role)) {
                log.debug("관리자 권한 없음");
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "관리자 권한 없음");
                return;
            }
        }

        // 필터 체인 진행
        chain.doFilter(request, response);
    }
}