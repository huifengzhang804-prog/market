package com.medusa.gruul.carrier.pigeon.service.modules.sms.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.model.enums.SmsPlatformType;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.model.enums.SmsType;
import com.medusa.gruul.common.fastjson2.annotation.Desensitize;
import com.medusa.gruul.common.mp.model.BaseEntity;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * 短信签名
 *
 * @author miskw
 * @since 2022/12/9
 */

@Getter
@Setter
@Accessors(chain = true)
@TableName("t_sms_sign")
public class SmsSign extends BaseEntity {

    /**
     * 短信平台类型
     */
    private SmsPlatformType type;

    /**
     * 短信类型
     */

    private SmsType smsType;
    /**
     * 说明
     */
    @TableField("description")
    private String description;

    /**
     * 供应商应用appid
     */
    @TableField("provider_app_id")
    @NotNull(message = "请输入供应商应用appid！")
    @Desensitize(start = 3, end = 3)
    private String providerAppId;

    /**
     * 供应商应用secret
     */
    @TableField("provider_app_secret")
    @NotNull(message = "请输入供应商应用secret！")
    @Desensitize(start = 3, end = 3)
    private String providerAppSecret;

    /**
     * 短信签名名称
     */
    @TableField("signature")
    @NotNull(message = "请输入短信签名名称！")
    private String signature;


    /**
     * 是否正在使用
     */
    @TableField(exist = false)
    private Boolean using;
}
