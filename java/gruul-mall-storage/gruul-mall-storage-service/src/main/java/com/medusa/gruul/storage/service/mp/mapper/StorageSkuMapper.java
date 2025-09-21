package com.medusa.gruul.storage.service.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.common.model.base.ActivityShopKey;
import com.medusa.gruul.common.model.base.ActivityShopProductKey;
import com.medusa.gruul.common.model.base.ActivityShopProductSkuKey;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.storage.api.dto.ShopProductKeyDTO;
import com.medusa.gruul.storage.api.entity.StorageSku;
import com.medusa.gruul.storage.api.vo.ProductSaleVolumeVO;
import com.medusa.gruul.storage.api.vo.ProductSkusVO;
import com.medusa.gruul.storage.api.vo.ProductStatisticsVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 商品 仓储 表 Mapper 接口
 *
 * @author 张治保
 * @since 2022-06-21
 */
public interface StorageSkuMapper extends BaseMapper<StorageSku> {


    /**
     * 批量查询商品统计
     *
     * @param shopProductKeys      店铺与商品ID集合
     * @param includeActivityStock 是否包含参加活动的商品
     * @return 查询结果
     */
    List<ProductStatisticsVO> getProductStatisticsList(@Param("includeActivityStock") boolean includeActivityStock, @Param("shopProductKeys") Set<ShopProductKey> shopProductKeys);

    /**
     * 获取店铺商品销量
     *
     * @param shopId 店铺id
     * @param size   查询数量
     * @return 店铺热销商品
     */
    List<ProductSaleVolumeVO> getShopProductSaleVolume(@Param("shopId") Long shopId, @Param("size") Long size);

    /**
     * 获取商品规格信息
     *
     * @param shopProductKeys 店铺与商品id集合
     * @return 商品规格信息
     */
    List<ProductSkusVO.SkuVO> getProductSkusByShopProductKeys(@Param("shopProductKeys") List<ShopProductKeyDTO> shopProductKeys);


    /**
     * 批量查询活动Sku数据
     *
     * @param keys 店铺活动商品sku key集合
     * @return 活动Sku数据
     */
    List<StorageSku> getSkus(@Param("keys") Set<ActivityShopProductSkuKey> keys);

    /**
     * 批量查询活动Sku数据 不包含商品id与sku id
     *
     * @param keys          店铺活动集合 不包含sku id
     * @param haveProductId 是否包含商品id
     * @return 活动Sku数据 每条记录只包含  活动类型 活动id 店铺id 商品 id sku id和库存数据
     */
    List<StorageSku> getSkusByPrefix(@Param("haveProductId") boolean haveProductId, @Param("keys") Set<ActivityShopKey> keys);


    /**
     * 批量删除活动Sku数据 不包含商品id与sku id
     *
     * @param keys 店铺活动集合 不包含 商品id与sku id
     */
    void deleteSkusByPrefix(@Param("keys") Set<ActivityShopKey> keys);

    /**
     * 获取店铺销量
     *
     * @param sortAsc 是否升序
     * @return 店铺销量
     */
    List<ProductStatisticsVO> getShopSales(Boolean sortAsc);

    /**
     * 获取店铺销量
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
     * 查询店铺限量无库存商品ids
     *
     * @param shopProductKey key
     * @return sku
     */
    List<StorageSku> shopEmptyStockProIds(@Param("key") ActivityShopProductKey shopProductKey);

    /**
     * 查询店铺不限量无库存商品ids
     *
     * @param shopProductKey key
     * @return shopIds
     */
    Set<Long> shopUnlimitProIds(@Param("key") ActivityShopProductKey shopProductKey);
}
