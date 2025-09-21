package com.medusa.gruul.addon.invoice.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.invoice.addon.InvoiceAddonSupporter;
import com.medusa.gruul.addon.invoice.handler.InvoiceRequestHandler;
import com.medusa.gruul.addon.invoice.model.dto.InvoiceRequestCreateDTO;
import com.medusa.gruul.addon.invoice.model.dto.InvoiceRequestQueryDTO;
import com.medusa.gruul.addon.invoice.model.dto.InvoiceTryRequestDTO;
import com.medusa.gruul.addon.invoice.model.dto.RefuseInvoiceRequestDTO;
import com.medusa.gruul.addon.invoice.model.enums.InvoiceError;
import com.medusa.gruul.addon.invoice.model.enums.InvoiceStatus;
import com.medusa.gruul.addon.invoice.model.vo.InvoiceVO;
import com.medusa.gruul.addon.invoice.model.vo.TryInvoiceRequestVO;
import com.medusa.gruul.addon.invoice.mp.entity.InvoiceAttachment;
import com.medusa.gruul.addon.invoice.mp.entity.InvoiceRequest;
import com.medusa.gruul.addon.invoice.mp.service.IInvoiceAttachmentService;
import com.medusa.gruul.addon.invoice.mp.service.IInvoiceRequestService;
import com.medusa.gruul.addon.invoice.service.InvoiceRequestHandlerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InvoiceRequestHandlerServiceImpl implements InvoiceRequestHandlerService {


    private final Map<String, InvoiceRequestHandler> invoiceRequestHandlerMap;
    private final IInvoiceRequestService invoiceRequestService;
    private final IInvoiceAttachmentService invoiceAttachmentService;
    private final InvoiceAddonSupporter invoiceAddonSupporter;


    /**
     * 申请开票
     *
     * @param invoiceRequest 申请开票参数
     */
    @Override
    public void invoiceRequest(InvoiceRequestCreateDTO invoiceRequest) {
        InvoiceRequestHandler invoiceRequestHandler = invoiceRequestHandlerMap.get(invoiceRequest.getInvoiceOwnerType().getName());
        invoiceRequestHandler.invoiceRequest(invoiceRequest);
    }

    /**
     * 分页查询开票申请
     *
     * @param invoiceRequestQuery 查询参数
     * @return 开票申请列表
     */
    @Override
    public IPage<InvoiceVO> pagInvoiceRequest(InvoiceRequestQueryDTO invoiceRequestQuery) {
        return invoiceRequestService.pagInvoiceRequest(invoiceRequestQuery);
    }

    /**
     * 获取发票申请详情
     *
     * @param id 发票申请ID
     * @return {@link InvoiceVO}
     */
    @Override
    public InvoiceVO invoiceDetail(Long id) {
        InvoiceRequest invoiceRequest = invoiceRequestService.lambdaQuery()
                .eq(InvoiceRequest::getId, id).one();
        InvoiceError.INVOICE_REQUEST_NOT_EXIST.trueThrow(invoiceRequest == null);
        InvoiceAttachment attachment = invoiceAttachmentService.lambdaQuery().eq(InvoiceAttachment::getShopId, invoiceRequest.getShopId())
                .eq(InvoiceAttachment::getInvoiceRequestId, invoiceRequest.getId())
                .one();
        List<String> attachments = Optional.ofNullable(attachment)
                .map(InvoiceAttachment::getInvoiceAttachmentUrl)
                .orElse(new ArrayList<>());
        return new InvoiceVO()
                .setApplicantId(invoiceRequest.getApplicantId())
                .setApplicantShopId(invoiceRequest.getShopId())
                .setInvoiceOwnerType(invoiceRequest.getInvoiceOwnerType())
                .setShopSupplierName(invoiceRequest.getShopSupplierName())
                .setOrderNo(invoiceRequest.getOrderNo())
                .setInvoiceAmount(invoiceRequest.getInvoiceAmount())
                .setInvoiceType(invoiceRequest.getInvoiceType())
                .setInvoiceStatus(invoiceRequest.getInvoiceStatus())
                .setApplicationTime(invoiceRequest.getApplicationTime())
                .setBillingRemarks(invoiceRequest.getBillingRemarks())
                .setDenialReason(invoiceRequest.getDenialReason())
                .setCreateTime(invoiceRequest.getCreateTime())
                .setUpdateTime(invoiceRequest.getUpdateTime())
                .setId(invoiceRequest.getId())
                .setAttachments(attachments)
                .setInvoiceHeaderType(invoiceRequest.getInvoiceHeaderType())
                .setHeader(invoiceRequest.getHeader())
                .setTaxIdentNo(invoiceRequest.getTaxIdentNo())
                .setOpeningBank(invoiceRequest.getOpeningBank())
                .setBankAccountNo(invoiceRequest.getBankAccountNo())
                .setEnterprisePhone(invoiceRequest.getEnterprisePhone())
                .setEnterpriseAddress(invoiceRequest.getEnterpriseAddress())
                .setEmail(invoiceRequest.getEmail())
                .setBillingRemarks(invoiceRequest.getBillingRemarks())
                ;

    }

    /**
     * 撤销开票
     *
     * @param id 发票ID
     */
    @Override
    public void cancelInvoice(Long id) {

        // 检查发票是否存在
        InvoiceRequest invoiceRequest = invoiceRequestService.lambdaQuery()
                .eq(InvoiceRequest::getId, id).one();
        InvoiceError.INVOICE_REQUEST_NOT_EXIST.trueThrow(invoiceRequest == null);
        boolean updateResult = invoiceRequestService.lambdaUpdate()
                .set(InvoiceRequest::getDenialReason, "客户撤销开票")
                .set(InvoiceRequest::getInvoiceStatus, InvoiceStatus.CLIENT_CANCEL_REQUEST)
                .eq(InvoiceRequest::getId, id)
                .update();
        // 同步发票状态
        invoiceAddonSupporter.syncSupplyOrderInvoiceStatus(invoiceRequest.getOrderNo(),
                InvoiceStatus.CLIENT_CANCEL_REQUEST.getValue());
        // 检查撤销开票结果
        InvoiceError.INVOICE_REQUEST_CANCEL_FAIL.falseThrow(updateResult);
    }

    /**
     * 拒绝开票申请
     *
     * @param refuseInvoiceRequest 拒绝开票申请参数
     */
    @Override
    public void refuseInvoiceRequest(RefuseInvoiceRequestDTO refuseInvoiceRequest) {
        // 检查发票是否存在
        InvoiceRequest invoiceRequest = invoiceRequestService.lambdaQuery()
                .eq(InvoiceRequest::getId, refuseInvoiceRequest.getId()).one();
        InvoiceError.INVOICE_REQUEST_NOT_EXIST.trueThrow(invoiceRequest == null);
        boolean updateResult = invoiceRequestService.lambdaUpdate()
                .set(InvoiceRequest::getDenialReason, refuseInvoiceRequest.getDenialReason())
                .set(InvoiceRequest::getInvoiceStatus, InvoiceStatus.FAILED_INVOICE_REQUEST)
                .eq(InvoiceRequest::getId, refuseInvoiceRequest.getId())
                .update();
        // 检查撤销开票结果
        InvoiceError.INVOICE_REQUEST_REJECTION_FAIL.falseThrow(updateResult);
        // 同步发票状态
        invoiceAddonSupporter.syncSupplyOrderInvoiceStatus(invoiceRequest.getOrderNo(),
                InvoiceStatus.FAILED_INVOICE_REQUEST.getValue());
    }

    /**
     * 尝试申请开票
     *
     * @param dto {@link InvoiceTryRequestDTO}
     * @return {@link TryInvoiceRequestVO}
     */
    @Override
    public TryInvoiceRequestVO tryRequestInvoice(InvoiceTryRequestDTO dto) {
        InvoiceRequestHandler invoiceRequestHandler = invoiceRequestHandlerMap.get(dto.getInvoiceOwnerType().getName());
        return invoiceRequestHandler.tryRequestInvoice(dto);
    }


}
