package com.medusa.gruul.carrier.pigeon.api.model.dto;

import com.medusa.gruul.carrier.pigeon.api.enums.SubscribeMsg;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 小程序订阅消息
 * @author WuDi
 * date: 2023/1/9
 */
@Data
public class SubscribeAppletMsgDTO implements Serializable {

    /**
     * 订阅模板类型
     */
    private SubscribeMsg subscribeMsg;

    /**
     * 微信 open id
     */
    private String openid;

    /**
     * 发送内容
     */
    private Map<String, String> data;


}
