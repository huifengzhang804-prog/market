package com.medusa.gruul.common.security.server.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 刷新 token登录认证请求参数
 *
 * @author 张治保
 * @since 2023/11/2
 */
@Getter
@Setter
@Accessors(chain = true)
public final class RefreshTokenRequest extends AuthenticationRequest {
    /**
     * 刷新token
     */
    private String value;
}