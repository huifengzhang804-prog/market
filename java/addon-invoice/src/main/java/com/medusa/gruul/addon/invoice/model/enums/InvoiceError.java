package com.medusa.gruul.addon.invoice.model.enums;

import com.medusa.gruul.global.i18n.I18N;
import com.medusa.gruul.global.model.exception.Error;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InvoiceError implements Error {

    USER_NOT_EXIST(90000, "invoice.error.user.not.exist"),
    SHOP_NOT_EXIST(90001, "invoice.error.shop.not.exist"),

    /**
     * 创建发票抬头失败
     */
    FAILED_TO_CREATE_INVOICE_HEADER(90002, "invoice.header.create.failed"),

    /**
     * 发票抬头更新失败
     */
    FAILED_TO_UPDATE_INVOICE_HEADER(90003, "invoice.header.update.failed"),

    /**
     * 发票抬头不存在
     */
    INVOICE_HEADER_DOES_NOT_EXIST(90004, "invoice.header.does.not.exist"),

    /**
     * 无效的OwnerType
     */
    INVOICE_HEADER_INVALID_OWNER_TYPE(90005, "invoice.header.invalid.owner.type"),

    /**
     * 设置默认发票抬头失败
     */
    INVOICE_HEADER_SET_DEFAULT_FAIL(90006, "invoice.header.set.default.fail"),

    /**
     * 当前不可申请发票
     */
    INVOICES_ARE_CURRENTLY_NOT_AVAILABLE(90007, "invoices.are.not.currently.available"),

    /**
     * 发票不存在
     */
    INVOICE_DOES_NOT_EXIST(90008, "invoice.does.not.exist"),

    /**
     * 发票申请已过期
     */
    INVOICES_REQUEST_EXPIRED(90009, "invoices.request.expired"),

    /**
     * 发票申请中或者已经申请过
     */
    INVOICE_REQUEST_SUCCESS_OR_PROCESSING(90010, "invoice.request.success.or.processing"),

    /**
     * 发票申请不存在
     */
    INVOICE_REQUEST_NOT_EXIST(90011, "invoice.request.not.exist"),

    /**
     * 撤销发票申请失败
     */
    INVOICE_REQUEST_CANCEL_FAIL(90012, "invoice.request.cancel.fail"),

    /**
     * 拒绝发票申请失败
     */
    INVOICE_REQUEST_REJECTION_FAIL(90013, "invoice.request.rejection.fail"),
    /**
     * 发票附件不存在
     */
    INVOICE_REQUEST_ATTACHMENT_NOT_EXIST(90014, "invoice.request.attachment.not.exist"),


    /**
     * 申请发票失败
     */
    INVOICE_REQUEST_FAILED(90015, "invoice.request.failed"),

    /**
     * 订单不存在
     */
    INVOICE_REQUEST_ORDER_NOT_EXIST(90016, "invoice.request.order.not.exist"),

    /**
     * 发票附件保存失败
     */
    INVOICE_ATTACHMENT_SAVED_FAILED(90017, "invoice.attachment.saved.failed"),

    /**
     * 发票设置不存在
     */
    INVOICE_SETTINGS_NOT_EXIST(90018, "invoice.settings.not.exist"),

    /**
     * 服务端发票设置已经变更
     */
    INVOICE_SETTINGS_CHANGED(90019, "invoice.settings.have.been.changed"),

    /**
     * 供应商发票设置不存在
     */
    SUPPLIER_INVOICE_SETTINGS_NOT_EXIST(90020, "supplier.invoice.settings.not.exist");


    private final int code;
    private final String msgCode;

    @Override
    public int code() {
        return getCode();
    }

    @Override
    public String msg() {
        return I18N.msg(getMsgCode());
    }
}
