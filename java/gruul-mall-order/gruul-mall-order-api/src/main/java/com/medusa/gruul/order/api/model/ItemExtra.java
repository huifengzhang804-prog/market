package com.medusa.gruul.order.api.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2023/6/21
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class ItemExtra implements Serializable {

    /**
     * 优惠的运费
     */
    private Long discountFreight = 0L;

    /**
     * 分润比率
     */
    private Integer profitRate = 0;

    /**
     * 供应商分润比例
     */
    private Integer supplierProfitRate = 0;

    /**
     * 属性金额
     */
    private Long attributeMoney;

    /**
     * sku 真实价格 (如果供应商 该价格就是供应商可以得到得金额)
     */
    private Long skuPracticalSalePrice;

}
