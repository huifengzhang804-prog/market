package com.medusa.gruul.addon.invoice.model.dto;

import com.medusa.gruul.addon.invoice.model.enums.InvoiceHeaderOwnerTypeEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


/**
 * 设置默认发票抬头DTO
 */
@Getter
@Setter
public class SetDefaultInvoiceHeaderDTO {

    /**
     * 发票抬头ID
     */
    @NotNull
    private Long id;

    /**
     * 发票抬头拥有者ID
     */
    @NotNull
    private Long ownerId;

    /**
     * 发票抬头拥有者类型
     */
    @NotNull
    private InvoiceHeaderOwnerTypeEnum ownerType;
}
