package com.medusa.gruul.storage.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.medusa.gruul.common.model.base.ActivityShopProductSkuKey;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.config.MybatisPlusConfig;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import com.medusa.gruul.storage.api.dto.SkuDTO;
import com.medusa.gruul.storage.api.dto.SpecGroupDTO;
import com.medusa.gruul.storage.api.dto.StorageSpecSkuDTO;
import com.medusa.gruul.storage.api.entity.StorageSku;
import com.medusa.gruul.storage.api.enums.StockChangeType;
import com.medusa.gruul.storage.service.components.StorageSupplierExists;
import com.medusa.gruul.storage.service.mp.entity.StorageDetail;
import com.medusa.gruul.storage.service.mp.entity.StorageSpec;
import com.medusa.gruul.storage.service.mp.entity.StorageSpecGroup;
import com.medusa.gruul.storage.service.mp.mapper.StorageSkuMapper;
import com.medusa.gruul.storage.service.mp.service.IStorageDetailService;
import com.medusa.gruul.storage.service.mp.service.IStorageSkuService;
import com.medusa.gruul.storage.service.mp.service.IStorageSpecGroupService;
import com.medusa.gruul.storage.service.mp.service.IStorageSpecService;
import com.medusa.gruul.storage.service.service.SpecSkuService;
import com.medusa.gruul.storage.service.util.StorageUtil;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author 张治保
 * date 2022/7/12
 */
@Service
@RequiredArgsConstructor
public class SpecSkuServiceImpl implements SpecSkuService {

    private final SqlSessionFactory sqlSessionFactory;
    private final IStorageSkuService storageSkuService;
    private final IStorageSpecService storageSpecService;
    private final StorageSupplierExists storageSupplierExists;
    private final IStorageDetailService storageDetailService;
    private final IStorageSpecGroupService storageSpecGroupService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateSpecSku(StorageSpecSkuDTO storageSpecSku) {
        //保存供应商 id到供应商列表
        ISecurity.match()
                .ifAnySupplierAdmin(secureUser -> storageSupplierExists.add(secureUser.getShopId()));
        Long shopId = ISecurity.userMust().getShopId();
        Long productId = storageSpecSku.getProductId();
        OrderType activityType = OrderType.COMMON;
        Long activityId = 0L;
        //判断是否是新商品
        boolean newSku = !storageSkuService.lambdaQuery()
                .eq(StorageSku::getActivityType, activityType)
                .eq(StorageSku::getActivityId, activityId)
                .eq(StorageSku::getShopId, shopId)
                .eq(StorageSku::getProductId, productId)
                .exists();
        //如果是新商品 保存规格与规格组
        if (newSku) {
            this.saveNewSpecs(shopId, productId, storageSpecSku.getSpecGroups());
        }
        //渲染出目标 sku 列表
        List<StorageSku> currentSkuInfos = this.currentSkuInfos(newSku, activityType, activityId, shopId, productId, storageSpecSku.getSkus());
        //如果是新sku则保存sku 并生成明细
        if (newSku) {
            this.saveNewSkus(shopId, storageSpecSku, currentSkuInfos);
            return;
        }
        //非新sku
        //需要编辑的 skuKey 对应的map集合
        this.editOldSkus(currentSkuInfos);
    }

    /**
     * 渲染出目标 sku 列表
     *
     * @param newSku       是否是新sku
     * @param activityType 活动类型
     * @param activityId   活动id
     * @param shopId       店铺id
     * @param productId    商品id
     * @param skus         sku参数列表
     * @return 目标sku列表
     */
    private List<StorageSku> currentSkuInfos(boolean newSku, OrderType activityType, Long activityId, Long shopId, Long productId, List<SkuDTO> skus) {
        List<StorageSku> storageSkus = new ArrayList<>();
        for (int index = 0; index < skus.size(); index++) {
            SkuDTO sku = skus.get(index);
            StorageSku storageSku = new StorageSku();
            storageSku.setId(newSku ? null : sku.getId());
            storageSku.setActivityType(activityType)
                    .setActivityId(activityId)
                    .setShopId(shopId)
                    .setProductId(productId)
                    .setStockType(sku.getStockType())
                    .setStock(sku.getInitStock())
                    .setSalesVolume(0L)
                    .setInitSalesVolume(sku.getInitSalesVolume())
                    .setMinimumPurchase(sku.getMinimumPurchase())
                    .setLimitType(sku.getLimitType())
                    .setLimitNum(sku.getLimitNum())
                    .setSpecs(CollUtil.defaultIfEmpty(sku.getSpecs(), List.of()))
                    .setImage(sku.getImage())
                    .setPrice(sku.getPrice())
                    .setSalePrice(sku.getSalePrice())
                    .setWeight(sku.getWeight())
                    .setSort(index);
            storageSkus.add(storageSku);
        }
        return storageSkus;
    }

