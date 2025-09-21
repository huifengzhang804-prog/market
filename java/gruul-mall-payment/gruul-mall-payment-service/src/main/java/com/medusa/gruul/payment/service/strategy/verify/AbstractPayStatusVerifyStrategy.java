package com.medusa.gruul.payment.service.strategy.verify;

import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.global.model.strategy.IStrategy;
import com.medusa.gruul.payment.api.enums.TradeStatus;
import com.medusa.gruul.payment.service.common.model.OrderPaymentRecord;
import com.medusa.gruul.payment.service.service.MultiPayOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 支付状态校验处理器
 * todo 可以先试用回调参数校验是否成功 然后再使用查询接口校验
 *
 * @author xiaoq
 * @since 2022-08-01 15:49
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractPayStatusVerifyStrategy implements IStrategy<PayType, String, TradeStatus> {

    private final MultiPayOrderService multiPayOrderService;


    /**
     * 处理参数
     *
     * @param type    支付类型
     * @param orderNo 订单号
     * @return 返回内容 <T>返回数据类型
     */
    @Override
    public TradeStatus execute(PayType type, String orderNo) {
        if (StrUtil.isBlank(orderNo)) {
            throw new IllegalArgumentException("orderNo is null");
        }

        OrderPaymentRecord paymentRecord = multiPayOrderService.paymentRecord(orderNo);
        TradeStatus status = handle(paymentRecord.getDetailsId(), orderNo);
        log.debug("支付状态检查结果:{}", status);
        return status;
    }

    /**
     * handle
     *
     * @param detailsId  商户支付配置列表id
     * @param outTradeNo 业务单号
     * @return 交易状态
     */
    protected abstract TradeStatus handle(String detailsId, String outTradeNo);


}
