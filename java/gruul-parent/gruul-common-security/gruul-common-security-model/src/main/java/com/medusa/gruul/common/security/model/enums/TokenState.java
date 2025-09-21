package com.medusa.gruul.common.security.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * @since 2023/11/23
 */
@RequiredArgsConstructor
@Getter
public enum TokenState {

    /**
     * 有效
     */
    ONLINE,

    /**
     * 被主动下线
     */
    KICK,

    /**
     * 重置用户资料
     */
    REFRESH

}
