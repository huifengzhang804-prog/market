package com.medusa.gruul.carrier.pigeon.service.im.tasks;

import com.alibaba.fastjson2.JSON;
import com.medusa.gruul.carrier.pigeon.service.im.model.dto.TimedClearanceChatMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * <p>定时清除指定天数之前的聊天记录</p>
 *
 * @author Andy.Yan
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TimedClearanceChatMessageTask {

    private final StringRedisTemplate redisTemplate;

    @Value("${im.chat.message.clearance.days:180}")
    private Integer IM_CHAT_MESSAGE_CLEARANCE_DAYS;

    /**
     * 每天定时清除聊天记录
     */
    @Scheduled(cron = "0 0 23 * * ?")
    public void clearanceChatMessage() {
        log.debug("Start a timed task to clear Redis IM Keys");
        TimedClearanceChatMessageDTO dto = new TimedClearanceChatMessageDTO(IM_CHAT_MESSAGE_CLEARANCE_DAYS);
        log.debug("The parameter of doClearanceChatMessage() is : " + JSON.toJSONString(dto));
        doClearanceChatMessage(dto);
        log.debug("Timed task to clear Redis IM Keys executed");
    }

    /**
     * 根据{@code dto},使用Pipeline删除满足条件的Redis Key
     *
     * @param dto 定时清除消息DTO,参考{@link TimedClearanceChatMessageDTO}
     */
    private void doClearanceChatMessage(TimedClearanceChatMessageDTO dto) {
        // Key: ChatRoomKey, value: [{"score" : 19, "value" : {"message" : "hello"}}]
        Map<String, Set<ZSetOperations.TypedTuple<String>>> waitingRemoveMessageMap = new HashMap<>();

        // 遍历需要处理的Redis Key前缀. 对于每一个前缀,使用Scan进行match
        // 对于匹配到的聊天室,使用score进行范围查询
        for (String keyPrefix : dto.getRedisMessageKeysPrefixes()) {
            log.debug("Start processing keys : {}", keyPrefix);
            ScanOptions match = ScanOptions.scanOptions().match(keyPrefix).build();
            redisTemplate.scan(match).stream().forEach(chatRoomKey -> {
                log.debug("Start processing key: {}", chatRoomKey);
                waitingRemoveMessageMap.put(chatRoomKey, redisTemplate
                        .opsForZSet()
                        .reverseRangeByScoreWithScores(chatRoomKey, dto.getStartTimestamp(), dto.getEndTimestamp()));
            });
        }
        if (CollectionUtils.isEmpty(waitingRemoveMessageMap)) {
            log.debug("There are no Redis Keys to be deleted");
        } else {
            // 使用Pipeline批量删除聊天记录
            Long removedKeyCount = (Long) Optional
                    .ofNullable(batchRemove(waitingRemoveMessageMap))
                    .orElse(new ArrayList<>())
                    .stream()
                    .reduce((a, b) -> Long.parseLong(String.valueOf(a)) + Long.parseLong(String.valueOf(b)))
                    .orElse(0L);
            log.debug("There are {} Redis keys that have been deleted", removedKeyCount);
        }
    }

    /**
     * 批量删除Redis Key
     *
     * @param messageMap key:Redis聊天记录Key,value:包装了score和value的TypedTuple对象
     * @return 删除的Key的数量
     */
    public List<Object> batchRemove(Map<String, Set<ZSetOperations.TypedTuple<String>>> messageMap) {
        return redisTemplate.executePipelined((RedisCallback<Long>) connection -> {
            StringRedisConnection stringRedisConn = (StringRedisConnection) connection;
            messageMap.forEach((k, v) -> stringRedisConn.zRem(k, v.stream().map(ZSetOperations.TypedTuple::getValue).toList().toArray(new String[]{})));
            return null;
        });
    }
}
