package com.medusa.gruul.goods.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.goods.service.service.ProductSalesRankService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author 张治保
 * @since 2024/5/22
 */
@Service
@RequiredArgsConstructor
public class ProductSalesRankServiceImpl implements ProductSalesRankService {

    private static final String PRODUCT_SALES_RANK = "gruul:mall:goods:product:sales:rank";

    private final RedisTemplate<String, Object> redisTemplate;


    /**
     * 店铺销量排行榜key
     *
     * @param shopId 店铺id
     * @return key
     */
    private String shopSalesRankKey(Long shopId) {
        return RedisUtil.key(PRODUCT_SALES_RANK, shopId);
    }


    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void incrSales(Map<ShopProductKey, Long> productSalesMap) {
        RedisUtil.executePipelined(
                operations -> {
                    ZSetOperations zSet = operations.opsForZSet();
                    productSalesMap.forEach((key, value) -> {
                        //平台的销量排行榜
                        zSet.incrementScore(PRODUCT_SALES_RANK, key, value);
                        //店铺的排行榜
                        zSet.incrementScore(
                                shopSalesRankKey(key.getShopId()),
                                key.getProductId(),
                                value
                        );
                    });
                }
        );
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void removeSales(Set<ShopProductKey> productKeys) {
        RedisUtil.executePipelined(
                operations -> {
                    ZSetOperations zSet = operations.opsForZSet();
                    productKeys.forEach(key -> {
                        //平台的销量排行榜
                        zSet.remove(PRODUCT_SALES_RANK, key);
                        //店铺的排行榜
                        zSet.remove(shopSalesRankKey(key.getShopId()), key.getProductId());
                    });
                }
        );
    }

    @Override
    public LinkedHashMap<ShopProductKey, Long> platformSalesRank(Integer size) {
        ZSetOperations<String, Object> zSet = redisTemplate.opsForZSet();
        Set<ZSetOperations.TypedTuple<Object>> typedTuples = zSet.reverseRangeWithScores(PRODUCT_SALES_RANK, 0, size - 1);
        LinkedHashMap<ShopProductKey, Long> result = new LinkedHashMap<>(size);
        if (CollUtil.isEmpty(typedTuples)) {
            return result;
        }
        for (ZSetOperations.TypedTuple<Object> typedTuple : typedTuples) {
            Object value = typedTuple.getValue();
            if (value == null) {
                continue;
            }
            Double score = typedTuple.getScore();
            long sales = score == null ? 0 : score.longValue();
            result.put(
                    (ShopProductKey) value,
                    sales
            );

        }
        return result;
    }

    @Override
    public LinkedHashMap<Long, Long> shopSalesRank(Long shopId, Integer size) {
        ZSetOperations<String, Object> zSet = redisTemplate.opsForZSet();
        Set<ZSetOperations.TypedTuple<Object>> typedTuples = zSet.reverseRangeWithScores(shopSalesRankKey(shopId), 0, size - 1);
        LinkedHashMap<Long, Long> result = new LinkedHashMap<>(size);
        if (CollUtil.isEmpty(typedTuples)) {
            return result;
        }
        for (ZSetOperations.TypedTuple<Object> typedTuple : typedTuples) {
            Object value = typedTuple.getValue();
            if (value == null) {
                continue;
            }
            Double score = typedTuple.getScore();
            long sales = score == null ? 0 : score.longValue();
            result.put((Long) value, sales);
        }
        return result;
    }


}
