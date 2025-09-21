package com.medusa.gruul.live.service.task;

import cn.binarywang.wx.miniapp.api.WxMaLiveService;
import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.live.WxMaLiveResult;
import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.live.api.entity.LiveRoom;
import com.medusa.gruul.live.api.enums.RoomStatus;
import com.medusa.gruul.live.service.mp.service.LiveRoomService;
import com.xxl.job.core.handler.IJobHandler;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author miskw
 * date 2022/11/16
 * describe 定时修改直播间房间状态
 */
@Component
@RequiredArgsConstructor
public class LiveRoomReviewJob extends IJobHandler {

    private final LiveRoomService liveRoomService;

    private final WxMaService wxMaService;

    @Override
    @SneakyThrows
    public void execute() {
        List<RoomStatus> roomStatuses = Arrays.asList(RoomStatus.NOT_STARTED, RoomStatus.LIVE_BROADCAST);
        List<LiveRoom> liveRooms = liveRoomService.lambdaQuery().in(LiveRoom::getStatus, roomStatuses).list();
        if (CollUtil.isNotEmpty(liveRooms)) {
            List<Long> roomIds = liveRooms.stream().map(LiveRoom::getWechatRoomId).toList();
            WxMaLiveService wxMaLiveService = wxMaService.getLiveService();
            List<WxMaLiveResult.RoomInfo> liveInfos = wxMaLiveService.getLiveInfo(0, 100).getRoomInfos();
            liveInfos = liveInfos.stream()
                    .filter(liveInfo -> {
                        if (!roomIds.contains(liveInfo.getRoomId().longValue()) || liveInfo.getLiveStatus() == 102) {
                            return Boolean.FALSE;
                        }
                        return Boolean.TRUE;
                    }).toList();
            if (CollUtil.isNotEmpty(liveInfos)) {
                // key roomId
                // value直播对象
                Map<Long, List<LiveRoom>> roomIdAndRoomMap = liveRooms.stream().collect(Collectors.groupingBy(LiveRoom::getWechatRoomId));
                //筛选归类为直播中的直播间
                List<Long> broadcastRoom = new ArrayList<>();
                liveInfos
                        .stream()
                        .filter(liveInfo ->
                                !(liveInfo.getLiveStatus() == 103 || liveInfo.getLiveStatus() == 104 || liveInfo.getLiveStatus() == 107))
                        .forEach(liveInfo -> {
                            List<LiveRoom> roomList = roomIdAndRoomMap.get(liveInfo.getRoomId().longValue());
                            LiveRoom liveRoom = roomList.get(0);
                            if (liveRoom.getStatus().getCode() == RoomStatus.NOT_STARTED.getCode()) {
                                broadcastRoom.add(liveRoom.getWechatRoomId());
                            }
                        });
                if (CollUtil.isNotEmpty(broadcastRoom)) {
                    //直播未开始修改成直播中
                    liveRoomService.lambdaUpdate()
                            .in(LiveRoom::getWechatRoomId, broadcastRoom)
                            .set(LiveRoom::getStatus, RoomStatus.LIVE_BROADCAST)
                            .update();
                }

                //筛选归类为已结束的直播间
                List<Long> closeRoom = liveInfos.stream()
                        .filter(liveInfo ->
                                !(liveInfo.getLiveStatus() == 101 || liveInfo.getLiveStatus() == 105 || liveInfo.getLiveStatus() == 106))
                        .map(liveInfo -> liveInfo.getRoomId().longValue())
                        .collect(Collectors.toList());
                if (CollUtil.isNotEmpty(closeRoom)) {
                    liveRoomService.lambdaUpdate()
                            .in(LiveRoom::getWechatRoomId, closeRoom)
                            .set(LiveRoom::getStatus, RoomStatus.CLOSED)
                            .update();
                }
            }

        }
    }
}
