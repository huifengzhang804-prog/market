package com.medusa.gruul.addon.invoice.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.invoice.model.dto.InvoiceRequestCreateDTO;
import com.medusa.gruul.addon.invoice.model.dto.InvoiceRequestQueryDTO;
import com.medusa.gruul.addon.invoice.model.dto.InvoiceTryRequestDTO;
import com.medusa.gruul.addon.invoice.model.dto.RefuseInvoiceRequestDTO;
import com.medusa.gruul.addon.invoice.model.vo.InvoiceVO;
import com.medusa.gruul.addon.invoice.model.vo.TryInvoiceRequestVO;

public interface InvoiceRequestHandlerService {


    /**
     * 申请开票
     *
     * @param invoiceRequest 申请开票参数
     */
    void invoiceRequest(InvoiceRequestCreateDTO invoiceRequest);

    /**
     * 分页查询开票申请
     *
     * @param invoiceRequestQuery 查询参数
     * @return 开票申请列表
     */
    IPage<InvoiceVO> pagInvoiceRequest(InvoiceRequestQueryDTO invoiceRequestQuery);

    /**
     * 获取发票申请详情
     * @param id 发票申请ID
     * @return {@link InvoiceVO}
     */
    InvoiceVO invoiceDetail(Long id);


    /**
     * 撤销开票
     *
     * @param id 发票ID
     */
    void cancelInvoice(Long id);


    /**
     * 拒绝开票申请
     * @param refuseInvoiceRequest 拒绝开票申请参数
     */
    void refuseInvoiceRequest(RefuseInvoiceRequestDTO refuseInvoiceRequest);

    /**
     * 尝试申请开票
     * @param dto {@link InvoiceTryRequestDTO}
     * @return {@link TryInvoiceRequestVO}
     */
    TryInvoiceRequestVO tryRequestInvoice(InvoiceTryRequestDTO dto);
}
