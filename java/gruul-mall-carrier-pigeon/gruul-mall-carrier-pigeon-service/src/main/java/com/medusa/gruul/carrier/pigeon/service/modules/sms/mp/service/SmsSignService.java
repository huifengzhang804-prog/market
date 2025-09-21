package com.medusa.gruul.carrier.pigeon.service.modules.sms.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.carrier.pigeon.api.sms.SmsSendParam;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.model.dto.SmsConfigDto;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.model.dto.SmsCurrentConfigDTO;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.model.enums.SmsPlatformType;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.mp.entity.SmsSign;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.mp.entity.SmsTemplate;
import java.util.List;

/**
 * @author miskw
 * @date 2022/12/9
 */
public interface SmsSignService extends IService<SmsSign> {
    /**
     * 发送短信
     *
     * @param msg 参数
     */
    void smsSend(SmsSendParam msg);

    /**
     * 新增或修改短信签名
     *
     * @param smsSign 参数
     */
    void saveAndEditSmsSign(SmsSign smsSign);

    /**
     * 根据type 获取短信配置信息
     *
     * @param type 服务商类型
     * @return 短信配置
     */
    SmsConfigDto smsConfig(SmsPlatformType type);

    /**
     * 新增或修改短信模板
     *
     * @param smsTemplate 短信模板参数
     */
    void saveAndEditSmsTemplate(SmsTemplate smsTemplate);

    /**
     * 保存短信配置 【过渡使用】
     *
     * @param smsCurrentConfigDto 短信配置参数
     * @param enableCaptcha
     */
    void saveOrUpdateConf(SmsCurrentConfigDTO smsCurrentConfigDto, Boolean enableCaptcha);

    /**
     * 获取短信配置列表信息
     * @return
     */
    List<SmsSign> smsList();

    /**
     * 移除指定平台的短信配置
     * @param type
     */
    void removeBySmsPlatformType(SmsPlatformType type);

    /**
     * 使用指定供应商平台短信配置
     * @param type
     */
    void using(SmsPlatformType type);

    /**
     * 查询当前启用的短信是模拟还是真实
     * @return
     */
    Boolean querySmsSimulationFlag();
}
