package com.medusa.gruul.goods.api.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 商品详情店铺基本信息
 * @author: WuDi
 * @date: 2022/9/14
 */
@Data
@Accessors(chain = true)
public class ApiShopInfoVO {

    /**
     * "店铺id"
     */
    private Long shopId;
    /**
     * 店铺名称
     */
    private String shopName;
    /**
     * 店铺logo
     */
    private String shopLogo;
    /**
     * 在售商品数量
     */
    private Long saleProducts;

}
