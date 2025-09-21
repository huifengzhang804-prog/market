package com.medusa.gruul.storage.api.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2022/7/14
 */
@Getter
@Setter
@ToString
public class ProductStatisticsVO implements Serializable {

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 最低价
     */
    private Long lowestPrice;

    /**
     * 最高价 单位 毫 1毫 = 0。01分
     */
    private Long highestPrice;

    /**
     * 销量
     */
    private Long salesVolume;

    /**
     * 剩余库存
     */
    private Long remainingStock;
}
