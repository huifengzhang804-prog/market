package com.medusa.gruul.addon.integral.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 积分订单来源enum
 *
 * @author xiaoq
 * @Description 积分订单来源enum
 * @date 2023-01-31 13:37
 */
@Getter
@RequiredArgsConstructor
public enum IntegralOrderSourceType {

    /**
     * 商品详情
     */
    PRODUCT(1);

    @EnumValue
    private final Integer value;
}
