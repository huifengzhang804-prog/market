package com.medusa.gruul.payment.service.strategy.pay;

import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.common.system.model.model.Platform;
import com.medusa.gruul.global.model.strategy.IStrategy;
import com.medusa.gruul.payment.api.model.dto.PaymentInfoDTO;
import com.medusa.gruul.payment.api.model.pay.PayResult;
import com.medusa.gruul.payment.service.common.model.PayRequestOrder;
import com.medusa.gruul.payment.service.service.MultiPayOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * 各平台调起支付处理器 抽象超类
 *
 * @author xiaoq
 * @since 2022-07-25 15:16
 */
@RequiredArgsConstructor
@Slf4j
public abstract class AbstractPayRequestStrategy implements IStrategy<Platform, PaymentInfoDTO, PayResult> {

    protected final MultiPayOrderService multiPayOrderService;


    @Override
    public PayResult execute(Platform type, PaymentInfoDTO param) {
        //获取转换支付入参
        log.debug("支付请求入参：{} ", param);
        PayResult payResult = new PayResult();
        payResult.setOutTradeNo(param.getOrderNum());
        //预处理支付订单
        PayRequestOrder payRequestOrder = multiPayOrderService.pretreatmentOrder(param);

        // 如果支付模式为余额 不走回调 手动触发
        if (param.getPayType() == PayType.BALANCE) {
            // 用户余额支付业务处理
            multiPayOrderService.balancePay(param);
            return payResult;
        }
        //非余额支付
        // 微信支付附加参数长度限制
        String addition = payRequestOrder.getAddition();
        if (PayType.WECHAT == payRequestOrder.getPayType() && StrUtil.length(addition) > Byte.MAX_VALUE) {
            payRequestOrder.setAddition("{单号：" + payRequestOrder.getOutTradeNo() + "}");
        }
        try {
            payResult.setData(this.payData(param, payRequestOrder));
        } finally {
            // 还原附加参数
            payRequestOrder.setAddition(addition);
        }
        return payResult;
    }


    /**
     * 获取调起支付所需参数
     *
     * @param paymentInfoDTO  支付参数
     * @param payRequestOrder 支付订单
     * @return 调起支付所需数据
     */
    protected abstract Object payData(PaymentInfoDTO paymentInfoDTO, PayRequestOrder payRequestOrder);


}
