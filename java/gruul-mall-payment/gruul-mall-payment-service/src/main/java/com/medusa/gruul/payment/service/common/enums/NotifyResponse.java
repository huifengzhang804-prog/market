package com.medusa.gruul.payment.service.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * date 2021/12/16
 */
@Getter
@RequiredArgsConstructor
public enum NotifyResponse implements INotifyResponse {
    /**
     * 微信支付响应数据
     */
    WECHAT_SUCCESS("SUCCESS","成功"),
    WECHAT_FAIL("FAIL","失败"),
    ALI_SUCCESS("success","成功"),
    ALI_FAIL("fail","失败"),

    /**
     * 其他平台自行定义,作为补充使用
     */
    OTHER_SUCCESS("success","成功"),
    OTHER_FAIL("fail","失败");
    private final String code;
    private final String msg;

}
