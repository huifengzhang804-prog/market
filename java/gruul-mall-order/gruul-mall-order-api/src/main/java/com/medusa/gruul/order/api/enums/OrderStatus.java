package com.medusa.gruul.order.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 订单状态
 *
 * @author 张治保
 * date 2022/3/7
 */
@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    /**
     * 1 未支付
     */
    UNPAID(1, Boolean.FALSE, ShopOrderStatus.OK, Boolean.FALSE),

    /**
     * 2 已支付
     */
    PAID(2, Boolean.FALSE, ShopOrderStatus.OK, Boolean.FALSE),

    /**
     * 3 买家关闭订单
     */
    BUYER_CLOSED(3, Boolean.TRUE, ShopOrderStatus.BUYER_CLOSED, Boolean.FALSE),

    /**
     * 4 超时未支付 系统关闭
     */
    SYSTEM_CLOSED(4, Boolean.TRUE, ShopOrderStatus.SYSTEM_CLOSED, Boolean.TRUE),

    /**
     * 5 卖家关闭订单
     */
    SELLER_CLOSED(5, Boolean.TRUE, ShopOrderStatus.SELLER_CLOSED, Boolean.FALSE),

    /**
     * 7  拼团中
     */
    TEAMING(6, Boolean.FALSE, ShopOrderStatus.OK, Boolean.FALSE),

    /**
     * 拼团失败
     */
    TEAM_FAIL(7, Boolean.TRUE, ShopOrderStatus.SYSTEM_CLOSED, Boolean.TRUE);


    /**
     * db值
     */
    @EnumValue
    private final Integer value;
    /**
     * 是否已取消
     */
    private final boolean closed;
    /**
     * 对应的 店铺订单状态
     */
    private final ShopOrderStatus shopOrderStatus;

    /**
     * 是否是系统操作状态
     */
    private final boolean system;
}
