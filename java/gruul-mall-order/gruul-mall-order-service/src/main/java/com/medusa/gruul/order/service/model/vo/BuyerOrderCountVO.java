package com.medusa.gruul.order.service.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 *
 *     买家个人订单统计
 *
 *
 * @author 张治保
 * date 2022/6/17
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class BuyerOrderCountVO {

    /**
     * 待付款订单
     */
    private Long unpaid;

    /**
     * 待发货
     */
    private Long undelivered;

    /**
     * 待收货
     */
    private Long unreceived;

    /**
     * 待评价
     */
    private Long uncommented;

    /**
     * 未处理的售后工单
     */
    private Long unhandledAfs;

}
