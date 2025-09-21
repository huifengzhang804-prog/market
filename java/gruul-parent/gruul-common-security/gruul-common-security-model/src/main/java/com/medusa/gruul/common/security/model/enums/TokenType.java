package com.medusa.gruul.common.security.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * @since 2023/11/3
 */

@Getter
@RequiredArgsConstructor
public enum TokenType {
    /**
     * token
     */
    T(SecureCodes.TOKEN_EXPIRED, SecureCodes.TOKEN_INVALID),

    /**
     * 刷新token
     */
    RT(SecureCodes.REFRESH_TOKEN_EXPIRED, SecureCodes.REFRESH_TOKEN_INVALID);
    /**
     * claim type key
     */
    public static final String TYPE = "T";
    /**
     * claim payload key
     */
    public static final String KEY = "K";

    /**
     * 过期code异常
     */
    private final SecureCodes expired;

    /**
     * token 失效 code 异常
     */
    private final SecureCodes invalid;


}
