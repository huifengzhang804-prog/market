package com.medusa.gruul.addon.integral.service.impl;

import com.medusa.gruul.addon.integral.model.dto.IntegralReceiverDTO;
import com.medusa.gruul.addon.integral.model.enums.IntegralOrderStatus;
import com.medusa.gruul.addon.integral.mp.entity.IntegralOrder;
import com.medusa.gruul.addon.integral.mp.entity.IntegralOrderReceiver;
import com.medusa.gruul.addon.integral.mp.service.IIntegralOrderReceiverService;
import com.medusa.gruul.addon.integral.mp.service.IIntegralOrderService;
import com.medusa.gruul.addon.integral.service.ReceiverService;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.global.model.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author miskw
 * @date 2023/7/20
 * @describe 修改用户未支付订单收货地址
 */
@Service("integralReceiverServiceImpl")
@RequiredArgsConstructor
public class ReceiverServiceImpl implements ReceiverService {
    private final IIntegralOrderService integralOrderService;
    private final IIntegralOrderReceiverService integralOrderReceiverService;

    /**
     * 修改未支付订单收货地址
     *
     * @param orderNo  积分订单号
     * @param receiver 收货地址
     */
    @Override
    public void updateReceiver(String orderNo, IntegralReceiverDTO receiver) {
        IntegralOrder integralOrder = integralOrderService.lambdaQuery()
                .select(IntegralOrder::getId, IntegralOrder::getStatus)
                .eq(IntegralOrder::getNo, orderNo)
                .oneOpt()
                .orElseThrow(() -> new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "积分订单不存在"));
        if (integralOrder.getStatus() != IntegralOrderStatus.UNPAID) {
            throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "只能修改待支付的积分订单");
        }
        IntegralOrderReceiver orderReceiver = integralOrderReceiverService.lambdaQuery()
                .select(IntegralOrderReceiver::getId)
                .eq(IntegralOrderReceiver::getOrderNo, orderNo)
                .oneOpt()
                .orElse(new IntegralOrderReceiver().setOrderNo(orderNo));
        orderReceiver.setName(receiver.getName())
                .setMobile(receiver.getMobile())
                .setArea(receiver.getArea())
                .setAddress(receiver.getAddress());
        SystemCode.DATA_UPDATE_FAILED.falseThrow(
                integralOrderReceiverService.saveOrUpdate(orderReceiver)
        );

    }
}
