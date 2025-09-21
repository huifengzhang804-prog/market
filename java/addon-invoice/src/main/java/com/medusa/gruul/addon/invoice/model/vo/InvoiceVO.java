package com.medusa.gruul.addon.invoice.model.vo;

import com.medusa.gruul.addon.invoice.model.enums.InvoiceHeaderTypeEnum;
import com.medusa.gruul.addon.invoice.model.enums.InvoiceOwnerTypeEnum;
import com.medusa.gruul.addon.invoice.model.enums.InvoiceStatus;
import com.medusa.gruul.addon.invoice.model.enums.InvoiceType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class InvoiceVO {

    private Long id;

    /**
     * 申请人id
     */
    private Long applicantId;

    /**
     * 申请人店铺id
     */
    private Long applicantShopId;

    /**
     * 发票所属类型
     */
    private InvoiceOwnerTypeEnum invoiceOwnerType;


    /**
     * 店铺或供应商名称
     */
    private String shopSupplierName;

    /**
     * 订单号
     */
    private String orderNo;


    /**
     * 发票金额
     */
    private Long invoiceAmount;

    /**
     * 发票类型
     */
    private InvoiceType invoiceType;

    /**
     * 发票状态
     */
    private InvoiceStatus invoiceStatus;




    /**
     * 申请时间
     */
    private LocalDateTime applicationTime;

    /**
     * 开票备注
     */
    private String billingRemarks;

    /**
     * 拒绝原因
     */
    private String denialReason;



    /**
     * 申请时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    private List<String> attachments;

    /**
     * 发票抬头类型
     */
    private InvoiceHeaderTypeEnum invoiceHeaderType;

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
