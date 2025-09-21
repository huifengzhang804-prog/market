package com.medusa.gruul.addon.store.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 门店店铺交易汇总VO
 *
 * @author xiaoq
 * @Description 门店店铺交易汇总VO
 * @date 2023-03-17 09:27
 */
@Data
@Accessors(chain = true)
public class ShopStoreTransactionSummaryVO {

    /**
     * 今日交易额
     */
    private Long todayTotalAmount;

    /**
     * 今日交易量
     */
    private Long todayTransactionCount;

    /**
     * 今日客单价
     */
    private Long todayAverageTransactionValue;


    /**
     * 本月交易额
     */
    private Long monthTotalAmount;

    /**
     * 本月交易量
     */
    private Long monthTransactionCount;

    /**
     * 本月客单价
     */
    private Long monthAverageTransactionValue;


}
