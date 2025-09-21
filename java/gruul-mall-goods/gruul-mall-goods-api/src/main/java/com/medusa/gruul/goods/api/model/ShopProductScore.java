package com.medusa.gruul.goods.api.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2022/12/1
 */
@Getter
@Setter
@Accessors(chain = true)
public class ShopProductScore implements Serializable {

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 得分
     */
    private Long score;
}
