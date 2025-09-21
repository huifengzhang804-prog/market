package com.medusa.gruul.carrier.pigeon.service.modules.sms.handler;

import com.alibaba.fastjson2.JSONObject;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.handler.client.SmsClientService;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.handler.client.impl.AliSmsClientServiceImpl;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.handler.client.impl.TencentSmsClientServiceImpl;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.model.SmsConstant;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.model.enums.SmsPlatformType;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.global.model.exception.GlobalException;
import io.vavr.control.Option;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 短信客户端服务工厂
 *
 * @author 张治保
 * @since 2024/3/6
 */
public class SmsClientServiceFactory {




    /**
     * 获取正在短信客户端 不存在则创建 通过nullToCreate获取默配置 创建客户端，并把其设置为正在使用的配置
     *
     * @param nullToCreate 不存在则创建
     * @return SmsClientService 短信客户端
     */
    public static SmsClientService client(Function<SmsPlatformType, SmsClientConf> nullToCreate) {
        SmsPlatformType platform = getUsingPlatform().getOrElseThrow(() -> new GlobalException("短信未配置"));
        SmsClientService client = client(platform, () -> nullToCreate.apply(platform));
        setUsingPlatform(client.clientConf().getType());
        return client;
    }


    /**
     * 获取短信客户端 不存在则创建 通过nullToCreate获取默配置 创建客户端
     *
     * @param type         短信平台类型
     * @param nullToCreate 不存在则创建
     * @return SmsClientService
     */
    public static SmsClientService client(SmsPlatformType type, Supplier<SmsClientConf> nullToCreate) {
//
        Object cacheMapValue = RedisUtil.getCacheMapValue(SmsConstant.SMS_SIGN_INFO, type.name());
        if (Objects.isNull(cacheMapValue)) {
            cacheMapValue = nullToCreate.get();
            cacheMapValue=JSONObject.toJSONString(cacheMapValue);
            RedisUtil.setCacheMapValue(SmsConstant.SMS_SIGN_INFO, type.name(),cacheMapValue);
        }
        SmsClientConf smsClientConf = JSONObject.parseObject(cacheMapValue.toString(), SmsClientConf.class);
        return createClient(smsClientConf);
    }

    /**
     * 更新配置
     */


    /**
     * 更新配置并使用
     *
     * @param conf 配置
     */
    public static void updateAndUsing(SmsClientConf conf) {
        setUsingPlatform(conf.getType());
    }

    /**
     * 设置当前默认的短信平台
     */
    public static void setUsingPlatform(SmsPlatformType platform) {

        RedisUtil.setCacheObject(SmsConstant.SMS_USING_CONF_KEY, platform.name());

    }

    /**
     * 获取当前在用的短信平台
     *
     * @return 可能为空
     */
    public static Option<SmsPlatformType> getUsingPlatform() {
        return  Option.<String>of(RedisUtil.getCacheObject(SmsConstant.SMS_USING_CONF_KEY))
                .map(SmsPlatformType::valueOf);

    }






    /**
     * 短信客户端
     *
     * @param type         短信平台类型
     * @param nullToCreate 不存在则创建
     * @return SmsClientService
     */
    private static SmsClientService createClient(SmsPlatformType type, Supplier<SmsClientConf> nullToCreate) {
        return createClient(nullToCreate.get().setType(type));
    }

    private static SmsClientService createClient(SmsClientConf conf) {
        SmsPlatformType type = conf.getType();
        return switch (type) {
            case ALIYUN -> new AliSmsClientServiceImpl(conf);
            case TENCENT -> new TencentSmsClientServiceImpl(conf);
            default -> throw new UnsupportedOperationException("暂不支持的短信平台类型:" + type.name());
        };
    }

    public static void removePlatformConfig(SmsPlatformType platform) {
        RedisUtil.getRedisTemplate().opsForHash().delete(SmsConstant.SMS_SIGN_INFO, platform.name());
    }
}
