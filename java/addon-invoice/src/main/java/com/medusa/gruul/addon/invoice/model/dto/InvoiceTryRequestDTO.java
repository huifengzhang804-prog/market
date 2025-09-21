package com.medusa.gruul.addon.invoice.model.dto;

import com.medusa.gruul.addon.invoice.model.enums.InvoiceOwnerTypeEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class InvoiceTryRequestDTO {

    /**
     * 申请人id
     */
    private Long applicantId;

    /**
     * 申请人店铺id
     */
    @NotNull
    private Long applicantShopId;

    /**
     * 发票所属类型
     */
    @NotNull
    private InvoiceOwnerTypeEnum invoiceOwnerType;


    /**
     * 订单号
     */
    @NotNull
    private String orderNo;
}
