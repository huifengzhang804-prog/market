package com.medusa.gruul.live.service.service;

import com.baomidou.mybatisplus.core.metadata.IPage;

import com.medusa.gruul.live.api.dto.AppletsLiveRoomDto;
import com.medusa.gruul.live.api.dto.LiveRoomDto;
import com.medusa.gruul.live.api.dto.LiveUserDto;
import com.medusa.gruul.live.api.dto.PlatformLiveGoodsDto;
import com.medusa.gruul.live.api.vo.AppletsLiveRoomVo;
import com.medusa.gruul.live.api.vo.LiveGoodsVo;
import com.medusa.gruul.live.api.vo.LiveRoomVo;
import com.medusa.gruul.live.api.vo.LiveUserVo;

/**
 * @author miskw
 * @date 2022/11/16
 */
public interface LiveService {
    /**
     * 平台查询直播列表
     * @param liveRoomDto
     * @return
     */
    IPage<LiveRoomVo> liveRoom(LiveRoomDto liveRoomDto);


    /**
     * 平台批量删除直播间
     * @param roomIds
     */
    void deleteLiveRoomToPlatform(Long[] roomIds);

    /**
     * 分享直播间
     * @param roomId
     * @return
     */
    String shareLiveRoom(Long roomId);

    /**
     * 平台查询所有商品列表
     * @param platformLiveGoodsDto
     * @return
     */
    IPage<LiveGoodsVo> liveGoods(PlatformLiveGoodsDto platformLiveGoodsDto);

    /**
     * 平台批量删除商品
     * @param goodsIds
     */
    void deleteLiveGoods(Long[] goodsIds);

    /**
     * 平台查询所有主播成员
     * @param dto
     * @return
     */
    IPage<LiveUserVo> getLiveUser(LiveUserDto dto);

    /**
     * 批量删除直播成员
     * @param roomUserIds
     */
    void deleteLiveUser(Long[] roomUserIds);

    /**
     * 小程序直播间
     * @param dto
     * @return
     */
    IPage<AppletsLiveRoomVo> getAppletsLiveRoom(AppletsLiveRoomDto dto);

}
