package com.medusa.gruul.payment.api.model.pay;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * 业务支付结果
 * </p>
 *
 * @author xiaoq
 * @ Description PayResult.java
 * @date 2022-07-25 14:55
 */
@Data
@Accessors(chain = true)
public class PayResult implements Serializable {

    @Serial
    private static final long serialVersionUID = -8745504357116457006L;

    /**
     * 是否扔需要支付 如果需要 则使用支付数据 拉起支付 否则 直接跳转至支付成功页面
     */
    private boolean needPay = true;

    /**
     * 支付服务生成的外部支付订单号
     */
    private String outTradeNo;

    /**
     * 支付数据详情 前端需要的东西
     */
    private Object data;


}
