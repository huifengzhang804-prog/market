package com.medusa.gruul.addon.invoice.model.vo;

import com.medusa.gruul.addon.invoice.model.enums.InvoiceHeaderOwnerTypeEnum;
import com.medusa.gruul.addon.invoice.model.enums.InvoiceHeaderTypeEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class InvoiceHeadVO {


    /**
     * id
     */
    private Long id;

    /**
     * 发票类型
     */
    private InvoiceHeaderTypeEnum invoiceHeaderType;

    /**
     * 发票所属id
     */
    private Long ownerId;

    /**
     * 发票抬头所属方类型
     */
    private InvoiceHeaderOwnerTypeEnum ownerType;

    /**
     * 是否默认
     */
    private Boolean isDefault;

    /**
     * 发票抬头
     */
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
    private String email;
}
