package com.medusa.gruul.addon.integral.mp.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.integral.mp.entity.IntegralOrder;
import com.medusa.gruul.addon.integral.mp.entity.IntegralOrderPayment;
import com.medusa.gruul.addon.integral.mp.mapper.IntegralOrderPaymentMapper;
import com.medusa.gruul.addon.integral.mp.service.IIntegralOrderPaymentService;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.payment.api.model.dto.PayNotifyResultDTO;
import com.medusa.gruul.payment.api.model.dto.PaymentInfoDTO;
import org.springframework.stereotype.Service;

/**
 * @author shishuqian
 * date 2023/2/3
 * time 10:48
 **/

@Service
public class IntegralOrderPaymentServiceImpl extends ServiceImpl<IntegralOrderPaymentMapper, IntegralOrderPayment> implements IIntegralOrderPaymentService {
    @Override
    public void saveIntegralPaymentInfo(IntegralOrder integralOrder, PayNotifyResultDTO payNotifyResult) {

        PaymentInfoDTO paymentInfo = payNotifyResult.getBusinessParams();
        IntegralOrderPayment integralOrderPayment = new IntegralOrderPayment();
        integralOrderPayment.setOrderNo(paymentInfo.getOrderNum())
                .setSn(payNotifyResult.getShopIdTransactionMap().values().iterator().next().getTransactionId())
                .setPayerId(paymentInfo.getUserId())
                .setPayType(PayType.BALANCE)
                .setSecPayType(paymentInfo.getPayType())
                //积分商品一次只能买一件，付款的总费用就是运费
                .setFreightAmount(integralOrder.getFreightPrice())
                .setPayAmount(paymentInfo.getTotalFee())
                .setOpenId(paymentInfo.getOpenId())
                //只能买一件 总积分和支付积分都一样
                .setTotalIntegral(integralOrder.getPrice())
                .setPayIntegral(integralOrder.getPrice())
                .setSalePrice(integralOrder.getSalePrice())
                .setPayTime(integralOrder.getPayTime());

        this.save(integralOrderPayment);
    }

    @Override
    public void saveZeroPaymentInfo(IntegralOrder integralOrder) {
        IntegralOrderPayment integralOrderPayment = new IntegralOrderPayment();
        integralOrderPayment.setOrderNo(integralOrder.getNo())
                .setSn(IdUtil.getSnowflakeNextIdStr())
                .setPayerId(integralOrder.getBuyerId())
                .setPayType(PayType.BALANCE)
                .setFreightAmount(0L)
                .setTotalIntegral(integralOrder.getPrice())
                .setSalePrice(integralOrder.getSalePrice())
                .setPayIntegral(integralOrder.getPrice())
                .setPayAmount(0L)
                .setPayTime(integralOrder.getPayTime());

        this.save(integralOrderPayment);
    }


    @Override
    public boolean integralOrderPayStatus(String integralOrderNo) {
        return this.lambdaQuery()
                .eq(IntegralOrderPayment::getOrderNo, integralOrderNo)
                .exists();
    }
}
