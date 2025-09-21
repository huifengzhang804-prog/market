package com.medusa.gruul.storage.api.rpc;

import com.medusa.gruul.storage.api.bo.OrderStockBO;

/**
 * @author 张治保
 * date 2022/7/13
 */
public interface StorageOrderRpcService {
    /**
     * 减缓存库存操作
     *
     * @param skuStock 扣减库存参数
     */
    void reduceSkuStock(OrderStockBO skuStock);

    
}
