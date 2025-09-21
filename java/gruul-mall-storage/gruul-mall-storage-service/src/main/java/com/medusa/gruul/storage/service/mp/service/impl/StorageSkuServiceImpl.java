package com.medusa.gruul.storage.service.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.medusa.gruul.common.model.base.ActivityShopKey;
import com.medusa.gruul.common.model.base.ActivityShopProductKey;
import com.medusa.gruul.common.model.base.ActivityShopProductSkuKey;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.storage.api.dto.ShopProductKeyDTO;
import com.medusa.gruul.storage.api.entity.StorageSku;
import com.medusa.gruul.storage.api.vo.ProductSaleVolumeVO;
import com.medusa.gruul.storage.api.vo.ProductSkusVO;
import com.medusa.gruul.storage.api.vo.ProductStatisticsVO;
import com.medusa.gruul.storage.service.mp.mapper.StorageSkuMapper;
import com.medusa.gruul.storage.service.mp.service.IStorageSkuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 商品 仓储 表 服务实现类
 *
 * @author 张治保
 * @since 2022-06-21
 */
@Service
@RequiredArgsConstructor
public class StorageSkuServiceImpl extends ServiceImpl<StorageSkuMapper, StorageSku> implements
        IStorageSkuService {


    @Override
    public List<ProductStatisticsVO> getProductStatisticsList(boolean includeActivityStock,
                                                              Set<ShopProductKey> shopProductKeys) {
        return baseMapper.getProductStatisticsList(includeActivityStock, shopProductKeys);
    }

    /**
     * 获取店铺商品销量
     *
     * @param shopId 店铺id
     * @param size   查询数量
     * @return 店铺热销商品
     */
    @Override
    public List<ProductSaleVolumeVO> getShopProductSaleVolume(Long shopId, Long size) {
        return baseMapper.getShopProductSaleVolume(shopId, size == null ? 5 : size);
    }

    /**
     * 获取商品规格信息
     *
     * @param shopProductKeys 店铺与商品id集合
     * @return 商品规格信息
     */
    @Override
    public List<ProductSkusVO.SkuVO> getProductSkusByShopProductKeys(
            List<ShopProductKeyDTO> shopProductKeys) {
        return baseMapper.getProductSkusByShopProductKeys(shopProductKeys);
    }

    @Override
    public List<StorageSku> getSkusBatch(Set<ActivityShopProductSkuKey> keys) {
        return baseMapper.getSkus(keys);
    }


    @Override
    public Map<ActivityShopProductSkuKey, StorageSku> getSkusBatchMap(
            Set<ActivityShopProductSkuKey> keys) {
        return this.toSkuKeyMap(this.getSkusBatch(keys));
    }


    @Override
    public List<StorageSku> getSkusBatchByPrefix(boolean haveProductId, Set<ActivityShopKey> keys) {
        return baseMapper.getSkusByPrefix(haveProductId, keys);
    }

    @Override
    public Map<ActivityShopProductSkuKey, StorageSku> getSkusBatchByPrefixMap(boolean haveProductId,
                                                                              Set<ActivityShopKey> keys) {
        return this.toSkuKeyMap(this.getSkusBatchByPrefix(haveProductId, keys));
    }

    @Override
    public void deleteSkusByPrefix(Set<ActivityShopKey> keys) {
        baseMapper.deleteSkusByPrefix(keys);
    }

    @Override
    public List<ProductStatisticsVO> getShopSales(Boolean sortAsc) {
        return baseMapper.getShopSales(sortAsc);
    }

    /**
     * 获取指定店铺销量
     *
     * @param shopIds 店铺ids
     * @return 店铺销量
     */
    @Override
    public List<ProductStatisticsVO> getShopListSales(Set<Long> shopIds) {
        return baseMapper.getShopListSales(shopIds);
    }

    @Override
    public Long getShopSaleVolume(Long shopId) {
        return baseMapper.getShopSaleVolume(shopId);
    }
    

    private Map<ActivityShopProductSkuKey, StorageSku> toSkuKeyMap(List<StorageSku> skus) {
        return skus.stream()
                .collect(
                        Collectors.toMap(
                                sku -> {
                                    ActivityShopProductSkuKey key = new ActivityShopProductSkuKey().setSkuId(
                                            sku.getId());
                                    key.setProductId(sku.getProductId()).setShopId(sku.getShopId())
                                            .setActivityType(sku.getActivityType()).setActivityId(sku.getActivityId());
                                    return key;
                                },
                                v -> v
                        )
                );
    }

    @Override
    public List<Long> queryShopEmptyStockProIds(ActivityShopProductKey shopProductKey,
                                                Boolean emptyStock,
                                                Boolean unLimitStock) {
        Set<Long> productIds = Sets.newHashSet();
        if (emptyStock) {
            //获取店铺下商品的所有sku 按照商品id分组 统计出所有商品的库存为0的商品id
            List<StorageSku> storageSkus = baseMapper.shopEmptyStockProIds(shopProductKey);
            Set<Long> zeroStockProIds = Sets.newHashSet();
            if (!CollectionUtils.isEmpty(storageSkus)) {
                Map<Long, Long> collect = storageSkus.stream().collect(
                        Collectors.groupingBy(StorageSku::getProductId,
                                Collectors.summingLong(StorageSku::getStock)));
                collect.forEach((p, c) -> {
                    if (c.intValue() == CommonPool.NUMBER_ZERO) {
                        zeroStockProIds.add(p);
                    }
                });
            }
            productIds.addAll(zeroStockProIds);
        }
        if (unLimitStock) {
            productIds.addAll(baseMapper.shopUnlimitProIds(shopProductKey));
        }
        return Lists.newArrayList(productIds);
    }
}
