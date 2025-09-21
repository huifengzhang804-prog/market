package com.medusa.gruul.carrier.pigeon.service.modules.sms.handler.client.impl;

import com.alibaba.fastjson2.JSONObject;
import com.github.qcloudsms.SmsMultiSender;
import com.github.qcloudsms.SmsMultiSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.handler.SmsClientConf;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.handler.client.AbstractSmsClientService;
import com.medusa.gruul.common.model.constant.CommonPool;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 腾讯 短信客户端服务
 *
 * @author 张治保
 * @since 2024/3/6
 */
@Slf4j
public class TencentSmsClientServiceImpl extends AbstractSmsClientService {

    private final Lock lock = new ReentrantLock();
    private volatile SmsMultiSender client;

    public TencentSmsClientServiceImpl(SmsClientConf smsClientConf) {
        super(smsClientConf);
    }


    /**
     * 获取客户端 配置
     *
     * @return SmsClientConf 客户端配置
     */
    private SmsMultiSender client() {
        if (client != null) {
            return client;
        }
        lock.lock();
        try {
            if (client != null) {
                return client;
            }
            SmsClientConf conf = clientConf();
            client = new SmsMultiSender(Integer.parseInt(conf.getAppId()), conf.getAppKey());
            return client;
        } finally {
            lock.unlock();
        }
    }


    /**
     * 发送短信
     *
     * @param mobiles 手机号 腾讯云单次发送最多200个手机号
     * @param params  参数 要求有序参数
     */
    @Override
    public void send(Set<String> mobiles, Map<String, String> params) {
        SmsMultiSenderResult result;
        try {
            result = client().sendWithParam(
                    CommonPool.NATION_CODE,
                    new ArrayList<>(mobiles),
                    Integer.parseInt(clientConf().getTemplateCode()),
                    new ArrayList<>(params.values()),
                    clientConf().getSign(),
                    null,
                    null
            );
        } catch (HTTPException | IOException | JSONException e) {
            log.error("腾讯云短信发送失败:", e);
            throw new RuntimeException(e);
        }
        log.debug("""
                        腾讯云短信发送结果:
                        result :{}
                        errMsg:{}
                        ext:{}
                        details：{}"""
                , result.result, result.errMsg, result.ext, result.details
        );
        if (OK.equals(result.errMsg)) {
            return;
        }
        throw exception("腾讯云短信发送失败", JSONObject.from(result));

    }
}
