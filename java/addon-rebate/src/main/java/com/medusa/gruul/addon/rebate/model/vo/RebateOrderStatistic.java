package com.medusa.gruul.addon.rebate.model.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RebateOrderStatistic {

    /**
     * 已失效总返利
     */

    private Long totalExpired;

    /**
     * 待结算总返利
     */
    private Long totalPendingSettlement;

    /**
     * 返利合计
     */
    private Long totalRebate;

}
