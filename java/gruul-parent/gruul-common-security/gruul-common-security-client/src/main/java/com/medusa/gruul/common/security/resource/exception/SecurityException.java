package com.medusa.gruul.common.security.resource.exception;

import com.medusa.gruul.global.model.exception.Error;
import com.medusa.gruul.global.model.exception.GlobalException;
import org.springframework.security.core.AuthenticationException;

/**
 * 自行封装的安全异常
 *
 * @author 张治保
 * @since 2023/11/3
 */
public class SecurityException extends AuthenticationException {

    private SecurityException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

    public static AuthenticationException of(Error error) {
        if (error instanceof GlobalException globalException) {
            return of(globalException);
        }
        return new SecurityException(error.exception());
    }

    public static AuthenticationException of(GlobalException exception) {
        return new SecurityException(exception);
    }
}
