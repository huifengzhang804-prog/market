package com.medusa.gruul.carrier.pigeon.service.modules.sms.mp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.StrPool;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.carrier.pigeon.api.enums.PigeonRabbit;
import com.medusa.gruul.carrier.pigeon.api.sms.SmsSendParam;
import com.medusa.gruul.carrier.pigeon.api.sms.enums.SmsTemplateType;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.handler.SmsClientConf;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.handler.SmsClientServiceFactory;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.handler.client.SmsClientService;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.model.SmsConstant;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.model.dto.EditSmsConfigDto;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.model.dto.SmsConfigDto;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.model.dto.SmsCurrentConfigDTO;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.model.enums.SmsPlatformType;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.model.enums.SmsType;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.mp.entity.SmsRecord;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.mp.entity.SmsSign;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.mp.entity.SmsTemplate;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.mp.mapper.SmsSignMapper;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.mp.service.SmsRecordService;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.mp.service.SmsSignService;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.mp.service.SmsTemplateService;
import com.medusa.gruul.common.fastjson2.annotation.Desensitize;
import com.medusa.gruul.common.fastjson2.filter.DesensitizeValueFilter;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.model.SqlHelper;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.global.model.exception.GlobalException;
import io.vavr.control.Try;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author miskw
 * date 2022/12/9
 */
@Service
@RequiredArgsConstructor
public class SmsSignServiceImpl extends ServiceImpl<SmsSignMapper, SmsSign> implements SmsSignService {

    private final SmsTemplateService smsTemplateService;

    private final SmsRecordService smsRecordService;

    private final RabbitTemplate rabbitTemplate;


    /**
     * 发送短信
     *
     * @param param 参数
     */
    @Override
    public void smsSend(SmsSendParam param) {
        //随机获取短信配置的任务
        Function<SmsPlatformType, SmsClientConf> nullToCreate = (platform) -> {
            EditSmsConfigDto smsConfig = this.baseMapper.getConf(platform, param.getTemplateType());
            if (smsConfig == null) {
                throw new GlobalException(SystemCode.DATA_NOT_EXIST_CODE, "当前短信配置不存在!");
            }
            return new SmsClientConf()
                    .setType(platform)
                    .setType(smsConfig.getType())
                    .setAppId(smsConfig.getProviderAppId())
                    .setAppKey(smsConfig.getProviderAppSecret())
                    .setSign(smsConfig.getSignature())
                    .setTemplateCode(smsConfig.getTemplateCode());
        };
        //随机获取短信客户端 服务
        SmsClientService smsClientService = SmsClientServiceFactory.client(nullToCreate);
        //发送失败保存发送记录的任务
        Consumer<Throwable> errorTask = exception -> {
            SmsRecord record = new SmsRecord()
                    .setMobiles(param.getMobiles())
                    .setConf(smsClientService.clientConf())
                    .setParam(new JSONObject(param.getParams()))
                    .setErr(
                            exception instanceof GlobalException globalEx ?
                                    (JSONObject) globalEx.getData() :
                                    JSONObject.of(StrPool.UNDERLINE, exception.getMessage())
                    );
            smsRecordService.save(record);
        };

        //执行短信发送操作
        Try.run(() -> smsClientService.send(param.getMobiles(), param.getParams()))
                .onFailure(errorTask);
    }

    /**
     * 新增或修改短信签名
     *
     * @param smsSign 参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAndEditSmsSign(SmsSign smsSign) {
        //todo 已删除不严谨代码 接口预留
    }

    /**
     * 根据type 获取短信配置信息
     *
     * @param type 短信平台类型
     * @return 短信配置信息
     */
    @Override
    public SmsConfigDto smsConfig(SmsPlatformType type) {
        SmsConfigDto result = getConfByType(type);
        Object enableCaptcha = RedisUtil.getCacheObject(SmsConstant.SMS_CAPTCHA_OPEN_FLAG);
        if (Objects.isNull(enableCaptcha)) {
            enableCaptcha=Boolean.FALSE;
        }
        result.setEnableCaptcha((Boolean) enableCaptcha);
        return result;

    }

