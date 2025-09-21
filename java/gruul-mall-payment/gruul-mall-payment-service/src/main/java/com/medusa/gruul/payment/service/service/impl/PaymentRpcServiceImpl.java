package com.medusa.gruul.payment.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.payment.api.entity.PaymentInfo;
import com.medusa.gruul.payment.api.model.dto.PaymentInfoDTO;
import com.medusa.gruul.payment.api.model.dto.RefundRequestDTO;
import com.medusa.gruul.payment.api.model.param.CombineOrderParam;
import com.medusa.gruul.payment.api.model.param.ProfitSharingParam;
import com.medusa.gruul.payment.api.model.param.ProfitSharingUnfreezeParam;
import com.medusa.gruul.payment.api.model.pay.PayResult;
import com.medusa.gruul.payment.api.model.transfer.TransferParam;
import com.medusa.gruul.payment.api.model.transfer.TransferResult;
import com.medusa.gruul.payment.api.rpc.PaymentRpcService;
import com.medusa.gruul.payment.service.common.model.OrderPaymentRecord;
import com.medusa.gruul.payment.service.common.model.RefundRequestParam;
import com.medusa.gruul.payment.service.model.enums.PaymentError;
import com.medusa.gruul.payment.service.mp.service.IPaymentInfoService;
import com.medusa.gruul.payment.service.properties.PaymentProperty;
import com.medusa.gruul.payment.service.service.MultiPayOrderService;
import com.medusa.gruul.payment.service.service.WxServiceService;
import com.medusa.gruul.payment.service.strategy.pay.PayRequestStrategyFactory;
import com.medusa.gruul.payment.service.strategy.refund.request.RefundRequestStrategyFactory;
import com.medusa.gruul.payment.service.strategy.transfer.TransferStrategyFactory;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xiaoq
 * @ Description
 * date 2022-07-25 14:59
 */
@Service
@DubboService
@RequiredArgsConstructor
public class PaymentRpcServiceImpl implements PaymentRpcService {

    private final PaymentProperty payProperty;
    private final WxServiceService wxServiceService;
    private final MultiPayOrderService multiPayOrderService;
    private final IPaymentInfoService paymentInfoService;

    @Resource
    @Lazy
    private final RefundRequestStrategyFactory refundRequestStrategyFactory;
    @Resource
    @Lazy
    private TransferStrategyFactory transferStrategyFactory;
    @Resource
    @Lazy
    private PayRequestStrategyFactory payRequestStrategyFactory;


    @Override
    public PayResult combinePay(CombineOrderParam param) {
        //走普通支付的第一种方式 未开启服务商模式或者不是微信支付
        if (!payProperty.isEnableServiceMode() || PayType.WECHAT != param.getPayType()) {
            return payRequest(param.toPaymentInfo());
        }
        //已开启服务商模式  并且是微信支付
        List<CombineOrderParam.SubOrder> subOrders = param.getSubOrders();
        Map<Long, CombineOrderParam.SubOrder> shopIdSubOrderMap = subOrders.stream()
                .collect(Collectors.toMap(CombineOrderParam.SubOrder::getShopId, v -> v));
        //店铺id对应的签约商户id
        Map<Long, String> shopSubMchIdMap = wxServiceService.shopSubMchIdMap(shopIdSubOrderMap.keySet());
        //走普通支付的第二种方式  订单里没有一个签约商户
        if (CollUtil.isEmpty(shopSubMchIdMap)) {
            return payRequest(param.toPaymentInfo());
        }

        Object combine = wxServiceService.combineOrder(param, shopSubMchIdMap);
        PayResult payResult = new PayResult();
        payResult.setOutTradeNo(param.getOrderNo());
        payResult.setData(combine);
        return payResult;
    }

    @Override
    public void profitSharing(ProfitSharingParam param) {
        wxServiceService.profitSharing(param);
    }

    @Override
    public void profitSharingUnfreeze(ProfitSharingUnfreezeParam param) {
        wxServiceService.profitSharingUnfreeze(param);
    }

    /**
     * 支付业务发生一笔请求
     *
     * @param paymentInfoDTO 支付信息DTO
     * @return 支付结果
     */
    @Override
    public PayResult payRequest(PaymentInfoDTO paymentInfoDTO) {
        return payRequestStrategyFactory.execute(paymentInfoDTO.getPayPlatform(), paymentInfoDTO);
    }

    /**
     * 退款业务发生一笔请求
     *
     * @param request 退款信息DTO
     */
    @Override
    public void refundRequest(RefundRequestDTO request) {
        if (wxServiceService.refund(request)) {
            return;
        }
        OrderPaymentRecord orderPaymentRecord = multiPayOrderService.paymentRecord(request.getOrderNum());
        if (StrUtil.isBlank(orderPaymentRecord.getNotifyParam())) {
            throw PaymentError.NOT_FOUND_PAYMENT_RECORD.exception();
        }
        if (orderPaymentRecord.getDetailsId() == null) {
            orderPaymentRecord.setDetailsId("0");
        }
        refundRequestStrategyFactory.execute(
                orderPaymentRecord.getPayType(),
                new RefundRequestParam()
                        .setNotifyParam(orderPaymentRecord.getNotifyParam())
                        .setRequest(request)
                        .setDetailsId(orderPaymentRecord.getDetailsId())
        );
    }


    @Override
    public TransferResult transfer(TransferParam transferParam) {
        return transferStrategyFactory.execute(transferParam.getPayType(), transferParam);
    }

    @Override
    public boolean serviceEnable() {
        return wxServiceService.serviceEnable();
    }

    /**
     * 获取支付订单信息
     *
     * @param orderNo 订单号
     * @return 支付订单信息
     */
    @Override
    public PaymentInfo getPaymentInfo(String orderNo) {
        return TenantShop.disable(() -> paymentInfoService.lambdaQuery().eq(PaymentInfo::getOrderNum, orderNo).one());
    }

}
