package com.medusa.gruul.addon.distribute.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * date 2022/11/14
 */
@Getter
@RequiredArgsConstructor
public enum AnotherCondition {

    /**
     * 首次点击链接
     */
    FIRST_LINK_CLICK(1),

    /**
     * 首次付款
     */
    FIRST_PAID(2);

    @EnumValue
    private final Integer value;

}