    private SmsConfigDto getConfByType(SmsPlatformType type) {
        SmsSign smsSign = this.lambdaQuery().eq(SmsSign::getType, type).one();
        if (smsSign == null) {
            return new SmsConfigDto();
        }
        List<SmsTemplate> templates = smsTemplateService.lambdaQuery()
                .eq(SmsTemplate::getSmsTemplateType, SmsTemplateType.CAPTCHA)
                .eq(SmsTemplate::getSmsSignId, smsSign.getId())
                .list();
        SmsConfigDto smsConfigDto = new SmsConfigDto();
        BeanUtil.copyProperties(smsSign, smsConfigDto);
        if (!CollectionUtils.isEmpty(templates)) {
            smsConfigDto.setSmsTemplate(templates.get(0));
        }
        return smsConfigDto;
    }


    @Override
    public void saveAndEditSmsTemplate(SmsTemplate smsTemplate) {
        //todo 已删除不严谨代码 接口预留
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Redisson(name = SmsConstant.SMS_CONF_LOCK_KEY, key = "#param.type.name()")
    public void saveOrUpdateConf(SmsCurrentConfigDTO param, Boolean enableCaptcha) {
        //设置短信验证码拖拽滑块是否启用
        RedisUtil.setCacheObject(SmsConstant.SMS_CAPTCHA_OPEN_FLAG, enableCaptcha);
        //如果providerAppId为空 则表示是删除操作
        //获取短信平台类型
        SmsPlatformType platformType = param.getType();
        SmsSign smsSign = param.toSmsSign();
        //如果查询到了数据 说明是更新操作
        SmsTemplate smsTemplate = param.getSmsTemplate();

        //直接更新 如果失败说明数据库种不存在此种类型的配置
        final SmsSign dbSign = this.lambdaQuery()
                .eq(SmsSign::getType, platformType)
                .last(SqlHelper.SQL_LIMIT_1)
                .one();

        if (Objects.nonNull(dbSign)) {
            //处理敏感数据
             dealDesensitizationData(smsSign, dbSign);
        }
        RedisUtil.doubleDeletion(
                () -> {
                    if (dbSign != null) {
                        //如果是更新
                        this.lambdaUpdate()
                                .set(SmsSign::getProviderAppId, smsSign.getProviderAppId())
                                .set(SmsSign::getProviderAppSecret, smsSign.getProviderAppSecret())
                                .set(SmsSign::getDescription, smsSign.getDescription())
                                .set(SmsSign::getSmsType, smsSign.getSmsType())
                                .set(SmsSign::getSignature, smsSign.getSignature())
                                .eq(SmsSign::getId, dbSign.getId())
                                .update();
                        smsTemplateService.lambdaUpdate()
                                .set(SmsTemplate::getTemplateName, smsTemplate.getTemplateName())
                                .set(SmsTemplate::getTemplateCode, smsTemplate.getTemplateCode())
                                .set(SmsTemplate::getSmsTemplateContent, smsTemplate.getSmsTemplateContent())
                                .eq(SmsTemplate::getSmsTemplateType, smsTemplate.getSmsTemplateType())
                                .eq(SmsTemplate::getSmsSignId, dbSign.getId())
                                .update();
                        SmsPlatformType smsPlatformType = SmsClientServiceFactory.getUsingPlatform().getOrNull();

                        if (platformType.equals(smsPlatformType) &&
                                !dbSign.getSmsType().equals(smsSign.getSmsType())) {
                            //只有处于启用状态的供应商且短信类型改变 则发送消息通知
                            rabbitTemplate.convertAndSend(PigeonRabbit.SMS_SIMULATION.EXCHANGE,
                                    PigeonRabbit.SMS_SIMULATION.routingKey(),dbSign.getSmsType().getVirtual());
                        }


                    } else {
                        //未查到是新增操作
                        SmsSign newSign = new SmsSign()
                                .setType(platformType)
                                .setSmsType(smsSign.getSmsType())
                                .setDescription(smsSign.getDescription())
                                .setProviderAppId(smsSign.getProviderAppId())
                                .setProviderAppSecret(smsSign.getProviderAppSecret())
                                .setSignature(smsSign.getSignature());
                        this.save(newSign);
                        smsTemplate.setSmsSignId(newSign.getId());
                        smsTemplateService.save(smsTemplate);
                    }
                    //在编辑的时候删除缓存中指定平台的field
                    //SmsClientServiceFactory.removePlatformConfig(platformType);
                    SmsClientServiceFactory.removePlatformConfig(platformType);
                }

        );
    }

    @Override
    public List<SmsSign> smsList() {
        List<SmsSign> list = lambdaQuery().list();
        if (CollectionUtil.isEmpty(list)) {
            return list;
        }
        SmsPlatformType smsPlatformType = SmsClientServiceFactory.getUsingPlatform().getOrNull();
        if (smsPlatformType != null) {
            list.forEach(conf -> {
                if (smsPlatformType.equals(conf.getType())) {
                    conf.setUsing(true);
                }else {
                    conf.setUsing(false);
                }
            });
        }
        return list;
    }

    @Override
    public void removeBySmsPlatformType(SmsPlatformType type) {
        SmsSign one = lambdaQuery().eq(SmsSign::getType, type).one();
        if (Objects.isNull(one)) {
            return;
        }
        //当前正在使用的短信平台
        SmsPlatformType smsPlatformType = SmsClientServiceFactory.getUsingPlatform().getOrNull();
        if (one.getType().equals(smsPlatformType)) {
            throw new GlobalException("当前短信平台正在使用中，无法删除");
        }
        //删除短信配置 与相应的模板数据
        lambdaUpdate().eq(SmsSign::getId, one.getId()).remove();
        smsTemplateService.lambdaUpdate().eq(SmsTemplate::getSmsSignId, one.getId()).remove();


    }

    @Override
    public void using(SmsPlatformType type) {
        SmsConfigDto conf = getConfByType(type);
        if (Objects.isNull(type)) {
            throw new GlobalException("平台配置数据不存在，无法启用");
        }
        //更新保存配置 并将其设置为正在使用中
       SmsClientServiceFactory.updateAndUsing(
                new SmsClientConf()
                        .setType(type)
                        .setAppId(conf.getProviderAppId())
                        .setAppKey(conf.getProviderAppSecret())
                        .setSign(conf.getSignature())
                        .setTemplateCode(conf.getSmsTemplate().getTemplateCode())
        );
        //同步短信平台配置是真实短信还是模拟短信到UAA

        rabbitTemplate.send(PigeonRabbit.SMS_SIMULATION.EXCHANGE,
                PigeonRabbit.SMS_SIMULATION.routingKey(),
                new Message(conf.getSmsType().getVirtual().toString().getBytes(StandardCharsets.UTF_8)));

    }

    @Override
    public Boolean querySmsSimulationFlag() {
        SmsPlatformType smsPlatformType = SmsClientServiceFactory.getUsingPlatform().getOrNull();
        if (Objects.isNull(smsPlatformType)) {
            //没有启用短信时 使用模拟短信
            return Boolean.TRUE;
        }
        SmsSign one = lambdaQuery().eq(SmsSign::getType, smsPlatformType).one();
        if (Objects.isNull(one)) {
            return Boolean.TRUE;
        }
        //当前启用的短信是否是模拟短信
        return one.getSmsType().equals(SmsType.VIRTUAL);
    }

    private void dealDesensitizationData(SmsSign conf, SmsSign dbSmsSign) {
        Field[] declaredFields = conf.getClass().getDeclaredFields();
        String updateFieldValue;
        String dbFileValue;
        for (Field declaredField : declaredFields) {
            Desensitize annotation = declaredField.getAnnotation(Desensitize.class);
            if (Objects.isNull(annotation)) {
                continue;
            }
            try {
                declaredField.setAccessible(true);
                updateFieldValue= (String) declaredField.get(conf);
                dbFileValue= (String) declaredField.get(dbSmsSign);
                if (StringUtils.equals(updateFieldValue,
                        DesensitizeValueFilter.hide(annotation,dbFileValue))) {
                    //如果数据库中字段经过脱敏后处理与前端传递过来的数据一致，说明字段没有发生变化 直接用数据库中的值
                    declaredField.set(conf,dbFileValue);
                }
            } catch (IllegalAccessException e) {
                SystemCode.FAILURE.exception(e);
            }

        }
    }

}
