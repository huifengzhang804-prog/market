package com.medusa.gruul.addon.member.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.addon.member.model.vo.PaidMemberRightsVO;
import com.medusa.gruul.addon.member.mp.entity.PaidMember;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * <p>
 *
 * </p>
 *
 * @author xiaoq
 * @Description
 * @date 2022-11-15 13:31
 */
public interface PaidMemberMapper extends BaseMapper<PaidMember> {

    /**
     * 付费会员级别信息
     *
     * @return List<付费会员权益VO>
     */
    List<PaidMemberRightsVO> getPaidMemberList();

    /**
     * 根据memberIds 查看是否存在不可以用会员信息(会员级别删除)
     *
     * @param memberIds 会员ids
     * @return 不可用会员ids
     */
    List<Long> getCloseMemberByIds(@Param("memberIds") Set<Long> memberIds);


    /**
     * 付费会员级别信息
     *
     * @param id 付费会员级别id
     * @return PaidMemberRightsVO.java
     */
    PaidMemberRightsVO getPaidMemberInfo(@Param("id") Long id);
}
