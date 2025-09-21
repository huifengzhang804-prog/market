package com.medusa.gruul.payment.service.strategy.notify;

import com.egzosn.pay.common.api.PayMessageHandler;
import com.egzosn.pay.common.bean.PayOutMessage;
import com.egzosn.pay.common.exception.PayErrorException;
import com.egzosn.pay.wx.v3.api.WxPayService;
import com.egzosn.pay.wx.v3.bean.response.WxPayMessage;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.overview.api.rpc.WithdrawRpcService;
import com.medusa.gruul.payment.api.enums.TradeStatus;
import com.medusa.gruul.payment.service.common.constant.WxConst;
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
 * 微信支付回调处理器
 *
 * @author xiaoq
 * @since 2022-08-01 15:21
 */
@Slf4j
@RequiredArgsConstructor
public class WxPayMessageHandler implements PayMessageHandler<WxPayMessage, WxPayService> {

    private final PayStatusVerifyStrategyFactory payStatusVerifyStrategyFactory;
    private final MultiPayOrderService orderPaymentService;
    private final MultiPayRefundService multiPayRefundService;
    private final WithdrawRpcService withdrawRpcService;

    /**
     * 处理支付回调消息的处理器接口
     *
     * @param payMessage WxPayMessage 支付消息
     * @param context    上下文，如果handler或interceptor之间有信息要传递，可以用这个
     * @param payService 支付服务
     * @return xml, text格式的消息，如果在异步规则里处理的话，可以返回null
     * @throws PayErrorException 支付错误异常
     */
    @Override
    public PayOutMessage handle(WxPayMessage payMessage, Map<String, Object> context, WxPayService payService) throws PayErrorException {
        log.debug("::微信回调通知::notify::\n{}", payMessage.toString());
        Map<String, Object> payMessageDetail = payMessage.getPayMessage();
        Object refundStatus = payMessageDetail.get(WxConst.Refund.REFUND_STATUS);
        String outTradeNo = payMessage.getOutTradeNo();

        //退款回调通知
        if (refundStatus != null) {
            log.debug("退款回调");
            multiPayRefundService.refundNotify(PayType.WECHAT, payMessage);
            return payService.getPayOutMessage(NotifyResponse.WECHAT_SUCCESS.getCode(), NotifyResponse.WECHAT_SUCCESS.getMsg());
        }
        //转账回调通知
        String batchStatus = (String) payMessageDetail.get(WxConst.TransferConst.BATCH_STATUS);
        if (batchStatus != null) {
            log.debug("转账回调");
            String outBatchNo = (String) payMessageDetail.get(WxConst.TransferConst.OUT_BATCH_NO);
            String closeReason = (String) payMessageDetail.get(WxConst.TransferConst.CLOSE_REASON);
//            String outBatchNo = payMessage.getOutBatchNo();
//            String batchStatus = result.getBatchStatus();
//            String closeReason = result.getCloseReason();

            withdrawRpcService.updateWithdrawOrderStatus(outBatchNo, batchStatus, closeReason);
            return payService.getPayOutMessage(NotifyResponse.WECHAT_SUCCESS.getCode(), NotifyResponse.WECHAT_SUCCESS.getMsg());
        }
        //支付结果通知
        log.debug("支付回调");
        TradeStatus tradeStatus = payStatusVerifyStrategyFactory.execute(PayType.WECHAT, outTradeNo);
        log.debug("::微信支付::notify::交易状态:{}", tradeStatus);
        if (TradeStatus.SUCCESS_PAYMENT == tradeStatus) {
            orderPaymentService.success(payMessage);
        }
        INotifyResponse response = PayHelper.notifyResponse(tradeStatus, PayType.WECHAT);
        log.debug("::::::支付回调完毕::::::");
        return payService.getPayOutMessage(response.getCode(), response.getMsg());
    }
}
