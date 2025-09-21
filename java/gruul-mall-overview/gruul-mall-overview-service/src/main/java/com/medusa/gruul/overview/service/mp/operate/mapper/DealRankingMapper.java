package com.medusa.gruul.overview.service.mp.operate.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.overview.api.entity.DealRanking;
import com.medusa.gruul.overview.service.model.param.DealRankingParam;
import com.medusa.gruul.overview.service.model.vo.DealStatisticsVO;
import com.medusa.gruul.overview.service.model.vo.SalableProductTypeVO;
import com.medusa.gruul.overview.service.model.vo.SalableShopVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *
 * @author xiaoq
 * @Description
 * @date 2022-10-20 14:40
 */
public interface DealRankingMapper extends BaseMapper<DealRanking> {

    /**
     *  获取热销店铺
     *
     * @param dealRankingParam 交易排行查询条件
     * @return   List<SalableShopVO>
     */
    List<SalableShopVO> querySalableShop(@Param("dealRankingParam") DealRankingParam dealRankingParam);


    /**
     * 获取交易排行信息
     * @param dealRankingParam 交易排行查询条件
     * @return  List<DealStatisticsVO>
     */
    List<DealStatisticsVO> getDealStatistics(@Param("dealRankingParam") DealRankingParam dealRankingParam);


    /**
     * 获取热销商品销售额排行信息
     *
     * @param dealRankingParam 交易排行查询条件
     * @return List<SalableProductTypeVO>
     */
    List<SalableProductTypeVO> getSalableProductMoney(@Param("dealRankingParam") DealRankingParam dealRankingParam);

    /**
     * 获取热销商品销量排行信息
     *
     * @param dealRankingParam 交易排行查询条件
     * @return  List<SalableProductTypeVO>
     */
    List<SalableProductTypeVO> getSalableProductNum(@Param("dealRankingParam") DealRankingParam dealRankingParam);

    /**
     * 移动端商家后台 店铺指定月份的交易概况
     *
     * @param dealRankingParam 指定月份
     * @return 店铺指定月份的交易概况
     */
    DealStatisticsVO getMobileDealStatistics(@Param("dealRankingParam") DealRankingParam dealRankingParam);
}
