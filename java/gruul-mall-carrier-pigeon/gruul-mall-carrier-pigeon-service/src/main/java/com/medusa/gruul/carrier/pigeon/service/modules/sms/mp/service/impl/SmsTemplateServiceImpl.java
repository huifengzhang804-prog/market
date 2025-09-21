package com.medusa.gruul.carrier.pigeon.service.modules.sms.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.mp.entity.SmsTemplate;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.mp.mapper.SmsTemplateMapper;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.mp.service.SmsTemplateService;
import org.springframework.stereotype.Service;

/**
 * @author miskw
 * @date 2022/12/9
 */
@Service
public class SmsTemplateServiceImpl extends ServiceImpl<SmsTemplateMapper, SmsTemplate> implements SmsTemplateService {
}