    /**
     * 编辑老的sku
     *
     * @param oldSkus 新的sku
     */
    @SuppressWarnings({"unchecked"})
    private void editOldSkus(List<StorageSku> oldSkus) {
        Map<ActivityShopProductSkuKey, StorageSku> editTargetSkuKeySkuMap = oldSkus.stream()
                .filter(storageSku -> Objects.nonNull(storageSku.getId()))
                .collect(
                        Collectors.toMap(
                                editedStorageSku -> {
                                    ActivityShopProductSkuKey key = new ActivityShopProductSkuKey().setSkuId(editedStorageSku.getId());
                                    key.setProductId(editedStorageSku.getProductId()).setShopId(editedStorageSku.getShopId())
                                            .setActivityType(editedStorageSku.getActivityType()).setActivityId(editedStorageSku.getActivityId());
                                    return key;
                                },
                                v -> v
                        )
                );
        //需要更新的sku
        boolean emptyUpdateSkus = CollUtil.isEmpty(editTargetSkuKeySkuMap);
        if (!emptyUpdateSkus) {
            try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
                StorageSkuMapper mapper = sqlSession.getMapper(StorageSkuMapper.class);
                for (StorageSku value : editTargetSkuKeySkuMap.values()) {
                    mapper.update(
                            null,
                            Wrappers.lambdaUpdate(StorageSku.class)
                                    .eq(StorageSku::getActivityType, value.getActivityType())
                                    .eq(StorageSku::getActivityId, value.getActivityId())
                                    .eq(StorageSku::getShopId, value.getShopId())
                                    .eq(StorageSku::getProductId, value.getProductId())
                                    .eq(StorageSku::getId, value.getId())
                                    .set(StorageSku::getInitSalesVolume, value.getInitSalesVolume())
                                    .set(StorageSku::getLimitType, value.getLimitType())
                                    .set(StorageSku::getLimitNum, value.getLimitNum())
                                    .set(StorageSku::getImage, value.getImage())
                                    .set(StorageSku::getPrice, value.getPrice())
                                    .set(StorageSku::getSalePrice, value.getSalePrice())
                                    .set(StorageSku::getWeight, value.getWeight())
                                    .set(StorageSku::getMinimumPurchase, value.getMinimumPurchase())
                    );
                }
                sqlSession.commit();
            }
        }
        /* --------- 删除缓存 ------------ */
        if (!emptyUpdateSkus) {
            RedisUtil.executePipelined(
                    redisOperations -> {
                        redisOperations.delete(
                                editTargetSkuKeySkuMap.keySet().stream().map(StorageUtil::generateSkuRedisKey).collect(Collectors.toSet())
                        );
                    }
            );
        }
    }

    /**
     * 保存新的sku 并生成明细
     *
     * @param shopId         店铺id
     * @param storageSpecSku 商品规格与sku信息
     * @param newSkus        新的sku
     */
    private void saveNewSkus(Long shopId, StorageSpecSkuDTO storageSpecSku, List<StorageSku> newSkus) {
        storageSkuService.saveBatch(newSkus);
        // 发布自有商品时才会 生产库存明细 列 : 店铺商品发布自有商品  供应商发布采购商品或代销
        AtomicReference<SellType> sellType = new AtomicReference<>(storageSpecSku.getSellType());
        AtomicReference<Boolean> isExecute = new AtomicReference<>(Boolean.TRUE);
        ISecurity.match().ifAnyShopAdmin(secureUser -> {
            if (sellType.get() != SellType.OWN) {
                isExecute.set(Boolean.FALSE);
            }
        });
        if (!isExecute.get()) {
            return;
        }
        //如果是非上架的商品状态 不生成相关明细
        ProductStatus status = storageSpecSku.getProductCurrentStatus().get("status", ProductStatus.class);
        //过滤掉库存为0的sku 不做记录
        newSkus = newSkus.stream().filter(x -> x.getStock() > 0).collect(Collectors.toList());
        if (CollUtil.isEmpty(newSkus)) {
            return;
        }
        // 批量保存库存明细
        storageDetailService.saveBatch(
                newSkus.stream()
                        .map(storageSku -> new StorageDetail()
                                .setProductId(storageSpecSku.getProductId())
                                .setShopId(shopId)
                                .setSkuId(storageSku.getId())
                                .setSpecName(storageSku.getSpecs())
                                .setStockChangeNum(storageSku.getStock())
                                .setIsShow(status == ProductStatus.SELL_ON)
                                .setStockChangeType(StockChangeType.PUBLISHED_INBOUND)
                                .setSellType(sellType.get())
                                .setProductName(storageSpecSku.getProductName())
                        ).toList()
        );
    }

    /**
     * 保存规格组与规格信息
     */
    private void saveNewSpecs(Long shopId, Long productId, List<SpecGroupDTO> specGroups) {
        if (CollUtil.isEmpty(specGroups)) {
            return;
        }
        IdentifierGenerator keyGenerator = getKeyGenerator();
        List<StorageSpecGroup> storageSpecGroups = new ArrayList<>();
        List<StorageSpec> specs = new ArrayList<>();
        for (int index = 0; index < specGroups.size(); index++) {
            SpecGroupDTO group = specGroups.get(index);
            StorageSpecGroup storageSpecGroup = new StorageSpecGroup()
                    .setShopId(shopId)
                    .setProductId(productId)
                    .setName(group.getName())
                    .setOrder(ObjectUtil.defaultIfNull(group.getOrder(), index));
            long groupId = keyGenerator.nextId(storageSpecGroup).longValue();
            storageSpecGroup.setId(groupId);
            specs.addAll(
                    group.getChildren()
                            .stream()
                            .map(
                                    spec -> {
                                        String name = spec.getName();
                                        StorageSpec storageSpec = new StorageSpec()
                                                .setShopId(shopId)
                                                .setProductId(productId)
                                                .setGroupId(groupId)
                                                .setName(name)
                                                .setOrder(spec.getOrder());
                                        long specId = keyGenerator.nextId(storageSpec).longValue();
                                        storageSpec.setId(specId);
                                        return storageSpec;
                                    }
                            ).toList()
            );
            storageSpecGroups.add(storageSpecGroup);
        }
        boolean success = storageSpecGroupService.saveBatch(storageSpecGroups);
        SystemCode.DATA_ADD_FAILED.falseThrow(success);
        success = storageSpecService.saveBatch(specs);
        SystemCode.DATA_ADD_FAILED.falseThrow(success);
    }

    private IdentifierGenerator getKeyGenerator() {
        return MybatisPlusConfig.IDENTIFIER_GENERATOR;
    }
}
