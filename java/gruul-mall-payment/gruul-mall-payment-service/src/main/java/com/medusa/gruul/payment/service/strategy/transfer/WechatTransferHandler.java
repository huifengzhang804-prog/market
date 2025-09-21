/*
package com.medusa.gruul.payment.service.strategy.transfer;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.egzosn.pay.common.bean.TransferOrder;
import com.egzosn.pay.spring.boot.core.PayServiceManager;
import com.egzosn.pay.wx.bean.WxTransferType;
import com.github.binarywang.wxpay.bean.transfer.TransferBatchesRequest;
import com.github.binarywang.wxpay.bean.transfer.TransferBatchesRequest.TransferDetail;
import com.github.binarywang.wxpay.bean.transfer.TransferBatchesResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.google.common.collect.Lists;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.common.system.model.model.Platform;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.payment.api.enums.TransferEnum;
import com.medusa.gruul.payment.api.model.transfer.TransferParam;
import com.medusa.gruul.payment.api.model.transfer.TransferResult;
import com.medusa.gruul.payment.api.model.transfer.WechatTransferParam;
import com.medusa.gruul.payment.service.common.annotation.TransferHandler;
import com.medusa.gruul.payment.service.common.constant.WxConst;
import com.medusa.gruul.payment.service.model.enums.PaymentError;
import com.medusa.gruul.payment.service.model.vo.MerchantDetailsVO;
import com.medusa.gruul.payment.service.mp.service.IPaymentMerchantConfigService;
import com.medusa.gruul.payment.service.properties.PaymentProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

*/
/**
 * @author 张治保 date 2022/11/25
 *//*

@Component
@TransferHandler(PayType.WECHAT)
@Slf4j
public class WechatTransferHandler extends AbstractTransferHandler implements
        ApplicationContextAware {
    private final IPaymentMerchantConfigService paymentMerchantConfigService;
    private final PaymentProperty payProperty;
    */
/**
 * 是否采用V3版本的微信转账 未配置 默认为false
 *//*

    @Value("${gruul.pay.vx-trans-use-v3:false}")
    private Boolean vXV3Use;
    private WxPayService wxPayService;

    public WechatTransferHandler(PayServiceManager payServiceManager,
                                 IPaymentMerchantConfigService paymentMerchantConfigService,
                                 PaymentProperty payProperty) {
        super(payServiceManager);
        this.paymentMerchantConfigService = paymentMerchantConfigService;
        this.payProperty = payProperty;
    }

    @Override
    String getDetailsId() {
        MerchantDetailsVO merchantDetail = paymentMerchantConfigService.getMerchantDetailByPlatform(
                PayType.WECHAT, Platform.WECHAT_MINI_APP);
        String detailsId;
        if (merchantDetail == null || StrUtil.isBlank(detailsId = merchantDetail.getDetailsId())) {
            throw PaymentError.PAYMENT_CHANNEL_NOT_CONFIGURED.exception();
        }
        return detailsId;
    }

    @Override
    Map<String, Object> transfer(TransferParam transferParam, TransferOrder transferOrder) {
        if (vXV3Use) {
            //使用微信V3转账功能
            return vXV3Trans((WechatTransferParam) transferParam, transferOrder);
        }
        return super.transfer(transferParam, transferOrder);
    }

    */
