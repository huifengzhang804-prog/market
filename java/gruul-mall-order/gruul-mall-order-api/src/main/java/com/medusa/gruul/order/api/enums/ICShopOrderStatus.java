package com.medusa.gruul.order.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 同城单配送状态
 *
 * @author 张治保
 * @since 2024/8/27
 */
@Getter
@RequiredArgsConstructor
public enum ICShopOrderStatus {
    /**
     * 运费价格计算 待发单（未发货）
     * 这种状态的单子可以随时丢弃
     */
    PRICE_PADDING(1, ""),

    /**
     * 待接单
     */
    PENDING(2, "shippingTime"),

    /**
     * 已接单待到店
     */
    TAKEN(3, "takeOrderTime"),

    /**
     * 已到店 待取货
     */
    ARRIVED_SHOP(4, "arrivalShopTime"),

    /**
     * 已取货 配送中
     */
    PICKUP(5, "pickupTime"),

    /**
     * 已送达
     */
    DELIVERED(6, "deliveredTime"),

    /**
     * 异常 配送异常
     */
    ERROR(7, "errorTime");

    @EnumValue
    private final int value;


    /**
     * 对应进度的时间字段名
     */
    private final String timeFiled;

}
