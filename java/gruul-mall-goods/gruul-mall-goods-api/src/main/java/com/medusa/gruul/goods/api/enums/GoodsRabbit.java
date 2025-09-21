package com.medusa.gruul.goods.api.enums;

import com.medusa.gruul.common.model.enums.RabbitParent;
import lombok.RequiredArgsConstructor;


/**
 * GoodsRabbit.java
 *
 * @author xiaoq
 */
@RequiredArgsConstructor
public enum GoodsRabbit implements RabbitParent {

    /**
     * 商品发布
     */
    GOODS_RELEASE("goods:release"),

    /**
     * 商品删除
     */
    GOODS_DELETE("goods.delete"),

    /**
     * 商品更新
     */
    GOODS_UPDATE("goods.update"),

    /**
     * 商品名称修改
     */
    GOODS_NAME_UPDATE("goods.name.update"),

    /**
     * 商品更新状态
     */
    GOODS_UPDATE_STATUS("goods.update.status"),

    /**
     * 商品下架
     */
    GOODS_SELL_OFF("goods.sell.off"),

    /**
     * 用户扫商品码之后成为分销员
     */
    GOODS_DISTRIBUTION_BINDING("goods.distribution.binding"),

    /**
     * 供应商商品状态更新
     */
    SUPPLIER_GOODS_UPDATE_STATUS("supplier.goods.update.status"),

    /**
     * 强制删除商品
     */
    SUPPLIER_FORCE_GOODS_STATUS("supplier.force.goods.status"),

    /**
     * 供应商代销商品修改
     */
    SUPPLIER_UPDATE_GOODS("supplier.update.goods"),

    /**
     * 用户关注店铺
     */
    SHOP_ATTENTION("user.attention.shop"),

    /**
     * 商品标签删除
     */
    GOODS_LABEL_DELETE("goods.label.delete"),
    /**
     * 商品标签更新
     */
    GOODS_LABEL_UPDATE("goods.label.update"),

    /**
     * 商品自定义扣率发生变化
     */
    GOODS_CUSTOM_DEDUCTION_RATIO_CHANGE("goods.custom.deduction.ratio.change");


    public static final String EXCHANGE = "goods.direct";
    private final String routingKey;

    @Override
    public String exchange() {
        return GoodsRabbit.EXCHANGE;
    }

    @Override
    public String routingKey() {
        return routingKey;
    }
}
