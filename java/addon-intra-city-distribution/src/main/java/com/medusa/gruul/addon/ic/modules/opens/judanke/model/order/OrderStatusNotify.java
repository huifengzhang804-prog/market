package com.medusa.gruul.addon.ic.modules.opens.judanke.model.order;

import com.medusa.gruul.addon.ic.modules.opens.judanke.model.enums.OrderStatusType;
import com.medusa.gruul.addon.ic.modules.opens.judanke.model.enums.DeliveryBrand;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 张治保
 * @since 2024/8/8
 */
@Getter
@Setter
@ToString
public class OrderStatusNotify {


    /**
     * type	string	是
     * 类型：
     * order.create=创建外卖订单推送；
     * order.confirm=外卖订单接单推送；
     * order.complete=外卖订单已完成推送；
     * order.refund.apply=顾客申请退款推送；
     * order.refund.agree=商家同意退款申请推送；
     * order.refund.disagree=商家拒绝退款申请推送；
     * order.status.update=订单状态更新推送；
     * order.cancel=顾客取消订单推送
     */
    private OrderStatusType type;

    /**
     * order_id	int	是
     * 订单号
     */
    private Long orderId;

    /**
     * source_brand	string	是
     * 外卖平台 美团 meituan 饿了么 ele
     */
    private DeliveryBrand brand;

    /**
     * source_order_no	string	是
     * 外卖订单号
     */
    private String sourceOrderNo;

    /**
     * merchant_id	string	是
     * 美团商家ID
     */
    private String merchantId;

    /**
     * refund_info	object	否
     * 退款信息（退款状态下才有）
     */
    private Object refundInfo;

    /**
     * shop_id	int	是
     * 本地店铺id (绑定外卖平台接口关联的shop_id)
     */
    private Long shopId;

    /**
     * day_seq	int	是
     * 外卖平台今日序号
     */
    private Integer daySeq;
}
