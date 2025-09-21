package com.medusa.gruul.carrier.pigeon.service.modules.sms.handler.client;

import com.medusa.gruul.carrier.pigeon.service.modules.sms.handler.SmsClientConf;

import java.util.Map;
import java.util.Set;

/**
 * @author 张治保
 * @since 2024/3/6
 */
public interface SmsClientService {

    String OK = "OK";

    /**
     * 发送短信
     *
     * @param mobiles 手机号
     * @param params  参数 某些平台需要注意放入顺序 根据 values 顺序 依次渲染参数
     */
    void send(Set<String> mobiles, Map<String, String> params);

    /**
     * 获取客户端配置
     *
     * @return SmsClientConf 客户端配置
     */
    SmsClientConf clientConf();

    /**
     * 关闭客户端 如果需要 则重写
     */
    default void close() {

    }

}
