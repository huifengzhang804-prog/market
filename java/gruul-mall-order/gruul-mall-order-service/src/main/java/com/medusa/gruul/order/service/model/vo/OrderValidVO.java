package com.medusa.gruul.order.service.model.vo;

import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Set;

/**
 * @author 张治保
 * @since 2024/4/2
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OrderValidVO implements Serializable {

    /**
     * 不存在的 商品sku
     */
    private Set<ShopProductSkuKey> notExists;

    /**
     * 库存不足的 商品sku
     */
    private Set<StockNotEnoughSku> stockNotEnough;

    /**
     * 限购数量不足
     */
    private Set<LimitNotEnoughSku<?>> limitNotEnough;

    /**
     * 库存不足的 sku 及现有库存
     */
    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode(of = "key")
    @Accessors(chain = true)
    public static class StockNotEnoughSku implements Serializable {
        /**
         * 商品sku key
         */
        private ShopProductSkuKey key;

        /**
         * 现有库存
         */
        private Long stock;
    }


    /**
     * 限购数量不足的 sku 及现有库存
     */
    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode(of = "key")
    @Accessors(chain = true)
    public static class LimitNotEnoughSku<T extends Serializable> implements Serializable {
        /**
         * 商品sku key
         */
        private T key;

        /**
         * 限购数量
         */
        private Integer limit;

        /**
         * 已购数量
         */
        private Long bought;


    }


}
