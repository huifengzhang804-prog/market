package com.medusa.gruul.addon.invoice.model.dto;

import com.medusa.gruul.common.web.valid.group.Create;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InvoiceAttachmentDTO {

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

    /**
     * 发票附件URL地址
     */
    @Size(max = 5)
    @NotNull(groups = Create.class)
    private List<String> invoiceAttachmentUrl;
}
