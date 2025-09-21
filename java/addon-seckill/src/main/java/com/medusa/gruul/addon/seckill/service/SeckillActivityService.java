package com.medusa.gruul.addon.seckill.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.seckill.model.dto.*;
import com.medusa.gruul.addon.seckill.model.vo.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * 秒杀活动 service
 *
 * @author 张治保
 * @since 2024/5/28
 */
public interface SeckillActivityService {


    /**
     * 获取该日期可用场次 及每场场次的开始和结束时间
     *
     * @param date 日期
     * @return 可用场次
     */
    List<RoundVO> rounds(LocalDate date);

    /**
     * 创建秒杀活动
     *
     * @param shopId   店铺id
     * @param activity 秒杀活动信息
     */
    void create(Long shopId, SeckillActivityDTO activity);

    /**
     * 分页查询秒杀活动
     *
     * @param query 查询条件
     * @return 分页数据
     */
    IPage<SeckillActivityVO> page(SeckillActivityQueryDTO query);

    /**
     * 查询秒杀活动详情
     *
     * @param shopId     店铺id
     * @param activityId 活动id
     * @return 秒杀活动详情
     */
    SeckillActivityDetailVO detail(Long shopId, Long activityId);

    /**
     * 分页查询秒杀活动场次 按开始时间从小到大排序
     *
     * @param page 分页查询参数
     * @return 分页查询结果
     */
    IPage<SeckillRoundVO> roundPage(SeckillRoundPageDTO page);

    /**
     * 分页查询活动场次对应商品
     *
     * @param page 分页查询参数
     * @return 分页查询结果
     */
    IPage<SeckillRoundProductVO> roundProductPage(SeckillRoundProductPageDTO page);

    /**
     * 秒杀活动下架
     *
     * @param isShop   是否店铺自己下架
     * @param offShelf 下架信息
     */
    void offShelf(boolean isShop, OffShelfDTO offShelf);

    /**
     * 批量删除秒杀活动
     *
     * @param shopId      店铺id
     * @param activityIds 活动 id 集合
     */
    void delete(Long shopId, Set<Long> activityIds);
}
