package com.medusa.gruul.overview.service.mp.operate.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.overview.api.entity.DealRanking;
import com.medusa.gruul.overview.service.model.param.DealRankingParam;
import com.medusa.gruul.overview.service.model.vo.DealStatisticsVO;
import com.medusa.gruul.overview.service.model.vo.SalableProductTypeVO;
import com.medusa.gruul.overview.service.model.vo.SalableShopVO;
import com.medusa.gruul.overview.service.mp.operate.mapper.DealRankingMapper;
import com.medusa.gruul.overview.service.mp.operate.service.IDealRankingService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author xiaoq
 * @Description
 * @date 2022-10-20 14:40
 */
@Service
public class DealRankingServiceImpl extends ServiceImpl<DealRankingMapper, DealRanking> implements IDealRankingService {
    @Override
    public List<SalableShopVO> getSalableShop(DealRankingParam dealRankingParam) {
       return this.baseMapper.querySalableShop(dealRankingParam);
    }

    @Override
    public List<DealStatisticsVO> getDealStatistics(DealRankingParam dealRankingParam) {
        return this.baseMapper.getDealStatistics(dealRankingParam);
    }

    @Override
    public List<SalableProductTypeVO> getSalableProductMoney(DealRankingParam dealRankingParam) {
        return this.baseMapper.getSalableProductMoney(dealRankingParam);
    }

    @Override
    public List<SalableProductTypeVO> getSalableProductNum(DealRankingParam dealRankingParam) {
        return this.baseMapper.getSalableProductNum(dealRankingParam);
    }
    /**
     * 移动端商家后台 店铺指定月份的交易概况
     *
     * @param dealRankingParam 指定月份
     * @return 店铺指定月份的交易概况
     */
    @Override
    public DealStatisticsVO getMobileDealStatistics(DealRankingParam dealRankingParam) {
        return this.baseMapper.getMobileDealStatistics(dealRankingParam);
    }
}
