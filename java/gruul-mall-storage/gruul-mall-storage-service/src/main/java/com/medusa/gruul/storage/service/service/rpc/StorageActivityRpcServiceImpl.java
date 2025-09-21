package com.medusa.gruul.storage.service.service.rpc;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.medusa.gruul.common.model.base.ActivityShopKey;
import com.medusa.gruul.common.model.base.ActivityShopProductSkuKey;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.search.api.model.ProductActivityBind;
import com.medusa.gruul.search.api.model.ProductActivityUnbind;
import com.medusa.gruul.search.api.rpc.SearchRpcService;
import com.medusa.gruul.storage.api.dto.activity.ActivityCloseKey;
import com.medusa.gruul.storage.api.dto.activity.ActivityCreateDTO;
import com.medusa.gruul.storage.api.dto.activity.ActivitySkuDTO;
import com.medusa.gruul.storage.api.entity.StorageSku;
import com.medusa.gruul.storage.api.enums.LimitType;
import com.medusa.gruul.storage.api.enums.StockType;
import com.medusa.gruul.storage.api.rpc.StorageActivityRpcService;
import com.medusa.gruul.storage.service.mp.service.IStorageSkuService;
import com.medusa.gruul.storage.service.service.SkuStockService;
import com.medusa.gruul.storage.service.util.StorageUtil;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;


/**
 * @author 张治保
 * date 2023/3/7
 */
@Service
@DubboService
@RequiredArgsConstructor
public class StorageActivityRpcServiceImpl implements StorageActivityRpcService {

    private final SearchRpcService searchRpcService;
    private final SkuStockService skuStockService;
    private final IStorageSkuService storageSkuService;

    /**
     * 活动sku库存预处理
     *
     * @param create 活动库存
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void activityCreate(ActivityCreateDTO create) {
        List<ActivitySkuDTO> stockSkus = create.getSkus();
        if (CollUtil.isEmpty(stockSkus)) {
            return;
        }
        Long shopId = create.getShopId();
        Map<ShopProductKey, List<ActivitySkuDTO>> productKeyActivitiesMap = stockSkus.stream()
                .collect(
                        Collectors.groupingBy(
                                sku -> new ShopProductKey(shopId, sku.getProductId())
                        )
                );
        // 商品代销关系 如果是代销关系则不为空
        Map<ShopProductKey, Long> consignmentMap = StorageUtil.consignmentRelationWithKey(productKeyActivitiesMap.keySet());
        //ActivitySkuDTO 转为普通 sku Key的函数
        BiFunction<ShopProductKey, ActivitySkuDTO, ActivityShopProductSkuKey> commonSkuKey = (productKey, activitySku) -> {
            Long curShopId = consignmentMap.getOrDefault(productKey, productKey.getShopId());
            return (ActivityShopProductSkuKey) new ActivityShopProductSkuKey()
                    .setSkuId(activitySku.getSkuId())
                    .setProductId(activitySku.getProductId())
                    .setShopId(curShopId)
                    .setActivityType(OrderType.COMMON)
                    .setActivityId(0L);
        };

        // 转成 原始无活动的 skuKey
        Set<ActivityShopProductSkuKey> skuKeys = productKeyActivitiesMap.entrySet()
                .stream()
                .flatMap(entry -> {
                    ShopProductKey productKey = entry.getKey();
                    List<ActivitySkuDTO> value = entry.getValue();
                    return value.stream()
                            .map(activitySku -> commonSkuKey.apply(productKey, activitySku));
                }).collect(Collectors.toSet());
        //查询库存信息
        Map<ActivityShopProductSkuKey, StorageSku> skuMap = skuStockService.getSkusBatch(skuKeys);

        //生成库存数据
        OrderType activityType = create.getActivityType();
        Long activityId = create.getActivityId();
        boolean success = storageSkuService.saveBatch(
                productKeyActivitiesMap.entrySet()
                        .stream()
                        .flatMap(
                                entry -> {
                                    ShopProductKey productKey = entry.getKey();
                                    return entry.getValue()
                                            .stream()
                                            .map(
                                                    activitySku -> {
                                                        StorageSku storageSku = skuMap.get(commonSkuKey.apply(productKey, activitySku));
                                                        StorageSku newStorageSku = new StorageSku()
                                                                .setActivityType(activityType)
                                                                .setActivityId(activityId)
                                                                .setShopId(shopId)
                                                                .setProductId(storageSku.getProductId())
                                                                .setStockType(StockType.LIMITED)
                                                                .setStock(activitySku.getStock().longValue())
                                                                .setSalesVolume(0L)
                                                                .setInitSalesVolume(0L)
                                                                .setLimitType(LimitType.UNLIMITED)
                                                                .setLimitNum(0)
                                                                .setSpecs(storageSku.getSpecs())
                                                                .setImage(storageSku.getImage())
                                                                .setPrice(storageSku.getPrice())
                                                                .setSalePrice(ObjectUtil.defaultIfNull(activitySku.getSalePrice(), storageSku::getSalePrice))
                                                                .setWeight(storageSku.getWeight())
                                                                .setSort(storageSku.getSort());
                                                        newStorageSku.setId(storageSku.getId());
                                                        return newStorageSku;
                                                    }
                                            );
                                }
                        ).toList()
        );
        SystemCode.DATA_ADD_FAILED.falseThrow(success);
        //是否与其他活动互斥
        if (!activityType.isExclusive()) {
            return;
        }
        //活动商品绑定
        searchRpcService.activityBind(
                new ProductActivityBind()
                        .setActivityType(activityType)
                        .setActivityId(activityId)
                        .setShopId(shopId)
                        .setProductIds(stockSkus.stream().map(ActivitySkuDTO::getProductId).collect(Collectors.toSet()))
                        .setStartTime(create.getStartTime())
                        .setEndTime(create.getEndTime())
        );
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void activityClose(Set<ActivityCloseKey> activityCloses) {
        if (CollUtil.isEmpty(activityCloses)) {
            return;
        }
        //转换成 ActivityShopKey 集合
        Set<ActivityShopKey> keyPrefixSet = activityCloses.stream()
                .map(
                        close -> (ActivityShopKey) new ActivityShopKey()
                                .setShopId(close.getShopId())
                                .setActivityType(close.getActivityType())
                                .setActivityId(close.getActivityId())
                )
                .collect(Collectors.toSet());

        //转成活动解绑需要的数据
        //仅互斥活动需要解绑
        Set<ProductActivityUnbind> unbinds = keyPrefixSet.stream()
                .filter(key -> key.getActivityType().isExclusive())
                .map(
                        activityShopKey -> new ProductActivityUnbind()
                                .setActivityType(activityShopKey.getActivityType())
                                .setActivityId(activityShopKey.getActivityId())
                                .setShopId(activityShopKey.getShopId())
                )
                .collect(Collectors.toSet());
        if (CollUtil.isEmpty(unbinds)) {
            return;
        }
        searchRpcService.activityUnbind(unbinds);

        //活动 redis 缓存 key 前缀 匹配
        Set<String> activityKeyPatterns = keyPrefixSet.stream()
                .map(key -> StorageUtil.generateSkuRedisKeyPattern(key.getActivityType(), key.getActivityId(), key.getShopId()))
                .collect(Collectors.toSet());
        // 双删 redis中的数据 删除数据库的数据
        RedisUtil.doubleDeletion(
                () -> {
                    //批量删除数据库数据
                    storageSkuService.deleteSkusByPrefix(keyPrefixSet);
                },
                () -> RedisUtil.matchThenDelete(activityKeyPatterns)
        );
    }

}
