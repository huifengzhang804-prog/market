package com.medusa.gruul.addon.team.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.team.model.dto.TeamPageDTO;
import com.medusa.gruul.addon.team.model.vo.TeamPageVO;
import com.medusa.gruul.addon.team.model.vo.TeamProductVO;
import com.medusa.gruul.addon.team.mp.entity.TeamActivity;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 拼团活动表 Mapper 接口
 * </p>
 *
 * @author 张治保
 */
public interface TeamActivityMapper extends BaseMapper<TeamActivity> {

    /**
     * 分页查询团购活动
     *
     * @param query  分页查询参数
     * @param shopId 店铺id
     * @return 分页查询结果
     */
    IPage<TeamPageVO> activityPage(@Param("query") TeamPageDTO query, @Param("shopId") Long shopId);


    /**
     * 查询商品拼团状态
     *
     * @param shopId    店铺id
     * @param productId 商品id
     * @return 商品拼团状态
     */
    TeamProductVO productTeamStatus(@Param("shopId") Long shopId, @Param("productId") Long productId);
}