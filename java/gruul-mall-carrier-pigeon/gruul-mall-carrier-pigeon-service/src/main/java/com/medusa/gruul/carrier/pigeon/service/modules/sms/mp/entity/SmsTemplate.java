package com.medusa.gruul.carrier.pigeon.service.modules.sms.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.carrier.pigeon.api.sms.enums.SmsTemplateType;
import com.medusa.gruul.common.mp.model.BaseEntity;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


/**
 * @author miskw
 * @date 2022/12/9
 * @Describe 短信模板
 */
@Getter
@Setter
@ToString
@TableName("t_sms_template")
@Accessors(chain = true)
public class SmsTemplate extends BaseEntity {

    /**
     * 签名id
     */
    @TableField("sms_sign_id")
    private Long smsSignId;

    /**
     * 模板类型
     */
    @TableField("type")
    @NotNull(message = "请输入模板类型！")
    private SmsTemplateType smsTemplateType;

    /**
     * 模板名称
     */
    @TableField("template_name")
    @NotNull(message = "请输入模板名称！")
    private String templateName;
    /**
     * 模板code
     */
    @TableField("template_code")
    @NotNull(message = "请输入模板code！")
    private String templateCode;

    /**
     * 模版内容
     */
    @TableField("sms_template_content")
    @NotNull(message = "请输入模版内容!")
    private String smsTemplateContent;


}
