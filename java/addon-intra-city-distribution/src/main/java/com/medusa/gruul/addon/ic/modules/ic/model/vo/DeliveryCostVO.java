package com.medusa.gruul.addon.ic.modules.ic.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 配送费
 *
 * @author miskw
 * @date 2023/3/7
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class DeliveryCostVO {
    /**
     * 店铺id
     */
    private Long shopId;
    /**
     * 配送费
     */
    private BigDecimal cost;
}

