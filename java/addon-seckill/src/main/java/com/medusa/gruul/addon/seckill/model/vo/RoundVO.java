package com.medusa.gruul.addon.seckill.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalTime;

/**
 * @author 张治保
 * @since 2024/5/29
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class RoundVO {
    /**
     * 活动场次值
     */
    Integer round;

    /**
     * 该场次开始时间
     */
    LocalTime startTime;

    /**
     * 该场次结束时间
     */
    LocalTime endTime;
}
