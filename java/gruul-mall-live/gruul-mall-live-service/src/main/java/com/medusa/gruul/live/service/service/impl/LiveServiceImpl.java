package com.medusa.gruul.live.service.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.live.api.dto.AppletsLiveRoomDto;
import com.medusa.gruul.live.api.dto.LiveRoomDto;
import com.medusa.gruul.live.api.dto.LiveUserDto;
import com.medusa.gruul.live.api.dto.PlatformLiveGoodsDto;
import com.medusa.gruul.live.api.entity.LiveGoodsExamine;
import com.medusa.gruul.live.api.entity.LiveMember;
import com.medusa.gruul.live.api.entity.LiveRoom;
import com.medusa.gruul.live.api.entity.LiveRoomGoods;
import com.medusa.gruul.live.api.vo.AppletsLiveRoomVo;
import com.medusa.gruul.live.api.vo.LiveGoodsVo;
import com.medusa.gruul.live.api.vo.LiveRoomVo;
import com.medusa.gruul.live.api.vo.LiveUserVo;
import com.medusa.gruul.live.service.mp.service.LiveGoodsExamineService;
import com.medusa.gruul.live.service.mp.service.LiveMemberService;
import com.medusa.gruul.live.service.mp.service.LiveRoomGoodsService;
import com.medusa.gruul.live.service.mp.service.LiveRoomService;
import com.medusa.gruul.live.service.service.LiveBroadcastService;
import com.medusa.gruul.live.service.service.LiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author miskw
 * @date 2022/11/16
 */
@Service
@RequiredArgsConstructor
public class LiveServiceImpl implements LiveService {

    private final LiveRoomService liveRoomService;

    private final LiveBroadcastService liveBroadcastService;

    private final LiveGoodsExamineService liveGoodsExamineService;

    private final LiveMemberService liveMemberService;

    private final LiveRoomGoodsService liveRoomGoodsService;



    /**
     * 平台查询直播列表
     *
     * @param liveRoomDto
     * @return
     */
    @Override
    public IPage<LiveRoomVo> liveRoom(LiveRoomDto liveRoomDto) {
        IPage<LiveRoom> room = liveRoomService.liveRoom(liveRoomDto);
        List<LiveRoom> records = room.getRecords();
        if (CollUtil.isEmpty(records)) {
            return new Page<>(liveRoomDto.getCurrent(), liveRoomDto.getSize());
        }
        List<LiveRoomVo> liveRoomVos = new ArrayList<>();
        for (LiveRoom record : records) {
            LiveRoomVo vo=new LiveRoomVo();
            BeanUtil.copyProperties(record,vo);
            liveRoomVos.add(vo);
        }
        IPage<LiveRoomVo> page=new Page<>();
        BeanUtil.copyProperties(room,page);
        page.setRecords(liveRoomVos);
        return page;
    }

    /**
     * 平台批量删除直播间
     *
     * @param roomIds
     */
    @Override
    public void deleteLiveRoomToPlatform(Long[] roomIds) {
        liveBroadcastService.deleteRoom(roomIds);
    }

    /**
     * 分享直播间
     *
     * @param roomId
     * @return
     */
    @Override
    public String shareLiveRoom(Long roomId) {
        return liveBroadcastService.shareLiveRoom(roomId);
    }

