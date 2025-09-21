package com.medusa.gruul.addon.invoice.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.addon.invoice.model.enums.InvoiceHeaderTypeEnum;
import com.medusa.gruul.addon.invoice.model.enums.InvoiceOwnerTypeEnum;
import com.medusa.gruul.addon.invoice.model.enums.InvoiceStatus;
import com.medusa.gruul.addon.invoice.model.enums.InvoiceType;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 发票申请表
 * </p>
 *
 * @since 2023-08-07
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_invoice_request")
public class InvoiceRequest extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 申请人id
     */
    private Long applicantId;

    /**
     * 申请人店铺id
     */
    private Long shopId;

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
     * 发票类型
     */
    private InvoiceHeaderTypeEnum invoiceHeaderType;

    /**
     * 申请时间
     */
    private LocalDateTime applicationTime;
    /**
     * 开票时间
     */
    @TableField(value = "invoice_time")
    private LocalDateTime invoiceTime;

    /**
     * 开票备注
     */
    private String billingRemarks;

    /**
     * 拒绝原因
     */
    private String denialReason;

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
