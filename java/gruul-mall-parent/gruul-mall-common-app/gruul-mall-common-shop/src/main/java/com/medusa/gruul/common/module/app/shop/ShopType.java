package com.medusa.gruul.common.module.app.shop;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 店铺类型
 *
 * @author xiaoq
 * @Description 店铺类型
 * @date 2023-05-15 09:22
 */
@Getter
@RequiredArgsConstructor
public enum ShopType {

    /**
     * 自营
     */
    SELF_OWNED(0),

    /**
     * 优选
     */
    PREFERRED(1),

    /**
     * 普通
     */
    ORDINARY(2),
    ;

    @EnumValue
    private final Integer value;

}
