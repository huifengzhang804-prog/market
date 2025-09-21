package com.medusa.gruul.common.mp.exception;

import com.medusa.gruul.global.model.exception.GlobalException;

/**
 * @author 张治保
 * date 2022/3/25
 */
public class TenantException extends GlobalException {

    public TenantException(int code, String message) {
        super(code, message);
    }

    public TenantException(String message) {
        super(message);
    }

    public TenantException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public TenantException(String message, Throwable cause) {
        super(message, cause);
    }
}
