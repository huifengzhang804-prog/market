package com.medusa.gruul.carrier.pigeon.service.modules.sms.model.dto;

import lombok.Data;

/**
 * @author xiaoq
 * @Description CallbackDTO.java
 * @date 2023-11-21 13:48
 */
@Data
public class CallbackDTO {
    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 国家（或地区）码
     */
    private String nationcode;


    /**
     * 短信签名
     */
    private String sign;

    /**
     * 用户回复内容
     */
    private String text;

    /**
     * UNIX 时间戳（单位：秒）
     */
    private Long time;
}
