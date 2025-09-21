package com.medusa.gruul.addon.ic.modules.opens.judanke.model.enums;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 订单状态类型
 * 类型：
 * order.create=创建外卖订单推送；
 * order.confirm=外卖订单接单推送；
 * order.complete=外卖订单已完成推送；
 * order.refund.apply=顾客申请退款推送；
 * order.refund.agree=商家同意退款申请推送；
 * order.refund.disagree=商家拒绝退款申请推送；
 * order.status.update=订单状态更新推送；
 * order.cancel=顾客取消订单推送
 *
 * @author 张治保
 * @since 2024/8/8
 */
@Getter
@RequiredArgsConstructor
public enum OrderStatusType {
    /**
     * order.create=创建外卖订单推送；
     */
    CREATE("order.create"),

    /**
     * order.confirm=外卖订单接单推送
     */
    CONFIRM("order.confirm"),

    /**
     * order.complete=外卖订单已完成推送
     */
    COMPLETE("order.complete"),

    /**
     * order.refund.apply=顾客申请退款推送
     */
    REFUND_APPLY("order.refund.apply"),

    /**
     * order.refund.agree=商家同意退款申请推送；
     */
    REFUND_AGREE("order.refund.agree"),

    /**
     * order.refund.disagree=商家拒绝退款申请推送；
     */
    REFUND_DISAGREE("order.refund.disagree"),

    /**
     * order.status.update=订单状态更新推送；
     */
    STATUS_UPDATE("order.status.update"),

    /**
     * order.cancel=顾客取消订单推送
     */
    CANCEL("order.cancel");

    @JSONField
    private final String value;
}
