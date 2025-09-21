package com.medusa.gruul.addon.invoice.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.invoice.model.dto.InvoiceHeaderCreateDTO;
import com.medusa.gruul.addon.invoice.model.dto.InvoiceHeaderQueryDTO;
import com.medusa.gruul.addon.invoice.model.dto.SetDefaultInvoiceHeaderDTO;
import com.medusa.gruul.addon.invoice.model.enums.InvoiceHeaderOwnerTypeEnum;
import com.medusa.gruul.addon.invoice.model.vo.InvoiceHeadVO;
import com.medusa.gruul.addon.invoice.service.InvoiceHeaderHandlerService;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.o.Final;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * <p>发票抬头控制器</p>
 *
 * @author An.Yan
 */
@RequestMapping("/invoice/invoice-headers")
@RestController
@RequiredArgsConstructor
@PreAuthorize("""
            @S.matcher()
                .any(@S.ROLES,@S.USER,@S.SHOP_ADMIN,@S.R.SUPPLIER_ADMIN)
                .or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).any(@S.PERMS,'invoice-header:manage','mall:general:setting'))
                .or(@S.consumer().eq(@S.ROLES,@S.R.SUPPLIER_CUSTOM_ADMIN).any(@S.PERMS,'invoice-header:manage','mall:general:setting'))
                .match()
        """)
public class InvoiceHeaderController {

    private final InvoiceHeaderHandlerService invoiceHeaderService;

    /**
     * 创建发票抬头
     *
     * @param invoiceHeaderCreateDTO 抬头参数
     */
    @Idem(500)
    @Log("创建发票抬头")
    @PostMapping("/invoice-header")
    public Result<Void> create(@Valid @RequestBody InvoiceHeaderCreateDTO invoiceHeaderCreateDTO) {
        invoiceHeaderService.create(invoiceHeaderCreateDTO);
        return Result.ok();
    }


    /**
     * 分页查询发票抬头
     *
     * @param invoiceHeaderQuery 查询参数
     * @return 发票抬头列表
     */
    @Log("分页查询发票抬头")
    @GetMapping("/pageInvoiceHeader")
    public Result<IPage<InvoiceHeadVO>> pageInvoiceHeader(@Valid InvoiceHeaderQueryDTO invoiceHeaderQuery) {
        return Result.ok(
                invoiceHeaderService.pageInvoiceHeader(invoiceHeaderQuery)
        );
    }

    /**
     * 删除发票抬头
     *
     * @param invoiceHeaderId 删除发票抬头
     */
    @Log("删除发票抬头")
    @DeleteMapping("/{invoiceHeaderId}")
    public Result<Void> deleteInvoiceHeader(@PathVariable("invoiceHeaderId") Long invoiceHeaderId) {
        invoiceHeaderService.deleteInvoiceHeader(invoiceHeaderId);
        return Result.ok();
    }

    /**
     * 设置默认抬头
     *
     * @param dto {@link SetDefaultInvoiceHeaderDTO}
     */
    @Log("设置默认抬头")
    @PutMapping("/default-invoice-header")
    public Result<Void> setDefaultInvoiceHeader(@Valid @RequestBody SetDefaultInvoiceHeaderDTO dto) {
        invoiceHeaderService.setDefaultInvoiceHeader(dto);
        return Result.ok();
    }

    /**
     * 获取默认抬头
     *
     * @param invoiceHeaderOwnerType 发票抬头所属方类型
     * @return 默认抬头
     */
    @Log("获取默认抬头")
    @GetMapping("/getDefaultInvoiceHeader")
    public Result<InvoiceHeadVO> getDefaultInvoiceHeader(@RequestParam("invoiceHeaderOwnerType") InvoiceHeaderOwnerTypeEnum invoiceHeaderOwnerType) {
        Final<Long> ownId = new Final<>();
        ISecurity.match()
                .ifUser(secureUser -> ownId.set(secureUser.getId()))
                .ifAnyShopAdmin(secureUser -> ownId.set(secureUser.getShopId()));
        return Result.ok(
                invoiceHeaderService.getDefaultInvoiceHeader(ownId.get(), invoiceHeaderOwnerType)
        );
    }

    /**
     * 获取发票抬头详情
     *
     * @param invoiceHeaderId        发票抬头id
     * @param invoiceHeaderOwnerType 发票抬头所属方类型
     */
    @Log("获取发票抬头详情")
    @GetMapping
    public Result<InvoiceHeadVO> getInvoiceHeaderDetail(@RequestParam("invoiceHeaderId") Long invoiceHeaderId,
                                                        @RequestParam("invoiceHeaderOwnerType") InvoiceHeaderOwnerTypeEnum invoiceHeaderOwnerType) {
        return Result.ok(
                invoiceHeaderService.getInvoiceHeaderDetail(invoiceHeaderId, invoiceHeaderOwnerType)
        );
    }
}
