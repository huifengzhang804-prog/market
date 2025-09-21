package com.medusa.gruul.addon.live.mp.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.live.mp.entity.AnchorFollow;
import com.medusa.gruul.addon.live.mp.mapper.AnchorFollowMapper;
import com.medusa.gruul.addon.live.mp.service.AnchorFollowService;
import com.medusa.gruul.addon.live.param.FollowLiveParam;
import com.medusa.gruul.addon.live.vo.FollowLiveVO;
import org.springframework.stereotype.Service;

/**
 * @author miskw
 * @date 2023/6/5
 * @describe 描述
 */
@Service
public class AnchorFollowServiceImpl extends ServiceImpl<AnchorFollowMapper, AnchorFollow> implements AnchorFollowService {
    @Override
    public IPage<FollowLiveVO> followLiveList(FollowLiveParam followLiveParam, Long userId) {
        return baseMapper.followLiveList(followLiveParam, userId);
    }

    @Override
    public IPage<FollowLiveVO> discoverLiveList(FollowLiveParam followLiveParam, Long userId) {
        return baseMapper.discoverLiveList(followLiveParam, userId);
    }
}
