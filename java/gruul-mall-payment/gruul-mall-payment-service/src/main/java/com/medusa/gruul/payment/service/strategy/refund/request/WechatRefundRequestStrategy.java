package com.medusa.gruul.payment.service.strategy.refund.request;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.filter.Filter;
import com.egzosn.pay.common.bean.RefundOrder;
import com.egzosn.pay.common.bean.RefundResult;
import com.egzosn.pay.spring.boot.core.PayServiceManager;
import com.egzosn.pay.wx.v3.bean.response.WxPayMessage;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.mp.config.MybatisPlusConfig;
import com.medusa.gruul.payment.api.entity.PaymentRefund;
import com.medusa.gruul.payment.service.common.constant.WxConst;
import com.medusa.gruul.payment.service.mp.service.IPaymentRefundService;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author xiaoq
 * @Description WechatRefundHandler.java
 * @date 2022-08-08 10:17
 */
public class WechatRefundRequestStrategy extends AbstractRefundRequestStrategy {


    public WechatRefundRequestStrategy(IPaymentRefundService paymentRefundService, PayServiceManager payServiceManager) {
        super(paymentRefundService, payServiceManager);
    }

    /**
     * 非成功且非处理中状态 标记为失败
     *
     * @param result 退款结果
     * @return 是否失败
     */
    @Override
    protected boolean refundResultFailed(RefundResult result) {
        String status = result.getAttrString(WxConst.Refund.STATUS);
        return !WxConst.Refund.SUCCESS.equals(status)
                && !WxConst.Refund.PROCESSING.equals(status);
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
        refundOrder.setRefundAmount(BigDecimal.valueOf(refundFee).divide(CommonPool.BIG_DECIMAL_UNIT_CONVERSION_TEN_THOUSAND, 2, RoundingMode.DOWN));
        WxPayMessage wxPayMessage = JSON.parseObject(notifyParam, WxPayMessage.class, (Filter) null, JSONReader.Feature.FieldBased);
        refundOrder.setTotalAmount(wxPayMessage.getTotalFee().divide(CommonPool.BIG_DECIMAL_UNIT_CONVERSION_HUNDRED, 2, RoundingMode.DOWN));
        refundOrder.setOutTradeNo(wxPayMessage.getOutTradeNo());
        return refundOrder;
    }

    @Override
    protected boolean syncRefundSuccess(RefundResult result) {
        return WxConst.Refund.SUCCESS.equals(result.getAttrString(WxConst.Refund.STATUS));
    }
}
