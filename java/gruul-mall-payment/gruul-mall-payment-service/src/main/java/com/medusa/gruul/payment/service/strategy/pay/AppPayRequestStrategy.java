package com.medusa.gruul.payment.service.strategy.pay;

import com.egzosn.pay.common.http.UriVariables;
import com.egzosn.pay.spring.boot.core.PayServiceManager;
import com.medusa.gruul.payment.api.model.dto.PaymentInfoDTO;
import com.medusa.gruul.payment.service.common.model.PayRequestOrder;
import com.medusa.gruul.payment.service.service.MultiPayOrderService;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * app支付处理器
 *
 * @author xiaoq
 * @since 2022-07-26 16:57
 */
@Slf4j
public class AppPayRequestStrategy extends AbstractPayRequestStrategy {

    protected final PayServiceManager payServiceManager;


    public AppPayRequestStrategy(MultiPayOrderService multiPayOrderService, PayServiceManager payServiceManager) {
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
        log.debug("===App支付:正在生成支付订单详情");
        Map<String, Object> orderInfo = payServiceManager.app(payRequestOrder);
        return switch (paymentInfoDTO.getPayType()) {
            case ALIPAY -> UriVariables.getMapToParameters(orderInfo);
            case WECHAT -> orderInfo;
            default -> throw new IllegalArgumentException("payType is not support");
        };
    }
}
