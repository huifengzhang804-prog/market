package com.medusa.gruul.addon.team.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.team.model.dto.*;
import com.medusa.gruul.addon.team.model.key.TeamKey;
import com.medusa.gruul.addon.team.model.vo.TeamOrderPageVO;
import com.medusa.gruul.addon.team.model.vo.TeamOrderSummaryVO;
import com.medusa.gruul.addon.team.model.vo.TeamOrderUserPageVO;
import com.medusa.gruul.addon.team.model.vo.TeamPageVO;
import com.medusa.gruul.overview.api.model.ActivityConfigDTO;
import io.vavr.control.Option;

import java.util.Set;

/**
 * @author 张治保
 * date 2023/3/2
 */
public interface TeamManagerService {
    /**
     * 新建团购活动
     *
     * @param shopId       店铺id
     * @param teamActivity 活动信息
     */
    void newActivity(Long shopId, TeamDTO teamActivity);


    /**
     * 分页查询团购活动
     *
     * @param shopIdOpt 可能为空的店铺id 为空表示平台查询
     * @param page      分页信息
     * @return 活动信息
     */
    IPage<TeamPageVO> activityPage(Option<Long> shopIdOpt, TeamPageDTO page);

    /**
     * 删除团购活动
     *
     * @param teamKeys 活动key集合
     */
    void deleteActivityBatch(Set<TeamKey> teamKeys);

    /**
     * 获取团购活动
     *
     * @param shopId     店铺id
     * @param activityId 活动id
     * @return 活动信息
     */
    TeamDTO getActivity(Long shopId, Long activityId);


    /**
     * 违规下架 拼团活动
     *
     * @param violationDTO     违规下架信息
     */
    void violateActivity(ViolationDTO violationDTO);

    /**
     * 分页查询拼团订单
     *
     * @param query 分页信息
     * @return 订单信息
     */
    IPage<TeamOrderPageVO> orderPage(TeamOrderPageDTO query);

    /**
     * 获取订单摘要信息
     *
     * @param summary 摘要查询条件
     * @return 摘要信息
     */
    TeamOrderSummaryVO getOrderSummary(TeamSummaryDTO summary);

    /**
     * 分页查询拼团订单用户列表
     *
     * @param query 分页信息
     * @return 分页查询结果
     */
    IPage<TeamOrderUserPageVO> orderUserPage(TeamOrderUserPageDTO query);

    /**
     * 用户 团购活动状态校验
     *
     * @param userId 用户id
     * @param teamNo 团购活动编号
     * @return true:成功 false:失败
     */
    boolean teamStatusValid(Long userId, String teamNo);

    /**
     * 店铺下架
     * @param teamActivityId 活动id
     */
    void shopShelfOff(Long teamActivityId);

    /**
     * 查询活动违规下架的原因
     * @param teamActivityId 活动id
     * @return 违规下架原因
     */
    String queryIllegalReason(Long teamActivityId);

    /**
     * 保存活动规则
     * @param activityConfigDTO 活动规则内容
     */
    void saveRule(ActivityConfigDTO activityConfigDTO);

    /**
     * 查活动规则
     * @return 活动规则
     */
    String queryRule();
}
