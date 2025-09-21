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
public class ActivityShopProductKey extends ActivityShopKey {

    /**
     * 商品id
     */
    private Long productId;


    /**
     * 转换为无活动信息的店铺商品key
     *
     * @return 店铺商品key
     */
    public final ShopProductKey toShopProductKey() {
        return new ShopProductKey().setShopId(this.getShopId()).setProductId(this.getProductId());
    }

}
