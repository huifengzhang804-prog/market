package com.medusa.gruul.cart.service.mq.impl;

import com.medusa.gruul.cart.api.enums.CartRabbit;
import com.medusa.gruul.cart.service.model.vo.ProductSkuVO;
import com.medusa.gruul.cart.service.mq.CartRabbitService;
import com.medusa.gruul.common.model.constant.CommonPool;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 购物车 Rabbit 实现类
 *
 * @author wufuzhong
 * @date 2023/12/14
 */
@Service
@RequiredArgsConstructor
public class CartRabbitServiceImpl implements CartRabbitService {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 发送店铺加购数 更新
     *
     * @param shopCountMap 店铺加购数计数 map
     */
    @Override
    public void sendShopCartUpdate(Map<Long, List<ProductSkuVO>> shopCountMap) {
        //过滤数量为0 或总数为零的数据
        Map<Long, List<ProductSkuVO>> newCountMap = new HashMap<>();
        shopCountMap.forEach((shopId, skus) -> {
            if (skus == null || skus.isEmpty()) {
                return;
            }
            List<ProductSkuVO> newSkus = skus.stream().filter(productSkuVO -> CommonPool.NUMBER_ZERO != (productSkuVO.getNum())).toList();
            if (newSkus.stream().mapToInt(ProductSkuVO::getNum).sum() == CommonPool.NUMBER_ZERO) {
                return;
            }
            newCountMap.put(shopId, newSkus);
        });
        if (newCountMap.isEmpty()) {
            return;
        }
        rabbitTemplate.convertAndSend(CartRabbit.UPDATE_SHOP_CART.exchange(), CartRabbit.UPDATE_SHOP_CART.routingKey(), newCountMap);
    }
}
