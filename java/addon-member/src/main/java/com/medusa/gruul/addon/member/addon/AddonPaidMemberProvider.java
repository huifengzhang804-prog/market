package com.medusa.gruul.addon.member.addon;

import com.medusa.gruul.user.api.enums.MemberType;
import com.medusa.gruul.user.api.model.vo.PaidMemberInfoVO;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author xiaoq
 * @Description
 * @date 2022-11-17 13:56
 */
public interface AddonPaidMemberProvider {

    /**
     * 付费会员vo
     *
     * @param memberIds 付费会员级别ids
     * @return 付费会员vo
     */
    PaidMemberInfoVO getPaidMemberInfo(Set<Long> memberIds);

    /**
     * 获取付费会员
     *
     * @return 付费会员
     */
    Map<Integer, PaidMemberInfoVO> paidMembers(MemberType memberType);

    /**
     * 付费会员的最高等级
     *
     * @return rankCode
     */
    List<Integer> maxPaidMemberRankCode(Integer paidRankCode);

    /**
     * 根据权益ID查询哪些会员级别拥该权益id
     * @param rightId
     * @return
     */
    List<Long> queryHasRightsMemberId(Long rightId);

    /**
     * 查询会员权益是否在使用中
     * @param rightId
     * @return
     */
    Boolean queryMemberRightInUse(Long rightId);
}
