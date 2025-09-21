package com.medusa.gruul.addon.live.mp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.addon.live.mp.entity.BaseLive;
import com.medusa.gruul.addon.live.param.LiveRoomParam;
import com.medusa.gruul.addon.live.vo.LiveAnchorVO;
import com.medusa.gruul.addon.live.vo.LiveRoomVO;

/**
 * @author miskw
 * @date 2023/6/5
 * @describe 描述
 */
public interface BaseLiveService extends IService<BaseLive> {
    IPage<LiveAnchorVO> liveAnchorList(Page<LiveAnchorVO> page, Long userId);

    /**
     * 管理端直播间列表
     * @param liveRoomParam 直播间查询参数
     */
    IPage<LiveRoomVO> livePage(LiveRoomParam liveRoomParam);

    /**
     * 查询主播是否已经有开播的直播间
     * @param userId 用户id
     * @return 是否有开播的直播间
     */
    boolean isBeginLive(Long userId);

    /**
     * 直播间详情
     * @param id 直播间id
     * @return 直播间详情
     */
    LiveRoomVO detail(Long id);

    /**
     * 用户随机获取一条直播间信息
     *
     * @param id 直播间id
     * @return 直播间信息
     */
    LiveRoomVO randomView(Long id);

    /**
     * 根据直播间id与用户id获取直播间信息
     * @param liveId 直播间id
     * @param userId 用户id
     * @return 直播间信息
     */
    BaseLive getLiveRoom(Long liveId, Long userId);
}
