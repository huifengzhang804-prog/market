package com.medusa.gruul.common.security.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * date 2022/2/23
 */
@Getter
@RequiredArgsConstructor
public enum MenuType {
    /**
     * 目录
     */
    CATALOG(0, true),

    /**
     * 菜单
     */
    MENU(1, true),

    /**
     * 接口
     */
    API(2, false),

    /**
     * 按钮
     */
    BUTTON(3, false);

    /**
     * mysql 值映射
     */
    @EnumValue
    private final Integer value;

    /**
     * 是否有子节点
     */
    private final boolean hasChildren;
}
