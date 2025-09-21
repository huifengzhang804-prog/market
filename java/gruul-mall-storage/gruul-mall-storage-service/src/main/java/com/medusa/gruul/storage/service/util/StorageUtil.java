package com.medusa.gruul.storage.service.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.BooleanUtil;
import com.medusa.gruul.common.model.base.ActivityShopProductSkuKey;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.storage.api.bo.StSvBo;
import com.medusa.gruul.storage.service.model.constant.StorageConstant;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.SetOperations;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 张治保
 * date 2023/3/6
 */
public interface StorageUtil {

    /**
     * 订单消费检查
     *
     * @param orderNo     订单号
     * @param successTask 成功任务
     * @param failTask    失败任务
     */
    static void orderNoCheck(String orderNo, Runnable successTask, Runnable failTask) {
        if (orderNo == null) {
            return;
        }
        String key = RedisUtil.key(StorageConstant.CACHE_KEY_ORDER_NO, orderNo);

        Boolean success = RedisUtil.getRedisTemplate()
                .opsForValue()
                .setIfAbsent(
                        key,
                        CommonPool.NUMBER_ONE,
                        CommonPool.NUMBER_TEN,
                        TimeUnit.MINUTES
                );
        success = BooleanUtil.isTrue(success);
        if (!success) {
            failTask.run();
            RedisUtil.delete(key);
            return;
        }
        try {
            successTask.run();
        } catch (Exception ex) {
            //发生异常 时才需要删除 ，故意留着key 避免 重复消费 和给 fail流出执行逻辑
            RedisUtil.delete(key);
            throw ex;
        }
    }

    /**
     * 生成sku的redis缓存key
     *
     * @param key shopId 店铺id | productId 商品id | skuId | activityType 活动类型 | activityId   活动id
     * @return key
     */
    static String generateSkuRedisKey(ActivityShopProductSkuKey key) {
        return RedisUtil.key(StorageConstant.CACHE_KEY_PRODUCT_SKU_STORAGE, key.getActivityType(), key.getActivityId(), key.getShopId(), key.getProductId(), key.getSkuId());
    }

    /**
     * 生成sku的redis缓存key的通配符
     *
     * @param shopId       店铺id
     * @param activityType 活动类型
     * @param activityId   活动id
     * @return 通配符key
     */
    static String generateSkuRedisKeyPattern(OrderType activityType, Long activityId, Long shopId) {
        return RedisUtil.key(StorageConstant.CACHE_KEY_PRODUCT_SKU_STORAGE, activityType, activityId, shopId, "*", "*");
    }

    /**
     * 生成根据店铺id和商品id的缓存key通配符
     *
     * @param shopId    店铺id
     * @param productId 商品id
     * @return 通配符key
     */
    static String generateSkuRedisKeyPattern(Long shopId, Long productId) {
        return RedisUtil.key(StorageConstant.CACHE_KEY_PRODUCT_SKU_STORAGE, "*", "*", shopId, productId, "*");
    }

    /**
     * 生成redisson分布式锁key
     *
     * @param key shopId 店铺id | productId 商品id | skuId | activityType 活动类型 | activityId   活动id
     * @return lock key
     */
    static String generateRedissonLockKey(ActivityShopProductSkuKey key) {
        return RedisUtil.key(StorageConstant.CACHE_KEY_PRODUCT_SKU_STORAGE, key.getActivityType(), key.getActivityId(), key.getShopId(), key.getProductId(), key.getSkuId(), "lock");
    }


    /**
     * 使用redis 保存代销关系 仅当代销商品时调用
     *
     * @param shopId     店铺id
     * @param productId  商品id
     * @param supplierId 供应商 id
     */
    @SuppressWarnings("unchecked")
    static void saveConsignmentRelation(Long shopId, Long productId, Long supplierId) {
        //店铺商品代销关系
        RedisUtil.executePipelined(
                redisTemplate -> {
                    //店铺商品代销关系
                    redisTemplate
                            .opsForHash()
                            .putIfAbsent(
                                    StorageConstant.CACHE_KEY_CONSIGNMENT_RELATION,
                                    consignmentHashKey(shopId, productId),
                                    supplierId

                            );
                    //供应商商品被代销关系
                    redisTemplate.opsForSet()
                            .add(
                                    consignmentSupplierRelationKey(supplierId, productId),
                                    shopId
                            );
                }
        );
    }

    static String consignmentSupplierRelationKey(Long supplierId, Long productId) {
        return RedisUtil.key(StorageConstant.CACHE_KEY_CONSIGNMENT_SUPPLIER_RELATION, supplierId, productId);
    }

