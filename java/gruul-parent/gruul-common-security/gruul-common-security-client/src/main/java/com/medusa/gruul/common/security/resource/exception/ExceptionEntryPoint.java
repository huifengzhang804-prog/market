package com.medusa.gruul.common.security.resource.exception;

import com.medusa.gruul.common.security.resource.tool.ResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

/**
 * 认证异常处理
 *
 * @author 张治保
 */
@Slf4j
public final class ExceptionEntryPoint implements AccessDeniedHandler, AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex) throws IOException {
        this.handleError(response, ex);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        this.handleError(response, accessDeniedException);
    }

    @SneakyThrows
    private void handleError(HttpServletResponse response, Throwable throwable) {
        ResponseUtil.response(response, SecureExceptionAdvice.authExceptionMapping(throwable));

    }
}
