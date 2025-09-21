package com.medusa.gruul.shop.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author: wufuzhong
 * @Date: 2023/11/07 09:58:58
 * @Description: 自营商家、供应商枚举
 */

@Getter
@RequiredArgsConstructor
public enum SelfShopEnum {

    /**
     * 否
     */
    NO(0),
    /**
     * 是
     */
    YES(1);

    @EnumValue
    private final Integer value;
}
