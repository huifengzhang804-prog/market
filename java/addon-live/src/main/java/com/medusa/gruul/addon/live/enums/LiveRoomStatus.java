package com.medusa.gruul.addon.live.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author miskw
 * @date 2023/6/5
 * @describe 描述
 */
@Getter
@RequiredArgsConstructor
public enum LiveRoomStatus {
    /**
     * 未开播
     */
    NOT_STARTED(0),
    /**
     * 直播中
     */
    PROCESSING(1),
    /**
     * 已结束
     */
    OVER(2),
    /**
     * 违规下架
     */
    ILLEGAL_SELL_OFF(3);

    @EnumValue
    private final Integer value;

}
