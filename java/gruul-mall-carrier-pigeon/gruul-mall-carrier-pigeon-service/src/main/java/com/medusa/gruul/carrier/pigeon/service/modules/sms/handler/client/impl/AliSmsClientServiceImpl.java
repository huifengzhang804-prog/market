package com.medusa.gruul.carrier.pigeon.service.modules.sms.handler.client.impl;

import cn.hutool.core.text.StrPool;
import com.alibaba.fastjson2.JSONObject;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import com.aliyun.teaopenapi.models.Config;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.handler.SmsClientConf;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.handler.client.AbstractSmsClientService;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 阿里云 短信客户端服务
 *
 * @author 张治保
 * @since 2024/3/6
 */
@Slf4j
public class AliSmsClientServiceImpl extends AbstractSmsClientService {

    private final static String ENDPOINT = "dysmsapi.aliyuncs.com";
    private final Lock lock = new ReentrantLock();
    private volatile Client client;

    public AliSmsClientServiceImpl(SmsClientConf smsClientConf) {
        super(smsClientConf);
    }

    /**
     * 获取阿里云客户端
     *
     * @return Client 阿里云客户端
     */
    private Client client() {
        if (client != null) {
            return client;
        }
        lock.lock();
        try {
            if (client != null) {
                return client;
            }
            SmsClientConf conf = clientConf();
            Config config = new Config()
                    .setAccessKeyId(conf.getAppId())
                    .setAccessKeySecret(conf.getAppKey())
                    .setEndpoint(ENDPOINT);
            try {
                client = new Client(config);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return client;
        } finally {
            lock.unlock();
        }
    }


    /**
     * 发送短信
     *
     * @param mobiles 手机号 阿里云单次发送最多1000个手机号
     * @param params  参数 有序参数
     */
    @Override
    public void send(Set<String> mobiles, Map<String, String> params) {
        SmsClientConf conf = clientConf();
        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setPhoneNumbers(String.join(StrPool.COMMA, mobiles))
                .setSignName(conf.getSign())
                .setTemplateCode(conf.getTemplateCode())
                .setTemplateParam(new JSONObject(params).toString());
        SendSmsResponse result;
        try {
            result = client().sendSms(sendSmsRequest);
        } catch (Exception e) {
            log.error("阿里云短信发送失败:", e);
            throw new RuntimeException(e);
        }

        SendSmsResponseBody body = result.getBody();
        log.debug(
                """
                        阿里云短信发送结果:
                        code :{}
                        message:{}
                        requestId:{}
                        bizId:{}""",
                body.getCode(),
                body.getMessage(),
                body.getRequestId(),
                body.getBizId()
        );
        if (OK.equalsIgnoreCase(body.getCode())) {
            return;
        }
        throw exception("阿里云短信发送失败", JSONObject.from(result));
    }
}
