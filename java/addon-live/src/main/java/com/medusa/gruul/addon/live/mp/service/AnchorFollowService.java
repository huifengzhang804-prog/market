package com.medusa.gruul.addon.live.mp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.addon.live.mp.entity.AnchorFollow;
import com.medusa.gruul.addon.live.param.FollowLiveParam;
import com.medusa.gruul.addon.live.vo.FollowLiveVO;

/**
 * @author miskw
 * @date 2023/6/5
 * @describe 描述
 */
public interface AnchorFollowService extends IService<AnchorFollow> {
    IPage<FollowLiveVO> followLiveList(FollowLiveParam followLiveParam, Long userId);

    IPage<FollowLiveVO> discoverLiveList(FollowLiveParam followLiveParam, Long userId);
}
