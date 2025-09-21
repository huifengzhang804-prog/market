package com.medusa.gruul.carrier.pigeon.service.modules.sms.model.dto;

import com.medusa.gruul.carrier.pigeon.service.modules.sms.model.enums.SmsPlatformType;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.model.enums.SmsType;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.mp.entity.SmsSign;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.mp.entity.SmsTemplate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author miskw
 * @since 2023/4/24
 */
@Data
@Accessors(chain = true)
public class SmsCurrentConfigDTO implements Serializable {

    /**
     * 短信平台类型
     */
    @NotNull(message = "配置类型不能为空")
    private SmsPlatformType type;
    /**
     * 短信类型
     */
    @NotNull(message = "短信类型不能为空")
    private SmsType smsType;
    /**
     * 说明
     */
    @NotNull(message = "说明不能为空")
    private String description;

    /**
     * 供应商应用appid 如果为空 则表示是删除操作
     */
    @NotBlank(message = "供应商应用appid不能为空")
    private String providerAppId;

    /**
     * 供应商应用secret
     */
    @NotBlank(message = "供应商应用secret不能为空")
    private String providerAppSecret;

    /**
     * 短信签名名称
     */
    @NotBlank(message = "短信签名名称不能为空")
    private String signature;


    @NotNull(message = "短信模板数据不能为空")
    @Valid
    private SmsTemplate smsTemplate;

    public SmsSign toSmsSign(){
        return new SmsSign().setProviderAppId(providerAppId)
                .setProviderAppSecret(providerAppSecret)
                .setSignature(signature)
                .setType(type)
                .setSmsType(smsType)
                .setDescription(description);
    }

}
