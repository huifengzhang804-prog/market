package com.medusa.gruul.carrier.pigeon.service.modules.sms.model.dto;

import com.medusa.gruul.carrier.pigeon.api.sms.enums.SmsTemplateType;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.model.enums.SmsPlatformType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * 短信配置Dto
 *
 * @Description:
 * @Author: xiaoq
 * @Date : 2022-03-29 18:34
 */
@Data
public class EditSmsConfigDto {
    /**
     * 短信签名id
     */
    private Long smsSignId;

    /**
     * sms 服务商类型
     */
    @NotNull
    private SmsPlatformType type;
    /**
     * 供应商应用appId
     */
    @NotNull
    private String providerAppId;
    /**
     * 供应商应用appSecret
     */
    @NotNull
    private String providerAppSecret;
    /**
     * 短信签名
     */
    @NotNull
    private String signature;
    /**
     * 模板Id
     */
    private Long templateId;
    /**
     * 模板类型
     */
    @NotNull
    private SmsTemplateType smsTemplateType;
    /**
     * 模板code
     */
    @NotNull
    private String templateCode;

}
