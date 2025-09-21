/*
package com.medusa.gruul.payment.service.strategy.transfer;

import com.alibaba.fastjson.JSONObject;
import com.egzosn.pay.common.bean.TransferOrder;
import com.egzosn.pay.spring.boot.core.PayServiceManager;
import com.medusa.gruul.common.web.handler.Handler;
import com.medusa.gruul.payment.api.model.transfer.TransferParam;
import com.medusa.gruul.payment.api.model.transfer.TransferResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

*/
/**
 * @author 张治保
 * date 2022/11/25
 *//*

@Slf4j
@RequiredArgsConstructor
@Getter
public abstract class AbstractTransferHandler implements Handler<TransferResult> {

    private final PayServiceManager payServiceManager;
//

    @Override
    public TransferResult handle(Object... params) {
        if (hasErrorParam(params, TransferParam.class)) {
            throw new IllegalArgumentException("param is not TransferParam");
        }
        TransferParam transferParam = cast(params[0], TransferParam.class);
        transferParam.validParam();
        TransferOrder transferOrder = getTransferOrder(transferParam);
        TransferResult transferResult;
        Map<String, Object> response;
        //null 表示需要重试
        do {

            response = transfer(transferParam, transferOrder);

            transferResult = this.responseCheck((JSONObject) response);
        } while (transferResult == null);

        return transferResult;
    }


    */
/**
 * 获取商户支付配置详情id
 *
 * @return 商户支付配置详情id
 *//*

    abstract String getDetailsId();

    */
/**
 * 转账
 *
 * @param transferParam
 * @param transferOrder
 * @return
 *//*

    Map<String, Object> transfer(TransferParam transferParam, TransferOrder transferOrder) {
        return payServiceManager.transfer(getDetailsId(), transferOrder);
    }

    */
/**
 * 生成转账订单
 *
 * @param transferParam 原始转账订单数据
 * @return 转账订单
 *//*

    abstract TransferOrder getTransferOrder(TransferParam transferParam);

    */
/**
 * 检查转账结果并返回参数
 *
 * @param transferResponse 转账响应 返回null表示需要重试
 * @return 返回必要参数
 *//*

    abstract TransferResult responseCheck(JSONObject transferResponse);


    protected void error(String errorMsg) {
        log.error(errorMsg);
        throw new RuntimeException(errorMsg);
    }
}
*/
