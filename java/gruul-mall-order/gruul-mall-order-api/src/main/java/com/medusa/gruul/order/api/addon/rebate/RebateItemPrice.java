package com.medusa.gruul.order.api.addon.rebate;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2023/9/13
 */

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class RebateItemPrice implements Serializable {

    /**
     * 调整后的item对应的实际成交价
     */
    private Long dealPrice;

    /**
     * 调整后的修正价格
     */
    private Long fixPrice;

    /**
     * 返利折扣价
     */
    private Long rebateDiscount;
}
