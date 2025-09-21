package com.medusa.gruul.addon.seckill.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * @since 2024/5/28
 */
@Getter
@RequiredArgsConstructor
public enum SeckillStatus {
    /**
     * 正常状态
     */
    OK(1),

    /**
     * 活动结束
     */
    FINISHED(2),

    /**
     * 平台下架
     */
    OFF_SHELF(3),

    /**
     * 店铺下架
     */
    VIOLATION_OFF_SHELF(4);

    @EnumValue
    private final Integer value;

}
