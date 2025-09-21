package com.medusa.gruul.addon.ic.modules.opens.uupt.model.order;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author 张治保
 * @since 2024/8/12
 */
@Getter
@Setter
@ToString
public class OrderPriceResp {

    /**
     * 三方对接平台订单ID
     */
    private String originId;

    /**
     * 金额令牌，提交订单前必须先计算价格
     */
    private String priceToken;

    /**
     * priceToken过期时间（单位：秒）
     */
    private Long expiresIn;

    /**
     * 订单总金额（优惠前）(单位分)
     */
    private Long totalMoney;

    /**
     * 实际需要支付金额（单位分）
     */
    private Long needPayMoney;

    /**
     * 总优惠金额（单位分）
     */
    private Long totalPriceOff;

    /**
     * 配送距离（单位：米）
     */
    private Long distance;

    /**
     * 费用明细列表
     */
    private List<OrderPriceDetailResp> feeDetailList;
}
