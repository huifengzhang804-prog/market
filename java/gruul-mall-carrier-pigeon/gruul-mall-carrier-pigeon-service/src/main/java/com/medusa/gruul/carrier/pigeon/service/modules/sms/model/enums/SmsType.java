package com.medusa.gruul.carrier.pigeon.service.modules.sms.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 短信类型
 * @author jipeng
 * @since 2024/12/31
 */
@Getter
@Accessors(chain = true)
@RequiredArgsConstructor
public enum SmsType {
    // 虚拟短信
    VIRTUAL(1,Boolean.TRUE),
    // 真实短信
    REAL(2,Boolean.FALSE);
    @EnumValue
    private final Integer value;

    private final Boolean virtual;
}
