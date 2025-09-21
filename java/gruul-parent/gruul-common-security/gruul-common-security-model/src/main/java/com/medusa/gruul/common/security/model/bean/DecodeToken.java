package com.medusa.gruul.common.security.model.bean;

import com.medusa.gruul.common.security.model.enums.TokenType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author 张治保
 * @since 2023/11/3
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class DecodeToken implements Serializable {

    /**
     * jti
     */
    private String tokenId;

    /**
     * token 类型
     */
    private TokenType tokenType;

    /**
     * 是否过期
     */
    private boolean expired;

    /**
     * 用户信息
     */
    private SecureUser<?> secureUser;

    /**
     * 签名时间
     */
    private LocalDateTime issuedAt;

    /**
     * 过期时间
     */
    private LocalDateTime expireAt;
}
