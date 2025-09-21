package com.medusa.gruul.payment.api.rpc;

import com.medusa.gruul.payment.api.entity.PaymentInfo;
import com.medusa.gruul.payment.api.model.dto.PaymentInfoDTO;
import com.medusa.gruul.payment.api.model.dto.RefundRequestDTO;
import com.medusa.gruul.payment.api.model.param.CombineOrderParam;
import com.medusa.gruul.payment.api.model.param.ProfitSharingParam;
import com.medusa.gruul.payment.api.model.param.ProfitSharingUnfreezeParam;
import com.medusa.gruul.payment.api.model.pay.PayResult;
import com.medusa.gruul.payment.api.model.transfer.TransferParam;
import com.medusa.gruul.payment.api.model.transfer.TransferResult;
import jakarta.validation.constraints.NotNull;


/**
 * 订单业务处理rpc
 *
 * @author xiaoq
 * description PaymentRpcService.java
 * date 2022-07-25 14:02
 */
public interface PaymentRpcService {

    /**
     * 合单支付接口
     *
     * @param param 合单支付参数 目前只支持订单服务支付
     * @return 支付结果
     * @author 张治保
     */
    PayResult combinePay(CombineOrderParam param);

    /**
     * 分账接口
     *
     * @param param 分账参数
     */
    void profitSharing(ProfitSharingParam param);

    /**
     * 解冻分账剩余资金
     *
     * @param param 解冻参数
     */
    void profitSharingUnfreeze(ProfitSharingUnfreezeParam param);

    /**
     * 支付业务发生一笔请求
     *
     * @param paymentInfoDTO 支付信息DTO
     * @return 支付结果
     */
    PayResult payRequest(PaymentInfoDTO paymentInfoDTO);


    /**
     * 退款业务发生一笔请求
     *
     * @param refundRequestDTO 退款信息
     */
    void refundRequest(RefundRequestDTO refundRequestDTO);

    /**
     * 转账给用户
     *
     * @param transferParam 转账订单
     * @return 转账处理结果 转账订单号与转账时间
     */
    TransferResult transfer(TransferParam transferParam);

    /**
     * 检查是否开启了服务商模式
     *
     * @return 是否开启了服务商模式
     */
    boolean serviceEnable();

    /**
     * 获取支付订单信息
     *
     * @param orderNo 订单号
     * @return 支付订单信息
     */
    PaymentInfo getPaymentInfo(@NotNull String orderNo);
}
