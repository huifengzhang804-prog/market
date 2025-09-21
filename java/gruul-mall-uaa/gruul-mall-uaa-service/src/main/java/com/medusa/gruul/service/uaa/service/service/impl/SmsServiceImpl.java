package com.medusa.gruul.service.uaa.service.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.carrier.pigeon.api.rpc.PigeonChatStatisticsRpcService;
import com.medusa.gruul.carrier.pigeon.api.sms.SmsSendParam;
import com.medusa.gruul.carrier.pigeon.api.sms.enums.SmsRabbit;
import com.medusa.gruul.carrier.pigeon.api.sms.enums.SmsTemplateType;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.service.uaa.api.constant.UaaConstant;
import com.medusa.gruul.service.uaa.api.enums.SmsType;
import com.medusa.gruul.service.uaa.service.model.enums.UaaError;
import com.medusa.gruul.service.uaa.service.properties.UaaProperties;
import com.medusa.gruul.service.uaa.service.service.SmsService;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 短信服务
 *
 * @author 张治保
 * date 2022/10/25
 */
@Service
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {


    private final RabbitTemplate rabbitTemplate;
    private final PigeonChatStatisticsRpcService pigeonChatStatisticsRpcService;

    /**
     * 是否开启短信验证码模拟发送
     */
    private Boolean smsSimulation;

    @Override
    public String sendSmsCode(SmsType smsType, String mobile) {
        String code = RandomUtil.randomNumbers(UaaConstant.SMS_CODE_LENGTH);
        RedisUtil.setCacheObject(
                RedisUtil.key(smsType.getKey(), mobile), code, 5, TimeUnit.MINUTES
        );
        if (Objects.isNull(smsSimulation)) {
            smsSimulation=pigeonChatStatisticsRpcService.querySmsSimulationFlag();
        }
        if (smsSimulation) {
            return code;
        }
        SmsSendParam smsSendParam = new SmsSendParam()
                .setMobiles(Set.of(mobile))
                .smsCode(code)
                .setTemplateType(SmsTemplateType.CAPTCHA);
        rabbitTemplate.convertAndSend(SmsRabbit.SMS_SEND.exchange(), SmsRabbit.SMS_SEND.routingKey(), smsSendParam);
        return null;
    }

    @Override
    public void checkSmsCode(SmsType smsType, String mobile, String inputCode) {
        String smsCodeKey = RedisUtil.key(smsType.getKey(), mobile);
        Boolean deleteKey = Boolean.FALSE;
        try {
            String code = RedisUtil.getCacheObject(smsCodeKey);
            if (code == null) {
                throw UaaError.SMS_CODE_EXPIRED.exception();
            }
            if (!StrUtil.equals(code, inputCode)) {
                throw UaaError.SMS_CODE_ERROR.exception();
            }
            deleteKey = Boolean.TRUE;
        } finally {
            if (deleteKey) {
                RedisUtil.delete(smsCodeKey);
            }
        }
    }

    public void setSmsSimulation(Boolean smsSimulation) {
        this.smsSimulation = smsSimulation;
    }

    @EventListener
    public void onContextRefreshed(ContextRefreshedEvent event) {
        try {
            //获取短信模拟开关
            smsSimulation = pigeonChatStatisticsRpcService.querySmsSimulationFlag();
        } catch (Exception e) {
            // ignore
        }

    }


}