    /**
     * 平台查询所有商品列表
     *
     * @param platformLiveGoodsDto
     * @return
     */
    @Override
    public IPage<LiveGoodsVo> liveGoods(PlatformLiveGoodsDto platformLiveGoodsDto) {
        IPage<LiveGoodsExamine> page = new Page<>(platformLiveGoodsDto.getCurrent(), platformLiveGoodsDto.getSize());
        liveGoodsExamineService.lambdaQuery()
                .eq(platformLiveGoodsDto.getAuditStatus() != null, LiveGoodsExamine::getAuditStatus, platformLiveGoodsDto.getAuditStatus())
                .like(StrUtil.isNotBlank(platformLiveGoodsDto.getKeywords()), LiveGoodsExamine::getProductName, platformLiveGoodsDto.getKeywords())
                .orderByDesc(LiveGoodsExamine::getCreateTime)
                .or()
                .like(StrUtil.isNotBlank(platformLiveGoodsDto.getKeywords()), LiveGoodsExamine::getShopName, platformLiveGoodsDto.getKeywords())
                .eq(platformLiveGoodsDto.getAuditStatus() != null, LiveGoodsExamine::getAuditStatus, platformLiveGoodsDto.getAuditStatus())
                .orderByDesc(LiveGoodsExamine::getCreateTime)
                .page(page);
        List<LiveGoodsExamine> records = page.getRecords();
        List<LiveGoodsVo> liveGoodsVos = new ArrayList<>();
        for (LiveGoodsExamine record : records) {
            LiveGoodsVo vo = new LiveGoodsVo();
            BeanUtil.copyProperties(record, vo);
            liveGoodsVos.add(vo);
        }
        IPage<LiveGoodsVo> voPage=new Page<>();
        BeanUtil.copyProperties(page,voPage);
        voPage.setRecords(liveGoodsVos);
        return voPage;
    }

    /**
     * 平台批量删除商品
     * @param goodsIds
     */
    @Override
    public void deleteLiveGoods(Long[] goodsIds) {
        liveBroadcastService.deleteGoodsInfos(goodsIds);
    }

    /**
     * 平台查询所有主播成员
     * @param dto
     * @return
     */
    @Override
    public IPage<LiveUserVo> getLiveUser(LiveUserDto dto) {
        IPage<LiveMember> liveUser = liveMemberService.getLiveUser(dto);
        List<LiveMember> records = liveUser.getRecords();
        if (CollUtil.isEmpty(records)){
            return new Page<>();
        }
        List<LiveUserVo> vos=new ArrayList<>();
        for (LiveMember record : records) {
            LiveUserVo vo=new LiveUserVo();
            BeanUtil.copyProperties(record,vo);
            vos.add(vo);
        }
        IPage<LiveUserVo> voPage =new Page<>();
        BeanUtil.copyProperties(liveUser,voPage );
        voPage .setRecords(vos);
        return voPage ;
    }

    /**
     * 批量删除直播成员
     * @param roomUserIds
     */
    @Override
    public void deleteLiveUser(Long[] roomUserIds) {
        liveBroadcastService.deleteLiveUser(roomUserIds);
    }

    /**
     * 小程序直播间
     * @param dto
     * @return
     */
    @Override
    public IPage<AppletsLiveRoomVo> getAppletsLiveRoom(AppletsLiveRoomDto dto) {
        IPage<LiveRoom> page = new Page<>(dto.getCurrent(), dto.getSize());
        liveRoomService.lambdaQuery().orderByDesc(LiveRoom::getStartTime).page(page);
        List<LiveRoom> records = page.getRecords();
        if (CollUtil.isEmpty(records)){
            return new Page<>(dto.getCurrent(),dto.getSize());
        }
        List<AppletsLiveRoomVo> vos=new ArrayList<>();
        for (LiveRoom record : records) {
            AppletsLiveRoomVo vo=new AppletsLiveRoomVo();
            List<LiveRoomGoods> roomGoods = liveRoomGoodsService.lambdaQuery().eq(LiveRoomGoods::getLiveRoomId, record.getWechatRoomId()).list();
            if (CollUtil.isNotEmpty(roomGoods)){
                List<Long> collect = roomGoods.stream().map(LiveRoomGoods::getLiveGoodsExamineId).collect(Collectors.toList());
                List<LiveGoodsExamine> goodsExamines = liveGoodsExamineService.lambdaQuery().in(LiveGoodsExamine::getGoodsId, collect).list();
                vo.setExamineList(goodsExamines);
            }
            BeanUtil.copyProperties(record,vo);
            vos.add(vo);
        }
        IPage<AppletsLiveRoomVo> iPage=new Page<>();
        BeanUtil.copyProperties(page,iPage);
        iPage.setRecords(vos);
        return iPage;
    }
}
