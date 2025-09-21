package com.medusa.gruul.overview.service.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 畅销商品类型VO
 *
 * @author xiaoq
 * @Description SalableProductTypeVO.java
 * @date 2022-10-21 16:33
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class SalableProductTypeVO {
    /**
     * 实际交易量
     */
    private Long realTradeVolume;

    /**
     * 实付交易额
     */
    private Long realTradingVolume;

    /**
     * 产品所属店铺id
     */
    private Long shopId;

    /**
     * 产品id
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;
    /**
     * 图片
     */
    private String pic;
    /**
     * 商品销售价
     */
    private Long salePrice;

    /**
     * 店铺名称
     */
    private String shopName;
}
