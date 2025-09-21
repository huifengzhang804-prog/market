package com.medusa.gruul.common.model.base;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2022/10/21
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class ShopProductSkuKey implements Serializable {
    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * sku id
     */
    private Long skuId;


    /**
     * 转换铺商品key
     *
     * @return 店铺商品key
     */
    public final ShopProductKey toShopProductKey() {
        return new ShopProductKey().setShopId(this.getShopId()).setProductId(this.getProductId());
    }
}
