package com.medusa.gruul.addon.invoice.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ReSendInvoiceAttachmentDTO {

    /**
     * 发票申请表id
     */
    @NotNull
    private Long invoiceRequestId;

    /**
     * 店铺id
     */
    @NotNull
    private Long shopId;

}
