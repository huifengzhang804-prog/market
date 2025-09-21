package com.medusa.gruul.user.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 开卡方式
 *
 * @author xiaoq
 * @Description OpenType.java
 * @date 2022-11-17 10:22
 */
@Getter
@RequiredArgsConstructor
public enum OpenType {
    /**
     * 付费购买
     */
    PAID_BUY(0),

    /**
     * 成长值达标
     */
    GROWTH_VALUE_REACH(1),;

    /**
     *  // 平台赠送
     * 1.PLATFORM_GIVE
     *
     * // 购买指定商品
     * 2. BUY_ASSIGN_GOODS
     */
    @EnumValue
    private final Integer value;
}
