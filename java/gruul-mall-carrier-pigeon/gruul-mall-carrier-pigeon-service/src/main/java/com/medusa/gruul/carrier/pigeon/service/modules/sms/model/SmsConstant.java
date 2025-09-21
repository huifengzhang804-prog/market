package com.medusa.gruul.carrier.pigeon.service.modules.sms.model;

/**
 * sms 常量池
 *
 * @author 张治保
 * @since 2024/3/6
 */
public interface SmsConstant {
    /**
     * sms 缓存前缀
     */
    String SMS_PREFIX = "gruul:mall:pigeon:sms";

    /**
     * 使用种的短信配置缓存key
     */
    String SMS_USING_CONF_KEY = SMS_PREFIX + ":conf:using";

    /**
     * 短信签名分分布式锁
     */
    String SMS_CONF_LOCK_KEY = SMS_PREFIX + ":conf:lock";
    /**
     * 短信签名信息缓存key
     */
    String SMS_SIGN_INFO= SMS_PREFIX + ":sign:info";
    /**
     * 发送短信是否启用拖拽滑块
     */
    String SMS_CAPTCHA_OPEN_FLAG = SMS_PREFIX + ":captcha:flag";


}

