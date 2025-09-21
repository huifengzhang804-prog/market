package com.medusa.gruul.addon.invoice.model.dto;

import com.medusa.gruul.addon.invoice.model.enums.InvoiceOwnerTypeEnum;
import com.medusa.gruul.addon.invoice.model.enums.InvoiceStatus;
import com.medusa.gruul.addon.invoice.model.enums.InvoiceType;
import com.medusa.gruul.addon.invoice.mp.entity.InvoiceHeader;
import com.medusa.gruul.addon.invoice.mp.entity.InvoiceRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Getter
@Setter
@Accessors(chain = true)
public class InvoiceRequestCreateDTO {

    /**
     * 申请人id
     */
    private Long applicantId;

    /**
     * 申请人店铺id
     */
    @NotNull
    private Long applicantShopId;

    /**
     * 发票所属类型
     */
    @NotNull
    private InvoiceOwnerTypeEnum invoiceOwnerType;

    /**
     * 店铺或供应商名称
     */
    private String shopSupplierName;

    /**
     * 订单号
     */
    @NotNull
    private String orderNo;

    /**
     * 申请时间
     */
    private LocalDateTime applicationTime;

    /**
     * 发票金额
     */
    private Long invoiceAmount;

    /**
     * 发票类型
     */
    private InvoiceType invoiceType;

    /**
     * 开票备注
     */
    @Size(max = 500)
    private String billingRemarks;

    /**
     * 拒绝原因
     */
    private String denialReason;

    /**
     * 发票抬头id
     */
    @NotNull
    private Long invoiceHeaderId;


    public InvoiceRequest newInvoiceRequest(Long invoiceAmount, Long userId, InvoiceHeader invoiceHeader) {
        return new InvoiceRequest()
                .setInvoiceType(this.getInvoiceType())
                .setApplicantId(userId)
                .setShopId(applicantShopId)
                .setInvoiceOwnerType(invoiceOwnerType)
                .setInvoiceAmount(invoiceAmount)
                .setShopSupplierName(shopSupplierName)
                .setOrderNo(orderNo)
                .setInvoiceStatus(InvoiceStatus.REQUEST_IN_PROCESS)
                .setApplicationTime(LocalDateTime.now())
                .setInvoiceHeaderType(invoiceHeader.getType())
                .setHeader(invoiceHeader.getHeader())
                .setTaxIdentNo(invoiceHeader.getTaxIdentNo())
                .setOpeningBank(invoiceHeader.getOpeningBank())
                .setBankAccountNo(invoiceHeader.getBankAccountNo())
                .setEnterprisePhone(invoiceHeader.getEnterprisePhone())
                .setEnterpriseAddress(invoiceHeader.getEnterpriseAddress())
                .setEmail(invoiceHeader.getEmail())
                .setBillingRemarks(this.getBillingRemarks());


    }

    public InvoiceRequest newInvoiceRequest(Long invoiceAmount, InvoiceHeader invoiceHeader) {
        return new InvoiceRequest()
                .setInvoiceType(this.getInvoiceType())
                .setShopId(applicantShopId)
                .setInvoiceOwnerType(invoiceOwnerType)
                .setInvoiceAmount(invoiceAmount)
                .setShopSupplierName(shopSupplierName)
                .setOrderNo(orderNo)
                .setInvoiceStatus(InvoiceStatus.REQUEST_IN_PROCESS)
                .setApplicationTime(LocalDateTime.now())
                .setInvoiceHeaderType(invoiceHeader.getType())
                .setHeader(invoiceHeader.getHeader())
                .setTaxIdentNo(invoiceHeader.getTaxIdentNo())
                .setOpeningBank(invoiceHeader.getOpeningBank())
                .setBankAccountNo(invoiceHeader.getBankAccountNo())
                .setEnterprisePhone(invoiceHeader.getEnterprisePhone())
                .setEnterpriseAddress(invoiceHeader.getEnterpriseAddress())
                .setEmail(invoiceHeader.getEmail())
                .setBillingRemarks(this.getBillingRemarks());


    }
}
