package com.medusa.gruul.payment.service.strategy.pay;

import com.egzosn.pay.spring.boot.core.PayServiceManager;
import com.medusa.gruul.payment.api.model.dto.PaymentInfoDTO;
import com.medusa.gruul.payment.service.common.model.PayRequestOrder;
import com.medusa.gruul.payment.service.service.MultiPayOrderService;
import lombok.extern.slf4j.Slf4j;

/**
 * 微信公众号,微信小程序支付处理器
 *
 * @author xiaoq
 * @since 2022-07-26 16:45
 */
@Slf4j
public class WechatJsApiRequestStrategy extends AbstractPayRequestStrategy {

    protected final PayServiceManager payServiceManager;

    public WechatJsApiRequestStrategy(MultiPayOrderService multiPayOrderService, PayServiceManager payServiceManager) {
        super(multiPayOrderService);
        this.payServiceManager = payServiceManager;
    }

    /**
     * 获取调起支付所需参数
     *
     * @param paymentInfoDTO  支付参数
     * @param payRequestOrder 支付订单
     * @return 调起支付所需数据
     */
    @Override
    protected Object payData(PaymentInfoDTO paymentInfoDTO, PayRequestOrder payRequestOrder) {
        log.debug("===微信JSAPI支付:正在生成微信支付订单详情");
        log.debug(payRequestOrder.getAddition());
        return payServiceManager.getOrderInfo(payRequestOrder);
    }
}

