package com.medusa.gruul.addon.rebate.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class RebateTransactionsVO {

    /**
     * 用户id
     */
    private Long userId;


    /**
     * 返利余额
     */
    private Long rebateBalance;


    /**
     * 累计返利
     */
    private Long accumulatedRebate;

    /**
     * 待结算返利
     */
    private Long unsettledRebate;

    /**
     * 已失效返利
     */
    private Long expiredRebate;
}
