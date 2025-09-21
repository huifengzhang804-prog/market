package com.medusa.gruul.addon.live.mp.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.live.mp.entity.BaseLive;
import com.medusa.gruul.addon.live.mp.mapper.BaseLiveMapper;
import com.medusa.gruul.addon.live.mp.service.BaseLiveService;
import com.medusa.gruul.addon.live.param.LiveRoomParam;
import com.medusa.gruul.addon.live.vo.LiveAnchorVO;
import com.medusa.gruul.addon.live.vo.LiveRoomVO;
import org.springframework.stereotype.Service;

/**
 * @author miskw
 * @date 2023/6/5
 * @describe 描述
 */
@Service
public class BaseLiveServiceImpl extends ServiceImpl<BaseLiveMapper, BaseLive> implements BaseLiveService {
    @Override
    public IPage<LiveAnchorVO> liveAnchorList(Page<LiveAnchorVO> page, Long userId) {
        return baseMapper.liveAnchorList(page, userId);
    }

    @Override
    public IPage<LiveRoomVO> livePage(LiveRoomParam liveRoomParam) {
        return baseMapper.livePage(liveRoomParam);
    }

    @Override
    public boolean isBeginLive(Long userId) {
        return baseMapper.isBeginLive(userId);
    }

    @Override
    public LiveRoomVO detail(Long id) {
        return baseMapper.detail(id);
    }

    @Override
    public LiveRoomVO randomView(Long id) {
        return baseMapper.randomView(id);
    }

    @Override
    public BaseLive getLiveRoom(Long liveId, Long userId) {
        return baseMapper.getLiveRoom(liveId, userId);
    }
}
