package com.medusa.gruul.addon.supplier.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.medusa.gruul.addon.supplier.model.enums.InvoiceStatus;
import com.medusa.gruul.addon.supplier.model.enums.OrderQueryStatus;
import com.medusa.gruul.addon.supplier.mp.entity.SupplierOrder;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author 张治保
 * date 2023/7/21
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OrderQueryPageDTO extends Page<SupplierOrder> {

    /**
     * 店铺 id
     */
    private Long shopId;

    /**
     * 供应商 id
     */
    private Long supplierId;

    /**
     * 订单状态
     */
    private OrderQueryStatus status;

    /**
     * 订单号
     */
    private String no;

    /**
     * 采购商
     */
    private String purchaser;

    /**
     * 下单开始时间
     */
    private String startTime;

    /**
     * 下单结束时间
     */
    private String endTime;


    /**
     * 是否需要联查供应商信息
     */
    private Boolean needSupplier = Boolean.FALSE;
    /**
     * 导出功能的标记
     */
    private Boolean export = Boolean.FALSE;
    /**
     * 导出订单ids
     */

    private List<String> exportOrderIds;
    /**
     * 采购员手机号
     */
    private String purchasePhone;
    /**
     * 发票状态
     */
    private InvoiceStatus invoiceStatus;

    private List<InvoiceStatus> invoiceStatusList= Lists.newArrayList();


}
