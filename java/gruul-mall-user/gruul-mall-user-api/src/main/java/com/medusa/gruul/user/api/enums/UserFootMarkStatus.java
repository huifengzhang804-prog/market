package com.medusa.gruul.user.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 用户状态枚举
 * @author WuDi
 */
@Getter
@RequiredArgsConstructor
public enum UserFootMarkStatus {


    /**
     * 清空
     */
    EMPTY(0),
    /**
     * 删除
     */
    DELETE(1);

    @EnumValue
    private final Integer value;

}
