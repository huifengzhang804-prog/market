package com.medusa.gruul.carrier.pigeon.api.sms.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @Description: 短信模板
 * @Author: xiaoq
 * @Date : 2022-03-23 16:21
 */
@Getter
@RequiredArgsConstructor
public enum  SmsTemplateType {

    /**
     * 验证码
     */
    CAPTCHA(100),


    ;

    @EnumValue
    private final Integer value;
}