    /**
     * 查询 代销供应商商品的店铺id 集合
     *
     * @param supplierProductKeys 供应商商品 key
     * @return 供应商 被代销的店铺id  key -》供应商商品key  value -》代销key商品的店铺id集合
     */
    @SuppressWarnings("unchecked")
    static Map<ShopProductKey, Set<Long>> consignmentRelationShopIds(List<ShopProductKey> supplierProductKeys) {
        if (CollUtil.isEmpty(supplierProductKeys)) {
            return Map.of();
        }
        //主要为了更新代销商品ES库存
        List<Object> shopIdSets = RedisUtil.executePipelined(
                redisTemplate -> {
                    SetOperations<String, Object> redisSet = redisTemplate.opsForSet();
                    for (ShopProductKey shopProductKey : supplierProductKeys) {
                        redisSet.members(StorageUtil.consignmentSupplierRelationKey(shopProductKey.getShopId(), shopProductKey.getProductId()));
                    }
                }
        );
        Map<ShopProductKey, Set<Long>> resultMap = new HashMap<>(shopIdSets.size());
        for (int index = 0; index < supplierProductKeys.size(); index++) {
            Object members = shopIdSets.get(index);
            resultMap.put(
                    supplierProductKeys.get(index), members == null ? Set.of() : (Set<Long>) members
            );
        }
        return resultMap;
    }

    /**
     * 生成代销关系的hash key
     *
     * @param shopId    店铺id
     * @param productId 商品id
     * @return hash key
     */
    static String consignmentHashKey(Long shopId, Long productId) {
        return shopId + StrPool.DASHED + productId;
    }

