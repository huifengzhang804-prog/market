package com.medusa.gruul.shop.api.enums;

import com.medusa.gruul.common.model.enums.RabbitParent;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * date 2022/5/25
 */
@RequiredArgsConstructor
public enum ShopRabbit implements RabbitParent {

    /**
     * 更换店铺管理员
     */
    SHOP_ADMIN_CHANGE("shop.admin.new"),
    /**
     * 启用店铺
     */
    SHOP_ENABLE_DISABLE("shop:enable_disable"),

    /**
     * 店铺信息 更新
     */
    SHOP_UPDATE("shop:update"),


    /**
     * 店铺搜索 搜过的店
     */
    SHOP_SEARCH("shop:search"),

    /**
     * 店铺签约类目自定义扣率发生变化
     */
    SHOP_SGINING_CATEGORY_CUSTOM_DEDUCTION_RATIO_CHANGE("shop:signing_category:custom_deduction_ratio_change"),
    /**
     * 店铺上架商品数量发生变化
     */
    SHOP_ON_SHELF_GOODS_COUNT_CHANGE("shop:on_shelf_goods_count_change"),
    ;

    public static final String EXCHANGE = "shop.direct";
    private final String routingKey;

    @Override
    public String exchange() {
        return EXCHANGE;
    }

    @Override
    public String routingKey() {
        return routingKey;
    }
}
