package com.medusa.gruul.addon.live.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.live.enums.ProhibitedType;
import com.medusa.gruul.addon.live.model.*;
import com.medusa.gruul.addon.live.mp.entity.Prohibited;
import com.medusa.gruul.addon.live.param.AnchorParam;
import com.medusa.gruul.addon.live.param.FollowLiveParam;
import com.medusa.gruul.addon.live.vo.AnchorVO;
import com.medusa.gruul.addon.live.vo.FollowLiveVO;
import com.medusa.gruul.addon.live.vo.LiveRoomVO;

import java.util.Set;

/**
 * @author miskw
 * @date 2023/6/6
 * @describe 描述
 */
public interface LiveUserService {
    /**
     * 预约直播间
     * @param reservationLiveDto 预约直播间
     */
    void addReservation(ReservationLiveDTO reservationLiveDto);

    /**
     * 关注直播间列表
     * @param followLiveParam 参数
     * @return 关注直播间列表
     */
    IPage<FollowLiveVO> followLiveList(FollowLiveParam followLiveParam, Long userId);

    IPage<FollowLiveVO> discoverLiveList(FollowLiveParam followLiveParam, Long userId);

    /**
     * 关注主播
     * @param followAnchorDto 主播信息
     */
    void addFollow(FollowAnchorDTO followAnchorDto);
    /**
     * 管理端主播列表
     * @param anchorParam 主播列表查询参数
     * @return 分页主播
     */
    IPage<AnchorVO> anchorPage(AnchorParam anchorParam);

    /**
     * 启用/禁用主播
     * @param id 主播id
     * @param isEnable 启用 | 禁用
     */
    void updateAnchorStatus(Long id, Boolean isEnable);

    /**
     * 平台 恢复/违规禁播主播
     * @param updateAnchorDto 恢复/违规主播参数
     */
    void updateAnchorStatusForPlatform(UpdateAnchorDTO updateAnchorDto);
    /**
     * 查看违禁原因
     * @param id 来源id
     * @param type 来源类型 直播 | 主播
     * @return 违禁原因
     */
    Prohibited banReason(Long id, ProhibitedType type);

    /**
     * 获取主播信息
     * @param userId 用户id
     * @return 主播信息
     */
    AnchorVO anchorMessage(Long userId);

    void addAnchor(AddAnchorDTO addAnchorDto);

    /**
     * 用户随机获取一条直播间信息
     *
     * @param id 直播间id
     * @return 直播间信息
     */
    LiveRoomVO randomView(Long id);

    /**
     * 直播间互动数据落库
     * @param liveIds 直播间ids
     */
    void saveInteractionDbBatch(Set<String> liveIds);

    /**
     * 添加直播间观看人数
     * @param liveId 直播间id
     */
    void addViewership(String liveId);

    /**
     * 腾讯云推流禁用 || 关闭直播间
     *
     * @param streamName streamName
     * @param reason     禁播原因
     */
     void tencentPushAddress(String streamName, String reason);

    /**
     * 用户是否关注 前端需要过滤一遍用户登陆状态
     * @param userId 用户id
     * @param anchorId 主播id
     * @return 是否关注
     */
    Boolean viewershipStatus(Long userId, Long anchorId);
}
