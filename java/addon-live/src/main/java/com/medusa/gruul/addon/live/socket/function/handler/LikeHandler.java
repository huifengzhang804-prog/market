package com.medusa.gruul.addon.live.socket.function.handler;

import cn.hutool.json.JSONUtil;
import com.medusa.gruul.addon.live.constant.LiveConstants;
import com.medusa.gruul.addon.live.socket.dto.InteractionSocketDTO;
import com.medusa.gruul.addon.live.socket.enums.SocketMessageType;
import com.medusa.gruul.addon.live.socket.function.LiveSocket;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.redis.util.RedisUtil;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author miskw
 * @date 2023/6/19
 * @describe 点赞处理
 */
@Component
@LiveSocket(SocketMessageType.LIKE)
public class LikeHandler extends AbstractLiveInteractionHandler {
    @Override
    public Boolean handler(Object socketMessageDTO) {
        InteractionSocketDTO likeSocketDTO = JSONUtil.toBean( socketMessageDTO.toString(), InteractionSocketDTO.class);

        String likeRedisKey = RedisUtil.key(LiveConstants.LIVE_LIKE, likeSocketDTO.getLiveId());
        Integer redisLikeCount = RedisUtil.getCacheObject(likeRedisKey);
        if (redisLikeCount == null) {
            redisLikeCount = 0;
        }
        RedisUtil.setCacheObject(likeRedisKey, redisLikeCount + likeSocketDTO.getCount(), CommonPool.NUMBER_TWELVE, TimeUnit.HOURS);
        return true;
    }
}
