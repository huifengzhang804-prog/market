package com.medusa.gruul.addon.member.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.addon.member.model.vo.PaidMemberRightsVO;
import com.medusa.gruul.addon.member.mp.entity.PaidMember;

import java.util.List;
import java.util.Set;

/**
 *
 *
 * @author xiaoq
 * @Description IPaidMemberService.java
 * @date 2022-11-15 13:29
 */
public interface IPaidMemberService extends IService<PaidMember> {
    /**
     * 获取付费会员列表信息
     * @return  List<PaidMemberRightsVO>
     */
    List<PaidMemberRightsVO> getPaidMemberList();

    /**
     * 获取不可以会员级别id
     *
     * @param memberIds 会员ids
     * @return 不可用会员ids
     */
    List<Long> getCloseMemberByIds(Set<Long> memberIds);

    /**
     * 根据付费会员级别id获取付费会员信息
     *
     * @param id 付费会员级别id
     * @return PaidMemberRightsVO.java
     */
    PaidMemberRightsVO getPaidMemberInfo(Long id);
}
