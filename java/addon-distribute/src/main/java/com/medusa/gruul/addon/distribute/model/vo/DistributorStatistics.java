package com.medusa.gruul.addon.distribute.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 张治保
 * date 2022/11/17
 */
@Getter
@Setter
@ToString
public class DistributorStatistics {

    /**
     * 分销客户
     */
    private Long customer;

    /**
     * 分销订单数量
     */
    private Long order;

    /**
     * 待结算佣金
     */
    private Long bonus;


}
