package com.medusa.gruul.addon.team.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.team.model.dto.TeamProductOrderPageDTO;
import com.medusa.gruul.addon.team.model.vo.TeamActivityProductVO;
import com.medusa.gruul.addon.team.model.vo.TeamOrderPageVO;
import com.medusa.gruul.addon.team.model.vo.TeamProductVO;

/**
 * @author 张治保
 * date 2023/3/15
 */
public interface TeamConsumerService {

    /**
     * 查询商品拼团状态
     *
     * @param shopId    店铺id
     * @param productId 商品id
     * @return 商品拼团状态
     */
    TeamProductVO productTeamStatus(Long shopId, Long productId);

    /**
     * 分页查询商品凑团订单列表
     *
     * @param query 查询条件
     * @return 分页结果数据
     */
    IPage<TeamOrderPageVO> teamProductOrderPage(TeamProductOrderPageDTO query);


    /**
     * 分页查询拼团活动商品列表
     *
     * @param query 分页参数
     * @return 分页结果数据
     */
    IPage<TeamActivityProductVO> activityProductPage(Page<TeamActivityProductVO> query);
}