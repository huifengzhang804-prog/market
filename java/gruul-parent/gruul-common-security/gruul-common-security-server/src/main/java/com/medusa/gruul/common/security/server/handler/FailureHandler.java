package com.medusa.gruul.common.security.server.handler;

import com.medusa.gruul.common.security.resource.exception.SecureExceptionAdvice;
import com.medusa.gruul.common.security.resource.tool.ResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

/**
 * @author 张治保
 * @since 2023/11/2
 */
public final class FailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        ResponseUtil.response(
                response,
                SecureExceptionAdvice.authExceptionMapping(exception)
        );
    }
}