/**
 * V3版本微信转账
 *
 * @param transferParam
 * @param transferOrder
 * @return
 *//*

    private Map<String, Object> vXV3Trans(WechatTransferParam transferParam, TransferOrder transferOrder) {
        JSONObject response = new JSONObject();
        TransferBatchesRequest param = new TransferBatchesRequest();
        try {
            param.setAppid(wxPayService.getConfig().getAppId());
            param.setOutBatchNo(transferOrder.getOutNo());
            param.setNotifyUrl(payProperty.getNotifyUrlPrefix() + "/merchant/pay/transfer/notify");

            param.setBatchName(transferOrder.getOutNo() + " 商户转账到零钱");
            param.setBatchRemark(transferOrder.getRemark());
            param.setTotalAmount(transferOrder.getAmount().multiply(BigDecimal.valueOf(CommonPool.NUMBER_ONE_HUNDRED)).intValue());
            param.setTotalNum(1);
            List<TransferDetail> transferDetailList = Lists.newArrayList();
            TransferDetail transferDetail = new TransferDetail();
            transferDetail.setOutDetailNo(param.getOutBatchNo() + "1");
            transferDetail.setTransferAmount(param.getTotalAmount());
            transferDetail.setTransferRemark(transferOrder.getRemark());
            transferDetail.setOpenid(transferParam.getOpenid());
            transferDetailList.add(transferDetail);
            param.setTransferDetailList(transferDetailList);

            TransferBatchesResult transferBatchesResult = wxPayService.getTransferService().transferBatches(param);
            //ACCEPTED:已受理。批次已受理成功，若发起批量转账的30分钟后，转账批次单仍处于该状态，可能原因是商户账户余额不足等。商户可查询账户资金流水，若该笔转账批次单的扣款已经发生，则表示批次已经进入转账中，请再次查单确认
            //PROCESSING:转账中。已开始处理批次内的转账明细单
            //FINISHED:已完成。批次内的所有转账明细单都已处理完成
            //CLOSED:已关闭。可查询具体的批次关闭原因确认
            String batchStatus = transferBatchesResult.getBatchStatus();
            response.put(WxConst.PAYMENT_NO, transferBatchesResult.getBatchId());
            DateTimeFormatter formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
            LocalDateTime localDateTime = LocalDateTime.parse(transferBatchesResult.getCreateTime(),
                    formatter);
            response.put(WxConst.PAYMENT_TIME, localDateTime);
            response.put(WxConst.RESULT_CODE, WxConst.SUCCESS);
            response.put(WxConst.ERR_CODE, "OK");
            response.put(WxConst.RESULT_MSG, "已完成");
            response.put(WxConst.TransferConst.TRANS_VERSION, WxConst.TransferConst.TRANS_VERSION_V3);
            if (WxConst.TransferConst.BATCH_STATUS_ACCEPTED.equals(batchStatus) ||
                    WxConst.TransferConst.BATCH_STATUS_PROCESSING.equals(batchStatus)) {
                //已接受与转账中的状态
                response.put(WxConst.RETURN_CODE, WxConst.TransferConst.BATCH_STATUS_PROCESSING);
                response.put(WxConst.RETURN_MSG, "进行中");
                return response;
            }
            if (WxConst.TransferConst.BATCH_STATUS_CLOSED.equals(batchStatus)) {
                //已关闭的状态
                response.put(WxConst.RETURN_CODE, WxConst.TransferConst.BATCH_STATUS_CLOSED);
                response.put(WxConst.RETURN_MSG, "已关闭");
                return response;
            }
            if (WxConst.TransferConst.BATCH_STATUS_FINISH.equals(batchStatus)) {
                response.put(WxConst.RETURN_CODE, WxConst.TransferConst.BATCH_STATUS_FINISH);
                response.put(WxConst.RETURN_MSG, "已完成");
                return response;
            }
        } catch (WxPayException e) {
            response.put(WxConst.RETURN_CODE, e.getReturnCode());
            response.put(WxConst.RETURN_MSG, e.getReturnMsg());
            response.put(WxConst.RESULT_CODE, e.getResultCode());
            response.put(WxConst.ERR_CODE, e.getErrCode());
            response.put(WxConst.RESULT_MSG, e.getErrCodeDes());

        }

        return response;
    }


    @Override
    TransferOrder getTransferOrder(TransferParam transferParam) {
        WechatTransferParam wechat = (WechatTransferParam) transferParam;
        TransferOrder wechatTransfer = new TransferOrder();
        wechatTransfer.setOutNo(wechat.getOutNo());
        wechatTransfer.setPayeeAccount(wechat.getOpenid());
        wechatTransfer.addAttr("check_name", "NO_CHECK");
        wechatTransfer.setAmount(wechat.getAmountDecimal());
        wechatTransfer.setTransferType(WxTransferType.TRANSFERS);
        wechatTransfer.setRemark(wechat.getRemark());
        if (StrUtil.isNotBlank(wechat.getIp())) {
            wechatTransfer.setIp(wechat.getIp());
        }
        return wechatTransfer;
    }

    @Override
    TransferResult responseCheck(JSONObject response) {
        if (response.containsKey(WxConst.TransferConst.TRANS_VERSION) &&
                StrUtil.equals(response.get(WxConst.TransferConst.TRANS_VERSION).toString(),
                        WxConst.TransferConst.TRANS_VERSION_V3)) {
            return vxV3TransTransferResult(response);
        }


//    String returnCode = response.getString(WxConst.RETURN_CODE);
//    if (!WxConst.SUCCESS.equals(returnCode)) {
//      this.error(
//          StrUtil.format("微信转账异常：" + "\n returnCode：{} returnMsg:{}", returnCode,
//              response.getString(WxConst.RETURN_MSG))
//      );
//    }
        String resultCode = response.getString(WxConst.RESULT_CODE);
        if (!WxConst.SUCCESS.equals(resultCode)) {
            //系统错误需要重试
            if (WxConst.SYSTEM_ERROR.equals(response.getString(WxConst.ERR_CODE))) {
                return null;
            }
            throw new GlobalException(response.getString(WxConst.RESULT_MSG));
//      this.error(
//          StrUtil.format("微信转账异常：" + "\n resultCode：{} resultMsg:{}", resultCode,
//              response.getString(WxConst.RESULT_MSG))
//      );
        }
        return new TransferResult()
                .setTradNo(response.getString(WxConst.PAYMENT_NO))
                .setTradeTime(response.getObject(WxConst.PAYMENT_TIME, LocalDateTime.class));
    }

    */
/**
 * V3版本转账返回结果处理
 *
 * @param response
 * @return
 *//*

    private TransferResult vxV3TransTransferResult(JSONObject response) {
        if (response.containsKey(WxConst.SYSTEM_ERROR)) {
            log.error(StrUtil.format("微信转账异常：" + "\n returnCode：{} returnMsg:{}",
                    response.getString(WxConst.RESULT_CODE),
                    response.getString(WxConst.RETURN_MSG)));
            return null;
        }
        String returnCode = response.getString(WxConst.RETURN_CODE);
        TransferEnum transferEnum = switch (returnCode) {
            case WxConst.TransferConst.BATCH_STATUS_PROCESSING,
                 WxConst.TransferConst.BATCH_STATUS_ACCEPTED -> TransferEnum.WX_PROCESSING;
            case WxConst.TransferConst.BATCH_STATUS_FINISH -> TransferEnum.WX_SUCCESS;
            case WxConst.TransferConst.BATCH_STATUS_CLOSED -> TransferEnum.WX_CLOSED;
            default -> throw new RuntimeException("未知的微信转账状态");
        };

        return new TransferResult()
                .setTradNo(response.getString(WxConst.PAYMENT_NO))
                .setStatus(transferEnum)
                .setTradeTime(response.getObject(WxConst.PAYMENT_TIME, LocalDateTime.class));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        wxPayService = applicationContext.getBean(WxPayService.class);

    }


}
*/