    /**
     * 批量查询 商品的代销关系
     *
     * @param productKeys 商品 key
     * @return 商品的代销关系
     */
    static Map<ShopProductKey, Long> consignmentRelationWithKey(Set<ShopProductKey> productKeys) {
        Map<String, ShopProductKey> keyMap = productKeys.stream()
                .collect(Collectors.toMap(
                                productKey -> consignmentHashKey(productKey.getShopId(), productKey.getProductId()),
                                Function.identity()
                        )
                );
        Map<String, Long> resultMap = consignmentRelation(keyMap.keySet());
        return resultMap.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        entry -> keyMap.get(entry.getKey()),
                        Map.Entry::getValue
                ));
    }

    /**
     * 批量查询 商品的代销关系
     *
     * @param consignmentHashKeys 组装后的代销 hashKey
     * @return 商品的代销关系
     * @see StorageUtil#consignmentHashKey(Long, Long)
     */
    static Map<String, Long> consignmentRelation(Set<String> consignmentHashKeys) {
        if (CollUtil.isEmpty(consignmentHashKeys)) {
            return Map.of();
        }
        List<Object> sortedKeys = List.copyOf(consignmentHashKeys);
        HashOperations<String, Object, Object> hashOperations = RedisUtil.getRedisTemplate().opsForHash();
        List<Object> supplierIds = hashOperations.multiGet(
                StorageConstant.CACHE_KEY_CONSIGNMENT_RELATION,
                sortedKeys
        );
        Map<String, Long> result = MapUtil.newHashMap(sortedKeys.size());
        for (int index = 0; index < sortedKeys.size(); index++) {
            Object value = supplierIds.get(index);
            if (value == null) {
                continue;
            }
            result.put((String) sortedKeys.get(index), Long.valueOf(value.toString()));
        }
        return result;

    }


    /**
     * 渲染库存扣减数据
     * 1. 代销商品扣除供应商商品库存
     * 2. 如果是活动商品 扣除活动商品库存 且扣除普通商品库存（代销商品扣供应商，否则扣原店铺普通商品）
     * 3. 如果是普通商品 扣除普通商品库存
     *
     * @param stSvMap 原始sku key 与库存销量 map
     * @return 最终的sku key 与库存销量
     */
    static Map<ActivityShopProductSkuKey, StSvBo> allStockUpdateData(Map<ActivityShopProductSkuKey, StSvBo> stSvMap) {
        //商品代销关系 map 如果是代销商品则有该商品 key 对应的供应商 id
        Map<ShopProductKey, Long> consignmentRelationMap = StorageUtil.consignmentRelationWithKey(
                stSvMap.keySet()
                        .stream()
                        .map(ActivityShopProductSkuKey::toShopProductKey)
                        .collect(Collectors.toSet())
        );
        Map<ActivityShopProductSkuKey, StSvBo> finalSkuKeyStSvs = new HashMap<>(CommonPool.NUMBER_TWO * stSvMap.size());
        stSvMap.forEach(
                (key, stSv) -> {
                    Map<ActivityShopProductSkuKey, StSvBo> currentKeyMap = currentStockUpdateData(
                            consignmentRelationMap.get(key.toShopProductKey()),
                            key,
                            stSv
                    );
                    currentKeyMap.forEach(
                            (curKey, value) -> {
                                StSvBo stSvBo = finalSkuKeyStSvs.get(curKey);
                                if (stSvBo == null) {
                                    finalSkuKeyStSvs.put(curKey, value);
                                    return;
                                }
                                stSvBo.incrementStock(value.getSales())
                                        .incrementSales(value.getStock());
                            }
                    );
                }
        );

        return finalSkuKeyStSvs;
    }

    /**
     * 活动商品key 转换为普通商品key
     *
     * @param shopId 店铺id
     * @param key    活动商品key
     * @return 普通商品key
     */
    static ActivityShopProductSkuKey toCommonKey(Long shopId, ActivityShopProductSkuKey key) {
        return (ActivityShopProductSkuKey) new ActivityShopProductSkuKey()
                .setSkuId(key.getSkuId())
                .setProductId(key.getProductId())
                .setShopId(shopId)
                .setActivityType(OrderType.COMMON)
                .setActivityId((long) CommonPool.NUMBER_ZERO);
    }


    /**
     * 获取所有当前key对应的 key 集合
     * 1. 代销商品增加目标代销商品 key
     * 2. 如果是活动商品 增加当前 key 且增加普通商品 key（代销商品扣供应商，否则扣原店铺普通商品）
     * 3. 如果是普通商品 增加普通商品 key
     *
     * @param supplierId 供应商id 如果是代销商品则不为空
     * @param key        sku key
     * @return key 集合
     */
    static Set<ActivityShopProductSkuKey> getAllCurrentKeys(Long supplierId, ActivityShopProductSkuKey key) {
        boolean activity = key.getActivityType().isActivity();
        //如果是供应商 id 不为空 即为代销商品
        boolean isConsignment = supplierId != null;
        Set<ActivityShopProductSkuKey> finalSkuKeys = new HashSet<>(CommonPool.NUMBER_TWO);
        if (isConsignment) {
            finalSkuKeys.add(toCommonKey(supplierId, key));
            //如果是代销商品 而且是普通商品直接跳过下面的处理逻辑
            if (!activity) {
                return finalSkuKeys;
            }
        }
        //如果非代销商品  或者 是代销活动商品
        finalSkuKeys.add(key);
        //非活动商品 或者 代销的活动商品直接跳过
        if (!activity || isConsignment) {
            return finalSkuKeys;
        }
        //非代销的活动商品 则增加扣除普通商品的库存
        finalSkuKeys.add(toCommonKey(key.getShopId(), key));
        return finalSkuKeys;
    }

    /**
     * 渲染单挑库存扣减数据
     * 1. 代销商品扣除供应商商品库存
     * 2. 如果是活动商品 扣除活动商品库存 且扣除普通商品库存（代销商品扣供应商，否则扣原店铺普通商品）
     * 3. 如果是普通商品 扣除普通商品库存
     *
     * @param supplierId 供应商id 如果是代销商品则不为空
     * @param key        原始sku key
     * @param stSv       库存与销量
     * @return 最终的sku key 与库存销量
     */
    static Map<ActivityShopProductSkuKey, StSvBo> currentStockUpdateData(Long supplierId, ActivityShopProductSkuKey key, StSvBo stSv) {
        return getAllCurrentKeys(supplierId, key)
                .stream()
                .collect(
                        Collectors.toMap(
                                Function.identity(),
                                curKey -> key.equals(curKey) ? stSv : new StSvBo(stSv.getStock(), stSv.getSales()
                                )
                        )
                );
    }

    /**
     * 合并库存数据 相同 key 的数据合并
     *
     * @param keyStSvMaps 库存 key 与 库存销量 map
     * @return 合并后的数据
     */
    static Map<ActivityShopProductSkuKey, StSvBo> mergeStocks(List<Map<ActivityShopProductSkuKey, StSvBo>> keyStSvMaps) {
        Map<ActivityShopProductSkuKey, StSvBo> skuKeyStSvMap = new HashMap<>(CommonPool.NUMBER_FIFTEEN);
        for (Map<ActivityShopProductSkuKey, StSvBo> keyStSvMap : keyStSvMaps) {
            for (Map.Entry<ActivityShopProductSkuKey, StSvBo> entry : keyStSvMap.entrySet()) {
                ActivityShopProductSkuKey key = entry.getKey();
                StSvBo value = entry.getValue();
                StSvBo stSvBo = skuKeyStSvMap.computeIfAbsent(key, k -> new StSvBo());
                stSvBo.incrementStock(value.getStock());
                stSvBo.incrementSales(value.getSales());
            }
        }
        return skuKeyStSvMap;
    }


}
