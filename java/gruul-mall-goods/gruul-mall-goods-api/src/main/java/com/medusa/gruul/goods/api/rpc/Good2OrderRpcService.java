package com.medusa.gruul.goods.api.rpc;

import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.goods.api.entity.Product;

import java.util.Map;
import java.util.Set;

/**
 * 商品对 订单下单 功能支持 性能优先
 *
 * @author 张治保
 * @apiNote 注重性能实现 避免干扰订单
 * @since 2024/2/18
 */
public interface Good2OrderRpcService {

    /**
     * 批量获取商品 信息
     *
     * @param shopProductKeys shopId,productId 商品 key
     * @return Map<shopId, Map < productId, product>> 商品 key 对应商品信息 map
     */
    Map<Long, Map<Long, Product>> productBatch(Set<ShopProductKey> shopProductKeys);

}
