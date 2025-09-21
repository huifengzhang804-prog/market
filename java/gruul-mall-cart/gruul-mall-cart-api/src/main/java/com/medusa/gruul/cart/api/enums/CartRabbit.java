package com.medusa.gruul.cart.api.enums;

import com.medusa.gruul.common.model.enums.RabbitParent;
import lombok.RequiredArgsConstructor;

/**
 * <p>
 *     订单mq配置
 * </p>
 *
 * @author 张治保
 * date 2022/6/9
 */
@RequiredArgsConstructor
public enum CartRabbit implements RabbitParent {

    /**
     * 删除购物车 商品
     */
    DELETE_CART_PRODUCT("delete。product"),

    /**
     * 店铺加购数 更新
     */
    UPDATE_SHOP_CART("update.shop.cart"),

    /**
     * 店铺加购数 删除
     */
    DELETE_SHOP_CART("delete.shop.cart");

    /**
     * 路由key
     */
    private final String routingKey;

    public static final String EXCHANGE ="cart.direct";

    @Override
    public String exchange() {
        return CartRabbit.EXCHANGE;
    }

    @Override
    public String routingKey() {
        return routingKey;
    }
}
