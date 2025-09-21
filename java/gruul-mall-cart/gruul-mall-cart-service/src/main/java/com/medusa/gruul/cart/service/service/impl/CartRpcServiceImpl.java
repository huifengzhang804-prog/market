package com.medusa.gruul.cart.service.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.TypeReference;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.medusa.gruul.cart.api.rpc.CartRpcService;
import com.medusa.gruul.cart.service.model.vo.ProductSkuVO;
import com.medusa.gruul.cart.service.util.CartUtil;
import com.medusa.gruul.common.module.app.shop.ShopMode;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.global.model.enums.Mode;
import java.util.List;
import java.util.Map.Entry;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 购物车 RPC
 *
 * @author wufuzhong
 * @date 2023/12/14
 */
@Service
@DubboService
@RequiredArgsConstructor
public class CartRpcServiceImpl implements CartRpcService {

    /**
     * 查询店铺加购数
     *
     * @param shopIds 店铺id数组
     * @return 店铺加购数
     */
    @Override
    public Map<Long, Integer> getShopCartCache(Set<Long> shopIds) {
        Map<Long, Integer> shopCartCache = new HashMap<>();
        shopIds.forEach(shopId -> shopCartCache.put(shopId, CartUtil.getCacheShopCount(shopId)));
        return shopCartCache;
    }

    /**
     * 移除购物车商品
     * @param userId 用户ID
     * @param shopProductMap map key 店铺ID map value 商品ID集合
     * @return
     */
    @Override
    public Boolean removeProductFromCart(Long userId, Map<Long, List<Long>> shopProductMap) {
//        String cartKey=CartUtil.myCartCacheKey(userId, null, Mode.B2B2C);
//        //查詢用户购物车指定店铺的商品数据
//        List<ProductSkuVO> productSkuVOS = RedisUtil.getMultiCacheMapValue(cartKey,shopProductMap.keySet(),new TypeReference<>() {
//        });
        //获取用户缓存中的购物车信息
        String userCartKey=CartUtil.myCartCacheKey(userId, null, ShopMode.COMMON);
        Map<Long, List<ProductSkuVO>> shopIdProductSkuMap = CartUtil.getMyCartDetail(userId,
            ShopMode.COMMON, null);
        if (CollectionUtil.isEmpty(shopIdProductSkuMap)) {
          return Boolean.TRUE;
        }
        //删除对应店铺的商品数据
        shopProductMap.forEach((shopId, productIds) -> {
          List<ProductSkuVO> productSkuVOS = shopIdProductSkuMap.get(shopId);
          if (CollectionUtil.isEmpty(productSkuVOS)) {
            return;
          }
          productSkuVOS.removeIf(productSkuVO -> productIds.contains(productSkuVO.getProductId()));
        });
      /**
       * 判断购物车中此时商品数据
       *    将店铺商品数据已经为空的店铺ID放入一个集合 后续删除 hashkey的 field
       *    店铺商品有删除 但是未删空的店铺放入另一个集合 后续用于更新
       */
      Set<Long> shopIds = shopProductMap.keySet();
        Set<String> emptyShopIds = Sets.newHashSetWithExpectedSize(shopIds.size());
        Map<String, List<ProductSkuVO>> changedShopMap = Maps.newHashMapWithExpectedSize(
            shopIds.size());
        for (Entry<Long, List<ProductSkuVO>> entry : shopIdProductSkuMap.entrySet()) {
          if (!shopIds.contains(entry.getKey())) {
            continue;
          }
          if (CollectionUtil.isEmpty(entry.getValue())) {
            emptyShopIds.add(String.valueOf(entry.getKey()));
          } else {
            changedShopMap.put(String.valueOf(entry.getKey()), entry.getValue());
          }

        }
        RedisUtil.executePipelined(operation -> {
          if (CollectionUtil.isNotEmpty(emptyShopIds)) {
            //删除已经为空的field
            operation.opsForHash().delete(userCartKey,emptyShopIds.toArray());
          }
        },operation->{
          if (CollectionUtil.isNotEmpty(changedShopMap)) {
            //更新不为空的field
            RedisUtil.getRedisTemplate().opsForHash().putAll(userCartKey,changedShopMap);
          }
        });


        return Boolean.TRUE;
    }
}
