package com.medusa.gruul.user.service.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.user.service.model.vo.FreeMemberRightsVO;
import com.medusa.gruul.user.api.model.vo.RelevancyRightsVO;
import com.medusa.gruul.user.service.mp.entity.UserFreeMember;
import com.medusa.gruul.user.service.mp.mapper.UserFreeMemberMapper;
import com.medusa.gruul.user.service.mp.service.IUserFreeMemberService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 服务实现层
 *
 * @author xiaoq
 * @Description UserFreeMemberServiceImpl.java
 * @date 2022-11-10 18:52
 */
@Service
public class UserFreeMemberServiceImpl extends ServiceImpl<UserFreeMemberMapper, UserFreeMember> implements IUserFreeMemberService {


    @Override
    public List<FreeMemberRightsVO> getFreeMemberList() {
        return  this.baseMapper.queryFreeMemberList();
    }


    @Override
    public FreeMemberRightsVO getCurrentMemberRank(Long growthValue) {
        return  this.baseMapper.getCurrentMemberRankByGrowthValue(growthValue);
    }

    @Override
    public  List<RelevancyRightsVO>  getRelevancyRights(Long id){
        return this.baseMapper.queryRelevancyRights(id);
    }

    @Override
    public FreeMemberRightsVO getFreeMemberInfo(Long id) {
        return this.baseMapper.queryFreeMemberInfo(id);
    }

}
