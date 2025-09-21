package com.medusa.gruul.addon.integral.service.impl;

import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.addon.integral.model.dto.IntegralOrderPayDTO;
import com.medusa.gruul.addon.integral.model.enums.IntegralOrderRabbit;
import com.medusa.gruul.addon.integral.model.enums.IntegralOrderStatus;
import com.medusa.gruul.addon.integral.mp.entity.IntegralOrder;
import com.medusa.gruul.addon.integral.mp.service.IIntegralOrderPaymentService;
import com.medusa.gruul.addon.integral.mp.service.IIntegralOrderService;
import com.medusa.gruul.addon.integral.service.IntegralOrderPayService;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.payment.api.enums.FeeType;
import com.medusa.gruul.payment.api.model.dto.PayNotifyResultDTO;
import com.medusa.gruul.payment.api.model.dto.PaymentInfoDTO;
import com.medusa.gruul.payment.api.model.pay.PayResult;
import com.medusa.gruul.payment.api.rpc.PaymentRpcService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * @author shishuqian
 * date 2023/2/2
 * time 17:47
 **/

@Service
@RequiredArgsConstructor
public class IntegralOrderPayServiceImpl implements IntegralOrderPayService {

    private final IIntegralOrderService iIntegralOrderService;

    private final PaymentRpcService paymentRpcService;

    private final IIntegralOrderPaymentService iIntegralOrderPaymentService;

    @Override
    public PayResult toPay(IntegralOrderPayDTO integralOrderPayDTO, SecureUser user) {
        String integralOrderNo = integralOrderPayDTO.getIntegralOrderNo();
        //根据订单号 获取未支付订单
        IntegralOrder integralOrder = this.iIntegralOrderService.getUnpaidIntegralOrder(integralOrderNo);
        if (integralOrder == null) {
            throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "积分订单不可支付");
        }
        return this.getPayResult(integralOrderNo, integralOrderPayDTO.getPayType(), integralOrder.getFreightPrice() + integralOrder.getSalePrice(), user);
    }


    @Log("支付回调")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void payNotify(PayNotifyResultDTO payNotifyResult) {

        PaymentInfoDTO businessParams = payNotifyResult.getBusinessParams();
        String integralOrderNo = businessParams.getOrderNum();

        IntegralOrder unpaidIntegralOrder = this.iIntegralOrderService.getUnpaidIntegralOrder(integralOrderNo);
        if (unpaidIntegralOrder == null) {
            //说明支付过了
            return;
        }

        //当前时间
        LocalDateTime now = LocalDateTime.now();
        //更新积分订单的支付状态
        boolean success = this.iIntegralOrderService.lambdaUpdate()
                .set(IntegralOrder::getStatus, IntegralOrderStatus.PAID)
                .set(IntegralOrder::getPayTime, now)
                .eq(IntegralOrder::getStatus, IntegralOrderStatus.UNPAID)
                .eq(IntegralOrder::getNo, integralOrderNo)
                .eq(IntegralOrder::getAffiliationPlatform, payNotifyResult.getBusinessParams().getPayPlatform())
                .update();

        unpaidIntegralOrder.setPayTime(now);
        if (!success) {
            throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "积分订单状态已改变，不能支付");
        }

        //插入支付数据
        this.iIntegralOrderPaymentService.saveIntegralPaymentInfo(unpaidIntegralOrder, payNotifyResult);
    }

    /**
     * @param orderNo  订单号
     * @param payType  支付类型
     * @param totalFee 总费用
     * @return 支付渲染数据
     */
    private PayResult getPayResult(String orderNo, PayType payType, Long totalFee, SecureUser user) {
        PaymentInfoDTO paymentInfo = new PaymentInfoDTO();
        paymentInfo.setOrderNum(orderNo);
        paymentInfo.setPayType(payType);
        paymentInfo.setSubject(StrUtil.format("积分订单编号：{}", orderNo));
        paymentInfo.setBody("");
        paymentInfo.setExchange(IntegralOrderRabbit.INTEGRAL_ORDER_PAID_CALLBACK.exchange());
        paymentInfo.setRouteKey(IntegralOrderRabbit.INTEGRAL_ORDER_PAID_CALLBACK.routingKey());
        paymentInfo.setFeeType(FeeType.CNY);
        paymentInfo.setTotalFee(totalFee);
        paymentInfo.setTerminalIp(ISystem.ipMust());
        paymentInfo.setPayPlatform(ISystem.platformMust());
        paymentInfo.setSeconds(30 * 60L);
        paymentInfo.setUserId(user.getId());
        paymentInfo.setOpenId(user.getOpenid());
        paymentInfo.setAttach(null);
        paymentInfo.setShopId(ISystem.shopIdMust());
        paymentInfo.setAuthCode(null);
        paymentInfo.setWapUrl(null);
        paymentInfo.setWapName(null);

        return this.paymentRpcService.payRequest(paymentInfo);
    }
}
