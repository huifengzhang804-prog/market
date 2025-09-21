package com.medusa.gruul.addon.invoice.handler;


import com.medusa.gruul.addon.invoice.addon.InvoiceAddonSupporter;
import com.medusa.gruul.addon.invoice.constants.InvoiceConstant;
import com.medusa.gruul.addon.invoice.model.dto.InvoiceRequestCreateDTO;
import com.medusa.gruul.addon.invoice.model.dto.InvoiceTryRequestDTO;
import com.medusa.gruul.addon.invoice.model.enums.InvoiceError;
import com.medusa.gruul.addon.invoice.model.enums.InvoiceSetType;
import com.medusa.gruul.addon.invoice.model.enums.InvoiceSettingsClientType;
import com.medusa.gruul.addon.invoice.model.enums.InvoiceStatus;
import com.medusa.gruul.addon.invoice.model.vo.TryInvoiceRequestVO;
import com.medusa.gruul.addon.invoice.mp.entity.InvoiceHeader;
import com.medusa.gruul.addon.invoice.mp.entity.InvoiceRequest;
import com.medusa.gruul.addon.invoice.mp.entity.InvoiceSetupValue;
import com.medusa.gruul.addon.invoice.mp.service.IInvoiceHeaderService;
import com.medusa.gruul.addon.invoice.mp.service.IInvoiceRequestService;
import com.medusa.gruul.addon.invoice.service.InvoiceSettingsHandlerService;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.order.api.model.OrderBillDTO;
import com.medusa.gruul.order.api.rpc.OrderRpcService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@Component(InvoiceConstant.USER_INVOICE_HANDLER)
public class UserInvoiceRequestHandler implements InvoiceRequestHandler {

    private final IInvoiceRequestService invoiceRequestService;
    private final InvoiceSettingsHandlerService invoiceSettingsHandlerService;
    private final IInvoiceHeaderService invoiceHeaderService;
    private final InvoiceAddonSupporter invoiceAddonSupporter;

    private final OrderRpcService orderRpcService;

    /**
     * 申请开票
     *
     * @param dto 申请开票参数
     */
    @Override
    public void invoiceRequest(InvoiceRequestCreateDTO dto) {
        Long userId = ISecurity.userMust().getId();

        // 检查客户端所申请的发票类型是否与服务端当前发票配置的类型一致
        InvoiceSetupValue invoiceSetupValue = invoiceSettingsHandlerService.
                getOrderCompleteDays(InvoiceSettingsClientType.SHOP, dto.getApplicantShopId()).get(0);

        switch (dto.getInvoiceType()) {
            case VAT_GENERAL -> {
                if (!invoiceSetupValue.getInvoiceType().getValue().equals(InvoiceSetType.VAT_GENERAL.getValue()) &&
                        !invoiceSetupValue.getInvoiceType().getValue().equals(InvoiceSetType.VAT_COMBINED.getValue())) {
                    throw InvoiceError.INVOICE_SETTINGS_CHANGED.exception();
                }
            }
            case VAT_SPECIAL -> {
                if (!invoiceSetupValue.getInvoiceType().getValue().equals(InvoiceSetType.VAT_SPECIAL.getValue()) &&
                        !invoiceSetupValue.getInvoiceType().getValue().equals(InvoiceSetType.VAT_COMBINED.getValue())) {
                    throw InvoiceError.INVOICE_SETTINGS_CHANGED.exception();
                }
            }
        }

        // 获取发票订单完成天数
        InvoiceHeader invoiceHeader = invoiceHeaderService.lambdaQuery()
                .eq(InvoiceHeader::getId, dto.getInvoiceHeaderId())
                .one();
        if (invoiceHeader == null) {
            throw InvoiceError.INVOICE_HEADER_DOES_NOT_EXIST.exception();
        }

        // 检查发票申请状态
        TryInvoiceRequestVO checkResult = tryRequestInvoice(new InvoiceTryRequestDTO()
                .setApplicantShopId(dto.getApplicantShopId())
                .setApplicantId(dto.getApplicantId())
                .setInvoiceOwnerType(dto.getInvoiceOwnerType())
                .setOrderNo(dto.getOrderNo())
        );
        InvoiceError.INVOICE_REQUEST_FAILED.trueThrow(!checkResult.getInvoiceStatus().equals(InvoiceStatus.ALLOWED_INVOICING));

        // 保存申请记录
        invoiceRequestService.save(dto.newInvoiceRequest(dto.getInvoiceAmount(), userId, invoiceHeader));
        // 同步发票状态
        invoiceAddonSupporter.syncSupplyOrderInvoiceStatus(dto.getOrderNo(), InvoiceStatus.REQUEST_IN_PROCESS.getValue());

    }

