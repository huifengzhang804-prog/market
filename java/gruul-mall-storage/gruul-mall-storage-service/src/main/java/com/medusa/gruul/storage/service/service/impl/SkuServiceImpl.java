package com.medusa.gruul.storage.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.medusa.gruul.common.model.base.ActivityShopProductSkuKey;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.storage.api.bo.StSvBo;
import com.medusa.gruul.storage.api.dto.ProductPriceUpdateDTO;
import com.medusa.gruul.storage.api.dto.ProductSkuStockDTO;
import com.medusa.gruul.storage.api.entity.StorageSku;
import com.medusa.gruul.storage.api.enums.StockChangeType;
import com.medusa.gruul.storage.service.model.bo.UpdateStockOrder;
import com.medusa.gruul.storage.service.model.constant.StorageConstant;
import com.medusa.gruul.storage.service.model.dto.ProductSkuLimitDTO;
import com.medusa.gruul.storage.service.model.dto.ProductSkuPriceDTO;
import com.medusa.gruul.storage.service.mp.mapper.StorageSkuMapper;
import com.medusa.gruul.storage.service.mp.service.IStorageSkuService;
import com.medusa.gruul.storage.service.mq.service.StorageMqService;
import com.medusa.gruul.storage.service.properties.StorageProperties;
import com.medusa.gruul.storage.service.service.SkuService;
import com.medusa.gruul.storage.service.service.SkuStockService;
import com.medusa.gruul.storage.service.util.StorageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 张治保
 * date 2022/6/22
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SkuServiceImpl implements SkuService {

    private final SqlSessionFactory sqlSessionFactory;
    private final SkuStockService skuStockService;
    private final StorageProperties storageProperties;
    private final IStorageSkuService storageSkuService;
    private final StorageMqService storageRabbitService;

    /**
     * 更新 修改 sku 限购
     * 使用 事务保证一致性
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @Redisson(value = StorageConstant.CACHE_KEY_PRODUCT_SKU_STORAGE, key = "#productId+'-limitLock'")
    public void setProductLimit(Long productId, List<ProductSkuLimitDTO> limits) {
        if (CollUtil.isEmpty(limits)) {
            return;
        }
        Long shopId = ISecurity.userMust().getShopId();
        // 转换成 sku key limit map
        Map<ActivityShopProductSkuKey, ProductSkuLimitDTO> skuKeyLimitMap = getSkuKeyLimitMap(shopId, productId, limits);

        /*更新数据库
         */
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            StorageSkuMapper mapper = sqlSession.getMapper(StorageSkuMapper.class);
            for (Map.Entry<ActivityShopProductSkuKey, ProductSkuLimitDTO> entry : skuKeyLimitMap.entrySet()) {
                mapper.update(
                        null,
                        Wrappers.lambdaUpdate(StorageSku.class)
                                .set(StorageSku::getLimitType, entry.getValue().getLimitType())
                                .set(StorageSku::getLimitNum, entry.getValue().getLimitNum())
                                .eq(StorageSku::getActivityType, entry.getKey().getActivityType())
                                .eq(StorageSku::getActivityId, entry.getKey().getActivityId())
                                .eq(StorageSku::getShopId, entry.getKey().getShopId())
                                .eq(StorageSku::getProductId, entry.getKey().getProductId())
                                .eq(StorageSku::getId, entry.getKey().getSkuId())
                );
            }
            sqlSession.commit();
        }
        //如果缓存存在则更新缓存
        RedisUtil.batchPutIfPresent(
                skuKeyLimitMap.entrySet()
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        entry -> StorageUtil.generateSkuRedisKey(entry.getKey()),
                                        entry -> Map.of(
                                                StorageConstant.FIELD_SKU_STORAGE_LIMIT_TYPE, entry.getValue().getLimitType(),
                                                StorageConstant.FIELD_SKU_STORAGE_LIMIT_NUM, entry.getValue().getLimitNum()
                                        )

                                )
                        )
        );

    }


    /**
     * 更新商品sku价格
     *
     * @param productId       商品id
     * @param productSkuPrice 商品sku价格
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(rollbackFor = Exception.class)
    @Redisson(value = StorageConstant.CACHE_KEY_PRODUCT_SKU_STORAGE, key = "#productId+'-priceLock'")
    public void updateSkuPrice(Long productId, List<ProductSkuPriceDTO> productSkuPrice) {
        Long shopId = ISecurity.userMust().getShopId();

        // 转换成 sku key productSkuPrice map
        Map<ActivityShopProductSkuKey, ProductSkuPriceDTO> skuKeyPriceMap = getSkuKeyPriceMap(shopId, productId, productSkuPrice);

        // sqlSession.commit() 执行更新
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            StorageSkuMapper mapper = sqlSession.getMapper(StorageSkuMapper.class);
            for (Map.Entry<ActivityShopProductSkuKey, ProductSkuPriceDTO> entry : skuKeyPriceMap.entrySet()) {
                mapper.update(
                        null,
                        Wrappers.lambdaUpdate(StorageSku.class)
                                .set(StorageSku::getPrice, entry.getValue().getPrice())
                                .set(StorageSku::getSalePrice, entry.getValue().getSalePrice())
                                .eq(StorageSku::getActivityType, entry.getKey().getActivityType())
                                .eq(StorageSku::getActivityId, entry.getKey().getActivityId())
                                .eq(StorageSku::getShopId, entry.getKey().getShopId())
                                .eq(StorageSku::getProductId, entry.getKey().getProductId())
                                .eq(StorageSku::getId, entry.getKey().getSkuId())
                );
            }
            sqlSession.commit();
        }
        //如果缓存存在则更新缓存
        RedisUtil.batchPutIfPresent(
                skuKeyPriceMap.entrySet()
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        entry -> StorageUtil.generateSkuRedisKey(entry.getKey()),
                                        entry -> Map.of(
                                                StorageConstant.FIELD_SKU_STORAGE_SALE_PRICE, entry.getValue().getSalePrice(),
                                                StorageConstant.FIELD_SKU_STORAGE_PRICE, entry.getValue().getPrice()
                                        )

                                )
                        )
        );
        ProductPriceUpdateDTO priceUpdate = new ProductPriceUpdateDTO()
                .setShopIds(Set.of(shopId))
                .setProductId(productId)
                .setSkuPriceMap(
                        productSkuPrice.stream()
                                .collect(
                                        Collectors.toMap(
                                                ProductSkuPriceDTO::getSkuId,
                                                skuPrice -> new ProductPriceUpdateDTO.SkuPriceDTO()
                                                        .setPrice(skuPrice.getPrice())
                                                        .setSalePrice(skuPrice.getSalePrice())
                                        )
                                )
                );
        //如果是供应商商品，
        if (ISecurity.matcher()
                .anyRole(Roles.SUPPLIER_ADMIN, Roles.SUPPLIER_CUSTOM_ADMIN)
                .match()) {
            //则查询代销关系 更新指定店铺的消息
            Set<?> shopIds = RedisUtil.getRedisTemplate().opsForSet()
                    .members(StorageUtil.consignmentSupplierRelationKey(shopId, productId));
            //代销关系为空 则不发送消息
            if (CollUtil.isEmpty(shopIds)) {
                return;
            }
            priceUpdate.setShopIds((Set<Long>) shopIds);

        }
        storageRabbitService.sendUpdateSkuPriceMsg(priceUpdate);

    }

    private Map<ActivityShopProductSkuKey, ProductSkuPriceDTO> getSkuKeyPriceMap(Long shopId, Long productId, List<ProductSkuPriceDTO> productSkuPrices) {
        return productSkuPrices.stream()
                .collect(
                        Collectors.toMap(
                                productSkuPrice -> {
                                    ActivityShopProductSkuKey key = new ActivityShopProductSkuKey().setSkuId(productSkuPrice.getSkuId());
                                    key.setProductId(productId)
                                            .setShopId(shopId)
                                            .setActivityType(OrderType.COMMON)
                                            .setActivityId(0L);
                                    return key;
                                },
                                v -> v
                        )
                );
    }


    public Map<ActivityShopProductSkuKey, ProductSkuLimitDTO> getSkuKeyLimitMap(Long shopId, Long productId, List<ProductSkuLimitDTO> limits) {
        return limits.stream()
                .collect(
                        Collectors.toMap(
                                limit -> {
                                    ActivityShopProductSkuKey key = new ActivityShopProductSkuKey().setSkuId(limit.getSkuId());
                                    key.setProductId(productId).setShopId(shopId).setActivityType(OrderType.COMMON).setActivityId(0L);
                                    return key;
                                },
                                v -> v
                        )
                );
    }

    @Override
    public void updateSkuStock(Long productId, List<ProductSkuStockDTO> skuStocks) {
        Long shopId = ISecurity.userMust().getShopId();
        // 转换成 sku key对应的map
        Map<ActivityShopProductSkuKey, Long> skuKeyProductStockMap = skuStocks.stream()
                .collect(
                        Collectors.groupingBy(
                                sku -> (ActivityShopProductSkuKey) new ActivityShopProductSkuKey()
                                        .setSkuId(sku.getSkuId())
                                        .setProductId(productId)
                                        .setShopId(shopId)
                                        .setActivityType(OrderType.COMMON)
                                        .setActivityId(0L),
                                Collectors.mapping(
                                        ProductSkuStockDTO::getNum,
                                        Collectors.reducing(
                                                0L,
                                                Long::sum
                                        )
                                )
                        )
                );
        //过滤值为0的数据 并且根据 库存修改的正负值判断是出库还是入库
        Map<StockChangeType, Map<ActivityShopProductSkuKey, StSvBo>> map = MapUtil.newHashMap();
        skuKeyProductStockMap.forEach(
                (key, num) -> {
                    if (num == 0) {
                        return;
                    }
                    map.computeIfAbsent(
                            num > 0 ? StockChangeType.EDITED_INBOUND : StockChangeType.EDITED_OUTBOUND,
                            k -> MapUtil.newHashMap()
                    ).put(key, new StSvBo().setStock(num));
                }
        );
        //转换成 sku key 与销量 set
        List<UpdateStockOrder> updateStockOrders = map.entrySet()
                .stream()
                .map(entry -> new UpdateStockOrder()
                        .setGenerateDetail(true)
                        .setChangeType(entry.getKey())
                        .setStSvMap(entry.getValue())
                ).toList();
        log.debug("更新库存订单，{}", updateStockOrders);
        skuStockService.updateStock(
                true,
                false,
                updateStockOrders
        );
    }


    @Override
    public StorageSku getProductSku(ActivityShopProductSkuKey shopProductSkuKey) {
        Long shopId = shopProductSkuKey.getShopId();
        Long productId = shopProductSkuKey.getProductId();
        Long skuId = shopProductSkuKey.getSkuId();
        OrderType activityType = shopProductSkuKey.getActivityType();
        Long activityId = shopProductSkuKey.getActivityId();
        return RedisUtil.getCacheMap(
                StorageSku.class,
                () -> storageSkuService.lambdaQuery()
                        .eq(StorageSku::getShopId, shopId)
                        .eq(StorageSku::getProductId, productId)
                        .eq(StorageSku::getId, skuId)
                        .eq(StorageSku::getActivityType, activityType)
                        .eq(StorageSku::getActivityId, activityId)
                        .one(),
                Duration.ofSeconds(RedisUtil.expireWithRandom(storageProperties.getSkuExpireTime())),
                StorageUtil.generateSkuRedisKey(shopProductSkuKey)
        );
    }


}
