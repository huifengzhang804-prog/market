package com.medusa.gruul.addon.invoice.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.invoice.model.enums.InvoiceStatus;
import com.medusa.gruul.addon.invoice.model.enums.InvoiceType;
import com.medusa.gruul.addon.invoice.model.enums.SortEnum;
import com.medusa.gruul.addon.invoice.mp.entity.InvoiceRequest;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class InvoiceRequestQueryDTO extends Page<InvoiceRequest> {


    /**
     * id
     */
    private Long id;

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 发票抬头
     */
    private String header;


    /**
     * 税号
     */
    private String taxIdentNo;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 发票类型
     */
    private InvoiceType invoiceType;

    /**
     * 申请开始时间
     */
    private LocalDateTime applicationStartTime;


    /**
     * 申请结束时间
     */
    private LocalDateTime applicationEndTime;


    /**
     * 发票状态
     */
    private InvoiceStatus invoiceStatus;

    /**
     * 排序字段
     */
    private SortEnum sortEnum;
    /**
     * 开票开始时间
     */
    private LocalDateTime invoiceStartTime;
    /**
     * 开票结束时间
     */
    private LocalDateTime invoiceEndTime;
}
