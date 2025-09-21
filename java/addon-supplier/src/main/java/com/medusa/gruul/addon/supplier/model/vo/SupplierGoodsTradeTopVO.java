package com.medusa.gruul.addon.supplier.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>商品交易量TOP VO</p>
 * @author An.Yan
 */
@Getter
@Setter
@Accessors(chain = true)
public class SupplierGoodsTradeTopVO {

    private Integer rank;

    private String productName;

    private Long productId;

    private Long number;

    private String pic;
}
