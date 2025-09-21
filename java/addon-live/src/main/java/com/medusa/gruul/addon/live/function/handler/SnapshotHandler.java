package com.medusa.gruul.addon.live.function.handler;

import com.medusa.gruul.addon.live.mp.service.LiveExtendService;
import com.medusa.gruul.addon.live.param.LiveNotifyParam;
import com.medusa.gruul.addon.live.enums.LiveNotify;
import com.medusa.gruul.addon.live.enums.LiveRoomStatus;
import com.medusa.gruul.addon.live.function.LiveNotifyAnnotation;
import com.medusa.gruul.addon.live.mp.entity.BaseLive;
import com.medusa.gruul.addon.live.mp.service.BaseLiveService;
import com.medusa.gruul.addon.live.socket.service.ISocketService;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.global.model.exception.GlobalException;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;

/**
 * @author miskw
 * @date 2023/6/6
 * @describe 断播回调
 */
@Component
@RequiredArgsConstructor
@LiveNotifyAnnotation(LiveNotify.SNAPSHOT)
public class SnapshotHandler extends AbstractLiveNotifyHandler {
    private final BaseLiveService baseLiveService;
    private final LiveExtendService liveExtendService;
    private final ISocketService socketService;

    @Override
    public Boolean handler(LiveNotifyParam liveNotifyParam) {
        BaseLive baseLive = Option.of(
                        baseLiveService.lambdaQuery()
                                .eq(BaseLive::getPushAddress, liveNotifyParam.getPushAddress())
                                .one())
                .getOrElseThrow(() -> new GlobalException(SystemCode.DATA_NOT_EXIST.getCode(), SystemCode.DATA_NOT_EXIST.getMsg()));
        //主播不正常下播 应用划出、断网等等
        if (baseLive.getStatus() == LiveRoomStatus.PROCESSING) {
            baseLive.setStatus(LiveRoomStatus.OVER)
                    .setEndTime(LocalDateTime.now());
            baseLiveService.updateById(baseLive);
            //直播扩展表
            liveExtendService.handlerExtend(baseLive);
            //关闭这个直播间socket通道
            socketService.closeSocketChannel(Collections.singletonList(baseLive.getId().toString()));
        }
        return true;
    }
}
