package com.medusa.gruul.order.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * date 2022/6/17
 */
@Getter
@RequiredArgsConstructor
public enum ShopOrderStatus {

    /**
     * 正常状态
     */
    OK(1, false),

    /**
     * 系统关闭
     */
    SYSTEM_CLOSED(2, true),

    /**
     * 买家关闭订单
     */
    BUYER_CLOSED(3, true),

    /**
     * 卖家关闭订单
     */
    SELLER_CLOSED(4, true);

    @EnumValue
    private final Integer value;

    private final boolean closed;
}
