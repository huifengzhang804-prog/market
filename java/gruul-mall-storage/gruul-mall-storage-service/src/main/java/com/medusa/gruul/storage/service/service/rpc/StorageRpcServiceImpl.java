package com.medusa.gruul.storage.service.service.rpc;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.base.ActivityShopKey;
import com.medusa.gruul.common.model.base.ActivityShopProductKey;
import com.medusa.gruul.common.model.base.ActivityShopProductSkuKey;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.common.mp.config.MybatisPlusConfig;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.rpc.GoodsRpcService;
import com.medusa.gruul.storage.api.bo.StSvBo;
import com.medusa.gruul.storage.api.bo.StockPriceBO;
import com.medusa.gruul.storage.api.dto.ShopProductKeyDTO;
import com.medusa.gruul.storage.api.dto.SkuCopy;
import com.medusa.gruul.storage.api.dto.SkuDTO;
import com.medusa.gruul.storage.api.dto.SpecDTO;
import com.medusa.gruul.storage.api.dto.SpecGroupDTO;
import com.medusa.gruul.storage.api.dto.StorageSpecSkuDTO;
import com.medusa.gruul.storage.api.dto.StoragesCopyDTO;
import com.medusa.gruul.storage.api.entity.StorageSku;
import com.medusa.gruul.storage.api.enums.LimitType;
import com.medusa.gruul.storage.api.enums.StockChangeType;
import com.medusa.gruul.storage.api.enums.StockType;
import com.medusa.gruul.storage.api.rpc.StorageRpcService;
import com.medusa.gruul.storage.api.vo.ProductSaleVolumeVO;
import com.medusa.gruul.storage.api.vo.ProductSkusVO;
import com.medusa.gruul.storage.api.vo.ProductSpecsSkusVO;
import com.medusa.gruul.storage.api.vo.ProductStatisticsVO;
import com.medusa.gruul.storage.service.model.bo.UpdateStockOrder;
import com.medusa.gruul.storage.service.mp.entity.StorageDetail;
import com.medusa.gruul.storage.service.mp.entity.StorageSpec;
import com.medusa.gruul.storage.service.mp.entity.StorageSpecGroup;
import com.medusa.gruul.storage.service.mp.service.IStorageDetailService;
import com.medusa.gruul.storage.service.mp.service.IStorageSkuService;
import com.medusa.gruul.storage.service.mp.service.IStorageSpecGroupService;
import com.medusa.gruul.storage.service.mp.service.IStorageSpecService;
import com.medusa.gruul.storage.service.service.SkuService;
import com.medusa.gruul.storage.service.service.SkuStockDetailService;
import com.medusa.gruul.storage.service.service.SkuStockService;
import com.medusa.gruul.storage.service.service.SpecSkuService;
import com.medusa.gruul.storage.service.service.StorageShopProductService;
import com.medusa.gruul.storage.service.util.StorageUtil;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Option;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 张治保 date 2022/7/6
 */
@Service
@DubboService
@RequiredArgsConstructor
public class StorageRpcServiceImpl implements StorageRpcService {


    private final SkuService skuService;
    private final Executor globalExecutor;
    private final GoodsRpcService goodsRpcService;
    private final SkuStockService skuStockService;
    private final SpecSkuService specSkuService;
    private final IStorageSkuService storageSkuService;
    private final IStorageSpecService storageSpecService;
    private final SkuStockDetailService stockDetailService;
    private final IStorageDetailService storageDetailService;
    private final IStorageSpecGroupService storageSpecGroupService;
    private final StorageShopProductService storageShopProductService;

