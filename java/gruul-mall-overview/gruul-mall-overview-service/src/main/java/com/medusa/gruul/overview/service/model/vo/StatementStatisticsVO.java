package com.medusa.gruul.overview.service.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 对账单概况
 *
 * @author WuDi
 * date 2022/10/10
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class StatementStatisticsVO {

    /**
     * 收入金额
     */
    private Long income;
    /**
     * 收入笔数
     */
    private Long incomeCount;

    /**
     * 支出金额
     */
    private Long payout;

    /**
     * 支出笔数
     */
    private Long payoutCount;


    public StatementStatisticsVO() {
        this.setIncome(0L);
        this.setIncomeCount(0L);
        this.setPayout(0L);
        this.setPayoutCount(0L);
    }


}
