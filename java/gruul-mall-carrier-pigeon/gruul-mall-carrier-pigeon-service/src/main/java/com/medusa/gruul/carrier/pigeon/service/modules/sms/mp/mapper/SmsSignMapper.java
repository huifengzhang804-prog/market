package com.medusa.gruul.carrier.pigeon.service.modules.sms.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.carrier.pigeon.api.sms.enums.SmsTemplateType;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.model.dto.EditSmsConfigDto;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.model.enums.SmsPlatformType;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.mp.entity.SmsSign;
import org.apache.ibatis.annotations.Param;

/**
 * @author miskw
 * @date 2022/12/9
 */
public interface SmsSignMapper extends BaseMapper<SmsSign> {
    /**
     * 根据短信类型随机取一个短信配置 用于 发送短信
     *
     * @param platform 短信平台类型
     * @param template 短信模板类型
     * @return 短信配置
     */
    EditSmsConfigDto getConf(@Param("platform") SmsPlatformType platform, @Param("template") SmsTemplateType template);
}
