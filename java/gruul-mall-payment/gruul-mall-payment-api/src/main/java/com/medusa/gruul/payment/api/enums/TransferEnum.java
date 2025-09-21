package com.medusa.gruul.payment.api.enums;

/**
 * 转账状态枚举值
 * @author jipeng
 * @since 2024/11/7
 */
public enum TransferEnum {
    WX_PROCESSING(1, "微信转账处理中"),
    WX_SUCCESS(2, "微信转账成功"),
    WX_CLOSED(3, "微信转账关闭"),
    ALI_PROCESSING(4, "支付宝转账处理中"),
    ALI_SUCCESS(5, "支付宝转账成功"),
    ALI_FAIL(6, "支付宝转账失败"),
    ;
    private final Integer code;
    private final String desc;

    TransferEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
