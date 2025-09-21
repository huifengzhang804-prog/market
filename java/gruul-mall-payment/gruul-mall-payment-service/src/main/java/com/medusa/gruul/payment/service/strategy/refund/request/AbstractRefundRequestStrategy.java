package com.medusa.gruul.payment.service.strategy.refund.request;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.egzosn.pay.common.bean.RefundOrder;
import com.egzosn.pay.common.bean.RefundResult;
import com.egzosn.pay.spring.boot.core.PayServiceManager;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.common.mp.IManualTransaction;
import com.medusa.gruul.global.model.strategy.IStrategy;
import com.medusa.gruul.payment.api.entity.PaymentRefund;
import com.medusa.gruul.payment.api.model.dto.RefundRequestDTO;
import com.medusa.gruul.payment.service.common.model.RefundRequestParam;
import com.medusa.gruul.payment.service.model.enums.PaymentError;
import com.medusa.gruul.payment.service.mp.service.IPaymentRefundService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * abstract退款处理器
 *
 * @author xiaoq
 * Description AbstractRefundHandler.java
 * date 2022-08-08 10:12
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractRefundRequestStrategy implements IStrategy<PayType, RefundRequestParam, Void> {

    private final IPaymentRefundService paymentRefundService;
    private final PayServiceManager payServiceManager;

    /**
     * <pre>
     * <a href="https://opendocs.alipay.com/support/01rawa?pathHash=d69e3c80">支付宝-如何判断退款是否成功</a>
     * <a href="https://opendocs.alipay.com/support/01rawc?pathHash=4ad70fe3">支付宝-交易退款接口是否会触发异步通知</a>
     * </pre>
     */
    @Override
    public Void execute(PayType payType, RefundRequestParam param) {
        RefundRequestDTO refundRequest = param.getRequest();
        String detailsId = param.getDetailsId();
        String notifyParam = param.getNotifyParam();
        log.debug("退款---------付款成功回调数据：{}", notifyParam);
        log.debug("------退款金额:{}", refundRequest.getRefundFee());
        RefundOrder refundOrder = this.refundOrder(refundRequest.getRefundFee(), notifyParam);
        refundOrder.setDescription(refundRequest.getDesc());
        //生成退款记录
        PaymentRefund refund = new PaymentRefund();
        refund.setOrderNum(refundRequest.getOrderNum());
        refund.setRouteKey(refundRequest.getRouteKey());
        refund.setExchange(refundRequest.getExchange());
        refund.setAfsNum(refundRequest.getAfsNum());
        refund.setRefundNo(refundOrder.getRefundNo());
        refund.setAsynRequest(JSONObject.toJSONString(refundOrder));
        paymentRefundService.save(refund);
        log.debug("------退款订单信息{}", JSONObject.toJSONString(refundOrder));
        switch (payType) {
            case ALIPAY:
            case WECHAT:
                IManualTransaction.afterCommit(
                        () -> {
                            RefundResult result = this.refundResult(detailsId, refundOrder);
                            //如果同步退款已成功 则直接处理
                            if (syncRefundSuccess(result)) {
                                paymentRefundService.refundSuccess(false, result.getRefundNo(), result);
                                return;
                            }
                            // 修改存储result值
                            paymentRefundService.lambdaUpdate()
                                    .set(PaymentRefund::getAsynResult, JSON.toJSONString(result))
                                    .eq(PaymentRefund::getOrderNum, refund.getOrderNum())
                                    .eq(PaymentRefund::getAfsNum, refund.getAfsNum())
                                    .update();

                        }
                );
                break;
            case BALANCE:
                paymentRefundService.balanceRefundOrder(refundRequest, notifyParam, refund.getRefundNo());
                break;
            default:
        }
        return VOID;
    }


    /**
     * 申请退款
     *
     * @param detailsId   列表id
     * @param refundOrder 退款订单信息
     * @return 退款结果
     */
    private RefundResult refundResult(String detailsId, RefundOrder refundOrder) {
        RefundResult result;
        try {
            result = payServiceManager.refund(detailsId, refundOrder);
        } catch (Exception e) {
            log.error("退款异常", e);
            throw PaymentError.ORDER_REFUND_ERROR.exception();
        }
        if (refundResultFailed(result)) {
            log.error("------退款异常---------\n{}", JSONObject.toJSONString(result));
            throw PaymentError.ORDER_REFUND_ERROR.exception();
        }
        return result;
    }

    /**
     * 退款请求结果是否失败
     *
     * @param result 退款结果
     * @return true 失败 false 成功
     */
    protected abstract boolean refundResultFailed(RefundResult result);

    /**
     * 生成退款订单
     *
     * @param refundFee   退款金额
     * @param notifyParam 付款成功回调参数
     * @return 退款订单信息
     */
    protected abstract RefundOrder refundOrder(Long refundFee, String notifyParam);


    /**
     * 是否同步退款已成功
     */
    protected abstract boolean syncRefundSuccess(RefundResult result);
}
