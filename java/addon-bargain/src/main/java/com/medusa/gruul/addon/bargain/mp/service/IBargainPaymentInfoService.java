package com.medusa.gruul.addon.bargain.mp.service;

import com.medusa.gruul.addon.bargain.mp.entity.BargainPaymentInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.order.api.model.OrderPaidBroadcastDTO;
import com.medusa.gruul.order.api.pojo.OrderInfo;

/**
 * 砍价活动支付信息 服务类
 *
 * @author WuDi
 * @since 2023-03-25
 */
public interface IBargainPaymentInfoService extends IService<BargainPaymentInfo> {


    /**
     * 砍价活动支付信息
     * @param orderPaidBroadcast 订单支付信息
     */
    void bargainPaymentInfo(OrderPaidBroadcastDTO orderPaidBroadcast);


    /**
     * 砍价活动支付退款
     * @param orderInfo 订单信息
     */
    void bargainRefundInfo(OrderInfo orderInfo);
}
