package com.medusa.gruul.addon.invoice.handler;

import com.medusa.gruul.addon.invoice.model.dto.InvoiceRequestCreateDTO;
import com.medusa.gruul.addon.invoice.model.dto.InvoiceTryRequestDTO;
import com.medusa.gruul.addon.invoice.model.vo.TryInvoiceRequestVO;

public interface InvoiceRequestHandler {

    void invoiceRequest(InvoiceRequestCreateDTO invoiceRequest);

    /**
     * 尝试申请发票
     * @param invoiceRequest {@link InvoiceRequestCreateDTO}
     * @return {@link TryInvoiceRequestVO}
     */
    TryInvoiceRequestVO tryRequestInvoice(InvoiceTryRequestDTO invoiceRequest);
}
