package com.medusa.gruul.addon.live.function.handler.ban;

import com.medusa.gruul.addon.live.model.UpdateAnchorDTO;
import com.medusa.gruul.addon.live.mp.entity.BaseLive;
import com.medusa.gruul.common.web.handler.Handler;

import java.util.List;

/**
 * @author miskw
 * @date 2023/6/15
 * @describe 描述
 */
public abstract class AbstractProhibitedTypeHandler implements Handler<List<BaseLive> > {
    @Override
    public List<BaseLive> handle(Object... params) {
        return this.handler(
                cast(params[0], UpdateAnchorDTO.class)
        );
    }


    public abstract List<BaseLive>  handler(UpdateAnchorDTO updateAnchorDTO);
}
