package com.medusa.gruul.carrier.pigeon.service.modules.sms.model.enums;


/**
 * @Description: 存储类型type
 * @Author: xiaoq
 * @Date : 2022-05-19 17:05
 */

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(chain = true)
@RequiredArgsConstructor
public enum SmsPlatformType {
    /**
     * 阿里
     */
    ALIYUN(1),
    /**
     * 腾讯
     */
    TENCENT(2),
    ;

    @EnumValue
    private final Integer value;
}
