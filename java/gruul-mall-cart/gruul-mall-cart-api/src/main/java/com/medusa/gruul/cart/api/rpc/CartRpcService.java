package com.medusa.gruul.cart.api.rpc;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 购物车 RPC
 *
 * @author wufuzhong
 * @date 2023/12/14
 */
public interface CartRpcService {

    /**
     * 查询店铺加购数
     *
     * @param shopIds 店铺ids
     * @return 店铺加购数
     */
    Map<Long, Integer> getShopCartCache(Set<Long> shopIds);

    /**
     * 移除购物车商品
     * @param userId 用户ID
     * @param shopProductMap map key 店铺ID map value 商品ID集合
     * @return 操作是否成功 true 成功 false 失败
     */
    Boolean removeProductFromCart(Long userId, Map<Long, List<Long>> shopProductMap);
}
