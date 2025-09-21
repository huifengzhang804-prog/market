package com.medusa.gruul.order.service.modules.orderTest.mock.rpc;

import com.medusa.gruul.common.model.base.ActivityShopProductKey;
import com.medusa.gruul.common.model.base.ActivityShopProductSkuKey;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.storage.api.bo.StockPriceBO;
import com.medusa.gruul.storage.api.dto.ShopProductKeyDTO;
import com.medusa.gruul.storage.api.dto.StorageSpecSkuDTO;
import com.medusa.gruul.storage.api.dto.StoragesCopyDTO;
import com.medusa.gruul.storage.api.entity.StorageSku;
import com.medusa.gruul.storage.api.enums.LimitType;
import com.medusa.gruul.storage.api.enums.StockType;
import com.medusa.gruul.storage.api.rpc.StorageRpcService;
import com.medusa.gruul.storage.api.vo.ProductSaleVolumeVO;
import com.medusa.gruul.storage.api.vo.ProductSkusVO;
import com.medusa.gruul.storage.api.vo.ProductSpecsSkusVO;
import com.medusa.gruul.storage.api.vo.ProductStatisticsVO;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 张治保
 * @since 2024/2/20
 */
public class StorageMockTest implements StorageRpcService {
    @Override
    public Map<ActivityShopProductSkuKey, StorageSku> skuBatch(Set<ActivityShopProductSkuKey> shopProductSkuKeys) {
        return null;
    }

    @Override
    public Map<ActivityShopProductSkuKey, StorageSku> skuBatch(boolean possibleConsignment, Set<ActivityShopProductSkuKey> shopProductSkuKeys) {
        return null;
    }

    @Override
    public Map<ActivityShopProductSkuKey, Long> skuStockBatch(boolean possibleConsignment, Set<ActivityShopProductSkuKey> shopProductSkuKeys) {
        return null;
    }

    @Override
    public Map<ActivityShopProductKey, List<StorageSku>> productSkuStockBatch(Set<ActivityShopProductKey> shopProductKeys) {
        return null;
    }

    @Override
    public Map<ActivityShopProductKey, Map<Long, StockPriceBO>> productSkuStockBatchMap(Set<ActivityShopProductKey> shopProductKeys) {
        return null;
    }

    @Override
    public StorageSku getProductSku(ActivityShopProductSkuKey shopProductSkuKey) {
        Long shopId = shopProductSkuKey.getShopId();
        Long productId = shopProductSkuKey.getProductId();
        Long skuId = shopProductSkuKey.getSkuId();
        return (StorageSku) new StorageSku()
                .setShopId(shopId)
                .setProductId(productId)
                .setStockType(StockType.UNLIMITED)
                .setStock(5000L)
                .setLimitType(LimitType.UNLIMITED)
                .setLimitNum(0)
                .setImage("http://www.baidu.com")
                .setPrice(200 * 10000L)// 200.00
                .setSalePrice(199 * 10000L)// 199.00
                .setId(skuId);
    }

    @Override
    public void saveOrUpdateSpecSku(StorageSpecSkuDTO storageSpecSku) {

    }

    @Override
    public void saveConsignmentRelation(Long shopId, Long productId, Long supplierId) {

    }

    @Override
    public Map<String, ProductStatisticsVO> getProductStatisticsMap(List<ShopProductKeyDTO> shopProductKeys) {
        return null;
    }

    @Override
    public Map<ShopProductKey, ProductStatisticsVO> getProductStatisticsMap(Set<ShopProductKey> shopProductKeys, boolean includeActivityStock) {
        return null;
    }

    @Override
    public List<ProductSaleVolumeVO> getShopProductSaleVolume(Long shopId, Long size) {
        return null;
    }

    @Override
    public List<ProductSkusVO.SkuVO> getProductSkusByShopProductKeys(List<ShopProductKeyDTO> shopProductKeys) {
        return null;
    }

    @Override
    public List<ProductStatisticsVO> getShopSales(Boolean sortAsc) {
        return null;
    }

    @Override
    public List<ProductStatisticsVO> getShopListSales(Set<Long> shopIds) {
        return null;
    }

    @Override
    public Long getShopSaleVolume(Long shopId) {
        return null;
    }


    @Override
    public void storageCopy(StoragesCopyDTO storages) {

    }

    @Override
    public List<StorageSpecSkuDTO> getStorageSpecSku(Set<ShopProductKey> shopProductKeys) {
        return null;
    }

    @Override
    public void generateStorageDetail(Map<Long, Set<Long>> productMap) {

    }

    @Override
    public List<Long> getShopEmptyStockProIds(ActivityShopProductKey activityShopProductKey, Boolean emptyStock, Boolean unlimitStock) {
        return null;
    }

    @Override
    public ProductSpecsSkusVO getProductSpecsSkus(Long shopId, Long productId) {
        return null;
    }

    @Override
    public Map<Long, Long> queryShopHasOnSaleProducts(Map<Long, List<Long>> shopProductIdsMap) {
        return Map.of();
    }


}
