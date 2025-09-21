package com.medusa.gruul.overview.service.mp.operate.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.overview.api.entity.DealRanking;
import com.medusa.gruul.overview.service.model.param.DealRankingParam;
import com.medusa.gruul.overview.service.model.vo.DealStatisticsVO;
import com.medusa.gruul.overview.service.model.vo.SalableProductTypeVO;
import com.medusa.gruul.overview.service.model.vo.SalableShopVO;

import java.util.List;

/**
 *
 * @author xiaoq
 * @Description
 * @date 2022-10-20 14:38
 */
public interface IDealRankingService extends IService<DealRanking> {
    List<SalableShopVO> getSalableShop(DealRankingParam dealRankingParam);

    List<DealStatisticsVO> getDealStatistics(DealRankingParam dealRankingParam);

    List<SalableProductTypeVO> getSalableProductMoney(DealRankingParam dealRankingParam);

    List<SalableProductTypeVO> getSalableProductNum(DealRankingParam dealRankingParam);

    /**
     * 移动端商家后台 店铺指定月份的交易概况
     *
     * @param dealRankingParam 指定月份
     * @return 店铺指定月份的交易概况
     */
    DealStatisticsVO getMobileDealStatistics(DealRankingParam dealRankingParam);
}