    /**
     * 采购 sku 渲染成目标 sku
     *
     * @param targetShopId 目标店铺 id
     * @param sourceSkus   原始sku
     * @return 目标sku
     */
    private static Collection<StorageSku> targetSkus(Long targetShopId, Collection<StorageSku> sourceSkus) {
        long initValue = CommonPool.NUMBER_ZERO;
        sourceSkus.forEach(
                sku ->
                        sku.setShopId(targetShopId)
                                .setSalesVolume(initValue)
                                .setInitSalesVolume(initValue)
                                .setStockType(StockType.LIMITED)
                                .setStock(initValue)
                                .setLimitType(LimitType.UNLIMITED)
                                .setLimitNum(CommonPool.NUMBER_ZERO)
                                .setMinimumPurchase(CommonPool.NUMBER_ONE)
        );
        return sourceSkus;
    }

    @Override
    public Map<ActivityShopProductSkuKey, StorageSku> skuBatch(Set<ActivityShopProductSkuKey> shopProductSkuKeys) {
        return skuBatch(false, shopProductSkuKeys);
    }

    /**
     * 根据指定的条件查询一批商品 SKU 的库存信息。如果考虑代销（consignment）情况，则对商品的店铺 ID 进行转换。
     *
     * @param possibleConsignment 一个布尔值，表示是否考虑代销情况。
     * @param shopProductSkuKeys  一个 ActivityShopProductSkuKey 对象的集合，表示要查询库存的商品 SKU。
     * @return 一个 Map，包含了查询到的商品 SKU 信息，其中键是 ActivityShopProductSkuKey 对象，值是对应的 StorageSku 对象。
     */
    @Override
    public Map<ActivityShopProductSkuKey, StorageSku> skuBatch(boolean possibleConsignment,
            Set<ActivityShopProductSkuKey> shopProductSkuKeys) {
        if (!possibleConsignment) {
            return skuStockService.getSkusBatch(shopProductSkuKeys);
        }
        //还原原始 key 需要的 过滤调活动 sku key (活动 sku 必定是本店铺 id)
        Map<String, Set<ActivityShopProductSkuKey>> productKeySkuKeysMap = shopProductSkuKeys.stream()
                .filter(key -> !key.getActivityType().isActivity())
                .collect(
                        Collectors.groupingBy(
                                skuKey -> StorageUtil.consignmentHashKey(skuKey.getShopId(), skuKey.getProductId()),
                                Collectors.toSet()
                        )
                );
        //查询代销关系
        Map<String, Long> consignmenMap = StorageUtil.consignmentRelation(productKeySkuKeysMap.keySet());
        //如果代销关系为空 则直接查询
        if (MapUtil.isEmpty(consignmenMap)) {
            return skuStockService.getSkusBatch(shopProductSkuKeys);
        }
        //还原成参数的原始 sku key map
        Map<ActivityShopProductSkuKey, ActivityShopProductSkuKey> skuMap = new HashMap<>(shopProductSkuKeys.size());
        productKeySkuKeysMap.forEach(
                (productKey, skuKeys) -> {
                    //代销关系的目标店铺 id
                    Long supplierId = consignmenMap.get(productKey);
                    // 不存在代销关系
                    if (supplierId == null) {
                        return;
                    }
                    skuKeys.forEach(skuKey -> {
                        //原始店铺 id
                        Long originalShopId = skuKey.getShopId();
                        //更新为代销关系的sku key
                        skuKey.setShopId(supplierId);
                        skuMap.put(
                                skuKey,
                                (ActivityShopProductSkuKey) new ActivityShopProductSkuKey()
                                        .setSkuId(skuKey.getSkuId())
                                        .setProductId(skuKey.getProductId())
                                        .setShopId(originalShopId)
                                        .setActivityType(skuKey.getActivityType())
                                        .setActivityId(skuKey.getActivityId())
                        );
                    });
                }
        );
        //存在代销关系的数据
        Map<ActivityShopProductSkuKey, StorageSku> skusBatch = skuStockService.getSkusBatch(shopProductSkuKeys);
        //还原成原始 key 与库存关系的映射
        return skusBatch.entrySet()
                .stream()
                .collect(
                        Collectors.toMap(
                                entry -> {
                                    ActivityShopProductSkuKey key = entry.getKey();
                                    return skuMap.getOrDefault(key, key);
                                },
                                Map.Entry::getValue
                        )
                );
    }

