package com.medusa.gruul.goods.service.service;

import com.medusa.gruul.common.model.base.ShopProductKey;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 商品销量排行榜 服务类
 * todo 暂未使用
 *
 * @author 张治保
 * @since 2024/5/22
 */
public interface ProductSalesRankService {


    /**
     * 增加商品销量
     *
     * @param productSalesMap 商品销量
     */
    void incrSales(Map<ShopProductKey, Long> productSalesMap);


    /**
     * 从排行榜移除
     *
     * @param productKeys 商品key
     */
    void removeSales(Set<ShopProductKey> productKeys);

    /**
     * 获取销量排行榜
     *
     * @param size 获取数量
     * @return 销量排行榜
     */
    LinkedHashMap<ShopProductKey, Long> platformSalesRank(Integer size);


    /**
     * 获取店铺销量排行榜
     *
     * @param shopId 店铺id
     * @param size   获取数量
     * @return 销量排行榜
     */
    LinkedHashMap<Long, Long> shopSalesRank(Long shopId, Integer size);
}
