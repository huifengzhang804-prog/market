package com.medusa.gruul.addon.team.mp.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.team.model.dto.TeamPageDTO;
import com.medusa.gruul.addon.team.model.vo.TeamPageVO;
import com.medusa.gruul.addon.team.model.vo.TeamProductVO;
import com.medusa.gruul.addon.team.mp.entity.TeamActivity;
import com.medusa.gruul.addon.team.mp.mapper.TeamActivityMapper;
import com.medusa.gruul.addon.team.mp.service.ITeamActivityService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 拼团活动表 服务实现类
 * </p>
 *
 * @author 张治保
 */
@Service
public class TeamActivityServiceImpl extends ServiceImpl<TeamActivityMapper, TeamActivity> implements ITeamActivityService {

    @Override
    public IPage<TeamPageVO> activityPage(TeamPageDTO page, Long shopId) {
        return baseMapper.activityPage(page, shopId);
    }

    @Override
    public TeamProductVO productTeamStatus(Long shopId, Long productId) {
        return baseMapper.productTeamStatus(shopId, productId);
    }
}
