package com.medusa.gruul.addon.invoice.service;

import com.medusa.gruul.addon.invoice.model.dto.InvoiceAttachmentDTO;
import com.medusa.gruul.addon.invoice.model.dto.ReSendInvoiceAttachmentDTO;
import jakarta.servlet.http.HttpServletResponse;


public interface InvoiceAttachmentHandlerService {

    /**
     * 上传发票附件
     *
     * @param invoiceAttachmentDTO 发票附件参数
     */
    void uploadInvoiceAttachment(InvoiceAttachmentDTO invoiceAttachmentDTO);

    /**
     * 下载发票附件
     *
     * @param invoiceAttachmentDTO 发票附件参数
     * @param response             响应
     */
    void downloadInvoiceAttachment(InvoiceAttachmentDTO invoiceAttachmentDTO, HttpServletResponse response);

    /**
     * 重新发送发票附件到邮箱
     *
     * @param dto {@link ReSendInvoiceAttachmentDTO}
     */
    void reSendAttachment(ReSendInvoiceAttachmentDTO dto);
}
