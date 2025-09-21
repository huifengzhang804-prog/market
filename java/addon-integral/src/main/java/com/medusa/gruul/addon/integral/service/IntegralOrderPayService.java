package com.medusa.gruul.addon.integral.service;

import com.medusa.gruul.addon.integral.model.dto.IntegralOrderPayDTO;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.payment.api.model.dto.PayNotifyResultDTO;
import com.medusa.gruul.payment.api.model.pay.PayResult;

/**
 * @author shishuqian
 * date 2023/2/2
 * time 17:46
 * 订单支付服务
 **/
public interface IntegralOrderPayService {

    /**
     * 获取渲染支付数据
     *
     * @param integralOrderPayDTO 订单支付参数
     * @param user                登录用户信息
     * @return 调起支付所需数据
     */
    PayResult toPay(IntegralOrderPayDTO integralOrderPayDTO, SecureUser user);


    /**
     * 积分订单支付完成后的回调
     *
     * @param payNotifyResult 支付回调数据
     * @author shishuqian
     */
    void payNotify(PayNotifyResultDTO payNotifyResult);
}
