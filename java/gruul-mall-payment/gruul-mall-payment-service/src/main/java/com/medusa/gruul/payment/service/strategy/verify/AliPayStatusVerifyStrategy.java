package com.medusa.gruul.payment.service.strategy.verify;

import com.egzosn.pay.ali.api.AliPayService;
import com.egzosn.pay.spring.boot.core.PayServiceManager;
import com.egzosn.pay.spring.boot.core.bean.MerchantQueryOrder;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.payment.api.enums.TradeStatus;
import com.medusa.gruul.payment.service.common.constant.AliConst;
import com.medusa.gruul.payment.service.service.MultiPayOrderService;

import java.util.Map;

/**
 * @author xiaoq
 * @since 2022-08-01 15:52
 */
public class AliPayStatusVerifyStrategy extends AbstractPayStatusVerifyStrategy {

    private final PayServiceManager payServiceManager;


    public AliPayStatusVerifyStrategy(MultiPayOrderService multiPayOrderService, PayServiceManager payServiceManager) {
        super(multiPayOrderService);
        this.payServiceManager = payServiceManager;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected TradeStatus handle(String detailsId, String outTradeNo) {
        /*
         * 查询交易订单
         */
        MerchantQueryOrder queryOrder = new MerchantQueryOrder();
        queryOrder.setDetailsId(detailsId);
        queryOrder.setOutTradeNo(outTradeNo);
        Map<String, Object> queryOut = payServiceManager.cast(queryOrder.getDetailsId(), AliPayService.class)
                .query(queryOrder.getTradeNo(), queryOrder.getOutTradeNo());
        /*
         * 查询交易状态
         */
        Map<String, Object> query = (Map<String, Object>) queryOut.get(AliConst.TRADE_QUERY_KEY);
        if (query == null) {
            throw SystemCode.NOT_FOUND.exception();
        }
        if (!AliConst.SUCCESS_CODE.equals(query.get(AliConst.CODE))) {
            throw SystemCode.DATA_EXISTED.msgEx((String) query.get(AliConst.MSG));
        }
        /*
         * 返回对应状态
         */
        String tradeStatus = (String) query.get(AliConst.TRADE_STATUS);
        return switch (tradeStatus) {
            case AliConst.TradeStatus.TRADE_SUCCESS, AliConst.TradeStatus.TRADE_FINISHED -> TradeStatus.SUCCESS_PAYMENT;
            case AliConst.TradeStatus.WAIT_BUYER_PAY -> TradeStatus.PENDING_PAYMENT;
            case AliConst.TradeStatus.TRADE_CLOSED -> TradeStatus.OVERTIME_CLOSE;
            default -> throw new IllegalArgumentException("tradeStatus is illegal");
        };

    }
}
