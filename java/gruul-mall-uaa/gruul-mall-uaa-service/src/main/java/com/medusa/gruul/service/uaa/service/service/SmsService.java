package com.medusa.gruul.service.uaa.service.service;

import com.medusa.gruul.service.uaa.api.enums.SmsType;

/**
 * 短信服务
 *
 * @author 张治保
 * date 2022/10/25
 */
public interface SmsService {

    /**
     * 发送短信验证码
     *
     * @param smsType 短信类型
     * @param mobile 手机号
     * @return 验证码
     */
    String sendSmsCode(SmsType smsType, String mobile);

    /**
     * 校验短信验证码
     * @param smsType 短信类型
     * @param mobile 手机号
     * @param inputCode 输入的验证码
     */
    void checkSmsCode(SmsType smsType,String mobile,String inputCode);
}
