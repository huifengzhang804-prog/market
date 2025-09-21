package com.medusa.gruul.addon.invoice.handler;

import com.medusa.gruul.addon.invoice.addon.InvoiceAddonSupporter;
import com.medusa.gruul.addon.invoice.constants.InvoiceConstant;
import com.medusa.gruul.addon.invoice.model.dto.InvoiceRequestCreateDTO;
import com.medusa.gruul.addon.invoice.model.dto.InvoiceTryRequestDTO;
import com.medusa.gruul.addon.invoice.model.enums.*;
import com.medusa.gruul.addon.invoice.model.vo.TryInvoiceRequestVO;
import com.medusa.gruul.addon.invoice.mp.entity.InvoiceHeader;
import com.medusa.gruul.addon.invoice.mp.entity.InvoiceRequest;
import com.medusa.gruul.addon.invoice.mp.entity.InvoiceSetupValue;
import com.medusa.gruul.addon.invoice.mp.service.IInvoiceHeaderService;
import com.medusa.gruul.addon.invoice.mp.service.IInvoiceRequestService;
import com.medusa.gruul.addon.invoice.service.InvoiceSettingsHandlerService;
import com.medusa.gruul.common.model.constant.CommonPool;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static com.medusa.gruul.addon.invoice.constants.InvoiceConstant.DATE_TIME_FORMAT;
import static com.medusa.gruul.addon.invoice.constants.InvoiceConstant.ORDER_COMPLETED_TIME;
import static com.medusa.gruul.addon.invoice.model.enums.InvoiceStatus.*;

@RequiredArgsConstructor
@Component(InvoiceConstant.SHOP_INVOICE_HANDLER)
public class ShopInvoiceRequestHandler implements InvoiceRequestHandler {

    private final IInvoiceHeaderService invoiceHeaderService;
    private final IInvoiceRequestService invoiceRequestService;
    private final InvoiceSettingsHandlerService invoiceSettingsHandlerService;
    private final InvoiceAddonSupporter invoiceAddonSupporter;

    @Override
    public void invoiceRequest(InvoiceRequestCreateDTO dto) {

        // 检查header是否存在
        InvoiceHeader invoiceHeader = invoiceHeaderService.lambdaQuery()
                .eq(InvoiceHeader::getId, dto.getInvoiceHeaderId())
                .one();
        if (invoiceHeader == null) {
            throw InvoiceError.INVOICE_HEADER_DOES_NOT_EXIST.exception();
        }

        // 检查客户端所申请的发票类型是否与服务端当前发票配置的类型一致
        InvoiceSetupValue invoiceSetupValue = invoiceSettingsHandlerService.
                getOrderCompleteDays(InvoiceSettingsClientType.SUPPLIER, dto.getApplicantShopId()).get(0);

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

        // 检查发票状态是否正常
        TryInvoiceRequestVO checkResult = tryRequestInvoice(new InvoiceTryRequestDTO().setInvoiceOwnerType(InvoiceOwnerTypeEnum.SHOP)
                .setApplicantId(dto.getApplicantId())
                .setOrderNo(dto.getOrderNo())
                .setApplicantShopId(dto.getApplicantShopId()));
        InvoiceError.INVOICE_REQUEST_FAILED.trueThrow(
                !checkResult.getInvoiceStatus().equals(InvoiceStatus.ALLOWED_INVOICING) &&
                        !checkResult.getInvoiceStatus().equals(FAILED_INVOICE_REQUEST));

        // 保存开票申请
        boolean savedResult = invoiceRequestService.save(dto.newInvoiceRequest(dto.getInvoiceAmount(), invoiceHeader));
        // 同步发票状态
        invoiceAddonSupporter.syncSupplyOrderInvoiceStatus(dto.getOrderNo(), REQUEST_IN_PROCESS.getValue());
        InvoiceError.INVOICE_REQUEST_FAILED.falseThrow(savedResult);

    }

    /**
     * 尝试申请发票
     *
     * @param dto {@link InvoiceRequestCreateDTO}
     * @return {@link TryInvoiceRequestVO}
     */
    @Override
    public TryInvoiceRequestVO tryRequestInvoice(InvoiceTryRequestDTO dto) {
        TryInvoiceRequestVO resultVO = new TryInvoiceRequestVO();
        LocalDateTime now = LocalDateTime.now();

        // 获取允许开票时间
        InvoiceSetupValue invoiceSetupValue = invoiceSettingsHandlerService.getOrderCompleteDays(
                dto.getInvoiceOwnerType().equals(InvoiceOwnerTypeEnum.SHOP) ?
                        InvoiceSettingsClientType.SUPPLIER : InvoiceSettingsClientType.SHOP,
                dto.getApplicantShopId()
        ).get(CommonPool.NUMBER_ZERO);

        // 不允许开票
        if (InvoiceSetType.NO_INVOICE.equals(invoiceSetupValue.getInvoiceType())) {
            resultVO.setInvoiceStatus(InvoiceStatus.SERVER_NOT_SUPPORTED).setBillMoney((long) CommonPool.NUMBER_ZERO);
            return resultVO;
        }

        // 获取最近的开票记录
        InvoiceRequest request = this.invoiceRequestService.lambdaQuery()
                .eq(InvoiceRequest::getOrderNo, dto.getOrderNo())
                .eq(InvoiceRequest::getShopId, dto.getApplicantShopId())
                .orderByDesc(InvoiceRequest::getCreateTime)
                .last("limit 1")
                .one();
        if (request != null && (request.getInvoiceStatus().equals(REQUEST_IN_PROCESS) ||
                request.getInvoiceStatus().equals(SUCCESSFULLY_INVOICED) ||
                request.getInvoiceStatus().equals(FAILED_INVOICE_REQUEST))) {
            return resultVO
                    .setId(request.getId())
                    .setInvoiceStatus(request.getInvoiceStatus())
                    .setBillMoney(request.getInvoiceAmount());
        }

        // 获取订单详情
        DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        Map<String, String> orderBillInfoMap = invoiceAddonSupporter.getPurchaseOrderById(dto.getOrderNo());
        InvoiceError.INVOICE_REQUEST_ORDER_NOT_EXIST.trueThrow(orderBillInfoMap == null || orderBillInfoMap.isEmpty());

        // 检查订单完成时间
        LocalDateTime completedTime = LocalDateTime.parse(orderBillInfoMap.get(ORDER_COMPLETED_TIME), DATE_TIME_FORMATTER);
        if (completedTime.plusDays(invoiceSetupValue.getOrderCompleteDays()).isBefore(now)) {
            return resultVO
                    .setInvoiceStatus(InvoiceStatus.REQUEST_HAS_EXPIRED)
                    .setBillMoney(Long.parseLong(orderBillInfoMap.get(InvoiceConstant.ORDER_BILL_MONEY)));
        }
        return resultVO
                .setInvoiceStatus(InvoiceStatus.ALLOWED_INVOICING)
                .setBillMoney(Long.parseLong(orderBillInfoMap.get(InvoiceConstant.ORDER_BILL_MONEY)));
    }
}
