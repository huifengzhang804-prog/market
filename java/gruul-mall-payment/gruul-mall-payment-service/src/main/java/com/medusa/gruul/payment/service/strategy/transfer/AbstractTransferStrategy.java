package com.medusa.gruul.payment.service.strategy.transfer;

import com.alibaba.fastjson.JSONObject;
import com.egzosn.pay.common.bean.TransferOrder;
import com.egzosn.pay.common.exception.PayErrorException;
import com.egzosn.pay.spring.boot.core.PayServiceManager;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.global.model.strategy.IStrategy;
import com.medusa.gruul.payment.api.model.transfer.TransferParam;
import com.medusa.gruul.payment.api.model.transfer.TransferResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author 张治保
 * date 2022/11/25
 */
@Slf4j
@RequiredArgsConstructor
@Getter
public abstract class AbstractTransferStrategy implements IStrategy<PayType, TransferParam, TransferResult> {

    private final PayServiceManager payServiceManager;

    @Override
    public TransferResult execute(PayType type, TransferParam param) {
        param.validParam();
        TransferOrder transferOrder = getTransferOrder(param);
        TransferResult transferResult;
        Map<String, Object> response;
        String detailsId = getDetailsId();
        //null 表示需要重试
        do {
            try{
                response = payServiceManager.transfer(detailsId, transferOrder);

            } catch (PayErrorException e) {
                log.error("转账异常", e);
                throw new GlobalException(e.getPayError().getErrorMsg());
            }

            log.debug("转账请求响应结果:{}", response);
            transferResult = this.responseCheck((JSONObject) response);
        } while (transferResult == null);

        return transferResult;
    }


    /**
     * 获取商户支付配置详情id
     *
     * @return 商户支付配置详情id
     */
    protected abstract String getDetailsId();

    /**
     * 生成转账订单
     *
     * @param transferParam 原始转账订单数据
     * @return 转账订单
     */
    protected abstract TransferOrder getTransferOrder(TransferParam transferParam);

    /**
     * 检查转账结果并返回参数
     *
     * @param transferResponse 转账响应 返回null表示需要重试
     * @return 返回必要参数
     */
    protected abstract TransferResult responseCheck(JSONObject transferResponse);


    protected void error(String errorMsg) {
        log.error(errorMsg);
        throw new RuntimeException(errorMsg);
    }

}
