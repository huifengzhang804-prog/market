package com.medusa.gruul.storage.api.enums;

import com.medusa.gruul.common.model.enums.RabbitParent;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * date 2022/7/6
 */
@RequiredArgsConstructor
public enum StorageRabbit implements RabbitParent {

    /**
     * 更改商品sku库存
     */
    UPDATE_SKU_STOCK("storage.updateSkuStock"),

    /**
     * 更改商品库存
     */
    UPDATE_PRODUCT_STOCK("storage.updateProductStock"),

    /**
     * 多规格金额修改
     */
    UPDATE_SKU_PRICE("update.sku.price"),

    /**
     * 归还库存
     */
    RETURN_STOCK("storage.return.stock"),
    ;


    public static final String EXCHANGE = "storage.direct";
    private final String routingKey;

    @Override
    public String exchange() {
        return StorageRabbit.EXCHANGE;
    }

    @Override
    public String routingKey() {
        return routingKey;
    }
}
