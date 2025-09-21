package com.medusa.gruul.addon.invoice.model.vo;

import com.medusa.gruul.addon.invoice.model.enums.InvoiceStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 尝试申请开票返回数据VO
 */
@Getter
@Setter
@Accessors(chain = true)
public class TryInvoiceRequestVO {

    private InvoiceStatus invoiceStatus;

    private Long billMoney;

    private Long id;
}
