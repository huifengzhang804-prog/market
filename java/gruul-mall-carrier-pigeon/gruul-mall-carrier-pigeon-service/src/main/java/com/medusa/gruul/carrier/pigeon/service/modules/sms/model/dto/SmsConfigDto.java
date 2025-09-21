package com.medusa.gruul.carrier.pigeon.service.modules.sms.model.dto;

import com.medusa.gruul.carrier.pigeon.service.modules.sms.mp.entity.SmsSign;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.mp.entity.SmsTemplate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * @author miskw
 * @date 2022/12/9
 * @describe 编辑与保存短信配置dto
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class SmsConfigDto extends SmsSign {
    /**
     * 短信模板数据
     */

    private SmsTemplate smsTemplate;
    /**
     * 是否启用拖拽滑块
     */
    private Boolean enableCaptcha;

}
