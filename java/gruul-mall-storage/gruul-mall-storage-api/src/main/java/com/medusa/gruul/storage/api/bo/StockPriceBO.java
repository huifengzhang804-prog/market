package com.medusa.gruul.storage.api.bo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * @since 2024/7/8
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class StockPriceBO implements Serializable {

    /**
     * 库存
     */
    private Long stock;

    /**
     * 销售价
     */
    private Long salePrice;

    /**
     * 原价 (划线价)
     */
    private Long price;

}
