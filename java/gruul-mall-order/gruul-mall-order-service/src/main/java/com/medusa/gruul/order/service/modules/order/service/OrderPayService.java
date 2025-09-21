package com.medusa.gruul.order.service.modules.order.service;

import com.medusa.gruul.order.service.model.dto.OrderPayDTO;
import com.medusa.gruul.payment.api.model.dto.PayNotifyResultDTO;
import com.medusa.gruul.payment.api.model.pay.PayResult;

/**
 * 订单支付服务
 *
 * @author 张治保
 * date 2022/7/28
 */
public interface OrderPayService {

    /**
     * 获取渲染支付数据
     *
     * @param orderPay 订单支付参数
     * @return 调起支付所需数据
     */
    PayResult toPay(OrderPayDTO orderPay);

    /**
     * 支付结果回调
     *
     * @param payNotifyResult 支付结果
     */
    void payNotify(PayNotifyResultDTO payNotifyResult);

}
