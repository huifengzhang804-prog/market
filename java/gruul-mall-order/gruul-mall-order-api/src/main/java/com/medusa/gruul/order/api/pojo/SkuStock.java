package com.medusa.gruul.order.api.pojo;

import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2022/6/21
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class SkuStock implements Serializable {
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
     * 锁定数量
     */
    private Integer num;

    public ShopProductSkuKey skuKey() {
        return new ShopProductSkuKey()
                .setShopId(getShopId())
                .setProductId(getProductId())
                .setSkuId(getSkuId());
    }
}
