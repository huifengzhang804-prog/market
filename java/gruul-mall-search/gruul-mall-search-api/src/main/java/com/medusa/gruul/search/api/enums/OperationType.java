package com.medusa.gruul.search.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <p>
 *
 * </p>
 *
 * @author xiaoq
 * @Description
 * @date 2023-12-08 14:34
 */
@Getter
@RequiredArgsConstructor
public enum OperationType {

    /**
     * 检索店铺
     */
    SEARCH_SHOP(4, "搜过的店"),

    /**
     * 逛过的店铺
     */
    STROLL_SHOP(3, "逛过的店"),

    /**
     * 关注的店铺
     */
    ATTENTTION_SHOP(2, "关注的店"),

    /**
     * 买过的店铺
     */
    CONSUMPTION_SHOP(1, "买过的店"),
;
    @EnumValue
    private final Integer value;

    @EnumValue
    private final String message;

}
