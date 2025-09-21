package com.medusa.gruul.addon.invoice.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 发票附件表
 * </p>
 *
 * @since 2023-08-07
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "t_invoice_attachment", autoResultMap = true)
public class InvoiceAttachment extends BaseEntity {


    /**
     * 发票申请表id
     */
    private Long invoiceRequestId;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 发票附件URL地址
     */
    @TableField(value = "invoice_attachment_url", typeHandler = Fastjson2TypeHandler.class)
    private List<String> invoiceAttachmentUrl;
}
