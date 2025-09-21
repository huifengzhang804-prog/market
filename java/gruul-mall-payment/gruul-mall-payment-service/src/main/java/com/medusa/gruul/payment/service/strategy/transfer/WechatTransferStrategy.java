package com.medusa.gruul.payment.service.strategy.transfer;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.egzosn.pay.common.bean.TransferOrder;
import com.egzosn.pay.spring.boot.core.PayServiceManager;
import com.egzosn.pay.wx.v3.bean.WxTransferType;
import com.egzosn.pay.wx.v3.bean.transfer.TransferDetail;
import com.egzosn.pay.wx.v3.bean.transfer.WxTransferOrder;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.system.model.model.Platform;
import com.medusa.gruul.payment.api.enums.TransferEnum;
import com.medusa.gruul.payment.api.model.transfer.TransferParam;
import com.medusa.gruul.payment.api.model.transfer.TransferResult;
import com.medusa.gruul.payment.api.model.transfer.WechatTransferParam;
import com.medusa.gruul.payment.service.common.constant.WxConst;
import com.medusa.gruul.payment.service.model.PayConst;
import com.medusa.gruul.payment.service.model.enums.PaymentError;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <a href="https://pay.weixin.qq.com/doc/v3/merchant/4012458841">发起商家转账</a>
 *
 * @author 张治保 date 2022/11/25
 */
@Slf4j
public class WechatTransferStrategy extends AbstractTransferStrategy {


    public WechatTransferStrategy(PayServiceManager payServiceManager) {
        super(payServiceManager);
    }

    @Override
    protected String getDetailsId() {
        String detailsId = RedisUtil.getCacheMapValue(
                RedisUtil.key(PayConst.PAY_CONFIG_CACHE_KEY, PayType.WECHAT),
                Platform.WECHAT_MINI_APP.name(),
                String.class
        );
        if (StrUtil.isEmpty(detailsId)) {
            throw PaymentError.PAYMENT_CHANNEL_NOT_CONFIGURED.exception();
        }
        return detailsId;
    }

    @Override
    protected TransferOrder getTransferOrder(TransferParam transferParam) {
        WechatTransferParam wechat = (WechatTransferParam) transferParam;
        WxTransferOrder order = new WxTransferOrder();
        String outNo = wechat.getOutNo();
        order.setBatchNo(outNo);
        order.setBatchName("提现");
        // 批次备注】转账说明，UTF8编码，最多允许32个字符
        String remark = wechat.getRemark();
        if (remark == null) {
            remark = "转账到零钱";
        } else if (remark.length() > 32) {
            remark = remark.substring(0, 32);
        }
        order.setTransferType(WxTransferType.TRANSFER_BATCHES);
        order.setBatchRemark(remark);
        order.setTotalAmount(wechat.getAmountDecimal());
        order.setTotalNum(1);
        // 转账明细列表
        TransferDetail detail = new TransferDetail();
        detail.setTransferAmount(order.getTotalAmount().multiply(CommonPool.BIG_DECIMAL_UNIT_CONVERSION_HUNDRED).intValue());
        detail.setOpenid(wechat.getOpenid());
        detail.setTransferRemark(order.getRemark());
        detail.setOutDetailNo("V" + outNo);
        order.setTransferDetailList(List.of(detail));
        return order;
    }


    @Override
    protected TransferResult responseCheck(JSONObject response) {
        if (response.containsKey(WxConst.SYSTEM_ERROR)) {
            log.error("微信转账异常：" + "\n {}", response);
            return null;
        }
        String batchStatus = response.getString(WxConst.TransferConst.BATCH_STATUS);
        TransferEnum transferEnum = switch (batchStatus) {
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


}
