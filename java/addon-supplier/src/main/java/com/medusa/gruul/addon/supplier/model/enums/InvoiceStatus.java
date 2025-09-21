package com.medusa.gruul.addon.supplier.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InvoiceStatus {
    /**
     * 待开票
     */
    INVOICE_NOT_START(1),

    /**
     * 申请开票处理中
     */
    REQUEST_IN_PROCESS(2),




    /**
     * 发票申请失败
     */
    FAILED_INVOICE_REQUEST(4),

    /**
     * 已经成功开票
     */
    SUCCESSFULLY_INVOICED(5),



    /**
     * 客户端主动撤销申请
     */
    CLIENT_CANCEL_REQUEST(7)
    ;

    @EnumValue
    private final Integer value;

    public static InvoiceStatus getByValue(Integer value) {
        for (InvoiceStatus invoiceStatus : values()) {
            if (invoiceStatus.value.equals(value)) {
                return invoiceStatus;
            }
        }
        throw  new IllegalArgumentException("发票状态不匹配[" + value + "]");
    }

}
