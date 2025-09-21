package com.medusa.gruul.cart.api.constant;

/**
 * 购物车错误代码
 *
 * @author 张治保
 * date 2022/5/17
 */
public interface CartErrorCode {

    /**
     * 店铺不可用
     */
    int INVALID_SHOP = 11001;

    /**
     * 11002 商品不可用
     */
    int INVALID_PRODUCT = 11002;

    /**
     * 11003 sku不可用
     */
    int INVALID_SKU = 11003;

    /**
     * 超库存
     */
    int OUT_OF_STOCK = 11004;

    /**
     * 超限购
     */
    int OUT_OF_LIMIT = 11005;

    /**
     * 商品售罄
     */
    int SOLD_OUT = 11006;
}
