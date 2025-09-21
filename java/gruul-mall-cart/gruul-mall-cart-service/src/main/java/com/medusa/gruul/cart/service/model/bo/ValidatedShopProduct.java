package com.medusa.gruul.cart.service.model.bo;

import com.medusa.gruul.cart.service.model.vo.ProductSkuVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 区分购物车店铺下 可用不可用的商品
 * @author 张治保
 * date 2022/5/16
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ValidatedShopProduct {
    /**
     * 可用的商品
     */
    private ProductSkuVO valid;

    /**
     * 不可用的商品
     */
    private ProductSkuVO invalid;
}
