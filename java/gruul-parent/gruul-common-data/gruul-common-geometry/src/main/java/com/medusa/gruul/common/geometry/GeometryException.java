package com.medusa.gruul.common.geometry;

import com.medusa.gruul.global.model.exception.GlobalException;

/**
 * @author 张治保
 * date 2022/3/25
 */
public class GeometryException extends GlobalException {

    public GeometryException(int code, String message) {
        super(code, message);
    }

    public GeometryException(String message) {
        super(message);
    }

    public GeometryException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public GeometryException(String message, Throwable cause) {
        super(message, cause);
    }
}
