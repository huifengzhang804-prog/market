package com.medusa.gruul.addon.live.function.handler.ban;

import com.medusa.gruul.addon.live.enums.ProhibitedType;
import com.medusa.gruul.addon.live.model.UpdateAnchorDTO;
import com.medusa.gruul.addon.live.mp.entity.BaseLive;
import com.medusa.gruul.addon.live.mp.entity.Prohibited;
import com.medusa.gruul.addon.live.mp.service.BaseLiveService;
import com.medusa.gruul.addon.live.mp.service.ProhibitedService;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.global.model.exception.GlobalException;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author miskw
 * @date 2023/6/15
 * @describe 禁用直播间
 */
@Component
@RequiredArgsConstructor
@ProhibitedTypeAnnotation(ProhibitedType.LIVE)
public class BanLive extends AbstractProhibitedTypeHandler {
    private final BaseLiveService baseLiveService;
    private final ProhibitedService prohibitedService;

    @Override
    public List<BaseLive> handler(UpdateAnchorDTO liveNotifyParam) {
        BaseLive baseLive = Option.of(baseLiveService.getById(liveNotifyParam.getSourceId()))
                .getOrElseThrow(() -> new GlobalException(SystemCode.DATA_NOT_EXIST_CODE, SystemCode.DATA_NOT_EXIST.getMsg()));
        if (!liveNotifyParam.getIsEnable()) {
            //禁用
            return Collections.singletonList(baseLive);
        } else {
            boolean remove = prohibitedService.lambdaUpdate()
                    .eq(Prohibited::getType, ProhibitedType.LIVE)
                    .eq(Prohibited::getSourceId, baseLive.getId())
                    .eq(Prohibited::getShopId, baseLive.getShopId())
                    .remove();
            if (!remove) {
                throw new GlobalException(SystemCode.DATA_DELETE_FAILED_CODE, SystemCode.PARAM_TYPE_ERROR.getMsg());
            }
            return new ArrayList<>();
        }
    }
}
