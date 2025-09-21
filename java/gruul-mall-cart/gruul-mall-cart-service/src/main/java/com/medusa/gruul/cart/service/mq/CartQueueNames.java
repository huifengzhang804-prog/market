package com.medusa.gruul.cart.service.mq;

/**
 * <p></p>
 *
 * @author 张治保
 * date 2022/6/14
 */
public interface CartQueueNames {
    /**
     * 删除购物车部分商品 队列
     */
    String CART_DELETE_PRODUCT_QUEUE = "cart.delete.product";

    /**
     * 店铺加购数 更新 队列
     */
    String SHOP_CART_UPDATE = "shop.cart.update";

}