    @Override
    public Map<ActivityShopProductSkuKey, Long> skuStockBatch(boolean possibleConsignment,
            Set<ActivityShopProductSkuKey> shopProductSkuKeys) {
        return skuBatch(possibleConsignment, shopProductSkuKeys)
                .entrySet()
                .stream()
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> entry.getValue().getStock()
                        )
                );
    }

    @Override
    public Map<ActivityShopProductKey, List<StorageSku>> productSkuStockBatch(
            Set<ActivityShopProductKey> shopProductKeys) {
        return skuStockService.productSkuStockBatch(shopProductKeys);
    }

    @Override
    public Map<ActivityShopProductKey, Map<Long, StockPriceBO>> productSkuStockBatchMap(
            Set<ActivityShopProductKey> shopProductKeys) {
        Map<ActivityShopProductKey, List<StorageSku>> skuMap = skuStockService.productSkuStockBatch(shopProductKeys);
        Map<ActivityShopProductKey, Map<Long, StockPriceBO>> result = new HashMap<>(skuMap.size());
        skuMap.forEach(
                (key, skus) -> result.put(
                        key,
                        skus.stream()
                                .collect(
                                        Collectors.toMap(
                                                StorageSku::getId,
                                                sku -> new StockPriceBO()
                                                        .setStock(sku.getStock())
                                                        .setSalePrice(sku.getSalePrice())
                                                        .setPrice(sku.getPrice())
                                        )
                                )
                )
        );
        return result;
    }

    @Override
    @Log("获取商品sku")
    public StorageSku getProductSku(ActivityShopProductSkuKey shopProductSkuKey) {
        //非活动商品 查询是否是代销商品 如果是代销商品 则重置 skuId
        if (!shopProductSkuKey.getActivityType().isActivity()) {
            String key = StorageUtil.consignmentHashKey(shopProductSkuKey.getShopId(),
                    shopProductSkuKey.getProductId());
            Map<String, Long> consignmentRelation = StorageUtil.consignmentRelation(Set.of(key));
            Long supplierId = consignmentRelation.get(key);
            if (supplierId != null) {
                shopProductSkuKey.setShopId(supplierId);
            }
        }
        return skuService.getProductSku(shopProductSkuKey);
    }

    @Override
    @Log("新增/更新规格与sku")
    public void saveOrUpdateSpecSku(StorageSpecSkuDTO storageSpecSku) {
        specSkuService.saveOrUpdateSpecSku(storageSpecSku);
    }

    @Override
    public void saveConsignmentRelation(Long shopId, Long productId, Long supplierId) {

        StorageUtil.saveConsignmentRelation(shopId, productId, supplierId);
    }

    @Override
    public Map<String, ProductStatisticsVO> getProductStatisticsMap(List<ShopProductKeyDTO> shopProductKeys) {
        if (CollUtil.isEmpty(shopProductKeys)) {
            return Collections.emptyMap();
        }

        List<ProductStatisticsVO> productStatisticsList = storageShopProductService.getProductStatisticsList(
                false,
                shopProductKeys.stream()
                        .map(spk -> new ShopProductKey().setShopId(spk.getShopId()).setProductId(spk.getProductId()))
                        .collect(Collectors.toSet())
        );
        return CollUtil.emptyIfNull(productStatisticsList).stream()
                .collect(
                        Collectors.toMap(
                                statistics -> RedisUtil.key(statistics.getShopId(), statistics.getProductId()),
                                statistics -> statistics
                        )
                );
    }

    @Override
    public Map<ShopProductKey, ProductStatisticsVO> getProductStatisticsMap(Set<ShopProductKey> shopProductKeys,
            boolean includeActivityStock) {
        if (CollUtil.isEmpty(shopProductKeys)) {
            return Collections.emptyMap();
        }
        List<ProductStatisticsVO> productStatisticsList = storageShopProductService.getProductStatisticsList(
                includeActivityStock,
                shopProductKeys);
        if (CollUtil.isEmpty(productStatisticsList)) {
            return Collections.emptyMap();
        }
        return productStatisticsList.stream()
                .collect(
                        Collectors.toMap(
                                statistics -> new ShopProductKey().setShopId(statistics.getShopId())
                                        .setProductId(statistics.getProductId()),
                                statistics -> statistics
                        )
                );
    }

    @Log("店铺热销")
    @Override
    public List<ProductSaleVolumeVO> getShopProductSaleVolume(Long shopId, Long size) {
        return storageSkuService.getShopProductSaleVolume(shopId, size);
    }

    @Log("获取商品规格信息")
    @Override
    public List<ProductSkusVO.SkuVO> getProductSkusByShopProductKeys(List<ShopProductKeyDTO> shopProductKeys) {
        return storageSkuService.getProductSkusByShopProductKeys(shopProductKeys);
    }

    @Log("获取店铺销量")
    @Override
    public List<ProductStatisticsVO> getShopSales(Boolean sortAsc) {

        return storageSkuService.getShopSales(sortAsc);
    }

    /**
     * 获取指定店铺销量
     *
     * @param shopIds 店铺ids
     * @return 店铺销量
     */
    @Log("获取指定店铺销量")
    @Override
    public List<ProductStatisticsVO> getShopListSales(Set<Long> shopIds) {
        return storageSkuService.getShopListSales(shopIds);
    }

    /**
     * 获取指定店铺销量
     *
     * @param shopId 店铺
     * @return 销量
     */
    @Override
    public Long getShopSaleVolume(Long shopId) {
        return storageSkuService.getShopSaleVolume(shopId);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void storageCopy(StoragesCopyDTO storagesCopy) {
        //目标sku key 与 原始 sku 映射 map
        Map<ActivityShopProductSkuKey, SkuCopy> targetSkuKeyAndSourceKeyMap = targetSkuKeys(storagesCopy);
        if (CollUtil.isEmpty(targetSkuKeyAndSourceKeyMap)) {
            return;
        }
        //用于保存所有sku 数据
        Map<ActivityShopProductSkuKey, StorageSku> allSkuMap = new HashMap<>(targetSkuKeyAndSourceKeyMap.size());
        //先过滤已存在的 sku
        //查询已存在 sku
        Map<ActivityShopProductSkuKey, StorageSku> existStorageMap = this.skuBatch(
                targetSkuKeyAndSourceKeyMap.keySet());
        allSkuMap.putAll(existStorageMap);
        //移除已存在的 sku
        if (CollUtil.isNotEmpty(existStorageMap)) {
            existStorageMap.keySet().forEach(targetSkuKeyAndSourceKeyMap::remove);
        }
        // 目标店铺 id
        Long targetShopId = storagesCopy.getTargetShopId();

        //不存在的 sku
        if (CollUtil.isNotEmpty(targetSkuKeyAndSourceKeyMap)) {
            /*
             * 1. 根据targetSkuKeyAndSourceKeyMap 渲染出 商品 key 1.查询出所有 sku 需要的商品 key 2. 查询出所有规格组与规格需要的 key
             * 2.查询源商品规格组信息
             * 3.查询源商品sku信息
             * 执行 copy
             */
            //渲染出所有需要新增的商品 key 和活动商品 key 集合
            Tuple2<Set<ShopProductKey>, Set<ActivityShopKey>> sourceProductKeysTuple = this.productKeysTuple(
                    targetSkuKeyAndSourceKeyMap.values());
            //复制规格组和规格信息
            Set<StorageSpecGroup> groupsBatch = storageSpecGroupService.getGroupsBatch(sourceProductKeysTuple._1());
            //保存规格组与规格
            this.saveSpecGroupAndSpecs(targetShopId, groupsBatch);
            //查询出所有 复制源 sku key 注意
            Map<ActivityShopProductSkuKey, StorageSku> sourceSkuKeys = storageSkuService.getSkusBatchByPrefixMap(
                    Boolean.TRUE, sourceProductKeysTuple._2());
            //获取完整数据
            Map<ActivityShopProductSkuKey, StorageSku> skusBatch = skuStockService.getSkusBatch(sourceSkuKeys.keySet());
            //取出所有的 sku 重新设置 shopId 与初始化销量 与库存
            Collection<StorageSku> skus = targetSkus(targetShopId, skusBatch.values());
            allSkuMap.putAll(
                    skus.stream()
                            .collect(
                                    Collectors.toMap(
                                            sku -> (ActivityShopProductSkuKey) new ActivityShopProductSkuKey()
                                                    .setSkuId(sku.getId())
                                                    .setProductId(sku.getProductId())
                                                    .setShopId(sku.getShopId())
                                                    .setActivityType(sku.getActivityType())
                                                    .setActivityId(sku.getActivityId())
                                            ,
                                            Function.identity()
                                    )
                            )
            );
            //保存 sku
            storageSkuService.saveBatch(skus);
        }

        //数据准备好后 更新库存即可 与 记录库存明细
        skuStockService.updateStock(
                true,
                false,
                List.of(
                        new UpdateStockOrder()
                                .setOrderNo(storagesCopy.getTransactionId())
                                //不生成 库存明细 （下方已经主动生成了 所以这里不用继续不生成）
                                .setGenerateDetail(false)
                                .setChangeType(StockChangeType.PURCHASE_INBOUND)
                                .setStSvMap(
                                        storagesCopy.getTargets()
                                                .stream()
                                                .collect(
                                                        Collectors.toMap(
                                                                target -> {
                                                                    ShopProductSkuKey sourceKey = target.getSourceKey();
                                                                    ActivityShopProductSkuKey skuKey = new ActivityShopProductSkuKey();
                                                                    skuKey.setSkuId(sourceKey.getSkuId())
                                                                            .setProductId(sourceKey.getProductId())
                                                                            .setShopId(targetShopId)
                                                                            .setActivityType(OrderType.COMMON)
                                                                            .setActivityId(0L);
                                                                    return skuKey;
                                                                },
                                                                target -> new StSvBo().setStock(target.getNum())
                                                        )
                                                )
                                )
                )
        );
        // 这里主动异步生成库存明细
        globalExecutor.execute(
                () -> {
                    String orderNo = storagesCopy.getTransactionId();
                    Set<SkuCopy> copies = storagesCopy.getTargets();
                    Map<ShopProductKey, List<SkuCopy>> skusMap = copies.stream()
                            .collect(
                                    Collectors.groupingBy(
                                            copy -> new ShopProductKey(targetShopId, copy.getSourceKey().getProductId())
                                    )
                            );
                    //查询商品信息 用于获取商品名称
                    Map<ShopProductKey, Product> productMap = goodsRpcService.getProductListBatch(skusMap.keySet());

                    List<StorageDetail> storageDetails = new ArrayList<>(copies.size());
                    skusMap.forEach(
                            (key, skus) -> {
                                Product product = productMap.get(key);
                                skus.forEach(
                                        sku -> {
                                            //原始 key
                                            ShopProductSkuKey sourceKey = sku.getSourceKey();
                                            //获取当前的 sku 信息
                                            Long skuId = sourceKey.getSkuId();
                                            Long productId = sourceKey.getProductId();
                                            StorageSku storageSku = allSkuMap.get(
                                                    (ActivityShopProductSkuKey) new ActivityShopProductSkuKey()
                                                            .setSkuId(skuId)
                                                            .setProductId(productId)
                                                            .setShopId(targetShopId)
                                                            .setActivityType(OrderType.COMMON)
                                                            .setActivityId(0L)
                                            );
                                            // 生成库存明细
                                            storageDetails.add(
                                                    new StorageDetail()
                                                            .setShopId(targetShopId)
                                                            .setSkuId(skuId)
                                                            .setProductId(productId)
                                                            .setStockChangeType(StockChangeType.PURCHASE_INBOUND)
                                                            .setOrderNo(orderNo)
                                                            .setStockChangeNum(sku.getNum().longValue())
                                                            .setSpecName(storageSku == null ? List.of()
                                                                    : storageSku.getSpecs())
                                                            .setProductName(product == null ? sku.getProductName()
                                                                    : product.getName())
                                                            .setSellType(SellType.PURCHASE)
                                            );
                                        }
                                );
                            }
                    );
                    storageDetailService.saveBatch(storageDetails);
                }
        );
    }

    /**
     * 获取多规格数据
     *
     * @param shopProductKeys 商品id + 供应商id 分解多规格返回原始数据
     */
    @Override
    public List<StorageSpecSkuDTO> getStorageSpecSku(Set<ShopProductKey> shopProductKeys) {
        if (CollUtil.isEmpty(shopProductKeys)) {
            return new ArrayList<>();
        }
        Set<ActivityShopProductKey> activityShopProductKeys = shopProductKeys.stream().map(shopProductKey -> {
            ActivityShopProductKey activityShopProductKey = new ActivityShopProductSkuKey();
            activityShopProductKey.setProductId(shopProductKey.getProductId());
            activityShopProductKey.setShopId(shopProductKey.getShopId());
            activityShopProductKey.setActivityId(0L).setActivityType(OrderType.COMMON);
            return activityShopProductKey;
        }).collect(Collectors.toSet());
        Set<StorageSpecGroup> groupsBatch = Option.of(storageSpecGroupService.getGroupsBatch(shopProductKeys))
                .getOrElse(new HashSet<>());
        Map<ActivityShopProductKey, List<StorageSku>> activityShopProductKeyListMap = skuStockService.productSkuStockBatch(
                activityShopProductKeys);
        if (MapUtil.isEmpty(activityShopProductKeyListMap)) {
            return new ArrayList<>();
        }
        Map<ShopProductKey, List<SpecGroupDTO>> productKeyListMap = groupsBatch.stream()
                .filter(BeanUtil::isNotEmpty)
                .collect(Collectors.groupingBy(item ->
                        new ShopProductKey()
                                .setShopId(item.getShopId())
                                .setProductId(item.getProductId())
                )).entrySet().stream()
                .map(entry -> Map.entry(
                                entry.getKey(),
                                entry.getValue()
                                        .stream()
                                        .map(storageSpecGroup -> {
                                            SpecGroupDTO specGroupDTO = new SpecGroupDTO();
                                            specGroupDTO.setName(storageSpecGroup.getName());
                                            specGroupDTO.setOrder(storageSpecGroup.getOrder());
                                            specGroupDTO.setChildren(storageSpecGroup.getSpecs()
                                                    .stream()
                                                    .map(item -> {
                                                        SpecDTO specDTO = new SpecDTO();
                                                        specDTO.setName(item.getName());
                                                        specDTO.setOrder(item.getOrder());
                                                        return specDTO;
                                                    }).collect(Collectors.toList()));
                                            return specGroupDTO;
                                        }).collect(Collectors.toList())
                        )
                ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Map<ActivityShopProductKey, List<SkuDTO>> skuDTOMap = activityShopProductKeyListMap.entrySet()
                .stream()
                .map(entry ->
                        Map.entry(
                                entry.getKey(),
                                entry.getValue()
                                        .stream()
                                        .map(storageSku -> {
                                            SkuDTO skuDTO = new SkuDTO();
                                            BeanUtil.copyProperties(storageSku, skuDTO);
                                            skuDTO.setInitStock(storageSku.getStock());
                                            return skuDTO;
                                        }).collect(Collectors.toList())
                        )
                ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return skuDTOMap.entrySet()
                .stream()
                .map(entry -> {
                    StorageSpecSkuDTO storageSpecSkuDTO = new StorageSpecSkuDTO();
                    storageSpecSkuDTO.setProductId(entry.getKey().getProductId());
                    storageSpecSkuDTO.setSkus(entry.getValue());
                    storageSpecSkuDTO.setSpecGroups(
                            productKeyListMap.get(new ShopProductKey()
                                    .setProductId(entry.getKey().getProductId())
                                    .setShopId(entry.getKey().getShopId())));
                    return storageSpecSkuDTO;
                }).collect(Collectors.toList());

    }

    @Override
    public void generateStorageDetail(Map<Long, Set<Long>> productMap) {
        stockDetailService.generateStorageDetail(productMap);
    }

    @Override
    public List<Long> getShopEmptyStockProIds(ActivityShopProductKey shopProductKey, Boolean emptyStock,
            Boolean unlimitedStock) {
        return skuStockService.queryShopEmptyStockProIds(shopProductKey, emptyStock, unlimitedStock);
    }

    /**
     * 查询商品规格sku信息
     *
     * @param shopId    店铺id
     * @param productId 商品id
     * @return 商品规格sku信息
     */
    @Override
    public ProductSpecsSkusVO getProductSpecsSkus(Long shopId, Long productId) {
        return storageShopProductService.getProductSpecsSkus(shopId, productId, false);
    }

    @Override
    public Map<Long, Long> queryShopHasOnSaleProducts(Map<Long, List<Long>> shopProductIdsMap) {
        return storageShopProductService.queryShopHasOnSaleProducts(shopProductIdsMap);
    }


    private Tuple2<Set<ShopProductKey>, Set<ActivityShopKey>> productKeysTuple(Collection<SkuCopy> copies) {
        Tuple2<Set<ShopProductKey>, Set<ActivityShopKey>> productKeysTuple = Tuple.of(new HashSet<>(), new HashSet<>());
        copies.forEach(copy -> {
            ShopProductSkuKey sourceKey = copy.getSourceKey();
            Long shopId = sourceKey.getShopId();
            Long productId = sourceKey.getProductId();
            productKeysTuple._1().add(new ShopProductKey().setShopId(shopId).setProductId(productId));
            productKeysTuple._2()
                    .add((ActivityShopKey) new ActivityShopProductKey().setProductId(productId).setShopId(shopId)
                            .setActivityType(OrderType.COMMON).setActivityId(0L));
        });
        return productKeysTuple;

    }

    private void saveSpecGroupAndSpecs(Long shopId, Set<StorageSpecGroup> queryGroups) {
        if (CollUtil.isEmpty(queryGroups)) {
            return;
        }
        List<StorageSpec> specs = queryGroups.stream()
                .flatMap(group -> {
                    long groupId = MybatisPlusConfig.IDENTIFIER_GENERATOR.nextId(group).longValue();
                    group.setShopId(shopId).setId(groupId);
                    return group.getSpecs()
                            .stream()
                            .peek(
                                    spec -> spec.setGroupId(groupId)
                                            .setShopId(group.getShopId())
                                            .setProductId(group.getProductId())
                                            .setId(null)
                            );
                }).toList();
        storageSpecGroupService.saveBatch(queryGroups);
        if (CollUtil.isEmpty(specs)) {
            return;
        }
        storageSpecService.saveBatch(specs);

    }

    private Map<ActivityShopProductSkuKey, SkuCopy> targetSkuKeys(StoragesCopyDTO storagesCopy) {
        Set<SkuCopy> targets = storagesCopy.getTargets();
        if (CollUtil.isEmpty(targets)) {
            return Collections.emptyMap();
        }
        Long shopId = storagesCopy.getTargetShopId();
        Map<ActivityShopProductSkuKey, SkuCopy> targetSkuKeyMap = new HashMap<>(targets.size());
        targets.forEach(
                target -> {
                    ShopProductSkuKey sourceKey = target.getSourceKey();
                    targetSkuKeyMap.put(
                            (ActivityShopProductSkuKey) new
                                    ActivityShopProductSkuKey().setSkuId(sourceKey.getSkuId())
                                    .setProductId(sourceKey.getProductId()).setShopId(shopId)
                                    .setActivityType(OrderType.COMMON).setActivityId(0L)
                            ,
                            target
                    );
                }
        );

        return targetSkuKeyMap;
    }


}
