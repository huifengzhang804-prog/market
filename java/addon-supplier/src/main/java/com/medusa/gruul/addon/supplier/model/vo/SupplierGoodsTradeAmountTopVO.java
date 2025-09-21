package com.medusa.gruul.addon.supplier.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>商品交易金额TOP VO</p>
 * @author An.Yan
 */
@Getter
@Setter
@Accessors(chain = true)
public class SupplierGoodsTradeAmountTopVO {

    private Integer rank;

    private String productName;

    private Long productId;

    private Long amount;

    private String pic;
}
