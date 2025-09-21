package com.medusa.gruul.payment.service.strategy.refund.request;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.filter.Filter;
import com.egzosn.pay.ali.bean.AliPayMessage;
import com.egzosn.pay.common.bean.RefundOrder;
import com.egzosn.pay.common.bean.RefundResult;
import com.egzosn.pay.spring.boot.core.PayServiceManager;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.mp.config.MybatisPlusConfig;
import com.medusa.gruul.payment.api.entity.PaymentRefund;
import com.medusa.gruul.payment.service.mp.service.IPaymentRefundService;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author xiaoq
 * @Description
 * @since 2022-08-08 10:14
 */
public class AlipayRefundRequestStrategy extends AbstractRefundRequestStrategy {


    public AlipayRefundRequestStrategy(IPaymentRefundService paymentRefundService, PayServiceManager payServiceManager) {
        super(paymentRefundService, payServiceManager);
    }

    @Override
    protected boolean refundResultFailed(RefundResult result) {
        return !CommonPool.UNIT_CONVERSION_TEN_THOUSAND.toString().equals(result.getCode());
    }

    /**
     * 生成退款订单
     *
     * @param refundFee   退款金额
     * @param notifyParam 付款成功回调参数
     * @return 退款订单信息
     */
    @Override
    protected RefundOrder refundOrder(Long refundFee, String notifyParam) {
        RefundOrder refundOrder = new RefundOrder();
        String refundNo = MybatisPlusConfig.IDENTIFIER_GENERATOR.nextUUID(new PaymentRefund());
        refundOrder.setRefundNo(refundNo);
        refundOrder.setRefundAmount(BigDecimal.valueOf(refundFee).divide(CommonPool.BIG_DECIMAL_UNIT_CONVERSION_TEN_THOUSAND, 2, RoundingMode.HALF_EVEN));
        AliPayMessage aliPayMessage = JSON.parseObject(notifyParam, AliPayMessage.class, (Filter) null, JSONReader.Feature.FieldBased);
        refundOrder.setTotalAmount(aliPayMessage.getTotalAmount());
        refundOrder.setOutTradeNo(aliPayMessage.getOutTradeNo());
        return refundOrder;
    }

    @Override
    protected boolean syncRefundSuccess(RefundResult result) {
        return "Y".equalsIgnoreCase((String) result.getAttr("fund_change"));
    }
}
