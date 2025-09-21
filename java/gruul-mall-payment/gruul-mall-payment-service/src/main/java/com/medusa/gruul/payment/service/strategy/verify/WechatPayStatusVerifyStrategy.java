package com.medusa.gruul.payment.service.strategy.verify;

import cn.hutool.core.util.StrUtil;
import com.egzosn.pay.spring.boot.core.PayServiceManager;
import com.egzosn.pay.spring.boot.core.bean.MerchantQueryOrder;
import com.medusa.gruul.payment.api.enums.TradeStatus;
import com.medusa.gruul.payment.service.common.constant.WxConst;
import com.medusa.gruul.payment.service.model.enums.PaymentError;
import com.medusa.gruul.payment.service.service.MultiPayOrderService;

import java.util.Map;

/**
 * @author xiaoq
 * @since 2022-08-01 15:52
 */

public class WechatPayStatusVerifyStrategy extends AbstractPayStatusVerifyStrategy {

    private final PayServiceManager payServiceManager;

    public WechatPayStatusVerifyStrategy(MultiPayOrderService multiPayOrderService, PayServiceManager payServiceManager) {
        super(multiPayOrderService);
        this.payServiceManager = payServiceManager;
    }

    @Override
    protected TradeStatus handle(String detailsId, String outTradeNo) {
        MerchantQueryOrder queryOrder = new MerchantQueryOrder();
        queryOrder.setDetailsId(detailsId);
        queryOrder.setOutTradeNo(outTradeNo);
        Map<String, Object> query = payServiceManager.query(queryOrder);
//        if (this.isFail(query.get(WxConst.RETURN_CODE))) {
//            throw PaymentError.PAYMENT_STATUS_QUERY_ERROR.dataEx(query.get(WxConst.RETURN_MSG));
//        }
//        if (this.isFail(query.get(WxConst.RESULT_CODE))) {
//            throw PaymentError.PAYMENT_STATUS_QUERY_ERROR.dataEx(query.get(WxConst.RESULT_MSG));
//        }
        String tradeState = (String) query.get(WxConst.TRADE_STATE);
        if (StrUtil.isBlank(tradeState)) {
            throw PaymentError.PAYMENT_STATUS_QUERY_ERROR.dataEx(query.get(WxConst.TRADE_MSG));
        }
        return switch (tradeState) {
            case WxConst.TradeStatus.SUCCESS -> TradeStatus.SUCCESS_PAYMENT;
            case WxConst.TradeStatus.NOTPAY, WxConst.TradeStatus.USERPAYING, WxConst.TradeStatus.ACCEPT ->
                    TradeStatus.PENDING_PAYMENT;
            default -> TradeStatus.OVERTIME_CLOSE;
        };
    }

    private boolean isFail(Object state) {
        return !WxConst.SUCCESS.equals(state);
    }
}
