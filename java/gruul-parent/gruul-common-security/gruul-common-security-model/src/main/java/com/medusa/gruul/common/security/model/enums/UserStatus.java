package com.medusa.gruul.common.security.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * date 2022/2/24
 */
@Getter
@RequiredArgsConstructor
public enum  UserStatus {
    /**
     * 正常
     */
    NORMAL(0),
    /**
     * 已过期
     */
    EXPIRED(1),
    /**
     * 已锁定
     */
    LOCKED(2),
    /**
     * 凭证已过期
     */
    CREDENTIALS_EXPIRED(3),
    /**
     * 不可用
     */
    UNAVAILABLE(4);

    @EnumValue
    private final Integer value;
}
