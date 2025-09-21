package com.medusa.gruul.addon.invoice.mp.entity;

import com.medusa.gruul.addon.invoice.model.enums.InvoiceSetType;
import jakarta.validation.constraints.Max;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class InvoiceSetupValue {

    /**
     * 是否选中
     */
    private Boolean option;


    /**
     * 发票设置类型
     */
    private InvoiceSetType invoiceType;


    /**
     * 订单完成天数
     */
    @Max(30)
    private Long orderCompleteDays;
}
