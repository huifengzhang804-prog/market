package com.medusa.gruul.addon.member.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.member.model.vo.PaidMemberRightsVO;
import com.medusa.gruul.addon.member.mp.entity.PaidMember;
import com.medusa.gruul.addon.member.mp.mapper.PaidMemberMapper;
import com.medusa.gruul.addon.member.mp.service.IPaidMemberService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * <p>
 *
 * </p>
 *
 * @author xiaoq
 * @Description
 * @date 2022-11-15 13:30
 */
@Service
public class PaidMemberServiceImpl extends ServiceImpl<PaidMemberMapper, PaidMember> implements IPaidMemberService {
    @Override
    public List<PaidMemberRightsVO> getPaidMemberList() {
        return this.baseMapper.getPaidMemberList();
    }

    @Override
    public List<Long> getCloseMemberByIds(Set<Long> memberIds) {
        return this.baseMapper.getCloseMemberByIds(memberIds);
    }

    @Override
    public PaidMemberRightsVO getPaidMemberInfo(Long id) {

        return this.baseMapper.getPaidMemberInfo(id);
    }
}
