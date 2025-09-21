package com.medusa.gruul.common.custom.aggregation.decoration.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 装修功能类型
 *
 * @author xiaoq
 * @Description FunctionType.java
 * @date 2022-10-31 19:13
 */
@Getter
@RequiredArgsConstructor
public enum FunctionType {
    /**
     * 底部导航
     */
    TABBAR(1),

    /**
     * 页面
     */
    PAGE(2),

    /**
     * 分类页面
     */
    CLASSIFY_PAGE(3),

    /**
     * 个人中心
     */
    PERSONAL_CENTER(4);

    @EnumValue
    private final Integer value;
}
