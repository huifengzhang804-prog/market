package com.medusa.gruul.payment.service.mp.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.filter.Filter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.egzosn.pay.common.bean.PayMessage;
import com.egzosn.pay.common.bean.RefundResult;
import com.medusa.gruul.common.model.enums.BalanceHistoryOperatorType;
import com.medusa.gruul.common.model.enums.ChangeType;
import com.medusa.gruul.common.model.message.DataChangeMessage;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.IManualTransaction;
import com.medusa.gruul.payment.api.entity.PaymentHistory;
import com.medusa.gruul.payment.api.entity.PaymentInfo;
import com.medusa.gruul.payment.api.entity.PaymentRefund;
import com.medusa.gruul.payment.api.enums.DealType;
import com.medusa.gruul.payment.api.model.dto.RefundNotifyResultDTO;
import com.medusa.gruul.payment.api.model.dto.RefundRequestDTO;
import com.medusa.gruul.payment.service.model.enums.PaymentError;
import com.medusa.gruul.payment.service.mp.mapper.PaymentHistoryMapper;
import com.medusa.gruul.payment.service.mp.mapper.PaymentRefundMapper;
import com.medusa.gruul.payment.service.mp.service.IPaymentInfoService;
import com.medusa.gruul.payment.service.mp.service.IPaymentRefundService;
import com.medusa.gruul.user.api.rpc.UserRpcService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
/**
 * @author xiaoq
 * Description
 * date 2022-08-08 11:14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentRefundServiceImpl extends ServiceImpl<PaymentRefundMapper, PaymentRefund> implements IPaymentRefundService {

    private final PaymentRefundMapper paymentRefundMapper;

    private final IPaymentInfoService paymentInfoService;

    private final RabbitTemplate rabbitTemplate;

    private  UserRpcService userRpcService;

    private final PaymentHistoryMapper paymentHistoryMapper;

        @Lazy
    @Autowired
    public void setUserRpcService(UserRpcService userRpcService) {
        this.userRpcService = userRpcService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void balanceRefundOrder(RefundRequestDTO refundRequest, String notifyParam, String refundNo) {
        log.debug("余额退款");
        String orderNum = refundRequest.getOrderNum();
        PaymentInfo paymentInfo = paymentInfoService.lambdaQuery().eq(PaymentInfo::getOrderNum, orderNum).one();
        if (paymentInfo.getTotalFee() < refundRequest.getRefundFee()) {
            throw PaymentError.REFUND_AMOUNT_CANNOT_GREATER_THAN_PAYMENT_AMOUNT.exception();
        }
        Long sumRefundAmount = this.getSumRefundAmount(orderNum);
        if (paymentInfo.getTotalFee() < sumRefundAmount) {
            throw PaymentError.REFUND_AMOUNT_CANNOT_GREATER_THAN_PAYMENT_AMOUNT.exception();
        }
        PayMessage payMessage = JSON.parseObject(notifyParam, PayMessage.class, (Filter) null, JSONReader.Feature.FieldBased);
        String outTradeNo = payMessage.getPayMessage().get("out_trade_no").toString();

        PaymentRefund paymentRefund = paymentRefundMapper.selectOne(new QueryWrapper<PaymentRefund>().eq("refund_no", refundNo));
        if (paymentRefund == null) {
            return;
        }
        if (StrUtil.isNotEmpty(paymentRefund.getSynCallback())) {
            return;
        }
        //更新退款信息
        paymentRefund.setSynCallback(JSON.toJSONString(payMessage));
        paymentRefundMapper.updateById(paymentRefund);

        DataChangeMessage dataChangeMessage = new DataChangeMessage();
        dataChangeMessage.setChangeType(ChangeType.INCREASE);
        dataChangeMessage.setUserId(paymentInfo.getUserId());
        dataChangeMessage.setValue(refundRequest.getRefundFee());
        dataChangeMessage.setOperatorType(BalanceHistoryOperatorType.REFUND_SUCCESSFUL);
        dataChangeMessage.setOrderNo(orderNum);
        dataChangeMessage.setOperatorUserId(paymentInfo.getUserId());
        Boolean flag = userRpcService.userBalancePayment(dataChangeMessage);
        SystemCode.DATA_UPDATE_FAILED.falseThrow(flag);
        //生成明细
        PaymentHistory paymentHistory = new PaymentHistory();
        paymentHistory.setDealType(DealType.REFUND_SUCCEED);
        paymentHistory.setUserId(paymentInfo.getUserId());
        paymentHistory.setSubject(paymentInfo.getSubject());
        paymentHistory.setMoney(refundRequest.getRefundFee());
        paymentHistory.setChangeType(ChangeType.INCREASE);
        paymentHistory.setPayType(paymentInfo.getPayType());
        paymentHistoryMapper.insert(paymentHistory);
        // mq发送
        IManualTransaction.afterCommit(
                () -> {
                    log.debug("::::余额退款回调接口执行完毕::OutTradeNo:{}::RefundNo:{}", outTradeNo, refundNo);
                    rabbitTemplate.convertAndSend(
                            paymentRefund.getExchange(),
                            paymentRefund.getRouteKey(),
                            new RefundNotifyResultDTO()
                                    .setOutTradeNo(outTradeNo)
                                    .setAfsNum(paymentRefund.getAfsNum())
                                    .setRefundNum(refundNo)
                    );
                }
        );

    }

    @Override
    public void refundSuccess(boolean async, String refundNo, Object msg) {
        String outTradeNo;
        if (msg instanceof PayMessage message) {
            outTradeNo = message.getOutTradeNo();
        } else if (msg instanceof RefundResult refund) {
            outTradeNo = refund.getOutTradeNo();
        } else {
            throw new RuntimeException("unsupported msg type");
        }
        PaymentRefund refund = this.lambdaQuery()
                .eq(PaymentRefund::getRefundNo, refundNo)
                .one();
        if (refund == null) {
            return;
        }
        if (StrUtil.isNotEmpty(refund.getSynCallback())) {
            return;
        }
        //更新退款信息
        refund.setRefundNo(refundNo);
        if (async) {
            refund.setSynCallback(JSON.toJSONString(msg));
        } else {
            refund.setSynCallback(JSON.toJSONString(msg));
        }
        this.updateById(refund);
        log.debug("::::退款已完成::OutTradeNo:{}::RefundNo:{}::AFS{}", outTradeNo, refundNo, refund.getAfsNum());
        //如果路由key为空不进行回调
        if (StrUtil.isEmpty(refund.getExchange())) {
            return;
        }
        rabbitTemplate.convertAndSend(
                refund.getExchange(),
                refund.getRouteKey(),
                new RefundNotifyResultDTO()
                        .setOutTradeNo(outTradeNo)
                        .setAfsNum(refund.getAfsNum())
                        .setRefundNum(refundNo)
        );
    }


    /**
     * 获取该业务单号退款总计金额
     *
     * @param orderNum 业务单号
     * @return 业务单号退款总计金额
     */
    private Long getSumRefundAmount(String orderNum) {
        return paymentRefundMapper.querySumRefundAmount(orderNum);
    }

}
