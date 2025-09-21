package com.medusa.gruul.live.service.mp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.live.api.dto.LiveRoomDto;
import com.medusa.gruul.live.api.entity.LiveRoom;
import com.medusa.gruul.live.api.enums.RoomStatus;
import com.medusa.gruul.live.service.model.dto.LiveInfoDto;
import com.medusa.gruul.live.service.model.dto.WxMaLiveRoomDto;
import com.medusa.gruul.live.service.mp.mapper.LiveRoomMapper;
import com.medusa.gruul.live.service.mp.service.LiveMemberService;
import com.medusa.gruul.live.service.mp.service.LiveRoomService;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author miskw
 * @date 2022/11/8
 */
@Service
@Slf4j
public class LiveRoomServiceImpl extends ServiceImpl<LiveRoomMapper, LiveRoom> implements LiveRoomService {
    @Resource
    private LiveRoomMapper liveRoomMapper;
    @Resource
    private LiveMemberService liveMemberService;

    /**
     * 添加直播间
     *
     * @param roomId
     * @param shopId
     * @param wxMaLiveRoomDto
     * @param pushUrl
     * @param shopInfoByShopId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createRoom(Integer roomId, Long shopId, WxMaLiveRoomDto wxMaLiveRoomDto, String pushUrl, ShopInfoVO shopInfoByShopId) {
        LiveRoom liveRoom = new LiveRoom();
        BeanUtil.copyProperties(wxMaLiveRoomDto, liveRoom);
        liveRoom.setShopId(shopId)
                .setRoomName(wxMaLiveRoomDto.getName())
                .setCoverImg(wxMaLiveRoomDto.getOssCoverImgUrl())
                .setShareImg(wxMaLiveRoomDto.getOssShareImgUrl())
                .setFeedsImg(wxMaLiveRoomDto.getOssFeedsImgUrl())
                .setStatus(RoomStatus.NOT_STARTED)
                .setShopName(shopInfoByShopId.getName())
                .setShopLogo(shopInfoByShopId.getLogo())
                .setWechatNumber(wxMaLiveRoomDto.getAnchorWechat())
                .setWechatRoomId(Long.valueOf(roomId))
                .setWechatRoomJson(JSONUtil.toJsonStr(wxMaLiveRoomDto));
        if (StrUtil.isNotBlank(pushUrl)) {
            liveRoom.setStreamingAddress(pushUrl);
        }
        boolean flag = save(liveRoom);
        if (!flag) {
            throw new GlobalException(SystemCode.DATA_ADD_FAILED_CODE, "添加直播间失败!");
        }
    }

    /**
     * 获取直播间列表
     *
     * @param liveInfoDto
     * @return
     */
    @Override
    public IPage<LiveRoom> getLiveList(LiveInfoDto liveInfoDto) {
        Long shopId = ISecurity.userMust().getShopId();
        liveInfoDto.setShopId(shopId);
        LambdaQueryChainWrapper<LiveRoom> liveRoomLambdaQueryChainWrapper = this.lambdaQuery();
        IPage<LiveRoom> infoVoPage = new Page<>(liveInfoDto.getCurrent(), liveInfoDto.getSize());
        liveRoomLambdaQueryChainWrapper
                .eq(LiveRoom::getShopId, shopId)
                .like(StrUtil.isNotBlank(liveInfoDto.getLiveRoomName()), LiveRoom::getRoomName, liveInfoDto.getLiveRoomName())
                .like(StrUtil.isNotBlank(liveInfoDto.getLiveMemberName()), LiveRoom::getAnchorName, liveInfoDto.getLiveMemberName())
                .eq(liveInfoDto.getStatus() != null, LiveRoom::getStatus, liveInfoDto.getStatus())
                .between((liveInfoDto.getBeginLeftTime() != null && liveInfoDto.getBeginRightTime() != null), LiveRoom::getStartTime, liveInfoDto.getBeginLeftTime(), liveInfoDto.getBeginRightTime())
                .orderByDesc(LiveRoom::getStartTime);
        liveRoomLambdaQueryChainWrapper.page(infoVoPage);
        return infoVoPage;
    }

    /**
     * 获取直播间详情
     *
     * @param id
     * @return
     */
    @Override
    public LiveRoom getLiveInfo(Long id) {
        Long shopId = ISecurity.userMust().getShopId();
        return ISystem.shopId(shopId, () -> liveRoomMapper.selectById(id));
    }

    /**
     * 平台查询直播列表
     *
     * @param liveRoomDto
     * @return
     */
    @Override
    public IPage<LiveRoom> liveRoom(LiveRoomDto liveRoomDto) {
        IPage<LiveRoom> page = new Page<>(liveRoomDto.getCurrent(), liveRoomDto.getSize());
        this.lambdaQuery()
                .like(StrUtil.isNotBlank(liveRoomDto.getKeywords()), LiveRoom::getRoomName, liveRoomDto.getKeywords())
                .eq(liveRoomDto.getRoomStatus() != null, LiveRoom::getStatus, liveRoomDto.getRoomStatus())
                .orderByDesc(LiveRoom::getStartTime)
                .or()
                .like(StrUtil.isNotBlank(liveRoomDto.getKeywords()), LiveRoom::getShopName, liveRoomDto.getKeywords())
                .eq(liveRoomDto.getRoomStatus() != null, LiveRoom::getStatus, liveRoomDto.getRoomStatus())
                .orderByDesc(LiveRoom::getStartTime)
                .page(page);
        return page;
    }


}
