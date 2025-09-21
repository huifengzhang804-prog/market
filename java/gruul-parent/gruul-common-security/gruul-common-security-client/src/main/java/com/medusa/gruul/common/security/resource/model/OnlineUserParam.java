package com.medusa.gruul.common.security.resource.model;

import com.medusa.gruul.common.system.model.model.ClientType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 用于用户上线的参数
 *
 * @author 张治保
 * @since 2023/11/3
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@Accessors(chain = true)
public final class OnlineUserParam implements Serializable {
    /**
     * 客户端类型
     */
    private ClientType clientType;

    /**
     * 店铺 id
     */
    private Long shopId;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * tokenId
     */
    private String tokenId;

    /**
     * refreshToken 过期时间
     */
    private LocalDateTime refreshTokenExpireAt;

    /**
     * refreshToken 过期时间 转换为 Instant
     *
     * @return Instant
     */
    public Instant refreshTokenExpire() {
        return refreshTokenExpireAt.atZone(ZoneId.systemDefault()).toInstant();
    }


}
