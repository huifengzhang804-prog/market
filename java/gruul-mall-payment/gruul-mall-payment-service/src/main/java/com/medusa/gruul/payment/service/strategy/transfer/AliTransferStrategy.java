package com.medusa.gruul.payment.service.strategy.transfer;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.egzosn.pay.ali.bean.AliTransferOrder;
import com.egzosn.pay.ali.bean.AliTransferType;
import com.egzosn.pay.common.bean.TransferOrder;
import com.egzosn.pay.spring.boot.core.PayServiceManager;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.payment.api.model.transfer.AliTransferParam;
import com.medusa.gruul.payment.api.model.transfer.TransferParam;
import com.medusa.gruul.payment.api.model.transfer.TransferResult;
import com.medusa.gruul.payment.service.common.constant.AliConst;
import com.medusa.gruul.payment.service.model.PayConst;
import com.medusa.gruul.payment.service.model.enums.PaymentError;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author 张治保
 * date 2022/11/25
 */
public class AliTransferStrategy extends AbstractTransferStrategy {

    private static final String IDENTITY_TYPE = "ALIPAY_LOGON_ID";
    private static final String BUSINESS_PARAM = "{\"payer_show_name_use_alias\":\"true\"}";

    public AliTransferStrategy(PayServiceManager payServiceManager) {
        super(payServiceManager);
    }

    @Override
    protected String getDetailsId() {
        Map<String, String> platformDetailIdMap = RedisUtil.getCacheMap(
                RedisUtil.key(PayConst.PAY_CONFIG_CACHE_KEY, PayType.ALIPAY),
                new TypeReference<>() {
                }
        );
        if (CollUtil.isEmpty(platformDetailIdMap)) {
            throw PaymentError.PAYMENT_CHANNEL_NOT_CONFIGURED.exception();
        }
        return platformDetailIdMap.values().iterator().next();
    }


    @Override
    protected TransferOrder getTransferOrder(TransferParam transferParam) {
        AliTransferType transferType = AliTransferType.TRANS_ACCOUNT_NO_PWD;
        AliTransferParam transfer = (AliTransferParam) transferParam;
        //支付宝
        AliTransferOrder aliTransfer = new AliTransferOrder();
        aliTransfer.setOutBizNo(transfer.getOutNo());
        aliTransfer.setTransAmount(transfer.getAmountDecimal());
        aliTransfer.setTransferType(transferType);
        aliTransfer.setBizScene(transferType.getBizScene());
        aliTransfer.setIdentity(transfer.getAccount());
        aliTransfer.setIdentityType(IDENTITY_TYPE);
        aliTransfer.setName(transfer.getName());
        aliTransfer.setOrderTitle(transfer.getTitle());
        aliTransfer.setRemark(transfer.getRemark());
        //显示商户别名
        aliTransfer.setBusinessParams(BUSINESS_PARAM);
        return aliTransfer;
    }

    @Override
    protected TransferResult responseCheck(JSONObject transferResponse) {
        JSONObject response = transferResponse.getJSONObject(AliConst.ALIPAY_FUND_TRANS_UNI_TRANSFER_RESPONSE);
        String code = response.getString(AliConst.CODE);
        if (!AliConst.SUCCESS_CODE.equals(code)) {
            String subCode = response.getString(AliConst.SUB_CODE);
            //网络异常 重试
            if (AliConst.SYSTEM_ERROR_CODE.equals(subCode)) {
                return null;
            }
            this.error(StrUtil.format("转账发生错误：\n{}", response));
        }

        String status = response.getString(AliConst.STATUS);
        if (!AliConst.SUCCESS_STATUS.equals(status)) {
            this.error(StrUtil.format("转账发生错误：\n{}", response));
        }
        return new TransferResult()
                .setTradNo(response.getString(AliConst.ORDER_ID))
                .setTradeTime(response.getObject(AliConst.TRANS_DATE, LocalDateTime.class));
    }


}
