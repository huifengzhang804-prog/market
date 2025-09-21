package com.medusa.gruul.addon.integral.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.addon.integral.mp.entity.IntegralOrder;
import com.medusa.gruul.addon.integral.mp.entity.IntegralOrderPayment;
import com.medusa.gruul.payment.api.model.dto.PayNotifyResultDTO;

/**
 * @author shishuqian
 * date 2023/2/3
 * time 10:47
 **/
public interface IIntegralOrderPaymentService extends IService<IntegralOrderPayment> {
    /**
     * 插入一条积分订单支付数据
     *
     * @param integralOrder   积分订单信息
     * @param payNotifyResult 支付信息数据
     * @author shishuqian
     */
    void saveIntegralPaymentInfo(IntegralOrder integralOrder, PayNotifyResultDTO payNotifyResult);

    /**
     * 插入一条支付0元的数据(包邮的情况)
     *
     * @param integralOrder 积分订单信息
     * @author shishuqian
     */
    void saveZeroPaymentInfo(IntegralOrder integralOrder);


    /**
     * 根据订单号，查询订单是否支付成功
     *
     * @param integralOrderNo 积分订单号
     * @return true:支付成功
     * false:支付失败
     * @author shishuqian
     */
    boolean integralOrderPayStatus(String integralOrderNo);
}
