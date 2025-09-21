package com.medusa.gruul.addon.coupon.service;

import com.medusa.gruul.order.api.pojo.OrderInfo;

/**
 * @author 张治保
 * date 2022/11/11
 */
public interface CouponOnOrderCloseService {

    /**
     * 订单关闭时的处理
     *
     * @param orderInfo 订单信息
     */
    void orderClose(OrderInfo orderInfo);
}
