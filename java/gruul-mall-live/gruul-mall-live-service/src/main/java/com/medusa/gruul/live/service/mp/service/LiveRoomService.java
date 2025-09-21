package com.medusa.gruul.live.service.mp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.live.api.dto.LiveRoomDto;
import com.medusa.gruul.live.api.entity.LiveRoom;
import com.medusa.gruul.live.service.model.dto.LiveInfoDto;
import com.medusa.gruul.live.service.model.dto.WxMaLiveRoomDto;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;


/**
 * @author miskw
 * @date 2022/11/8
 */
public interface LiveRoomService extends IService<LiveRoom> {
    /**
     * 添加直播间
     * @param roomId
     * @param shopId
     * @param wxMaLiveRoomDto
     * @param pushUrl
     * @param shopInfoByShopId
     */
    void createRoom(Integer roomId, Long shopId, WxMaLiveRoomDto wxMaLiveRoomDto, String pushUrl, ShopInfoVO shopInfoByShopId);

    /**
     * 获取直播间列表
     * @param liveInfoDto
     * @return
     */
    IPage<LiveRoom> getLiveList(LiveInfoDto liveInfoDto);

    /**
     * 获取直播间详情
     * @param id
     * @return
     */
    LiveRoom getLiveInfo(Long id);

    /**
     * 平台查询直播间
     * @param liveRoomDto
     * @return
     */
    IPage<LiveRoom> liveRoom(LiveRoomDto liveRoomDto);

}
