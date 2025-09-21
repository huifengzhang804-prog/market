package com.medusa.gruul.addon.team.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 拼团活动状态
 *
 * @author 张治保
 * date 2023/3/10
 */
@Getter
@RequiredArgsConstructor
public enum TeamOrderStatus {


    /**
     * 进行中
     */
    ING(1),

    /**
     * 拼团成功
     */
    SUCCESS(2),

    /**
     * 拼团失败
     */
    FAIL(3);

    @EnumValue
    private final Integer value;
}
