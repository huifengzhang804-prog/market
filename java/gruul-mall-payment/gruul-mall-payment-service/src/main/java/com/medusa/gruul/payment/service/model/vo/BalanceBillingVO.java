package com.medusa.gruul.payment.service.model.vo;

import lombok.Data;

/**
 * 余额计费vo
 *
 * @author xiaoq
 * @Description BalanceBillingVO.java
 * @date 2022-11-25 11:27
 */
@Data
public class BalanceBillingVO {

    /**
     * 充值次数
     */
    private Long rechargeNumber;

    /**
     * 累计金额(个人充值，系统赠送,系统充值)
     */
    private  Long totalMoney;
}
