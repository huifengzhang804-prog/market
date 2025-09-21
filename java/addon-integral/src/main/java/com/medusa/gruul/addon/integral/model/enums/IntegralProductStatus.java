package com.medusa.gruul.addon.integral.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 积分商品状态
 *
 * @author xiaoq
 * @Description 积分商品状态
 * @date 2023-01-31 14:37
 */
@Getter
@RequiredArgsConstructor
public enum IntegralProductStatus {

    /**
     * 下架
     */
    SELL_OFF(0),
    /**
     * 上架
     */
    SELL_ON(1),
    ;
    /**
     * 值
     */
    @EnumValue
    private final int status;
}
