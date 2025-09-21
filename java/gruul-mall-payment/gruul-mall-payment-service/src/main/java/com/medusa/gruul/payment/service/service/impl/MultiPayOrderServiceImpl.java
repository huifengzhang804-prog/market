package com.medusa.gruul.payment.service.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.egzosn.pay.common.bean.PayMessage;
import com.egzosn.pay.common.bean.TransactionType;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.BalanceHistoryOperatorType;
import com.medusa.gruul.common.model.enums.ChangeType;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.common.model.message.DataChangeMessage;
import com.medusa.gruul.common.model.pay.Transaction;
import com.medusa.gruul.common.mp.config.MybatisPlusConfig;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.system.model.model.Platform;
import com.medusa.gruul.payment.api.entity.PaymentHistory;
import com.medusa.gruul.payment.api.entity.PaymentInfo;
import com.medusa.gruul.payment.api.entity.PaymentRecord;
import com.medusa.gruul.payment.api.enums.*;
import com.medusa.gruul.payment.api.model.dto.PayNotifyResultDTO;
import com.medusa.gruul.payment.api.model.dto.PaymentInfoDTO;
import com.medusa.gruul.payment.service.common.helper.PayHelper;
import com.medusa.gruul.payment.service.common.model.MerchantConfig;
import com.medusa.gruul.payment.service.common.model.OrderPaymentRecord;
import com.medusa.gruul.payment.service.common.model.PayRequestOrder;
import com.medusa.gruul.payment.service.model.PayConst;
import com.medusa.gruul.payment.service.model.enums.PaymentError;
import com.medusa.gruul.payment.service.mp.service.IPaymentHistoryService;
import com.medusa.gruul.payment.service.mp.service.IPaymentInfoService;
import com.medusa.gruul.payment.service.mp.service.IPaymentRecordService;
import com.medusa.gruul.payment.service.service.MultiPayOrderService;
import com.medusa.gruul.user.api.rpc.UserRpcService;
import jodd.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 商户业务订单支付实现
 * </p>
 *
 * @author xiaoq
 * Description MultiPayOrderServiceImpl.java
 * date 2022-07-25 16:01
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MultiPayOrderServiceImpl implements MultiPayOrderService {

    private final IPaymentInfoService paymentInfoService;
    private final IPaymentRecordService paymentRecordService;
    private final RabbitTemplate rabbitTemplate;
    private final UserRpcService userRpcService;
    private final IPaymentHistoryService paymentHistoryService;


    /**
     * 业务订单预处理
     *
     * @param paymentInfoDTO 支付信息DTO
     */
    @Override
    public PayRequestOrder pretreatmentOrder(PaymentInfoDTO paymentInfoDTO) {
        String detailId = "";
        String notifyUrl = "";
        PayType payType = paymentInfoDTO.getPayType();
        //非余额支付 从缓存获取支付配置
        if (payType != PayType.BALANCE) {
            detailId = RedisUtil.getCacheMapValue(
                    RedisUtil.key(PayConst.PAY_CONFIG_CACHE_KEY, payType),
                    paymentInfoDTO.getPayPlatform().name(),
                    String.class
            );
            MerchantConfig config;
            if (StrUtil.isEmpty(detailId) || (config = RedisUtil.getCacheMapValue(PayConst.PAY_CONFIG_CACHE_KEY, detailId, MerchantConfig.class)) == null) {
                throw PaymentError.PAYMENT_CHANNEL_NOT_CONFIGURED.dataEx(payType);
            }
            notifyUrl = config.getNotifyUrl();
        }

        // 封装支付业务订单信息
        PayRequestOrder payOrder = this.createOrder(detailId, notifyUrl, paymentInfoDTO);
        log.debug("业务订单信息 ".concat(payOrder.toString()));
        //初始化支付信息记录
        this.init(payOrder, paymentInfoDTO);
        return payOrder;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void success(PayMessage payMessage) {
        log.debug("进入支付成功处理-{}", payMessage);
        String orderNo = payMessage.getOutTradeNo();

        PaymentInfo paymentInfo = paymentInfoService.lambdaQuery().eq(PaymentInfo::getOrderNum, orderNo).one();
        if (paymentInfo == null) {
            log.debug("未查询到payment记录:orderNo:{}", orderNo);
            throw PaymentError.NOT_FOUND_PAYMENT_RECORD.dataEx(orderNo);
        }
        //业务已处理 结束后续操作
        if (paymentInfo.getBusinessNotifyStatus().equals(NotifyStatus.ACCOMPLISH) &&
                paymentInfo.getThirdPartyNotifyStatus().equals(NotifyStatus.ACCOMPLISH)) {
            return;
        }
        //设置回调参数
        PaymentRecord record = paymentRecordService.lambdaQuery().eq(PaymentRecord::getPaymentId, paymentInfo.getId()).one();
        record.setNotifyParam(JSON.toJSONString(payMessage));
        paymentRecordService.updateById(record);
        //设置交易成功
        if (paymentInfo.getPayType() == PayType.WECHAT) {
            paymentInfo.setTransactionId(payMessage.getPayMessage().get("transaction_id").toString());
        } else {
            paymentInfo.setTransactionId(payMessage.getPayMessage().get("notify_id").toString());
        }
        paymentInfo.setThirdPartyNotifyNumber(paymentInfo.getThirdPartyNotifyNumber() + 1);
        paymentInfo.setThirdPartyNotifyStatus(NotifyStatus.ACCOMPLISH);
        paymentInfo.setTradeStatus(TradeStatus.SUCCESS_PAYMENT);
        paymentInfo.setBusinessNotifyStatus(NotifyStatus.ACCOMPLISH);
        paymentInfoService.updateById(paymentInfo);
        if (StringUtil.isBlank(paymentInfo.getExchange())) {
            return;
        }
        log.debug("发送mq消息.....:{}-{}", paymentInfo.getExchange(), paymentInfo.getRouteKey());
        //支付成功回调
        rabbitTemplate.convertAndSend(paymentInfo.getExchange(), paymentInfo.getRouteKey(),
                new PayNotifyResultDTO()
                        .setPayOrderType(PayOrderType.COMMON)
                        .setOutTradeNo(paymentInfo.getOrderNum())
                        .setBusinessParams(JSON.parseObject(paymentInfo.getBusinessParams(), PaymentInfoDTO.class))
                        .setShopIdTransactionMap(Map.of(
                                0L, new Transaction()
                                        .setProfitSharing(false)
                                        .setTransactionId(paymentInfo.getTransactionId())
                        ))
        );


    }

    /**
     * 获取支付记录
     *
     * @param orderNum 内部业务订单号
     * @return OrderPaymentRecord.java
     */
    @Override
    public OrderPaymentRecord paymentRecord(String orderNum) {
        PaymentInfo payment = paymentInfoService.lambdaQuery()
                .eq(PaymentInfo::getOrderNum, orderNum)
                .one();
        if (payment == null) {
            throw PaymentError.NOT_FOUND_PAYMENT_RECORD.exception();
        }
        PaymentRecord record = paymentRecordService.lambdaQuery().eq(PaymentRecord::getPaymentId, payment.getId()).one();
        String sendParam;
        if (record == null || StrUtil.isBlank(sendParam = record.getSendParam())) {
            throw PaymentError.NOT_FOUND_PAYMENT_RECORD.exception();
        }
        OrderPaymentRecord paymentRecord = JSON.parseObject(sendParam, OrderPaymentRecord.class);
        paymentRecord.setNotifyParam(record.getNotifyParam());
        log.debug("查询支付记录:{}", paymentRecord);
        return paymentRecord;
    }

    /**
     * 根据业务单号 获取业务订单处理状态
     *
     * @param outTradeNo 业务订单号
     * @return NotifyStatus.java
     */
    @Override
    public NotifyStatus orderPayStatus(String outTradeNo) {
        PaymentInfo paymentInfo = paymentInfoService.lambdaQuery()
                .select(PaymentInfo::getBusinessNotifyStatus)
                .eq(PaymentInfo::getOrderNum, outTradeNo)
                .one();
        if (paymentInfo == null) {
            return NotifyStatus.ACCOMPLISH;
        }
        return paymentInfo.getBusinessNotifyStatus();
    }


    /**
     * 用户进行余额支付
     *
     * @param paymentInfoDTO 支付信息DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void balancePay(PaymentInfoDTO paymentInfoDTO) {
        DataChangeMessage message = new DataChangeMessage();
        message.setValue(paymentInfoDTO.getTotalFee());
        message.setUserId(paymentInfoDTO.getUserId());
        message.setChangeType(ChangeType.REDUCE);
        message.setOperatorType(BalanceHistoryOperatorType.SHOPPING_CONSUMPTION);
        message.setOrderNo(paymentInfoDTO.getOrderNum());
        message.setOperatorUserId(paymentInfoDTO.getUserId());
        message.setSubject(paymentInfoDTO.getSubject());
        Boolean flag = userRpcService.userBalancePayment(message);
        PaymentError.USER_BALANCE_NOT_ENOUGH.falseThrow(flag);
        //生成明细
        PaymentHistory paymentHistory = new PaymentHistory();
        paymentHistory.setDealType(DealType.SHOPPING_PURCHASE);
        paymentHistory.setUserId(paymentInfoDTO.getUserId());
        paymentHistory.setSubject(paymentInfoDTO.getSubject());
        paymentHistory.setMoney(paymentInfoDTO.getTotalFee());
        paymentHistory.setChangeType(ChangeType.REDUCE);
        paymentHistory.setPayType(PayType.BALANCE);
        paymentHistoryService.save(paymentHistory);

        //封装回调数据
        Map<String, Object> payMessageInfo = new HashMap<>(CommonPool.NUMBER_FIVE);
        payMessageInfo.put("subject", paymentInfoDTO.getSubject());
        payMessageInfo.put("total_amount", paymentInfoDTO.getTotalFee());
        payMessageInfo.put("out_trade_no", paymentInfoDTO.getOrderNum());
        payMessageInfo.put("notify_id", "16".concat(MybatisPlusConfig.IDENTIFIER_GENERATOR.nextId(new PaymentInfo()).toString()));
        PayMessage payMessage = new PayMessage(payMessageInfo);
        this.success(payMessage);
    }


    /**
     * 初始化支付记录
     *
     * @param payOrder       支付业务订单信息
     * @param paymentInfoDTO 支付信息DTO
     */
    private void init(PayRequestOrder payOrder, PaymentInfoDTO paymentInfoDTO) {
        //封装业务支付信息
        PaymentInfo paymentInfo = this.savePaymentInfo(payOrder, paymentInfoDTO);
        this.newPaymentRecord(paymentInfo.getId(), payOrder);

    }

    /**
     * 初始化支付记录表
     *
     * @param paymentId 支付信息id
     * @param payOrder  paymentInfo
     */
    private void newPaymentRecord(Long paymentId, PayRequestOrder payOrder) {
        paymentRecordService.lambdaUpdate().eq(PaymentRecord::getPaymentId, paymentId).remove();
        PaymentRecord paymentRecord = new PaymentRecord();
        paymentRecord.setPaymentId(paymentId);
        paymentRecord.setSendParam(JSON.toJSONString(payOrder));
        paymentRecord.setRequestParams(JSON.toJSONString(payOrder));
        paymentRecordService.save(paymentRecord);
    }


    /**
     * 保存业务支付信息
     *
     * @param payOrder       支付业务订单信息
     * @param paymentInfoDTO 支付信息DTO
     */
    private PaymentInfo savePaymentInfo(PayRequestOrder payOrder, PaymentInfoDTO paymentInfoDTO) {
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfoService.lambdaUpdate().eq(PaymentInfo::getOrderNum, payOrder.getOrderId()).remove();
        paymentInfo
                .setOrderNum(payOrder.getOrderId())
                .setPayType(payOrder.getPayType())
                .setFeeType(FeeType.CNY)
                .setTotalFee(paymentInfoDTO.getTotalFee())
                .setBody(payOrder.getBody())
                .setBusinessParams(payOrder.getAddition())
                .setTerminalIp(payOrder.getSpbillCreateIp())
                .setBusinessNotifyUrl(payOrder.getNotifyUrl())
                .setRouteKey(payOrder.getRouteKey())
                .setExchange(paymentInfoDTO.getExchange())
                .setBusinessNotifyUrl(payOrder.getNotifyUrl())
                .setFeeType(FeeType.valueOf(payOrder.getCurType().getType()))
                .setThirdPartyNotifyStatus(NotifyStatus.UNFINISHED)
                .setThirdPartyNotifyNumber(CommonPool.NUMBER_ZERO)
                .setBusinessNotifyStatus(NotifyStatus.UNFINISHED)
                .setTradeStatus(TradeStatus.PENDING_PAYMENT)
                .setShopId(payOrder.getShopId())
                .setOpenId(payOrder.getOpenid())
                .setSubject(payOrder.getSubject())
                .setUserId(payOrder.getUserId())
                .setSeconds(payOrder.getExpirationTime().toString())
                .setBusinessParams(JSON.toJSONString(paymentInfoDTO));
        paymentInfoService.save(paymentInfo);
        return paymentInfo;
    }


    /**
     * 封装支付订单信息
     *
     * @param detailsId      列表id
     * @param notifyUrl      回调地址
     * @param paymentInfoDTO 支付信息DTO
     */
    private PayRequestOrder createOrder(String detailsId, String notifyUrl, PaymentInfoDTO paymentInfoDTO) {
        //获取交易类型
        Platform platform = paymentInfoDTO.getPayPlatform();
        PayType payType = paymentInfoDTO.getPayType();
        TransactionType transactionType = PayHelper.wayType(platform, payType);

        //渲染支付订单数据
        PayRequestOrder payOrder = new PayRequestOrder();

        payOrder.setOrderId(paymentInfoDTO.getOrderNum());
        payOrder.setShopId(paymentInfoDTO.getShopId());
        payOrder.setPayType(paymentInfoDTO.getPayType());
        payOrder.setTradeType(PayHelper.tradeType(platform));
        payOrder.setRouteKey(paymentInfoDTO.getRouteKey());
        payOrder.setSubject(paymentInfoDTO.getSubject());
        payOrder.setOutTradeNo(paymentInfoDTO.getOrderNum());
        payOrder.setDetailsId(detailsId);
        payOrder.setNotifyUrl(notifyUrl);
        payOrder.setWayTrade(
                PayType.WECHAT == payType ? ((Enum<?>) transactionType).name() : transactionType.getType()
        );
        payOrder.setTransactionType(transactionType);
        payOrder.setSubject(paymentInfoDTO.getSubject());
        payOrder.setBody(paymentInfoDTO.getBody());
        //支付宝（元 ） 微信（分） 余额（豪） 单位转换 bigDecimal 元 保留两位小数
        payOrder.setPrice(PayHelper.getPrice(paymentInfoDTO.getTotalFee(), payType));
        payOrder.setCurType(PayHelper.curType(paymentInfoDTO.getFeeType()));
        //订单过期时间
        payOrder.setExpirationTime(new Date(System.currentTimeMillis() + (paymentInfoDTO.getSeconds() + 20) * 1000));
        payOrder.setWapName(paymentInfoDTO.getSubject());
        payOrder.setAddition(paymentInfoDTO.getAttach());
        //终端ip
        payOrder.setSpbillCreateIp(paymentInfoDTO.getTerminalIp());
        payOrder.setOpenid(paymentInfoDTO.getOpenId());
        payOrder.setUserId(paymentInfoDTO.getUserId());
        payOrder.setAuthCode(paymentInfoDTO.getAuthCode());
        payOrder.setWapName(paymentInfoDTO.getWapName());
        payOrder.setWapUrl(paymentInfoDTO.getWapUrl());
        return payOrder;
    }


}
