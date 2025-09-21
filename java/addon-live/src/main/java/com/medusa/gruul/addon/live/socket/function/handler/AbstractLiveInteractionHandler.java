package com.medusa.gruul.addon.live.socket.function.handler;

import com.medusa.gruul.common.web.handler.Handler;

/**
 * @author miskw
 * @date 2023/6/19
 * @describe 描述
 */
public abstract class AbstractLiveInteractionHandler implements Handler<Boolean> {
    @Override
    public Boolean handle(Object... params) {
        return this.handler(
                cast(params[0], Object.class)
        );
    }

    public abstract Boolean handler(Object socketMessageDTO);
}
