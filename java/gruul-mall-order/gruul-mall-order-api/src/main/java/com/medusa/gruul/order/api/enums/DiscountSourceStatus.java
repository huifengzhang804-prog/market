package com.medusa.gruul.order.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 优惠项状态
 *
 * @author 张治保
 * date 2022/6/8
 */
@Getter
@RequiredArgsConstructor
public enum DiscountSourceStatus {
    /**
     * 正常
     */
    OK(1, false),

    /**
     * 关闭
     */
    CLOSED(2, true);


    @EnumValue
    private final Integer value;

    /**
     * 是否已关闭,
     */
    private final boolean closed;
}
