package com.medusa.gruul.payment.service.strategy.notify;

import com.alibaba.fastjson.JSON;
import com.egzosn.pay.ali.api.AliPayService;
import com.egzosn.pay.ali.bean.AliPayMessage;
import com.egzosn.pay.common.api.PayMessageHandler;
import com.egzosn.pay.common.bean.PayOutMessage;
import com.egzosn.pay.common.exception.PayErrorException;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.payment.api.enums.TradeStatus;
import com.medusa.gruul.payment.service.common.constant.AliConst;
import com.medusa.gruul.payment.service.common.enums.INotifyResponse;
import com.medusa.gruul.payment.service.common.enums.NotifyResponse;
import com.medusa.gruul.payment.service.common.helper.PayHelper;
import com.medusa.gruul.payment.service.service.MultiPayOrderService;
import com.medusa.gruul.payment.service.service.MultiPayRefundService;
import com.medusa.gruul.payment.service.strategy.verify.PayStatusVerifyStrategyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 支付宝支付回调处理器
 *
 * @author xiaoq
 * @since 2022-08-01 15:21
 */
@Slf4j
@RequiredArgsConstructor
public class AliPayMessageHandler implements PayMessageHandler<AliPayMessage, AliPayService> {

    private final PayStatusVerifyStrategyFactory payStatusVerifyStrategyFactory;
    private final MultiPayOrderService orderPaymentService;
    private final MultiPayRefundService multiPayRefundService;

    /**
     * 处理支付回调消息的处理器接口
     *
     * @param payMessage AliPayMessage支付消息
     * @param context    上下文，如果handler或interceptor之间有信息要传递，可以用这个
     * @param payService 支付服务
     * @return xml, text格式的消息，如果在异步规则里处理的话，可以返回null
     * @throws PayErrorException 支付错误异常
     */
    @Override
    public PayOutMessage handle(AliPayMessage payMessage, Map<String, Object> context, AliPayService payService) throws PayErrorException {

        log.debug("::支付宝支付::notify::{}", JSON.toJSONString(payMessage));
        String outTradeNo = payMessage.getOutTradeNo();
        Map<String, Object> payMessageDetail = payMessage.getPayMessage();
        //  判断是否为退款回调
        if (payMessageDetail.get(AliConst.Refund.REFUND_FEE) != null) {
            multiPayRefundService.refundNotify(PayType.ALIPAY, payMessage);
            log.debug("::支付宝支付::notify::{}", "退款回调");
            return payService.getPayOutMessage(NotifyResponse.ALI_SUCCESS.getCode(), NotifyResponse.ALI_SUCCESS.getMsg());
        }
        log.debug("::支付宝支付::notify::{}", "支付回调");
        TradeStatus tradeStatus = payStatusVerifyStrategyFactory.execute(PayType.ALIPAY, outTradeNo);
        log.debug("::支付宝支付::notify::交易状态:{}", tradeStatus);
        if (TradeStatus.SUCCESS_PAYMENT == tradeStatus) {
            orderPaymentService.success(payMessage);
        }
        INotifyResponse response = PayHelper.notifyResponse(tradeStatus, PayType.ALIPAY);
        log.debug("::::::支付回调完毕::::::");
        return payService.getPayOutMessage(response.getCode(), response.getMsg());
    }


}
