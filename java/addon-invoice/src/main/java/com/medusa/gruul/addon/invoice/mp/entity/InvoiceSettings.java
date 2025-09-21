package com.medusa.gruul.addon.invoice.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.addon.invoice.model.enums.InvoiceSettingsClientType;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.util.List;

/**
 * 发票设置表
 *
 * @since 2023-08-07
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "t_invoice_settings", autoResultMap = true)
public class InvoiceSettings extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 所属客户端
     */
    private InvoiceSettingsClientType invoiceSettingsClientType;

    /**
     * 发票设置json
     */
    @TableField(value = "invoice_setup_value", typeHandler = Fastjson2TypeHandler.class)
    private List<InvoiceSetupValue> invoiceSetupValue;

}
