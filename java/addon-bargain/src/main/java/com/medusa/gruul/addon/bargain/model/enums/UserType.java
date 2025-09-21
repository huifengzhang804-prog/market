package com.medusa.gruul.addon.bargain.model.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 用户类型
 * @author wudi
 */
@Getter
@RequiredArgsConstructor
public enum UserType {

    /**
     * 不限
     */
    UNLIMITED(0),

    /**
     * 新用户
     */
    NEW_USER(1);

    @EnumValue
    private final Integer value;

}
