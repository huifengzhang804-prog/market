package com.medusa.gruul.carrier.pigeon.service.modules.sms.model.vo;

import com.medusa.gruul.carrier.pigeon.api.sms.enums.SmsTemplateType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author miskw
 * @date 2022/12/12
 * @describe 短信记录返回对象
 */
@Data
public class SmsRecordVo {
    /**
     * 短信记录Id
     */
    private Long smsRecordId;
    /**
     * 发送短信参数
     */
    private String smsSendParam;
    /**
     * 接收验证码手机号
     */
    private String smsSendMobiles;
    /**
     * 短信发送是否失败
     */
    private Boolean isErr;
    /**
     * 短信失败原因
     */
    private String errMsg;
    /**
     * 短信发送时间
     */
    private LocalDateTime smsSendTime;
    /**
     * 短信提供商名称
     */
    private String providerName;
    /**
     * 供应商应用appid
     */
    private String providerAppId;
    /**
     * 供应商应用secret
     */
    private String providerAppSecret;
    /**
     * 短信签名
     */
    private String signature;
    /**
     * 模板名称
     */
    private String templateName;
    /**
     * 模板code
     */
    private String templateCode;
    /**
     * 模板类型
     */
    private SmsTemplateType type;
    /**
     * 模板内容
     */
    private String templateContent;

}
