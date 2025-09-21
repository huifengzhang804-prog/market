package com.medusa.gruul.addon.invoice.service;

import com.medusa.gruul.addon.invoice.model.dto.InvoiceSettingsDTO;
import com.medusa.gruul.addon.invoice.model.enums.InvoiceSettingsClientType;
import com.medusa.gruul.addon.invoice.mp.entity.InvoiceSettings;
import com.medusa.gruul.addon.invoice.mp.entity.InvoiceSetupValue;

import java.util.List;

public interface InvoiceSettingsHandlerService {
    /**
     * 编辑发票设置
     *
     * @param invoiceSettingsDTO 发票设置DTO
     */
    void editInvoiceSettings(InvoiceSettingsDTO invoiceSettingsDTO);


    /**
     * 获取发票设置
     *
     * @param invoiceSettingsClientType 发票设置平台类型
     * @param shopId                    店铺id
     * @return 发票设置
     */
    InvoiceSettings getInvoiceSettings(InvoiceSettingsClientType invoiceSettingsClientType, Long shopId);

    /**
     * 获取发票设置
     * @param invoiceSettingsClientType {@link InvoiceSettingsClientType}
     * @param shopId 店铺ID
     * @return {@link InvoiceSetupValue}
     */
    List<InvoiceSetupValue> getOrderCompleteDays(InvoiceSettingsClientType invoiceSettingsClientType, long shopId);

}
