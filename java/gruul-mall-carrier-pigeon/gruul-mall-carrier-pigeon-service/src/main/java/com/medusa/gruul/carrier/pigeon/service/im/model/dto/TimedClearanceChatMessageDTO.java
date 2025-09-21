package com.medusa.gruul.carrier.pigeon.service.im.model.dto;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

/**
 * <p>定时清除Redis消息DTO</p>
 * 定时清除的时间范围由:
 * {@link TimedClearanceChatMessageDTO#startTimestamp}
 * 和
 * {@link TimedClearanceChatMessageDTO#endTimestamp}
 * 决定
 * @author Andy.Yan
 */
public class TimedClearanceChatMessageDTO {

    /**
     * 需要扫描的Redis消息Key前缀
     */
    private final static String REDIS_KEY_GROUP_MSG_PREFIX = "im:room:*";
    private final static String REDIS_KEY_C_END_MSG_PREFIX = "im:platform:room:*";

    /**
     * 需要清除的Redis消息的开始时间戳
     */
    private final Long startTimestamp;

    /**
     * 需要清除的Redis消息的结束时间戳
     */
    private final Long endTimestamp;

    /**
     * 初始化TimedClearanceChatMessageDTO对象
     * @param day 从当前时间减去的天数
     */
    public TimedClearanceChatMessageDTO(Integer day) {
        this.startTimestamp = 0L;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime needClearanceDay = now.minusDays(day);
        this.endTimestamp = needClearanceDay.toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    /**
     * Redis Keys
     * @return {@link List}
     */
    public List<String> getRedisMessageKeysPrefixes() {
        return Arrays.asList(REDIS_KEY_GROUP_MSG_PREFIX, REDIS_KEY_C_END_MSG_PREFIX);
    }

    public Long getStartTimestamp() {
        return this.startTimestamp;
    }

    public Long getEndTimestamp() {
        return this.endTimestamp;
    }
}
