package com.medusa.gruul.payment.service.strategy.refund.request;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.filter.Filter;
import com.egzosn.pay.common.bean.PayMessage;
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
 * <p>
 *
 * </p>
 *
 * @author xiaoq
 * @Description
 * @date 2022-10-14 13:47
 */
public class BalanceRefundRequestStrategy extends AbstractRefundRequestStrategy {

    public BalanceRefundRequestStrategy(IPaymentRefundService paymentRefundService, PayServiceManager payServiceManager) {
        super(paymentRefundService, payServiceManager);
    }

    @Override
    protected boolean refundResultFailed(RefundResult result) {
        return false;
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
        String refundNo = MybatisPlusConfig.IDENTIFIER_GENERATOR.nextId(new PaymentRefund()).toString();
        refundNo = "16".concat(refundNo);
        refundOrder.setRefundNo(refundNo);
        refundOrder.setRefundAmount(BigDecimal.valueOf(refundFee).divide(CommonPool.BIG_DECIMAL_UNIT_CONVERSION_TEN_THOUSAND, 2, RoundingMode.HALF_EVEN));
        PayMessage payMessage = JSON.parseObject(notifyParam, PayMessage.class, (Filter) null, JSONReader.Feature.FieldBased);
        refundOrder.setTotalAmount(new BigDecimal(payMessage.getPayMessage().get("total_amount").toString()));
        refundOrder.setOutTradeNo(payMessage.getPayMessage().get("out_trade_no").toString());
        return refundOrder;
    }

    @Override
    protected boolean syncRefundSuccess(RefundResult result) {
        return true;
    }


}
