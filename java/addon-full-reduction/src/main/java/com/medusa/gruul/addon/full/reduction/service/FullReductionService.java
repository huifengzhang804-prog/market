package com.medusa.gruul.addon.full.reduction.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.full.reduction.model.dto.FullReductionDTO;
import com.medusa.gruul.addon.full.reduction.model.dto.FullReductionPageDTO;
import com.medusa.gruul.addon.full.reduction.model.dto.FullReductionShelfDTO;
import com.medusa.gruul.addon.full.reduction.model.vo.FullReductionDetailVO;
import com.medusa.gruul.addon.full.reduction.model.vo.FullReductionVO;
import com.medusa.gruul.common.model.base.CurrentActivityKey;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 张治保
 * @since 2024/6/15
 */
public interface FullReductionService {

    /**
     * 保存或更新满减活动
     *
     * @param shopId 店铺 id
     * @param param  满减活动参数
     */
    void saveOrUpdate(Long shopId, FullReductionDTO param);


    /**
     * 分页查询满减活动
     *
     * @param param 查询参数
     * @return 满减活动分页结果
     */
    IPage<FullReductionVO> page(FullReductionPageDTO param);

    /**
     * 满减活动详情
     *
     * @param key 活动key
     * @return 满减活动详情
     */
    FullReductionDetailVO detail(CurrentActivityKey key);

    /**
     * 批量删除满减活动
     *
     * @param shopId      店铺id
     * @param activityIds 满减活动 id
     */
    void batchDelete(Long shopId, Set<Long> activityIds);

    /**
     * 满减活动下架
     *
     * @param param 下架参数
     */
    void shelf(boolean isShop, FullReductionShelfDTO param);

    /**
     * 获取店铺的满减规则标签列表
     *
     * @param shopIds 店铺id集合
     * @return 店铺的满减规则标签列表 key为店铺id value为满减规则标签列表
     */
    Map<Long, List<String>> shopRuleLabels(Set<Long> shopIds);
}
