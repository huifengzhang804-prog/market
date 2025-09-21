package com.medusa.gruul.addon.team.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.team.model.count.TeamOrderCount;
import com.medusa.gruul.addon.team.model.dto.TeamOrderPageDTO;
import com.medusa.gruul.addon.team.model.dto.TeamSummaryDTO;
import com.medusa.gruul.addon.team.model.key.TeamKey;
import com.medusa.gruul.addon.team.model.vo.TeamOrderPageVO;
import com.medusa.gruul.addon.team.model.vo.TeamOrderSummaryVO;
import com.medusa.gruul.addon.team.mp.entity.TeamOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 拼团订单表 Mapper 接口
 * </p>
 *
 * @author 张治保
 */
public interface TeamOrderMapper extends BaseMapper<TeamOrder> {

    /**
     * 获取拼团订单统计数据
     *
     * @param keys 拼团订单key集合
     * @return 拼团订单统计数据
     */
    List<TeamOrderCount> teamOrderCounts(@Param("keys") Set<TeamKey> keys);

    /**
     * 分页查询拼团订单
     *
     * @param query 分页信息
     * @return 订单信息
     */
    IPage<TeamOrderPageVO> orderPage(@Param("query") TeamOrderPageDTO query);

    /**
     * 获取拼团订单摘要信息
     *
     * @param summaryQuery 查询条件
     * @return 拼团订单摘要信息
     */
    TeamOrderSummaryVO getOrderSummary(@Param("query") TeamSummaryDTO summaryQuery);
}
