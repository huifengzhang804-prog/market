package com.medusa.gruul.common.security.server.model;

import com.medusa.gruul.common.security.model.bean.EncodeToken;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * 响应数据
 *
 * @author 张治保
 * @since 2023/11/2
 */
@Getter
@Setter
@Accessors(chain = true)
public final class AuthenticationResp extends EncodeToken {

    /**
     * 刷新token
     */
    private EncodeToken refreshToken;

    /**
     * 额外信息
     */
    private Object open = Map.of();
}
