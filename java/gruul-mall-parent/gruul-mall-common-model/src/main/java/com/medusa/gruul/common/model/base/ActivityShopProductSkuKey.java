package com.medusa.gruul.common.model.base;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * date 2023/3/29
 */
@Getter
@Setter
@ToString(callSuper = true)
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ActivityShopProductSkuKey extends ActivityShopProductKey {

    /**
     * sku Id
     */
    private Long skuId;


    /**
     * 转换为无活动信息的店铺商品sku key
     *
     * @return 店铺商品sku key
     */
    public final ShopProductSkuKey toShopProductSkuKey() {
        return new ShopProductSkuKey().setShopId(this.getShopId()).setProductId(this.getProductId()).setSkuId(this.getSkuId());
    }

}
