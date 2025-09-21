package com.medusa.gruul.cart.service.mq;

import com.medusa.gruul.cart.service.model.vo.ProductSkuVO;

import java.util.List;
import java.util.Map;

/**
 * 购物车 Rabbit
 *
 * @author wufuzhong
 * @date 2023/12/14
 */
public interface CartRabbitService {

    /**
     * 发送店铺加购数 更新
     *
     * @param shopCountMap 店铺加购数计数 map
     */
    void sendShopCartUpdate(Map<Long, List<ProductSkuVO>> shopCountMap);
}
