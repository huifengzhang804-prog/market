package com.medusa.gruul.addon.ic.modules.opens.uupt.model.enums;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * @since 2024/8/12
 */
@Getter
@RequiredArgsConstructor
public enum OrderState {
    //-1订单取消 -2客服取消（订单状态-1） -3超时无人接单取消（订单状态-1）
    /**
     * -3超时无人接单取消（订单状态-1）
     */
    CANCEL_3(-3, "超时无人接单取消"),

    /**
     * 2客服取消（订单状态-1）
     */
    CANCEL_2(-2, "客服取消"),

    /**
     * -1订单取消
     */
    CANCEL_1(-1, "订单取消"),

    /**
     * 当前状态1下单成功
     */
    ORDERED(1, "下单成功，待接单"),

    /**
     * 骑手取消订单 [跑男取消（订单状态回退到1]
     */
    ORDER_CANCELLED(2, "跑男取消，订单状态回退到待接单单状态"),

    /**
     * 已被骑手接单 3跑男抢单
     */
    ORDER_ACCEPTED(3, "跑男抢单"),

    /**
     * 骑手已到店 等待取货 4已到达
     */
    ARRIVED_FOR_PICK(4, "已到达 等待取货 "),

    /**
     * 骑手已取件 5已取件
     */
    PICKED_UP(5, "已取件"),

    /**
     * 已送达 6到达目的地
     */
    ARRIVED(6, "到达目的地"),

    /**
     * 已收货 10收件人已收货
     */
    RECEIVED(10, "收件人已收货");
    @JSONField
    private final int value;

    /**
     * 是否出现异常
     */
    private final String desc;
}
