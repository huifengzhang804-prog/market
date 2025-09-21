package com.medusa.gruul.user.service.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class StatementStatisticsVO {
    /**
     * 兑换金额
     */
    private Long exchangeIntegral;
    /**
     * 赚取积分
     */
    private Long earnIntegral;

    public StatementStatisticsVO() {
        exchangeIntegral = 0L;
        earnIntegral = 0L;
    }
}