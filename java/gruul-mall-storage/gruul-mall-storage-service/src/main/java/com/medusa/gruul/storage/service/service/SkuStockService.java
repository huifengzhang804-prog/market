package com.medusa.gruul.storage.service.service;

import com.medusa.gruul.common.model.base.ActivityShopProductKey;
import com.medusa.gruul.common.model.base.ActivityShopProductSkuKey;
import com.medusa.gruul.goods.api.model.dto.ProductDeleteDTO;
import com.medusa.gruul.storage.api.bo.StSvBo;
import com.medusa.gruul.storage.api.entity.StorageSku;
import com.medusa.gruul.storage.service.model.bo.UpdateStockOrder;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 商品仓储服务
 *
 * @author 张治保
 * date 2022/6/21
 */
public interface SkuStockService {


    /**
     * 批量更新sku缓存 库存与销量
     *
     * @param ignoreCacheNotExists 是否忽略不存在的sku key
     *                             true:忽略不存在的sku key
     *                             false:不存在的sku key 会报错提示
     * @param stSvMap              sku key与 库存销量
     */
    void updateSkuStockCache(boolean ignoreCacheNotExists, Map<ActivityShopProductSkuKey, StSvBo> stSvMap);

    /**
     * 批量更新数据库存与销量
     * 1。加锁 同时锁住多个key
     * 2。获取批量执行器 批量更新数据库
     *
     * @param skuKeyStSvs sku key与 库存销量
     * @author 张治保
     */
    void updateSkuStockDb(Map<ActivityShopProductSkuKey, StSvBo> skuKeyStSvs);

    /**
     * 删除库存
     *
     * @param productDeleteDTO 商品删除dto
     */
    void productDeleteStorageSku(ProductDeleteDTO productDeleteDTO);
    

    /**
     * 批量查询缓存库存 若库存中不存在 则直接查询数据库
     * 数据库的数据将批量放到缓存里 当前需要保证 返回的结果已被缓存
     *
     * @param keys 店铺商品sku关键信息
     * @return 库存列表
     */
    Map<ActivityShopProductSkuKey, StorageSku> getSkusBatch(Set<ActivityShopProductSkuKey> keys);


    /**
     * 批量查询商品sku库存
     *
     * @param shopProductKeys 店铺id与商品id 组成的key集合
     * @return sku库存列表
     */
    Map<ActivityShopProductKey, List<StorageSku>> productSkuStockBatch(Set<ActivityShopProductKey> shopProductKeys);


    /**
     * 查询指定店铺库存为空 或者无限库存的商品
     *
     * @param shopProductKey key
     * @param emptyStock     是否为空库存
     * @param unlimitStock   是否无限库存
     * @return 商品id集合
     */
    List<Long> queryShopEmptyStockProIds(ActivityShopProductKey shopProductKey, Boolean emptyStock, Boolean unlimitStock);


    /**
     * 更新库存信息
     *
     * @param ignoreCacheNotExists          是否忽略缓存中不存在的sku key
     *                                      true:忽略 false:不存在的sku key 会报错提示
     * @param possibleConsignmentOrActivity 是否可能是代销商品 或者活动商品
     *                                      代销商品需要同时处理供应商商品库存
     *                                      活动商品需要处理普通商品库存
     * @param updateStocks                  更新库存详情信息
     */
    void updateStock(boolean ignoreCacheNotExists, boolean possibleConsignmentOrActivity, List<UpdateStockOrder> updateStocks);
}
