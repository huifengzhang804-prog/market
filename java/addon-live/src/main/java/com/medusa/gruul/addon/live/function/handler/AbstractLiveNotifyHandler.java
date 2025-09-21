package com.medusa.gruul.addon.live.function.handler;

import com.medusa.gruul.addon.live.param.LiveNotifyParam;
import com.medusa.gruul.common.web.handler.Handler;

/**
 * @author miskw
 * @date 2023/6/6
 * @describe 描述
 */
public abstract class AbstractLiveNotifyHandler implements Handler<Boolean> {
    @Override
    public Boolean handle(Object... params) {
        return this.handler(
                cast(params[0], LiveNotifyParam.class)
        );
    }


    public abstract Boolean handler(LiveNotifyParam liveNotifyParam);
}
