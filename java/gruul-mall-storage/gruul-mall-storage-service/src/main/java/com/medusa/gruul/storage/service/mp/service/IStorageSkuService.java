package com.medusa.gruul.storage.service.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.common.model.base.ActivityShopKey;
import com.medusa.gruul.common.model.base.ActivityShopProductKey;
import com.medusa.gruul.common.model.base.ActivityShopProductSkuKey;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.storage.api.dto.ShopProductKeyDTO;
import com.medusa.gruul.storage.api.entity.StorageSku;
import com.medusa.gruul.storage.api.vo.ProductSaleVolumeVO;
import com.medusa.gruul.storage.api.vo.ProductSkusVO;
import com.medusa.gruul.storage.api.vo.ProductStatisticsVO;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 商品 仓储 表 服务类
 *
 * @author 张治保
 * @since 2022-06-21
 */
public interface IStorageSkuService extends IService<StorageSku> {

    /**
     * 批量查询商品统计
     *
     * @param includeActivityStock 是否包含参加活动的商品
     * @param shopProductKeys      店铺与商品ID集合
     * @return 查询结果
     */
    List<ProductStatisticsVO> getProductStatisticsList(boolean includeActivityStock, Set<ShopProductKey> shopProductKeys);


    /**
     * 获取店铺商品销量
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
     * 批量查询Sku数据
     *
     * @param keys 店铺活动商品sku key
     * @return Sku数据
     */
    List<StorageSku> getSkusBatch(Set<ActivityShopProductSkuKey> keys);

    /**
     * 批量查询活动Sku数据 返回key对应map
     *
     * @param keys 店铺活动商品sku集合
     * @return 活动Sku数据
     */
    Map<ActivityShopProductSkuKey, StorageSku> getSkusBatchMap(Set<ActivityShopProductSkuKey> keys);

    /**
     * 批量查询活动Sku数据 不包含商品id与sku id
     *
     * @param keys          店铺活动集合 不包含sku id
     * @param haveProductId 是否包含商品id
     * @return 活动Sku数据 每条记录只包含 活动类型 活动id 店铺id 商品 id sku id和库存数据
     */
    List<StorageSku> getSkusBatchByPrefix(boolean haveProductId, Set<ActivityShopKey> keys);


    /**
     * 批量查询活动Sku数据 返回key对应map
     *
     * @param keys          店铺活动商品sku集合 不包含sku id
     * @param haveProductId 是否包含商品id
     * @return 活动Sku数据 每条记录只包含 活动类型 活动id 店铺id 商品 id sku id和库存数据
     */
    Map<ActivityShopProductSkuKey, StorageSku> getSkusBatchByPrefixMap(boolean haveProductId, Set<ActivityShopKey> keys);


    /**
     * 批量删除活动Sku数据 不包含商品id与sku id
     *
     * @param keys 店铺活动集合 不包含 商品id与sku id
     */
    void deleteSkusByPrefix(Set<ActivityShopKey> keys);

    List<ProductStatisticsVO> getShopSales(Boolean sortAsc);

    /**
     * 获取指定店铺销量
     *
     * @param shopIds 店铺ids
     * @return 店铺销量
     */
    List<ProductStatisticsVO> getShopListSales(Set<Long> shopIds);

    /**
     * 查询店铺销量
     *
     * @param shopId 店铺id
     * @return 销量
     */
    Long getShopSaleVolume(Long shopId);


    /**
     * 查询指定店铺无限库存商品 或库存为0的商品
     *
     * @param shopProductKey key
     * @param emptyStock     是否查询空库存商品
     * @param unLimitStock   是否无限库存
     * @return 商品id集合
     */
    List<Long> queryShopEmptyStockProIds(ActivityShopProductKey shopProductKey, Boolean emptyStock, Boolean unLimitStock);
}
