package com.medusa.gruul.storage.service.service.rpc;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Maps;
import com.medusa.gruul.common.model.base.ActivityShopProductKey;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.model.dto.ConsignmentPriceDTO;
import com.medusa.gruul.goods.api.model.dto.ConsignmentPriceSettingDTO;
import com.medusa.gruul.goods.api.rpc.GoodsRpcService;
import com.medusa.gruul.storage.api.entity.StorageSku;
import com.medusa.gruul.storage.api.vo.ProductSpecsSkusVO;
import com.medusa.gruul.storage.api.vo.ProductStatisticsVO;
import com.medusa.gruul.storage.service.mp.service.IStorageSkuService;
import com.medusa.gruul.storage.service.mp.service.IStorageSpecGroupService;
import com.medusa.gruul.storage.service.service.SkuStockService;
import com.medusa.gruul.storage.service.service.StorageShopProductService;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

/**
 * @author 张治保 date 2022/7/14
 */
@Service
@DubboService
@RequiredArgsConstructor
public class StorageShopProductServiceImpl implements StorageShopProductService {


    private final SkuStockService skuStockService;
    private final IStorageSkuService storageSkuService;
    private final IStorageSpecGroupService storageSpecGroupService;
    private final GoodsRpcService goodsRpcService;


    @Override
    public ProductStatisticsVO getProductStatistics(Long shopId, Long productId) {
        return this.getProductStatisticsList(
                false,
                Set.of(new ShopProductKey().setShopId(shopId).setProductId(productId))
        ).stream().findFirst().orElse(null);
    }

    @Override
    public List<ProductStatisticsVO> getProductStatisticsList(boolean includeActivityStock,
            Set<ShopProductKey> shopProductKeys) {
        return storageSkuService.getProductStatisticsList(includeActivityStock, shopProductKeys);
    }

    @Override
    public ProductSpecsSkusVO getProductSpecsSkus(Long shopId, Long productId, boolean isSupplier) {
        ActivityShopProductKey key = new ActivityShopProductKey()
                .setProductId(productId);
        // 该处shopId 可能是suppliedId
        key.setShopId(shopId)
                .setActivityType(OrderType.COMMON)
                .setActivityId(0L);
        //供应商商品 直接查询数据库
        if (isSupplier) {
            List<StorageSku> storageSkus = skuStockService.productSkuStockBatch(Set.of(key))
                    .values()
                    .stream()
                    .findFirst()
                    .orElseGet(List::of);
            return new ProductSpecsSkusVO()
                    .setSpecGroups(storageSpecGroupService.getProductSpecs(shopId, productId))
                    .setSkus(storageSkus);
        }
        //非供应商 查询商品判断是否是代销商品 如果是代销商品则直接查询供应商商品
        Product product = goodsRpcService.getProductInfo(shopId, productId);
        if (product != null && SellType.CONSIGNMENT == product.getSellType()) {
            key.setShopId(product.getSupplierId());
            shopId = product.getSupplierId();
        }
        List<StorageSku> storageSkus = skuStockService.productSkuStockBatch(Set.of(key))
                .values()
                .stream()
                .findFirst()
                .orElseGet(List::of);
        if (CollUtil.isEmpty(storageSkus)) {
            return new ProductSpecsSkusVO()
                    .setSpecGroups(storageSpecGroupService.getProductSpecs(shopId, productId))
                    .setSkus(storageSkus);
        }
        //计算代销价格
        ConsignmentPriceSettingDTO consignmentPrice;
        if (product == null || (consignmentPrice = product.getExtra().getConsignmentPriceSetting()) == null) {
            return new ProductSpecsSkusVO()
                    .setSpecGroups(storageSpecGroupService.getProductSpecs(shopId, productId))
                    .setSkus(storageSkus);
        }
        storageSkus.forEach(
                sku -> {
                    ConsignmentPriceDTO price = consignmentPrice.singlePrice(sku.getSalePrice());
                    sku.setSalePrice(price.getSalePrice())
                            .setPrice(price.getPrice());
                }
        );
        return new ProductSpecsSkusVO()
                .setSpecGroups(storageSpecGroupService.getProductSpecs(shopId, productId))
                .setSkus(storageSkus);
    }

    @Override
    public Map<Long, Long> queryShopHasOnSaleProducts(Map<Long, List<Long>> shopProductIdsMap) {
        Map<Long, Long> result = Maps.newHashMap();
        shopProductIdsMap.forEach((shopId, productIds) -> {
            List<StorageSku> list = storageSkuService.lambdaQuery()
                    .select(StorageSku::getStock)
                    .eq(StorageSku::getActivityType, OrderType.COMMON)
                    .eq(StorageSku::getActivityId, 0L)
                    .eq(StorageSku::getShopId, shopId)
                    .in(StorageSku::getProductId, productIds)
                    .eq(StorageSku::getDeleted, false)
                    .list();
            //求和
            long stockSum = list.stream().map(StorageSku::getStock)
                    .filter(Objects::nonNull)
                    .mapToLong(Long::longValue).sum();
            result.put(shopId, stockSum);
        });
        return result;
    }
}
