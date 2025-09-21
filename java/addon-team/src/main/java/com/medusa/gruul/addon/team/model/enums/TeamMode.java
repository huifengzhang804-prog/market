package com.medusa.gruul.addon.team.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 拼团模式
 *
 * @author 张治保
 * date 2023/3/1
 */
@Getter
@RequiredArgsConstructor
public enum TeamMode {

    /**
     * 普通模式
     */
    COMMON(1),

    /**
     * 阶梯模式
     */
    STAIRS(2);

    @EnumValue
    private final Integer value;
}
