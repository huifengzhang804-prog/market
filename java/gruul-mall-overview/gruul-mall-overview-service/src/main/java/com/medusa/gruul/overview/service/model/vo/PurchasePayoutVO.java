package com.medusa.gruul.overview.service.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p></p>
 *
 * @author Andy.Yan
 */
@Getter
@Setter
@Accessors(chain = true)
public class PurchasePayoutVO {

    /**
     * 交易流水号
     */
    private String tradeNo;

    /**
     * 金额
     */
    private Long amount;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 采购订单号
     */
    private String purchaseOrderNo;

    /**
     * 支付时间
     */
    private LocalDateTime paidTime;

    private Long supplierId;
}
