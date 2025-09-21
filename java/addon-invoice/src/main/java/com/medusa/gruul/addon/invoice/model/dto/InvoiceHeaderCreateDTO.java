package com.medusa.gruul.addon.invoice.model.dto;

import com.medusa.gruul.addon.invoice.model.enums.InvoiceHeaderOwnerTypeEnum;
import com.medusa.gruul.addon.invoice.model.enums.InvoiceHeaderTypeEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class InvoiceHeaderCreateDTO {


    /**
     * 发票抬头id
     */
    private Long id;

    /**
     * 发票抬头所属方类型
     */
    @NotNull
    private InvoiceHeaderOwnerTypeEnum ownerType;

    /**
     * 发票抬头所属者ID
     */
    @NotNull
    private Long ownerId;

    /**
     * 发票抬头类型
     */
    @NotNull
    private InvoiceHeaderTypeEnum invoiceHeaderType;


    /**
     * 发票抬头
     */
    @NotNull
    private String header;

    /**
     * 税号
     */
    private String taxIdentNo;

    /**
     * 开户行
     */
    private String openingBank;

    /**
     * 银行账号
     */
    private String bankAccountNo;

    /**
     * 企业电话
     */
    private String enterprisePhone;

    /**
     * 企业地址
     */
    private String enterpriseAddress;

    /**
     * 邮箱地址
     */
    @NotNull
    private String email;
}
