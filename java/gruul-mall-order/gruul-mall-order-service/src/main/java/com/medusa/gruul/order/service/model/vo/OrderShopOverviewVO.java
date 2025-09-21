package com.medusa.gruul.order.service.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 店铺订单概览 统计
 *
 * @author 张治保
 * date 2022/10/25
 */
@Getter
@Setter
@ToString
public class OrderShopOverviewVO {

    /**
     * 待付款订单数
     */
    private Long unpaid = 0L;

    /**
     * 待发货订单数
     */
    private Long undelivered = 0L;

    /**
     * 待收货订单数
     */
    private Long unreceived = 0L;
}
