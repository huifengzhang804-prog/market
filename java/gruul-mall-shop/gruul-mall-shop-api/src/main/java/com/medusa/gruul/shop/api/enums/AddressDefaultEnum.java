package com.medusa.gruul.shop.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @Description : 默认地址
 * @author : xiaoq
 * @date : 2022-05-20 10:09
 */

@Getter
@RequiredArgsConstructor
public enum AddressDefaultEnum {

    /**
     * 不默认
     */
    NO(0),
    /**
     * 默认
     */
    YES(1);

    @EnumValue
    private final Integer value;
}
