package com.medusa.gruul.addon.invoice.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InvoiceStatus {

    /**
     * 申请开票已过期
     */
    REQUEST_HAS_EXPIRED(1),

    /**
     * 申请开票处理中
     */
    REQUEST_IN_PROCESS(2),


    /**
     * 服务端不支持开票
     */
    SERVER_NOT_SUPPORTED(3),

    /**
     * 发票申请失败
     */
    FAILED_INVOICE_REQUEST(4),

    /**
     * 已经成功开票
     */
    SUCCESSFULLY_INVOICED(5),

    /**
     * 可以开票
     */
    ALLOWED_INVOICING(6),

    /**
     * 客户端主动撤销申请
     */
    CLIENT_CANCEL_REQUEST(7)
    ;

    @EnumValue
    private final Integer value;

}
