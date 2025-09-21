package com.medusa.gruul.carrier.pigeon.service.modules.sms.handler;

import com.medusa.gruul.carrier.pigeon.service.modules.sms.model.enums.SmsPlatformType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * @since 2024/3/6
 */

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class SmsClientConf implements Serializable {

    /**
     * 短信平台类型
     */
    private SmsPlatformType type;

    /**
     * app id
     */
    private String appId;

    /**
     * app key
     */
    private String appKey;

    /**
     * 短信签名
     */
    private String sign;

    /**
     * 短信模板编码
     */
    private String templateCode;
}
