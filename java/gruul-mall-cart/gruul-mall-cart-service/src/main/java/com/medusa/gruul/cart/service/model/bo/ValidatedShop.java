package com.medusa.gruul.cart.service.model.bo;

import com.medusa.gruul.cart.service.model.vo.ShopProductSkuVO;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 存储校验过的购物车里店铺商品sku是否可用的对象
 * @author 张治保
 * date 2022/5/16
 */
@Getter
@Setter
@Accessors(chain = true)
public class ValidatedShop {

    /**
     * 可用的购物车数据
     */
    private ShopProductSkuVO valid;
    /**
     * 不可用的购物车数据
     */
    private ShopProductSkuVO invalid;
}
