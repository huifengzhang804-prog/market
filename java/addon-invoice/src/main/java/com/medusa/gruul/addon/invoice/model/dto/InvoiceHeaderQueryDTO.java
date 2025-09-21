package com.medusa.gruul.addon.invoice.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.invoice.model.enums.InvoiceHeaderOwnerTypeEnum;
import com.medusa.gruul.addon.invoice.mp.entity.InvoiceHeader;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class InvoiceHeaderQueryDTO extends Page<InvoiceHeader> {

    /**
     * 发票抬头所属方类型
     */
    @NotNull
    private InvoiceHeaderOwnerTypeEnum ownerType;

    //    /**
//     * 发票所属id
//     */
//    @NotNull
    private Long ownerId;
}
