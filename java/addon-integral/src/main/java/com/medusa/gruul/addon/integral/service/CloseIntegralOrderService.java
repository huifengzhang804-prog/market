package com.medusa.gruul.addon.integral.service;

import com.medusa.gruul.addon.integral.model.bo.IntegralOrderDetailInfoBO;
import com.medusa.gruul.addon.integral.model.enums.IntegralOrderStatus;

/**
 * @author shishuqian
 * date 2023/2/2
 * time 16:48
 * 关闭积分订单service
 **/
public interface CloseIntegralOrderService {

    /**
     * 关闭支付超时的订单
     *
     * @param integralOrderDetailInfoBO 积分订单信息bo
     * @author shishuqian
     */
    void closeIntegralOrderPaidTimeout(IntegralOrderDetailInfoBO integralOrderDetailInfoBO);

    /**
     * 关闭订单
     *
     * @param integralOrderDetailInfoBO 积分订单信息bo
     * @param integralOrderStatus       积分订单状态
     * @author shishuqian
     */
    void closeIntegralOrder(IntegralOrderDetailInfoBO integralOrderDetailInfoBO, IntegralOrderStatus integralOrderStatus);

}
