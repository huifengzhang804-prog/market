package com.medusa.gruul.addon.invoice.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.invoice.model.dto.InvoiceRequestCreateDTO;
import com.medusa.gruul.addon.invoice.model.dto.InvoiceRequestQueryDTO;
import com.medusa.gruul.addon.invoice.model.dto.InvoiceTryRequestDTO;
import com.medusa.gruul.addon.invoice.model.dto.RefuseInvoiceRequestDTO;
import com.medusa.gruul.addon.invoice.model.vo.InvoiceVO;
import com.medusa.gruul.addon.invoice.model.vo.TryInvoiceRequestVO;
import com.medusa.gruul.addon.invoice.service.InvoiceRequestHandlerService;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 发票申请表 前端控制器
 *
 * @since 2023-08-07
 */
@RestController
@RequestMapping("/invoice/invoiceRequest")
@RequiredArgsConstructor
@PreAuthorize("""
            @S.matcher()
                .any(@S.ROLES,@S.USER,@S.SHOP_ADMIN,@S.R.SUPPLIER_ADMIN,@S.R.SUPPLIER_CUSTOM_ADMIN,@S.R.SUPER_ADMIN)
                .or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).any(@S.PERMS,'invoice-header:manage','finance:InvoicingRequest'))
                .or(@S.consumer().eq(@S.ROLES,@S.R.SUPPLIER_CUSTOM_ADMIN).eq(@S.PERMS,'invoice-header:manage'))
                .match()
        """)
public class InvoiceRequestController {

    private final InvoiceRequestHandlerService invoiceRequestHandlerService;


    /**
     * 申请开票-前置校验
     *
     * @param dto 申请开票参数
     * @return 申请开票结果
     */
    @GetMapping("/pre-request")
    public Result<TryInvoiceRequestVO> tryRequestInvoice(@Valid InvoiceTryRequestDTO dto) {
        return Result.ok(invoiceRequestHandlerService.tryRequestInvoice(dto));
    }


    /**
     * 申请开票
     *
     * @param invoiceRequest 申请开票参数
     */
    @Idem(1000)
    @Log("申请开票")
    @PostMapping
    public Result<Void> invoiceRequest(@RequestBody @Valid InvoiceRequestCreateDTO invoiceRequest) {
        invoiceRequestHandlerService.invoiceRequest(invoiceRequest);
        return Result.ok();
    }

    /**
     * 分页查询开票申请
     *
     * @param invoiceRequestQuery 查询参数
     * @return 开票申请列表
     */
    @Log("分页查询开票申请")
    @GetMapping("")
    public Result<IPage<InvoiceVO>> pagInvoiceRequest(@Valid InvoiceRequestQueryDTO invoiceRequestQuery) {
        invoiceRequestQuery.setShopId(ISecurity.userMust().getShopId());
        return Result.ok(
                invoiceRequestHandlerService.pagInvoiceRequest(invoiceRequestQuery)
        );
    }


    /**
     * 获取发票申请详情
     *
     * @param id 发票申请ID
     * @return {@link InvoiceVO}
     */
    @Log("申请开票")
    @GetMapping("{id}")
    public Result<InvoiceVO> invoiceDetail(@PathVariable("id") Long id) {
        return Result.ok(
                invoiceRequestHandlerService.invoiceDetail(id)
        );
    }


    /**
     * 撤销开票
     *
     * @param id 发票申请ID
     */
    @Log("撤销开票")
    @PutMapping("{id}")
    public Result<Void> cancelInvoice(@PathVariable("id") Long id) {
        invoiceRequestHandlerService.cancelInvoice(id);
        return Result.ok();
    }


    /**
     * 拒绝开票申请
     *
     * @param refuseInvoiceRequest 拒绝开票申请参数
     */
    @Log("拒绝开票申请")
    @PostMapping("/refuseInvoiceRequest")
    public Result<Void> refuseInvoiceRequest(@RequestBody RefuseInvoiceRequestDTO refuseInvoiceRequest) {
        invoiceRequestHandlerService.refuseInvoiceRequest(refuseInvoiceRequest);
        return Result.ok();
    }
}
