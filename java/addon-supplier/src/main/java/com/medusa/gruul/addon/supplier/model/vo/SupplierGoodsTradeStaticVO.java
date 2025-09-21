package com.medusa.gruul.addon.supplier.model.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * 商品交易统计
 */

@Getter
@Setter
public class SupplierGoodsTradeStaticVO {

    private String xDate;

    private Long tradeNumber = 0L;

    private Long tradeAmount = 0L;
}
