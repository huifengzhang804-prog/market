package com.medusa.gruul.addon.ic.modules.ic.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * @author 张治保
 * @since 2024/8/28
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class DeliverPriceVO {

    /**
     * 总运费
     */
    private Long totalPrice;

    /**
     * 每个订单的运费
     * key 订单号 value 订单价格
     */
    private Map<String, DeliverItemPriceVO> orderPriceMap;

    /**
     * 当前账户余额
     */
    private Long balance;

}
