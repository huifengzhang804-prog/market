package com.medusa.gruul.common.security.model.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author 张治保
 * @since 2023/11/2
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class EncodeToken implements Serializable {

    /**
     * token
     */
    private String value;

    /**
     * 过期时间
     */
    private LocalDateTime expireAt;

    /**
     * 过期时长 单位：秒
     */
    private Long expiresIn;

}
