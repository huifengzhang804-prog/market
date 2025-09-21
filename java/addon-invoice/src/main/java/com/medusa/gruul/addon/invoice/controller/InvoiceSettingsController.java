package com.medusa.gruul.addon.invoice.controller;

import com.medusa.gruul.addon.invoice.model.dto.InvoiceSettingsDTO;
import com.medusa.gruul.addon.invoice.model.enums.InvoiceSettingsClientType;
import com.medusa.gruul.addon.invoice.mp.entity.InvoiceSettings;
import com.medusa.gruul.addon.invoice.service.InvoiceSettingsHandlerService;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 发票设置表 前端控制器
 *
 * @since 2023-08-07
 */
@RestController
@RequestMapping("/invoice/invoiceSettings")
@RequiredArgsConstructor

public class InvoiceSettingsController {

    private final InvoiceSettingsHandlerService invoiceSettingsHandlerService;

    /**
     * 编辑发票设置
     *
     * @param invoiceSettingsDTO 发票设置DTO
     */
    @Log("编辑发票设置")
    @PostMapping
    @PreAuthorize("""
        @S.matcher()
            .any(@S.ROLES,@S.SHOP_ADMIN,@S.R.SUPPLIER_ADMIN)
            .or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS,'invoice-header:manage'))
            .or(@S.consumer().eq(@S.ROLES,@S.R.SUPPLIER_CUSTOM_ADMIN).eq(@S.PERMS,'invoice-header:manage'))
            .match()
    """)
    public Result<Void> editInvoiceSettings(@RequestBody InvoiceSettingsDTO invoiceSettingsDTO) {
        invoiceSettingsHandlerService.editInvoiceSettings(invoiceSettingsDTO);
        return Result.ok();
    }

    /**
     * 获取发票设置
     *
     * @return 发票设置
     */
    @Log("获取发票设置")
    @GetMapping
    public Result<InvoiceSettings> getInvoiceSettings(@RequestParam("invoiceSettingsClientType") InvoiceSettingsClientType invoiceSettingsClientType,
                                                      @RequestParam("shopId") Long shopId) {
        return Result.ok(
                invoiceSettingsHandlerService.getInvoiceSettings(invoiceSettingsClientType, shopId)
        );
    }


}
