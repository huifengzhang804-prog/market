package com.medusa.gruul.user.service.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.user.api.model.vo.RelevancyRightsVO;
import com.medusa.gruul.user.service.mp.entity.MemberRights;
import com.medusa.gruul.user.service.mp.mapper.MemberRightsMapper;
import com.medusa.gruul.user.service.mp.service.IMemberRightsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * 服务实现层
 *
 * @author xiaoq
 * @Description MemberRightsServiceImpl.java
 * @date 2022-11-09 15:19
 */
@Service
public class MemberRightsServiceImpl extends ServiceImpl<MemberRightsMapper, MemberRights> implements IMemberRightsService {
    @Override
    public List<RelevancyRightsVO> getRightByIds(Set<Long> ids) {
        return this.baseMapper.queryRightByIds(ids);
    }

    @Override
    public Boolean checkRightInUse(Long rightId) {
        Integer count = baseMapper.checkRightInUse(rightId);
        return count>0;
    }


}
