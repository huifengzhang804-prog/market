package com.medusa.gruul.service.uaa.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * date 2022/3/8
 */
@Getter
@RequiredArgsConstructor
public enum Gender {

    /**
     * 未知
     */
    UNKNOWN(0, "未知"),
    /**
     * 男
     */
    MALE(1, "男"),
    /**
     * 女
     */
    FEMALE(2,"女");

    @EnumValue
    private final Integer value;

    private final String desc;

}