    /**
     * 尝试申请发票
     *
     * @param dto {@link InvoiceTryRequestDTO}
     * @return {@link TryInvoiceRequestVO}
     */
    @Override
    public TryInvoiceRequestVO tryRequestInvoice(InvoiceTryRequestDTO dto) {
        TryInvoiceRequestVO resultVO = new TryInvoiceRequestVO();
        LocalDateTime now = LocalDateTime.now();
        InvoiceSetupValue invoiceSetupValue = invoiceSettingsHandlerService.
                getOrderCompleteDays(InvoiceSettingsClientType.SHOP, dto.getApplicantShopId()).get(0);

        // 不允许开票
        if (InvoiceSetType.NO_INVOICE.equals(invoiceSetupValue.getInvoiceType())) {
            resultVO.setInvoiceStatus(InvoiceStatus.SERVER_NOT_SUPPORTED).setBillMoney((long) CommonPool.NUMBER_ZERO);
            return resultVO;
        }

        // 获取订单详情
        OrderBillDTO orderBillInfo = orderRpcService.getOrderBillInfo(dto.getOrderNo());

        InvoiceError.INVOICE_REQUEST_ORDER_NOT_EXIST.trueThrow(orderBillInfo == null);

        // 检查订单完成时间
        if (orderBillInfo.getOrderAccomplishTime().plusDays(invoiceSetupValue.getOrderCompleteDays()).isBefore(now)) {
            return resultVO.setInvoiceStatus(InvoiceStatus.REQUEST_HAS_EXPIRED).setBillMoney(orderBillInfo.getBillMoney());
        }

        // 检查发票申请状态：已成功开票&发票申请处理中
        InvoiceRequest otherStateOfInvoiceRequest = this.invoiceRequestService.lambdaQuery()
                .eq(InvoiceRequest::getApplicantId, dto.getApplicantId())
                .eq(InvoiceRequest::getOrderNo, dto.getOrderNo())
                .eq(InvoiceRequest::getShopId, dto.getApplicantShopId())
                .in(InvoiceRequest::getInvoiceStatus, InvoiceStatus.SUCCESSFULLY_INVOICED, InvoiceStatus.REQUEST_IN_PROCESS)
                .orderByDesc(InvoiceRequest::getCreateTime)
                .last("limit 1")
                .one();
        if (otherStateOfInvoiceRequest != null) {
            return resultVO.setId(otherStateOfInvoiceRequest.getId()).setInvoiceStatus(otherStateOfInvoiceRequest.getInvoiceStatus())
                    .setBillMoney(otherStateOfInvoiceRequest.getInvoiceAmount());
        }

        // 获取最近的开票失败记录
        InvoiceRequest failedRequest = this.invoiceRequestService.lambdaQuery()
                .eq(InvoiceRequest::getOrderNo, dto.getOrderNo())
                .eq(InvoiceRequest::getShopId, dto.getApplicantShopId())
                .eq(InvoiceRequest::getInvoiceStatus, InvoiceStatus.FAILED_INVOICE_REQUEST)
                .orderByDesc(InvoiceRequest::getCreateTime)
                .last("limit 1")
                .one();
        if (failedRequest != null) {
            return resultVO.setInvoiceStatus(InvoiceStatus.ALLOWED_INVOICING).setBillMoney(failedRequest.getInvoiceAmount());
        }

        return resultVO.setInvoiceStatus(InvoiceStatus.ALLOWED_INVOICING).setBillMoney(orderBillInfo.getBillMoney());
    }
}
