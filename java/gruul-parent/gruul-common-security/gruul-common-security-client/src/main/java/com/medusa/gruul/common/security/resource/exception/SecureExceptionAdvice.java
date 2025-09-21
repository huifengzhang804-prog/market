package com.medusa.gruul.common.security.resource.exception;

import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.model.enums.SecureCodes;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.exception.Error;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.security.web.authentication.rememberme.CookieTheftException;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.www.NonceExpiredException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 异常处理器
 *
 * @author 张治保
 * date 2022/3/1
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class SecureExceptionAdvice {

    /**
     * 认证异常映射
     *
     * @param ex 异常
     * @return Result
     */
    private static Result<Void> authExMapping(AuthenticationException ex) {
        if (ex.getCause() instanceof Error error) {
            return Result.failed(error);
        }
        if (isMachInstance(ex, InsufficientAuthenticationException.class)) {
            return Result.failed(SecureCodes.NEED_LOGIN);
        }
        if (isMachInstance(
                ex,
                UsernameNotFoundException.class,
                AuthenticationCredentialsNotFoundException.class,
                BadCredentialsException.class,
                CredentialsExpiredException.class,
                PreAuthenticatedCredentialsNotFoundException.class
        )
        ) {
            return Result.failed(SecureCodes.USERNAME_PASSWORD_INVALID);
        }

        if (isMachInstance(
                ex,
                AuthenticationServiceException.class,
                InternalAuthenticationServiceException.class,
                ProviderNotFoundException.class
        )) {
            return Result.failed(SecureCodes.AUTH_SERVER_ERROR);
        }
        if (isMachInstance(
                ex,
                AccountExpiredException.class,
                DisabledException.class,
                LockedException.class,
                NonceExpiredException.class)) {
            return Result.failed(SecureCodes.ACCOUNT_INVALID);
        }

        if (isMachInstance(
                ex,
                RememberMeAuthenticationException.class,
                CookieTheftException.class,
                InvalidCookieException.class,
                SessionAuthenticationException.class

        )) {
            return Result.failed(SecureCodes.ACCESS_DENIED);
        }
        return Result.failed(ex.getMessage());
    }

    /**
     * 认证异常处理
     *
     * @param ex 异常
     * @return Result
     */

    public static Result<Void> authExceptionMapping(Throwable ex) {
        log.debug("authExceptionMapping", ex);
        if (ex instanceof AuthenticationException authenticationException) {
            return authExMapping(authenticationException);
        }
        if (ex.getCause() instanceof AuthenticationException authenticationException) {
            return authExMapping(authenticationException);
        }
        if (ex instanceof AccessDeniedException && ISecurity.isAnonymous()) {
            return Result.failed(SecureCodes.NEED_LOGIN);
        }
        return Result.failed(SecureCodes.PERMISSION_DENIED);
    }

    /**
     * 判断ex 是否在classes实现类型中
     */
    public static boolean isMachInstance(Object ex, Class<?>... classes) {
        for (Class<?> clazz : classes) {
            if (clazz.isAssignableFrom(ex.getClass())) {
                return true;
            }
        }
        return false;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Result<Void> accessDeniedException(AccessDeniedException ex) {
        return SecureExceptionAdvice.authExceptionMapping(ex);
    }

    /**
     * AuthenticationException 认证服务异常
     */
    @ExceptionHandler(AuthenticationException.class)
    public Result<Void> authException(AuthenticationException ex) {
        return SecureExceptionAdvice.authExceptionMapping(ex);
    }

}
