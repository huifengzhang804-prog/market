package com.medusa.gruul.addon.live.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.live.model.PreviewLiveRoomDTO;
import com.medusa.gruul.addon.live.param.LiveRoomParam;
import com.medusa.gruul.addon.live.vo.LiveAnchorVO;
import com.medusa.gruul.addon.live.vo.LiveRoomMessageVO;
import com.medusa.gruul.addon.live.vo.LiveRoomVO;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

/**
 * @author miskw
 * @date 2023/6/5
 * @describe 描述
 */
public interface AddonLiveRoomService {
    /**
     * 创建直播间
     *
     * @param previewLiveRoom 创建直播间参数
     */
    LiveRoomMessageVO createLiveRoom(PreviewLiveRoomDTO previewLiveRoom);

    /**
     * 直播回调
     */
    void liveNotify(HttpServletRequest request) throws IOException;

    IPage<LiveAnchorVO> liveAnchorList(Page<LiveAnchorVO> page, Long userId);

    /**
     * 删除直播间
     *
     * @param liveId 直播间id
     * @param userId 用户id
     */
    void deletedLiveById(Long liveId, Long userId);

    /**
     * 管理端直播间列表
     *
     * @param liveRoomParam 直播间查询参数
     */
    IPage<LiveRoomVO> livePage(LiveRoomParam liveRoomParam);

    /**
     * 查询是否有已经开播的直播间
     *
     * @param userId 用户id
     * @param liveId 直播间id
     * @return 是否有已经开播的直播间
     */
    Boolean beginLive(Long userId, Long liveId);

    /**
     * 直播间详情
     *
     * @param id 直播间id
     * @return 直播间详情
     */
    LiveRoomVO detail(Long id);

    Long shopId(Long userId);

    /**
     * 直播间下播
     *
     * @param liveId 直播间id
     * @param userId 用户id
     */
    void lowerBroadcast(Long liveId, Long userId);

    /**
     * 直播间聊天室userSig
     *
     * @param userId 用户 ID
     * @return 生成的签名
     */
    String chatRoomUserSig(String userId);
}
