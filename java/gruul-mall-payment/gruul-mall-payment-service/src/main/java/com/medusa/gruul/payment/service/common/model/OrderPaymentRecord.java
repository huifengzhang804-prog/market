package com.medusa.gruul.payment.service.common.model;

import com.medusa.gruul.common.model.enums.PayType;
import lombok.Data;

/**
 * 支付记录信息
 *
 * @author xiaoq
 * @ Description
 * @date 2022-08-03 09:52
 */
@Data
public class OrderPaymentRecord {

    /**
     * 支付回调参数
     */
    private String notifyParam;

    /**
     * 支付类型
     */
    private PayType payType;

    /**
     * 商户配置详情id
     */
    private String detailsId;
}
