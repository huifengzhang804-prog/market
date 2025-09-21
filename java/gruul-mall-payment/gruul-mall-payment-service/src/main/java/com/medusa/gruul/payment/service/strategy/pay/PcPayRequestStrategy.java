package com.medusa.gruul.payment.service.strategy.pay;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.egzosn.pay.ali.bean.AliTransactionType;
import com.egzosn.pay.common.bean.TransactionType;
import com.egzosn.pay.spring.boot.core.PayServiceManager;
import com.egzosn.pay.wx.v3.bean.WxTransactionType;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.payment.api.model.dto.PaymentInfoDTO;
import com.medusa.gruul.payment.service.common.model.PayRequestOrder;
import com.medusa.gruul.payment.service.service.MultiPayOrderService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * pc支付处理器
 *
 * @author xiaoq
 * @since 2022-07-26 17:00
 */
@Slf4j
public class PcPayRequestStrategy extends AbstractPayRequestStrategy {


    protected final PayServiceManager payServiceManager;

    public PcPayRequestStrategy(MultiPayOrderService multiPayOrderService, PayServiceManager payServiceManager) {
        super(multiPayOrderService);
        this.payServiceManager = payServiceManager;
    }

    /**
     * 获取调起支付所需参数
     *
     * @param paymentInfoDTO  支付参数
     * @param payRequestOrder 支付订单
     * @return 调起支付所需数据
     */
    @Override
    @SneakyThrows
    protected Object payData(PaymentInfoDTO paymentInfoDTO, PayRequestOrder payRequestOrder) {
        log.debug("===网页支付,二维码调取");
        PayType payType = paymentInfoDTO.getPayType();
        TransactionType transactionType = switch (payType) {
            case ALIPAY -> AliTransactionType.SWEEPPAY;
            case WECHAT -> WxTransactionType.NATIVE;
            default -> throw new IllegalStateException("Unexpected value: " + payType);
        };
        payRequestOrder.setTransactionType(transactionType);
        String qrUrl = payServiceManager.getQrPay(payRequestOrder);
        log.debug("获取到二维码：" + qrUrl);
        return QrCodeUtil.generateAsBase64(qrUrl, QrConfig.create().setWidth(300).setHeight(300), ImgUtil.IMAGE_TYPE_PNG);
    }
}
