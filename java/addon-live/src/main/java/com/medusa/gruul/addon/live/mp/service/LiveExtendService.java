package com.medusa.gruul.addon.live.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.addon.live.mp.entity.BaseLive;
import com.medusa.gruul.addon.live.mp.entity.LiveExtend;

/**
 * @author miskw
 * @date 2023/6/5
 * @describe 描述
 */
public interface LiveExtendService extends IService<LiveExtend> {
    /**
     * 直播时长计算
     * @param baseLive 直播间数据
     */
    void handlerExtend(BaseLive baseLive);
}
