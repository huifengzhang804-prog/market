package com.medusa.gruul.payment.service.service;

import com.egzosn.pay.common.bean.PayMessage;
import com.medusa.gruul.common.model.enums.PayType;

/**
 * 退款service
 *
 * @author xiaoq
 * @Description MultiPayRefundService.java
 * @date 2022-08-08 10:06
 */
public interface MultiPayRefundService {

    /**
     * 退款回调处理
     *
     * @param payType    支付渠道枚举
     * @param payMessage 退款消息
     */
    void refundNotify(PayType payType, PayMessage payMessage);

}
