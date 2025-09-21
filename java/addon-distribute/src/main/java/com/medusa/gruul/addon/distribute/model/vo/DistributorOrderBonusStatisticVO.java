package com.medusa.gruul.addon.distribute.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 订单佣金统计
 *
 * @author 张治保
 * date 2023/5/13
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class DistributorOrderBonusStatisticVO implements Serializable {

    /**
     * 总佣金
     */
    private long total;

    /**
     * 待结算佣金
     */
    private long unsettled;

    /**
     * 已失效佣金
     */
    private long invalid;
}
