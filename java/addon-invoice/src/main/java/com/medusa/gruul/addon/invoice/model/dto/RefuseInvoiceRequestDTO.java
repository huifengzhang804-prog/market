package com.medusa.gruul.addon.invoice.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class RefuseInvoiceRequestDTO {

    /**
     * 发票申请ID
     */
    @NotNull
    private Long id;

    /**
     * 拒绝原因
     */
    @NotBlank
    @Size(max = 15)
    private String denialReason;

}
