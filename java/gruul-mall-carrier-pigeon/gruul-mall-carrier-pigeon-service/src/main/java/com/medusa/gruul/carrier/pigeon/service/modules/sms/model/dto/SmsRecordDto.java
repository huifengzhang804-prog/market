package com.medusa.gruul.carrier.pigeon.service.modules.sms.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.carrier.pigeon.api.sms.enums.SmsTemplateType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author miskw
 * @date 2022/12/12
 */
@Data
public class SmsRecordDto extends Page<Object> {
    /**
     * 发送短信手机号
     */
    private String smsSendMobiles;
    /**
     * 短信类型
     */
    private SmsTemplateType type;
    /**
     * 供应商名称 阿里 || 腾讯云
     */
    private String providerName;
    /**
     * 短信发送时间 左区间
     */
    private LocalDateTime smsSendTimeLeft;
    /**
     * 短信发送时间 右区间
     */
    private LocalDateTime smsSendTimeRight;
    /**
     * 短信发送是否成功
     * TRUE 发送失败的短信
     * FALSE 发送成功的短信
     */
    private Boolean isErr;

}
