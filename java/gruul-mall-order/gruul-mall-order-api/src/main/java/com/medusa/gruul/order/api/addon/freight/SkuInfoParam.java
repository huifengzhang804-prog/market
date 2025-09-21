package com.medusa.gruul.order.api.addon.freight;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author xiaoq
 * @Description
 * @date 2022-06-07 16:35
 */
@Data
public class SkuInfoParam implements Serializable {

    private static final long serialVersionUID = 2389819680245070838L;
    /**
     * 重量
     */
    private BigDecimal weight;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 数量
     */
    private Integer num;


    private Long skuId;
}
