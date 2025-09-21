package com.medusa.gruul.addon.invoice.model.dto;

import com.medusa.gruul.addon.invoice.model.enums.InvoiceSettingsClientType;
import com.medusa.gruul.addon.invoice.mp.entity.InvoiceSetupValue;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class InvoiceSettingsDTO {


    /**
     * id
     */
    private Long id;

    /**
     * 店铺id
     */
    @NotNull
    private Long shopId;

    /**
     * 发票设置json
     */
    @Valid
    @NotNull
    @Size(min = 1, max = 1)
    private List<InvoiceSetupValue> invoiceSetupValue;

    /**
     * 所属客户端
     */
    private InvoiceSettingsClientType invoiceSettingsClientType;
}
