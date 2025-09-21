package com.medusa.gruul.storage.api.rpc;

import com.medusa.gruul.common.model.base.ActivityShopProductKey;
import com.medusa.gruul.common.model.base.ActivityShopProductSkuKey;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.storage.api.bo.StockPriceBO;
import com.medusa.gruul.storage.api.dto.ShopProductKeyDTO;
import com.medusa.gruul.storage.api.dto.StorageSpecSkuDTO;
import com.medusa.gruul.storage.api.dto.StoragesCopyDTO;
import com.medusa.gruul.storage.api.entity.StorageSku;
import com.medusa.gruul.storage.api.vo.ProductSaleVolumeVO;
import com.medusa.gruul.storage.api.vo.ProductSkusVO;
import com.medusa.gruul.storage.api.vo.ProductSpecsSkusVO;
import com.medusa.gruul.storage.api.vo.ProductStatisticsVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 查询店铺商品sku关键信息
 *
 * @author 张治保 date 2022/7/6
 */
public interface StorageRpcService {

    /**
     * 批量查询SKU库存 直接使用 key 查询 没有处理代销关系 如果需要处理代销关系 请使用 {@link #skuBatch(boolean, Set)}
     *
     * @param shopProductSkuKeys 店铺商品sku key
     * @return 查询结果
     */
    Map<ActivityShopProductSkuKey, StorageSku> skuBatch(Set<ActivityShopProductSkuKey> shopProductSkuKeys);

    /**
     * 根据指定的条件查询一批商品 SKU 的库存信息。如果考虑代销（consignment）情况，则对商品的店铺 ID 进行转换。
     *
     * @param possibleConsignment 一个布尔值，表示是否考虑代销情况。
     * @param shopProductSkuKeys  一个 ActivityShopProductSkuKey 对象的集合，表示要查询库存的商品 SKU。
     * @return 一个 Map，包含了查询到的商品 SKU 信息，其中键是 ActivityShopProductSkuKey 对象，值是对应的 StorageSku 对象。
     */
    Map<ActivityShopProductSkuKey, StorageSku> skuBatch(boolean possibleConsignment,
            Set<ActivityShopProductSkuKey> shopProductSkuKeys);


    /**
     * 批量查询SKU库存 直接使用 key 查询  仅返回库存 不返回其它信息 如果需要处理代销关系 请使用 {@link #skuBatch(boolean, Set)}
     *
     * @param possibleConsignment 是否考虑代销情况。
     * @param shopProductSkuKeys  商品sku key集合
     * @return 商品sku库存 key -》 skuKey value-》库存
     */
    Map<ActivityShopProductSkuKey, Long> skuStockBatch(boolean possibleConsignment,
            @Valid @Size(min = 1) Set<ActivityShopProductSkuKey> shopProductSkuKeys);

    /**
     * 批量查询商品sku列表
     *
     * @param shopProductKeys 活动类型 活动ID 店铺id与商品id 组成的key集合
     * @return sku库存列表
     */
    Map<ActivityShopProductKey, List<StorageSku>> productSkuStockBatch(Set<ActivityShopProductKey> shopProductKeys);

    /**
     * 批量查询商品 sku库存 仅返回库存和价格信息，避免过多无效数据IO 原理同{@link StorageRpcService#productSkuStockBatch(Set)} 多了一步数据处理
     *
     * @return 商品可以与 sku 库存映射 Map<商品 key,Map<skuid,库存>>
     */
    Map<ActivityShopProductKey, Map<Long, StockPriceBO>> productSkuStockBatchMap(
            Set<ActivityShopProductKey> shopProductKeys);

    /**
     * 获取商品sku仓储信息 先走缓存,缓存没有 查数据库 数据库有则写入缓存并返回
     *
     * @param shopProductSkuKey 店铺商品数据id
     * @return 商品数据缓存信息
     */
    StorageSku getProductSku(@Valid ActivityShopProductSkuKey shopProductSkuKey);


    /**
     * 保存/更新 商品sku与spec规格信息
     *
     * @param storageSpecSku 商品规格与sku信息
     * @throws RuntimeException 私事
     */
    void saveOrUpdateSpecSku(@Valid StorageSpecSkuDTO storageSpecSku);

    /**
     * 保存代销关系 仅当代销商品时调用
     */
    void saveConsignmentRelation(@NotNull Long shopId, @NotNull Long productId, @NotNull Long supplierId);

    /**
     * 批量查询商品统计 (不包含参加活动的sku)
     *
     * @param shopProductKeys 店铺与商品ID集合
     * @return key => 店铺id:商品id value => 统计结果
     */
    Map<String, ProductStatisticsVO> getProductStatisticsMap(List<ShopProductKeyDTO> shopProductKeys);


    /**
     * 查询商品统计 包含参加活动的sku
     *
     * @param shopProductKeys      店铺与商品ID集合
     * @param includeActivityStock 包含活动库存
     * @return key => 店铺id,商品id value => 统计结果
     */
    Map<ShopProductKey, ProductStatisticsVO> getProductStatisticsMap(Set<ShopProductKey> shopProductKeys,
            boolean includeActivityStock);

    /**
     * pc端-店铺热销
     *
     * @param shopId 店铺id
     * @param size   查询数量
     * @return 店铺热销商品
     */
    List<ProductSaleVolumeVO> getShopProductSaleVolume(Long shopId, Long size);

    /**
     * 获取商品规格信息
     *
     * @param shopProductKeys 店铺与商品id集合
     * @return 商品规格信息
     */
    List<ProductSkusVO.SkuVO> getProductSkusByShopProductKeys(List<ShopProductKeyDTO> shopProductKeys);

    /**
     * 获取店铺销量
     *
     * @param sortAsc 是否正序
     * @return 店铺销量
     */
    List<ProductStatisticsVO> getShopSales(Boolean sortAsc);

    /**
     * 获取指定店铺销量
     *
     * @param shopIds 店铺ids
     * @return 店铺销量
     */
    List<ProductStatisticsVO> getShopListSales(Set<Long> shopIds);

    /**
     * 获取指定店铺销量
     *
     * @param shopId 店铺
     * @return 销量
     */
    Long getShopSaleVolume(Long shopId);

    /**
     * 批量 更新sku或新增 sku 目前只针对采购入库 会对目标sku做判断： 不存在sku会copy一份源数据并新曾 、已存在的sku则会更新其库存
     *
     * @param storages 需要处理的sku 信息
     */
    void storageCopy(@Valid StoragesCopyDTO storages);

    /**
     * 获取商品多规格数据
     *
     * @param shopProductKeys 商品id + 供应商id
     */
    List<StorageSpecSkuDTO> getStorageSpecSku(Set<ShopProductKey> shopProductKeys);

    /**
     * 生成仓储明细
     *
     * @param productMap 商品信息
     */
    void generateStorageDetail(Map<Long, Set<Long>> productMap);

    /**
     * 获取指定店铺空库存商品ids 或无限库存的商品ids
     */
    List<Long> getShopEmptyStockProIds(ActivityShopProductKey activityShopProductKey, Boolean emptyStock,
            Boolean unlimitedStock);

    /**
     * 查询商品规格sku信息
     *
     * @param shopId    店铺id
     * @param productId 商品id
     * @return 商品规格sku信息
     */
    ProductSpecsSkusVO getProductSpecsSkus(@NotNull Long shopId, @NotNull Long productId);

    /**
     * 查询店铺商品的库存总和
     *
     * @param shopProductIdsMap
     * @return
     */
    Map<Long, Long> queryShopHasOnSaleProducts(Map<Long, List<Long>> shopProductIdsMap);
}
