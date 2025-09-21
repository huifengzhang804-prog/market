package com.medusa.gruul.overview.api.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.overview.api.enums.OverviewDealType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * t_deal_ranking
 *
 * @author xiaoq
 */
@Getter
@Setter
@TableName("t_deal_ranking")
@Accessors(chain = true)
public class DealRanking extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 产品id
     */
    private Long productId;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 交易类型
     */
    private OverviewDealType overviewDealType;

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


}