package com.medusa.gruul.search.api.rpc;

import com.medusa.gruul.search.api.model.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 张治保 date 2023/3/22
 */
public interface SearchRpcService {


    /**
     * 获取能正常购买商品的店铺店铺 id
     */
    Set<Long> getSellProductShopIds();

    /**
     * 商品活动绑定
     *
     * @param bindParam 绑定参数
     */
    void activityBind(@Valid ProductActivityBind bindParam);

    /**
     * 批量 商品活动解绑
     *
     * @param unbindParams 解绑参数
     */
    void activityUnbind(@Valid Set<ProductActivityUnbind> unbindParams);

    /**
     * 修改商品是否在分销列表状态
     *
     * @param productDistributed 参数
     */
    void updateProduct(@Valid ProductDistributed productDistributed);

    /**
     * 查询商品最近有库存的活动
     *
     * @param shopId    店铺id
     * @param productId 商品id
     * @return 活动信息
     */
    ProductActivityVO getRecentActivity(Long shopId, Long productId);

    /**
     * 分类商品数统计
     *
     * @param param 统计参数
     * @return 分类商品数统计 key 分类 id（一级｜二级 ｜ 三级），value 商品数
     */
    Map<Long, CategoryStaticVo> categoryCount(@Valid CategoryCountParam param);


    /**
     * 查询每个店铺 销量排行前几（salesRanking）的商品
     *
     * @param shopIds      店铺 id 集合
     * @param salesRanking 销量排行前几
     * @return 每个店铺 销量排行前几（salesRanking）的商品
     */
    Map<Long, List<ProductVO>> salesRanking(Set<Long> shopIds, Integer salesRanking);

    /**
     * 查询存在上架商品的店铺ids
     * @param shopIds 待查询的店铺ids
     * @return
     */
    List<Long> queryHasSellOnProductShopIds(List<Long> shopIds);
}
