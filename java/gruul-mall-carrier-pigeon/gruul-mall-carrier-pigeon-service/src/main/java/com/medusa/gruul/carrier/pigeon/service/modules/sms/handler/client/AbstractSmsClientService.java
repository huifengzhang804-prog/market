package com.medusa.gruul.carrier.pigeon.service.modules.sms.handler.client;

import com.alibaba.fastjson2.JSONObject;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.handler.SmsClientConf;
import com.medusa.gruul.global.model.exception.GlobalException;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * @since 2024/3/6
 */
@RequiredArgsConstructor
public abstract class AbstractSmsClientService implements SmsClientService {

    private final SmsClientConf smsClientConf;


    /**
     * 获取客户端配置
     *
     * @return SmsClientConf 客户端配置
     */
    @Override
    public SmsClientConf clientConf() {
        return smsClientConf;
    }


    /**
     * @param msg  异常消息
     * @param data 异常数据
     * @return GlobalException
     */
    protected GlobalException exception(String msg, JSONObject data) {
        GlobalException globalException = new GlobalException(msg);
        globalException.setData(data);
        return globalException;
    }
}
