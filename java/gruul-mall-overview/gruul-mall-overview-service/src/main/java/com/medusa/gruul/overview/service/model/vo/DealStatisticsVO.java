package com.medusa.gruul.overview.service.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 *  交易排行VO
 * @author xiaoq
 * @Description DealStatisticsVO.java
 * @date 2022-10-21 16:18
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class DealStatisticsVO {

    /**
     * 实际交易量
     */
    private Long realTradeVolume;

    /**
     * 实付交易额
     */
    private Long realTradingVolume;

    /**
     * 年月日
     */
    private LocalDate date;

    /**
     * 年月
     */
    private String currentMonth;
}
