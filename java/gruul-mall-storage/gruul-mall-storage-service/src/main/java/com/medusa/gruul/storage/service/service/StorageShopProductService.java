package com.medusa.gruul.storage.service.service;

import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.storage.api.vo.ProductSpecsSkusVO;
import com.medusa.gruul.storage.api.vo.ProductStatisticsVO;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p 商品统计 服务
 *
 * @author 张治保 date 2022/7/14
 */
public interface StorageShopProductService {


    /**
     * 查询商品统计信息
     *
     * @param shopId    店铺id
     * @param productId 商品id
     * @return 商品统计信息
     */
    ProductStatisticsVO getProductStatistics(Long shopId, Long productId);

    /**
     * 批量查询商品统计
     *
     * @param includeActivityStock 是否包含参加活动的商品
     * @param shopProductKeys      店铺与商品ID集合
     * @return 查询结果
     */
    List<ProductStatisticsVO> getProductStatisticsList(boolean includeActivityStock,
            Set<ShopProductKey> shopProductKeys);

    /**
     * 查询商品规格sku信息
     *
     * @param shopId     店铺id
     * @param productId  商品id
     * @param isSupplier 是否查询供应商商品sku
     * @return 商品统计信息
     */
    ProductSpecsSkusVO getProductSpecsSkus(Long shopId, Long productId, boolean isSupplier);

    /**
     * 查询店铺商品在售数量
     *
     * @param shopProductKeys 店铺与商品ID集合
     * @return 店铺商品在售数量
     */
    Map<Long, Long> queryShopHasOnSaleProducts(Map<Long, List<Long>> shopProductIdsMap);
}
