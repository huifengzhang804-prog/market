package com.medusa.gruul.addon.live.function.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.addon.live.mp.entity.Reservation;
import com.medusa.gruul.addon.live.mp.service.ReservationService;
import com.medusa.gruul.addon.live.param.LiveNotifyParam;
import com.medusa.gruul.addon.live.enums.LiveNotify;
import com.medusa.gruul.addon.live.enums.LiveRoomStatus;
import com.medusa.gruul.addon.live.function.LiveNotifyAnnotation;
import com.medusa.gruul.addon.live.mp.entity.BaseLive;
import com.medusa.gruul.addon.live.mp.service.BaseLiveService;
import com.medusa.gruul.carrier.pigeon.api.enums.PigeonRabbit;
import com.medusa.gruul.carrier.pigeon.api.enums.SubscribeMsg;
import com.medusa.gruul.carrier.pigeon.api.model.dto.SubscribeAppletMsgDTO;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.global.model.exception.GlobalException;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author miskw
 * @date 2023/6/6
 * @describe 开播回调
 */
@Component
@RequiredArgsConstructor
@LiveNotifyAnnotation(LiveNotify.RECORD)
public class RecordHandler extends AbstractLiveNotifyHandler {
    private final BaseLiveService baseLiveService;
    private final ReservationService reservationService;
    private final RabbitTemplate rabbitTemplate;
    @Override
    public Boolean handler(LiveNotifyParam liveNotifyParam) {
        BaseLive baseLive = Option.of(baseLiveService.lambdaQuery().eq(BaseLive::getPushAddress, liveNotifyParam.getPushAddress()).one())
                .getOrElseThrow(() -> new GlobalException(SystemCode.DATA_NOT_EXIST_CODE, SystemCode.DATA_NOT_EXIST.getMsg()));
        if (baseLive.getStatus() == LiveRoomStatus.PROCESSING) {
            return true;
        }
        baseLive.setBeginTime(LocalDateTime.now())
                .setStatus(LiveRoomStatus.PROCESSING);
        if (!baseLiveService.updateById(baseLive)) {
            throw new GlobalException(SystemCode.DATA_UPDATE_FAILED_CODE, SystemCode.DATA_UPDATE_FAILED.getMsg());
        }
        //给预约当前直播用户发送微信订阅消息
        sendAppletSubscribe(baseLive);
        return true;
    }

    //给预约当前直播用户发送微信订阅消息
    private void sendAppletSubscribe(BaseLive baseLive) {
        List<Reservation> reservations = reservationService.lambdaQuery()
                .eq(Reservation::getIsPush, Boolean.FALSE)
                .eq(Reservation::getLiveId, baseLive.getId()).list();
        if (CollUtil.isEmpty(reservations)){
            return;
        }
        Map<String,String> data = new HashMap<>(CommonPool.NUMBER_SIX);
        data.put("thing1", baseLive.getLiveTitle());
        data.put("thing3", baseLive.getAnchorNickname());
        SubscribeAppletMsgDTO subscribeApplet = new SubscribeAppletMsgDTO();
        subscribeApplet.setData(data);
        subscribeApplet.setSubscribeMsg(SubscribeMsg.LIVE_DEBUT);

        reservations.stream()
                .map(Reservation::getOpenid)
                .filter(StrUtil::isNotEmpty)
                .forEach(openid->{
                    subscribeApplet.setOpenid(openid);
                    rabbitTemplate.convertAndSend(
                            PigeonRabbit.PIGEON_APPLET_SUBSCRIBE.exchange(),
                            PigeonRabbit.PIGEON_APPLET_SUBSCRIBE.routingKey(),
                            subscribeApplet);

                    reservationService.lambdaUpdate()
                            .eq(Reservation::getOpenid,openid)
                            .set(Reservation::getIsPush, Boolean.TRUE)
                            .update();
                });
    }
}
