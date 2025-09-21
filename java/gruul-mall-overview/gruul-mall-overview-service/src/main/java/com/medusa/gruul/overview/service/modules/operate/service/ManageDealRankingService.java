package com.medusa.gruul.overview.service.modules.operate.service;

import com.medusa.gruul.order.api.entity.ShopOrderItem;
import com.medusa.gruul.overview.service.model.param.DealRankingParam;
import com.medusa.gruul.overview.service.model.vo.DealStatisticsVO;
import com.medusa.gruul.overview.service.model.vo.SalableProductTypeVO;
import com.medusa.gruul.overview.service.model.vo.SalableShopVO;

import java.util.List;

/**
 * @author xiaoq
 * @Description
 * @date 2022-10-20 14:17
 */
public interface ManageDealRankingService {

    /**
     * 交易排行变动
     *
     * @param shopOrderItems 完成订单内的商品信息
     */
    void dealRankingChange(List<ShopOrderItem> shopOrderItems);

    /**
     * 获取畅销店铺TOP10
     *
     * @param dealRankingParam 交易排行查询param
     * @return List<SalableShopVO>
     */
    List<SalableShopVO> getSalableShop(DealRankingParam dealRankingParam);

    /**
     * 获取交易排行信息
     *
     * @param dealRankingParam 交易排行查询param
     * @return List<DealStatisticsVO>
     */
    List<DealStatisticsVO> getDealStatistics(DealRankingParam dealRankingParam);


    /**
     * 获取畅销商品销售额排行信息
     *
     * @param dealRankingParam 交易排行查询param
     * @return List<SalableProductTypeVO>
     */
    List<SalableProductTypeVO> getSalableProductMoney(DealRankingParam dealRankingParam);

    /**
     * 获取畅销商品销量排行信息
     *
     * @param dealRankingParam 交易排行查询param
     * @return List<SalableProductTypeVO>
     */
    List<SalableProductTypeVO> getSalableProductNum(DealRankingParam dealRankingParam);

    /**
     * 移动端商家后台 店铺指定月份的交易概况
     *
     * @param dealRankingParam 指定月份
     * @return 店铺指定月份的交易概况
     */
    DealStatisticsVO getMobileDealStatistics(DealRankingParam dealRankingParam);


}
