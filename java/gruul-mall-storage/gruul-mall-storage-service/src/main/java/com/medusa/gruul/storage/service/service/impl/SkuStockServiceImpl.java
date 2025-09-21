package com.medusa.gruul.storage.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.medusa.gruul.common.fastjson2.FastJson2;
import com.medusa.gruul.common.model.base.ActivityShopProductKey;
import com.medusa.gruul.common.model.base.ActivityShopProductSkuKey;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.goods.api.model.dto.ProductDeleteDTO;
import com.medusa.gruul.storage.api.bo.SkuKeyStSvBO;
import com.medusa.gruul.storage.api.bo.StSvBo;
import com.medusa.gruul.storage.api.entity.StorageSku;
import com.medusa.gruul.storage.service.model.bo.UpdateStockOrder;
import com.medusa.gruul.storage.service.model.constant.StorageConstant;
import com.medusa.gruul.storage.service.model.enums.StorageError;
import com.medusa.gruul.storage.service.mp.mapper.StorageSkuMapper;
import com.medusa.gruul.storage.service.mp.service.IStorageSkuService;
import com.medusa.gruul.storage.service.mq.service.StorageMqService;
import com.medusa.gruul.storage.service.properties.StorageProperties;
import com.medusa.gruul.storage.service.service.SkuStockDetailService;
import com.medusa.gruul.storage.service.service.SkuStockService;
import com.medusa.gruul.storage.service.util.LazyLoadScript;
import com.medusa.gruul.storage.service.util.StorageUtil;
import io.vavr.Tuple;
import io.vavr.Tuple3;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author 张治保 date 2022/6/21
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SkuStockServiceImpl implements SkuStockService {

    private final StorageProperties storageProperties;
    private final RedissonClient redissonClient;
    private final SqlSessionFactory sqlSessionFactory;
    private final IStorageSkuService storageSkuService;
    private final StringRedisTemplate stringRedisTemplate;
    private final StorageMqService storageRabbitService;
    private final SkuStockDetailService stockDetailService;

    @Resource
    @Lazy
    private SkuStockService skuStockService;


    /**
     * 渲染所有 redis lua脚本的key集合和对应参数
     *
     * @param stSvMap sku key与 库存销量
     * @return 校验结果
     */
    public Tuple3<List<String>, Object[], SkuKeyStSvBO[]> renderLuaKeysAndArgs(boolean ignoreCacheNotExists, Map<ActivityShopProductSkuKey, StSvBo> stSvMap) {

        int size = stSvMap.size();
        List<String> keys = new ArrayList<>(size);
        List<Object> args = new ArrayList<>(size + CommonPool.NUMBER_ONE);
        //第一个参数设置为 是否忽略不存在的sku key
        args.add(String.valueOf(ignoreCacheNotExists));
        //用于回滚的数据
        SkuKeyStSvBO[] skuKeyStSvMap = new SkuKeyStSvBO[size];
        //统计当前处理数据的下标
        int index = CommonPool.NUMBER_ZERO;
        //遍历 map渲染数据
        for (Map.Entry<ActivityShopProductSkuKey, StSvBo> entry : stSvMap.entrySet()) {
            ActivityShopProductSkuKey key = entry.getKey();
            StSvBo value = entry.getValue();
            keys.add(StorageUtil.generateSkuRedisKey(key));
            args.add(JSON.toJSONString(value));
            skuKeyStSvMap[index] = new SkuKeyStSvBO().setKey(key).setStSv(value);
            index++;
        }
        return Tuple.of(keys, args.toArray(), skuKeyStSvMap);
    }

    @Override
    public void updateSkuStockCache(boolean ignoreCacheNotExists, Map<ActivityShopProductSkuKey, StSvBo> stSvMap) {
        //渲染所有 redis key集合 和 下标对应的库存 数组
        Tuple3<List<String>, Object[], SkuKeyStSvBO[]> redisKeyAndStocks = this.renderLuaKeysAndArgs(ignoreCacheNotExists, stSvMap);
        //执行lua脚本扣库存
        //使用Redis事务执行Lua脚本扣库存
        List<String> keys = redisKeyAndStocks._1();
        Long success = stringRedisTemplate.execute(
                LazyLoadScript.UpdateStockAndSalesScript.SCRIPT,
                redisKeyAndStocks._1(),
                redisKeyAndStocks._2()
        );
        //更新成功
        if (success == null || success == 0 || success == keys.size()) {
            return;
        }
        int index = success.intValue();
        //更新失败，需要回滚
        this.rollback((SkuKeyStSvBO[]) ArrayUtil.copy(redisKeyAndStocks._3(), new SkuKeyStSvBO[index], index));
        //抛出异常
        throw StorageError.OUT_OF_STOCK.dataEx(redisKeyAndStocks._3()[index].getKey());
    }

    /**
     * 库存销量取反 归还库存和销量数据
     *
     * @param skuKeyStSvArray sku key与 库存销量数组
     */
    private void rollback(SkuKeyStSvBO[] skuKeyStSvArray) {
        //库存销量取反
        for (SkuKeyStSvBO skuKeyStSvBO : skuKeyStSvArray) {
            StSvBo stSv = skuKeyStSvBO.getStSv();
            stSv.setStock(-stSv.getStock());
            stSv.setSales(-stSv.getSales());
        }
        this.updateSkuStockCache(true,
                Stream.of(skuKeyStSvArray)
                        .collect(
                                Collectors.toMap(
                                        SkuKeyStSvBO::getKey,
                                        SkuKeyStSvBO::getStSv
                                )
                        )
        );
    }

    /**
     * 批量更新库存与销量 1。加锁 同时锁住多个key 2。获取批量执行器 批量更新数据库
     *
     * @param skuKeyStSvMap sku key与 库存销量 map
     * @author 张治保
     */
    @Override
    public void updateSkuStockDb(Map<ActivityShopProductSkuKey, StSvBo> skuKeyStSvMap) {
        log.debug("批量更新 sku 库存：{}", skuKeyStSvMap.toString());
        if (MapUtil.isEmpty(skuKeyStSvMap)) {
            return;
        }
        //批量锁
        RLock multiLock = redissonClient.getMultiLock(
                skuKeyStSvMap.keySet().stream()
                        .map(StorageUtil::generateRedissonLockKey)
                        .map(redissonClient::getLock)
                        .toArray(RLock[]::new)
        );
        //加锁
        multiLock.lock();
        try {
            //批量执行器
            SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
            //批量更新数据库库存数据 并归还库存和销量
            try {
                StorageSkuMapper mapper = sqlSession.getMapper(StorageSkuMapper.class);
                for (Map.Entry<ActivityShopProductSkuKey, StSvBo> entry : skuKeyStSvMap.entrySet()) {
                    ActivityShopProductSkuKey key = entry.getKey();
                    StSvBo value = entry.getValue();
                    LambdaUpdateWrapper<StorageSku> updateWrapper = Wrappers.lambdaUpdate(StorageSku.class)
                            .setSql(StrUtil.format(StorageConstant.SQL_SALES_VOLUME_INCREMENT_SQL_TEMPLATE,
                                    value.getSales()))
                            .setSql(StrUtil.format(StorageConstant.SQL_STOCK_INCREMENT_SQL_WITH_STOCK_TYPE_TEMPLATE,
                                    value.getStock()))
                            .eq(StorageSku::getActivityType, key.getActivityType())
                            .eq(StorageSku::getActivityId, key.getActivityId())
                            .eq(StorageSku::getShopId, key.getShopId())
                            .eq(StorageSku::getProductId, key.getProductId())
                            .eq(StorageSku::getId, key.getSkuId())
                            .and(value.getStock() < 0, qw -> qw.last(
                                    StrUtil.format(StorageConstant.SQL_STOCK_CONDITION_SQL_TEMPLATE,
                                            -value.getStock())));
                    mapper.update(null, updateWrapper);
                }
                sqlSession.commit();
            } catch (Exception exception) {
                log.error("库存操作失败", exception);
                sqlSession.rollback();
                throw SystemCode.DATA_UPDATE_FAILED.exception();
            } finally {
                sqlSession.close();
            }
        } finally {
            //释放锁
            multiLock.unlock();
        }
    }

    /**
     * 删除库存
     *
     * @param productDeleteDTO 商品删除数据关键key
     */
    @Override
    public void productDeleteStorageSku(ProductDeleteDTO productDeleteDTO) {
        //删除redis中的数据
        Set<String> keys = new HashSet<>(CommonPool.NUMBER_THIRTY);
        productDeleteDTO.getProductIds().forEach(productId -> {
            String cacheKey = RedisUtil.key(StorageConstant.CACHE_KEY_PRODUCT_SKU_STORAGE, productDeleteDTO.getShopId(),
                    CommonPool.NUMBER_ZERO, productId, "*");
            keys.add(cacheKey);
        });
        RedisUtil.delete(keys);

    }

    @Override
    public List<Long> queryShopEmptyStockProIds(ActivityShopProductKey shopProductKey, Boolean emptyStock,
                                                Boolean unLimitStock) {
        return storageSkuService.queryShopEmptyStockProIds(shopProductKey, emptyStock, unLimitStock);

    }

    @Override
    public void updateStock(boolean ignoreCacheNotExists, boolean possibleConsignment, List<UpdateStockOrder> updateStocks) {
        if (CollUtil.isEmpty(updateStocks)) {
            return;
        }
        //有可能是代销商品 代销商品 需要处理
        if (possibleConsignment) {
            for (UpdateStockOrder updateStock : updateStocks) {
                updateStock.setStSvMap(
                        StorageUtil.allStockUpdateData(updateStock.getStSvMap())
                );
            }
        }
        //获取所有 skuKey
        Set<ActivityShopProductSkuKey> skuKeys = updateStocks.stream()
                .flatMap(updateStockBO -> updateStockBO.getStSvMap().keySet().stream())
                .collect(Collectors.toSet());

        //如果不能忽略不存在的 sku 则把尽可能数据加载到缓存中
        if (!ignoreCacheNotExists) {
            //查询sku 数据 用于缓存全部数据
            skuStockService.getSkusBatch(skuKeys);
        }
        Map<ActivityShopProductSkuKey, StSvBo> mergeStocks = StorageUtil.mergeStocks(updateStocks.stream().map(UpdateStockOrder::getStSvMap).toList());
        //批量扣除缓存库存，普通商品和其他活动
        this.updateSkuStockCache(ignoreCacheNotExists, mergeStocks);
        //发送库存更新mq
        storageRabbitService.sendUpdateStockMsg(mergeStocks);
        //库存明细  库存明细只生成普通商品的库存明细即可
        List<UpdateStockOrder> generateDetailsOrders = updateStocks.stream()
                .filter(UpdateStockOrder::isGenerateDetail)
                .filter(updateStock -> {
                    Map<ActivityShopProductSkuKey, StSvBo> stSvMap = updateStock.getStSvMap();
                    Map<ActivityShopProductSkuKey, StSvBo> commonStSvMap = MapUtil.newHashMap(stSvMap.size());
                    stSvMap.forEach(
                            (key, value) -> {
                                if (OrderType.COMMON == key.getActivityType()) {
                                    commonStSvMap.put(key, value);
                                }
                            }
                    );
                    updateStock.setStSvMap(commonStSvMap);
                    return !commonStSvMap.isEmpty();
                }).toList();
        if (CollUtil.isEmpty(generateDetailsOrders)) {
            return;
        }
        //生成库存明细
        stockDetailService.generateStockDetails(generateDetailsOrders);
    }


    @Override
    public Map<ActivityShopProductSkuKey, StorageSku> getSkusBatch(Set<ActivityShopProductSkuKey> keys) {
        if (CollUtil.isEmpty(keys)) {
            return Collections.emptyMap();
        }
        // 从缓存中获取的SKU
        List<StorageSku> storageSkus = this.getCacheSkuBatch(keys);
        Map<ActivityShopProductSkuKey, StorageSku> skuKeyStorageSkuMap = this.skusToMap(storageSkus);
        //如果数量相等 则直接返回
        if (skuKeyStorageSkuMap.size() == keys.size()) {
            return skuKeyStorageSkuMap;
        }
        Set<ActivityShopProductSkuKey> keySet = keys.stream().filter(key -> !skuKeyStorageSkuMap.containsKey(key))
                .collect(Collectors.toSet());
        if (CollUtil.isEmpty(keySet)) {
            return skuKeyStorageSkuMap;
        }
        // 从数据库中获取的SKU
        Map<ActivityShopProductSkuKey, StorageSku> dbKeySkuMap = storageSkuService.getSkusBatchMap(keySet);
        //如果数据库查询出的结果为空 则说明数据设置不正确
        if (CollUtil.isEmpty(dbKeySkuMap)) {
            log.error("数据设置不正确，未查询到足够的数据:{}", keySet);
        }
        //将数据库数据放进缓存
        this.cacheSkuBatch(dbKeySkuMap);
        // 合并 缓存和数据库数据
        HashMap<ActivityShopProductSkuKey, StorageSku> skuKeySkuMap = new HashMap<>(skuKeyStorageSkuMap);
        skuKeySkuMap.putAll(dbKeySkuMap);
        return skuKeySkuMap;
    }

    @Override
    public Map<ActivityShopProductKey, List<StorageSku>> productSkuStockBatch(
            Set<ActivityShopProductKey> activityShopProductKeys) {
        if (CollUtil.isEmpty(activityShopProductKeys)) {
            return Collections.emptyMap();
        }
        Map<ActivityShopProductSkuKey, StorageSku> skusBatchByPrefixMap = storageSkuService.getSkusBatchByPrefixMap(
                true, new HashSet<>(activityShopProductKeys));
        if (CollUtil.isEmpty(skusBatchByPrefixMap)) {
            return Collections.emptyMap();
        }
        //从缓存中取数据 取不到再去数据库中取数据
        Map<ActivityShopProductSkuKey, StorageSku> skusBatchMap = this.getSkusBatch(skusBatchByPrefixMap.keySet());
        return skusBatchMap.entrySet().stream()
                .collect(
                        Collectors.groupingBy(
                                entry -> {
                                    ActivityShopProductSkuKey key = entry.getKey();
                                    ActivityShopProductKey activityShopProductKey = new ActivityShopProductKey();
                                    activityShopProductKey.setActivityType(key.getActivityType())
                                            .setActivityId(key.getActivityId());
                                    activityShopProductKey.setProductId(key.getProductId()).setShopId(key.getShopId());
                                    return activityShopProductKey;
                                },
                                Collectors.mapping(
                                        Map.Entry::getValue,
                                        Collectors.toList()
                                )
                        )
                );
    }

    /**
     * sku列表转sku map
     *
     * @param storageSkus sku列表
     * @return sku Map
     */
    public Map<ActivityShopProductSkuKey, StorageSku> skusToMap(List<StorageSku> storageSkus) {
        return storageSkus.stream()
                .collect(
                        Collectors.toMap(
                                sku -> {
                                    ActivityShopProductSkuKey key = new ActivityShopProductSkuKey();
                                    key.setSkuId(sku.getId()).setProductId(sku.getProductId())
                                            .setShopId(sku.getShopId()).setActivityType(sku.getActivityType())
                                            .setActivityId(sku.getActivityId());
                                    return key;
                                },
                                v -> v
                        )
                );
    }


    /**
     * 从缓存中取sku数据列表
     *
     * @param keys sku key集合
     * @return sku数据列表
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public List<StorageSku> getCacheSkuBatch(Set<ActivityShopProductSkuKey> keys) {
        List<Object> caches = RedisUtil.executePipelined(
                redisOperations -> {
                    HashOperations hashOperations = redisOperations.opsForHash();
                    for (ActivityShopProductSkuKey key : keys) {
                        hashOperations.entries(StorageUtil.generateSkuRedisKey(key));
                    }
                }
        );

        return FastJson2.convert(
                caches.stream().filter(cache -> {
                    if (!(cache instanceof Map map)) {
                        return false;
                    }
                    return !map.isEmpty();
                }).toList(), new TypeReference<>() {
                });
    }

    /**
     * 批量缓存sku数据
     *
     * @param dbKeySkuMap key对应 sku map
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void cacheSkuBatch(Map<ActivityShopProductSkuKey, StorageSku> dbKeySkuMap) {
        if (CollUtil.isEmpty(dbKeySkuMap)) {
            return;
        }
        RedisUtil.executePipelined(
                redisOperations -> {
                    HashOperations hashOperations = redisOperations.opsForHash();
                    for (Map.Entry<ActivityShopProductSkuKey, StorageSku> entry : dbKeySkuMap.entrySet()) {
                        String key = StorageUtil.generateSkuRedisKey(entry.getKey());
                        // 将sku数据放入缓存
                        hashOperations.putAll(key, RedisUtil.toBean(entry.getValue(), Map.class));
                        // 设置过期时间
                        redisOperations.expire(key,
                                Duration.ofSeconds(RedisUtil.expireWithRandom(storageProperties.getSkuExpireTime())));
                    }
                }
        );
    }
}
