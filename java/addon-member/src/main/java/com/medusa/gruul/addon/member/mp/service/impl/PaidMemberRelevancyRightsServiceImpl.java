package com.medusa.gruul.addon.member.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.member.mp.entity.PaidMemberRelevancyRights;
import com.medusa.gruul.addon.member.mp.mapper.PaidMemberRelevancyRightsMapper;
import com.medusa.gruul.addon.member.mp.service.IPaidMemberRelevancyRightsService;
import com.medusa.gruul.user.api.model.vo.MemberBasicsRelevancyRightsVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author xiaoq
 * @Description
 * @date 2022-11-15 14:20
 */
@Service
public class PaidMemberRelevancyRightsServiceImpl extends ServiceImpl<PaidMemberRelevancyRightsMapper, PaidMemberRelevancyRights> implements IPaidMemberRelevancyRightsService {
    @Override
    public List<MemberBasicsRelevancyRightsVO> getRightsList(Long id) {
        return  this.baseMapper.queryRightsList(id);
    }

    @Override
    public Boolean queryMemberRightInUse(Long rightId) {
         Integer count=baseMapper.queryMemberRightInUse(rightId);
         return count>0;
    }
}
