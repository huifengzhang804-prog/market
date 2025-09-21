package com.medusa.gruul.addon.team.mp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.addon.team.model.dto.TeamPageDTO;
import com.medusa.gruul.addon.team.model.vo.TeamPageVO;
import com.medusa.gruul.addon.team.model.vo.TeamProductVO;
import com.medusa.gruul.addon.team.mp.entity.TeamActivity;

/**
 * 拼团活动表 服务类
 *
 * @author 张治保
 */
public interface ITeamActivityService extends IService<TeamActivity> {

    /**
     * 分页查询团购活动
     *
     * @param page   分页信息
     * @param shopId 店铺id
     * @return 分页查询结果
     */
    IPage<TeamPageVO> activityPage(TeamPageDTO page, Long shopId);

    /**
     * 查询商品拼团状态
     *
     * @param shopId    店铺id
     * @param productId 商品id
     * @return 商品拼团状态
     */
    TeamProductVO productTeamStatus(Long shopId, Long productId);


}