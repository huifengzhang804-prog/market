package com.medusa.gruul.addon.invoice.service.impl;

import com.medusa.gruul.addon.invoice.constants.InvoiceConstant;
import com.medusa.gruul.addon.invoice.model.dto.InvoiceSettingsDTO;
import com.medusa.gruul.addon.invoice.model.enums.InvoiceError;
import com.medusa.gruul.addon.invoice.model.enums.InvoiceSettingsClientType;
import com.medusa.gruul.addon.invoice.model.enums.InvoiceSettingsDeletedType;
import com.medusa.gruul.addon.invoice.mp.entity.InvoiceSettings;
import com.medusa.gruul.addon.invoice.mp.entity.InvoiceSetupValue;
import com.medusa.gruul.addon.invoice.mp.service.IInvoiceSettingsService;
import com.medusa.gruul.addon.invoice.service.InvoiceSettingsHandlerService;
import com.medusa.gruul.common.redis.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class InvoiceSettingsHandlerServiceImpl implements InvoiceSettingsHandlerService {


    private final IInvoiceSettingsService invoiceSettingsService;

    /**
     * 获取发票设置
     *
     * @param invoiceSettingsClientType 客户端
     * @param shopId                    店铺id
     * @return 发票设置
     */
    @Override
    public InvoiceSettings getInvoiceSettings(InvoiceSettingsClientType invoiceSettingsClientType, Long shopId) {
        return RedisUtil.getCacheMap(
                InvoiceSettings.class,
                () -> invoiceSettingsService.lambdaQuery()
                        .eq(InvoiceSettings::getInvoiceSettingsClientType, invoiceSettingsClientType.getValue())
                        .eq(InvoiceSettings::getShopId, shopId)
                        .eq(InvoiceSettings::getDeleted, InvoiceSettingsDeletedType.NORMAL)
                        .one(),
                Duration.ofSeconds(300),
                RedisUtil.key(InvoiceConstant.INVOICE_CONFIG_CACHE_KEY, shopId)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editInvoiceSettings(InvoiceSettingsDTO invoiceSettingsDTO) {
        //前端保存后，没有调用查询接口，导致不刷新的情况下，一直点击，id一直为空，导致一直新增数据
        //当传入的id为空时，查询当前shopId的发票设置信息
        InvoiceSettings result = invoiceSettingsService.lambdaQuery()
                .eq(InvoiceSettings::getShopId, invoiceSettingsDTO.getShopId())
                .eq(InvoiceSettings::getDeleted, InvoiceSettingsDeletedType.NORMAL)
                .one();
        if (Objects.nonNull(result)) {
            invoiceSettingsDTO.setId(result.getId());
        }
        InvoiceSettings invoiceSettings = new InvoiceSettings()
                .setShopId(invoiceSettingsDTO.getShopId())
                .setInvoiceSetupValue(invoiceSettingsDTO.getInvoiceSetupValue())
                .setInvoiceSettingsClientType(invoiceSettingsDTO.getInvoiceSettingsClientType());
        invoiceSettings.setId(invoiceSettingsDTO.getId());
        RedisUtil.doubleDeletion(
                () -> invoiceSettingsService.saveOrUpdate(invoiceSettings),
                RedisUtil.key(InvoiceConstant.INVOICE_CONFIG_CACHE_KEY, invoiceSettingsDTO.getShopId())
        );

    }

    /**
     * 获取订单完成天数
     *
     * @param invoiceSettingsClientType 客户端类型
     * @param shopId                    店铺id
     * @return 订单完成天数
     */
    @Override
    public List<InvoiceSetupValue> getOrderCompleteDays(InvoiceSettingsClientType invoiceSettingsClientType, long shopId) {
        InvoiceSettings invoiceSettings = getInvoiceSettings(invoiceSettingsClientType, shopId);
        switch (invoiceSettingsClientType) {
            case SUPPLIER:
                InvoiceError.SUPPLIER_INVOICE_SETTINGS_NOT_EXIST.trueThrow(invoiceSettings == null);
            case SHOP:
                InvoiceError.INVOICE_SETTINGS_NOT_EXIST.trueThrow(invoiceSettings == null);
        }
        return invoiceSettings.getInvoiceSetupValue();
    }
}
