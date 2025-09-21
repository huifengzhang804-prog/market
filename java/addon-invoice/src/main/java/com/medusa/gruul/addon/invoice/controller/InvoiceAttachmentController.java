package com.medusa.gruul.addon.invoice.controller;

import com.medusa.gruul.addon.invoice.model.dto.InvoiceAttachmentDTO;
import com.medusa.gruul.addon.invoice.model.dto.ReSendInvoiceAttachmentDTO;
import com.medusa.gruul.addon.invoice.service.InvoiceAttachmentHandlerService;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.web.valid.group.Create;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 发票附件表 前端控制器
 *
 * @since 2023-08-07
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/invoice/invoiceAttachment")
public class InvoiceAttachmentController {

    private final InvoiceAttachmentHandlerService invoiceAttachmentHandlerService;

    /**
     * 上传发票附件/重新上传发票附件
     *
     * @param invoiceAttachmentDTO 发票附件参数
     */
    @PostMapping("/upload")
    public Result<Void> uploadInvoiceAttachment(@RequestBody @Validated(Create.class) InvoiceAttachmentDTO invoiceAttachmentDTO) {
        invoiceAttachmentHandlerService.uploadInvoiceAttachment(invoiceAttachmentDTO);
        return Result.ok();
    }

    /**
     * 重新发送附件
     *
     * @param dto 发票附件参数
     */
    @PostMapping("/re-send")
    public Result<Void> reSendAttachment(@RequestBody @Valid ReSendInvoiceAttachmentDTO dto) {
        invoiceAttachmentHandlerService.reSendAttachment(dto);
        return Result.ok();

    }

    /**
     * 下载发票附件
     *
     * @param invoiceAttachmentDTO 发票附件参数
     */
    @PostMapping("/downloadInvoiceAttachment")
    public Result<Void> downloadInvoiceAttachment(@RequestBody InvoiceAttachmentDTO invoiceAttachmentDTO,
                                                  HttpServletResponse response) {
        invoiceAttachmentHandlerService.downloadInvoiceAttachment(invoiceAttachmentDTO, response);
        return Result.ok();
    }
}
