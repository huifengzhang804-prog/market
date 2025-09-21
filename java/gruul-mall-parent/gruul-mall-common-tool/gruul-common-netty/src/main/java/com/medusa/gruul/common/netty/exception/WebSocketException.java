package com.medusa.gruul.common.netty.exception;

import com.medusa.gruul.global.model.exception.GlobalException;

import java.io.Serial;

/**
 * @author miskw
 * WebSocketException
 */
public class WebSocketException extends GlobalException {

    @Serial
    private static final long serialVersionUID = 1L;

    public WebSocketException(String message) {
        super(message);
    }

    public WebSocketException(String message, Throwable cause) {
        super(message, cause);
    }
}
