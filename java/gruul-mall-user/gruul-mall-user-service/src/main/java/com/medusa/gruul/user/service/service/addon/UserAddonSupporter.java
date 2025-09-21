package com.medusa.gruul.user.service.service.addon;

import com.medusa.gruul.common.addon.supporter.annotation.AddonMethod;
import com.medusa.gruul.common.addon.supporter.annotation.AddonSupporter;
import com.medusa.gruul.common.addon.supporter.helper.IAddon;
import com.medusa.gruul.user.api.enums.MemberType;
import com.medusa.gruul.user.api.model.vo.PaidMemberInfoVO;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用户插件Supporter
 *
 * @author xiaoq
 * @Description UserAddonSupporter.java
 * @date 2022-11-18 16:51
 */
@AddonSupporter(id = "userPaidMember")
public interface UserAddonSupporter extends IAddon {

    /**
     * 付费会员vo
     *
     * @param memberIds 会员ids
     * @return PaidMemberInfoVO.java
     * 
     * 插件实现服务 addon-member {@link com.medusa.gruul.addon.member.addon.impl.AddonPaidMemberProviderImpl#getPaidMemberInfo}
     */
    @AddonMethod(returnType = PaidMemberInfoVO.class)
    PaidMemberInfoVO getPaidMemberInfo(Set<Long> memberIds);

    /**
     * 获取付费会员
     *
     * @param paidMember 会员类型
     * @return 付费会员
     * 
     * 插件实现服务 addon-member {@link com.medusa.gruul.addon.member.addon.impl.AddonPaidMemberProviderImpl#paidMembers}
     */
    @AddonMethod(returnType = Map.class)
    Map<Integer, PaidMemberInfoVO> getPaidMembers(MemberType paidMember);

    /**
     * 根据会员权限查询有权限的会员id
     * @param rightId
     * @return 有查询权益的会员ID集合
     *
     * 插件实现服务 addon-member {@link com.medusa.gruul.addon.member.addon.impl.AddonPaidMemberProviderImpl#queryHasRightsMemberId}
     */
    @AddonMethod(returnType = List.class)
    List<Long> queryHasRightsMemberId(Long rightId);

    /**
     * 查询会员权益是否在使用中
     * @param rightId
     * @return
     * 插件实现服务 addon-member {@link com.medusa.gruul.addon.member.addon.impl.AddonPaidMemberProviderImpl#queryMemberRightInUse}
     */
    @AddonMethod(returnType = Boolean.class)
    Boolean queryMemberRightInUse(Long rightId);



}
