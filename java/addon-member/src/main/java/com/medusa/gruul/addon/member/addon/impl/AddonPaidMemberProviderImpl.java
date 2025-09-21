package com.medusa.gruul.addon.member.addon.impl;

import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.addon.member.addon.AddonPaidMemberProvider;
import com.medusa.gruul.addon.member.mp.entity.PaidMember;
import com.medusa.gruul.addon.member.mp.entity.PaidMemberRelevancyRights;
import com.medusa.gruul.addon.member.mp.service.IPaidMemberRelevancyRightsService;
import com.medusa.gruul.addon.member.mp.service.IPaidMemberService;
import com.medusa.gruul.common.addon.provider.AddonProvider;
import com.medusa.gruul.common.addon.provider.AddonProviders;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.common.mp.model.SqlHelper;
import com.medusa.gruul.global.model.constant.Services;
import com.medusa.gruul.user.api.enums.MemberType;
import com.medusa.gruul.user.api.model.vo.PaidMemberInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author xiaoq
 * Description AddonPaidMemberProviderImpl, java
 * date 2022-11-17 13:57
 */
@Slf4j
@AddonProviders
@DubboService
@Service
@RequiredArgsConstructor
public class AddonPaidMemberProviderImpl implements AddonPaidMemberProvider {

    private final IPaidMemberService paidMemberService;

    private final IPaidMemberRelevancyRightsService paidMemberRelevancyRightsService;

    /**
     * 付费会员vo
     *
     * @param memberIds 付费会员级别ids
     * @return 付费会员vo
     */
    @Override
    @AddonProvider(service = Services.GRUUL_MALL_USER, supporterId = "userPaidMember", methodName = "getPaidMemberInfo", order = 1)
    public PaidMemberInfoVO getPaidMemberInfo(Set<Long> memberIds) {
        if (CollUtil.isEmpty(memberIds)) {
            return null;
        }
        //获取最高等级的会员
        PaidMember paidMember = paidMemberService.lambdaQuery()
                .in(BaseEntity::getId, memberIds)
                .orderByDesc(PaidMember::getRankCode)
                .last(SqlHelper.SQL_LIMIT_1)
                .one();
        if (paidMember == null) {
            return null;
        }
        return new PaidMemberInfoVO()
                .setRankCode(paidMember.getRankCode())
                //获取已过期的或删除的会员信息
                .setMemberCloseIds(paidMemberService.getCloseMemberByIds(memberIds))
                //获取会员权限
                .setMemberBasicsRelevancyRightsList(paidMemberRelevancyRightsService.getRightsList(paidMember.getId()))
                .setMemberId(paidMember.getId())
                .setPaidMemberName(paidMember.getPaidMemberName())
                .setMemberLabel(paidMember.getLabelJson());
    }

    @Override
    @AddonProvider(service = Services.GRUUL_MALL_USER, supporterId = "userPaidMember", methodName = "getPaidMembers", order = 1)
    public Map<Integer, PaidMemberInfoVO> paidMembers(MemberType memberType) {
        List<PaidMember> paidMembers = paidMemberService.lambdaQuery()
                .orderByAsc(PaidMember::getRankCode)
                .list();
        if (CollUtil.isEmpty(paidMembers)) {
            return Collections.emptyMap();
        }
       return paidMembers.stream()
                .map(paidMember -> new PaidMemberInfoVO()
                        .setMemberId(paidMember.getId())
                        .setRankCode(paidMember.getRankCode())
                        .setPaidMemberName(paidMember.getPaidMemberName())
                        .setPaidRuleJson(paidMember.getPaidRuleJson()))
                .toList()
                .stream()
                .collect(Collectors.toMap(PaidMemberInfoVO::getRankCode, paidMemberInfoVO -> paidMemberInfoVO));
    }

    /**
     * 付费会员的最高等级
     *
     * @return rankCode
     */
    @Override
    @AddonProvider(service = Services.GRUUL_MALL_UAA, supporterId = "uaaPaidMember", methodName = "getMaxPaidMemberRankCode", order = 3)
    public List<Integer> maxPaidMemberRankCode(Integer paidRankCode) {
        return paidMemberService.lambdaQuery()
                .select(PaidMember::getRankCode)
                .list().stream()
                .map(PaidMember::getRankCode)
                .collect(Collectors.toList());
    }

    /**
     * 根据权益id查询何种会员拥有这权益 返回会员id
     * @param rightId
     * @return
     */
    @Override
    @AddonProvider(service = Services.GRUUL_MALL_USER, supporterId = "userPaidMember", methodName = "queryHasRightsMemberId", order = 1)
    public List<Long> queryHasRightsMemberId(Long rightId) {
        return  paidMemberRelevancyRightsService.lambdaQuery()
                .select(PaidMemberRelevancyRights::getPaidMemberId)
                .eq(PaidMemberRelevancyRights::getDeleted, Boolean.FALSE)
                .eq(PaidMemberRelevancyRights::getMemberRightsId, rightId)
                .list()
                .stream()
                .map(PaidMemberRelevancyRights::getPaidMemberId)
                .collect(Collectors.toList());

    }

    @Override
    @AddonProvider(service = Services.GRUUL_MALL_USER, supporterId = "userPaidMember", methodName = "queryMemberRightInUse", order = 1)
    public Boolean queryMemberRightInUse(Long rightId) {
        return paidMemberRelevancyRightsService.queryMemberRightInUse(rightId);
    }


}
