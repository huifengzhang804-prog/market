package com.medusa.gruul.addon.live.mp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.live.mp.entity.BaseLive;
import com.medusa.gruul.addon.live.mp.entity.LiveExtend;
import com.medusa.gruul.addon.live.mp.mapper.LiveExtendMapper;
import com.medusa.gruul.addon.live.mp.service.LiveExtendService;
import com.medusa.gruul.common.model.resp.SystemCode;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * @author miskw
 * @date 2023/6/5
 * @describe 描述
 */
@Service
public class LiveExtendServiceImpl extends ServiceImpl<LiveExtendMapper, LiveExtend> implements LiveExtendService {
    @Override
    public void handlerExtend(BaseLive baseLive) {
        LocalDateTime beginTime = baseLive.getBeginTime();
        LocalDateTime endTime = baseLive.getEndTime();
        long seconds = LocalDateTimeUtil.between(beginTime, endTime, ChronoUnit.SECONDS);
        BigDecimal secondsDecimal = BigDecimal.valueOf(seconds);
        BigDecimal divide = secondsDecimal.divide(BigDecimal.valueOf(60), 2, RoundingMode.DOWN);

        LiveExtend extend =lambdaQuery()
                .eq(LiveExtend::getLiveId, baseLive.getId())
                .eq(LiveExtend::getShopId, baseLive.getShopId())
                .one();
        if (BeanUtil.isEmpty(extend)) {
            extend = new LiveExtend()
                    .setShopId(baseLive.getShopId())
                    .setLiveId(baseLive.getId());
        }
        extend.setDuration(divide);

        SystemCode.DATA_UPDATE_FAILED.falseThrow(SpringUtil.getBean(LiveExtendServiceImpl.class).saveOrUpdate(extend));
    }
}
