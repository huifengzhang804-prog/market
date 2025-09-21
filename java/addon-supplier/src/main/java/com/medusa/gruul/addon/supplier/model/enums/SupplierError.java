package com.medusa.gruul.addon.supplier.model.enums;

import com.medusa.gruul.global.i18n.I18N;
import com.medusa.gruul.global.model.exception.Error;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 供应商错误码
 *
 * @author xiaoq
 * @since 2023-07-17 14:10
 */
@Getter
@RequiredArgsConstructor
public enum SupplierError implements Error {

    /**
     * 供应商不存在
     */
    SUPPLIER_NOT_EXIST(210000, "supplier.not.exist"),

    /**
     * 商品不存在
     */
    PRODUCT_NOT_EXIST(210001, "supplier.product.not.exist"),

    /**
     * 商品状态异常
     */
    PRODUCT_STATUS_ERROR(210002, "supplier.product.status.error"),

    /**
     * 商品 SKU 信息不存在
     */
    SKU_NOT_EXIST(210002, "supplier.sku.not.exist"),

    /**
     * 未满足起批数量
     */
    NOT_MEET_MINIMUM(210003, "supplier.not.meet.minimum"),

    /**
     * 库存不足
     */
    INSUFFICIENT_STOCK(210004, "supplier.insufficient.stock"),

    /**
     * 订单状态已改变
     */
    ORDER_STATUS_CHANGED(210005, "supplier.order.status.changed"),

    /**
     * 拆分数量设置有误
     */
    SPLIT_NUMBER_ERROR(210006, "supplier.split.number.error"),

    /**
     * 剩余采购数量不足
     */
    INSUFFICIENT_PURCHASE_NUM(210007, "supplier.insufficient.purchase.num"),

    /**
     * 该商品未发货货不存在购买记录
     */
    STORAGE_SKU_RECORD_NOT_EXIST(210008, "supplier.storage.record.sku.not.exist"),


    /**
     * 入库数量设置有误，总数不能超过采购数
     */
    STORAGE_NUM_SET_ERROR(210009, "supplier.storage.num.set.error"),

    /**
     * 订单未支付
     */
    ORDER_NOT_PAID(210010, "supplier.order.not.pay"),

    /**
     * 订单未发货 不可完成
     */
    ORDER_CANNOT_COMPLETE(210010, "supplier.cannot.complete"),

    /**
     * 商品已发布
     */
    PRODUCT_PUBLISHED(210011, "supplier.product.published"),


    /**
     * 查询类目未签约
     */
    QUERY_CATEGORY_UNSIGNED(210012, "supplier.query.category.unsigned"),


    /**
     * 当前删除商品存在未下架商品,不可删除
     */
    PRODUCT_CANNOT_BE_DELETED(210013, "supplier.product.cannot.be.deleted"),

    /**
     * 开始日期不能晚于结束日期
     */
    BEGIN_DATE_CAN_NOT_BE_AFTER_END_DATE(210014, "supplier.overview.begin.date.cannot.be.after.end.date"),

    /**
     * 代销商品校验失败
     */
    CONSIGNMENT_PRODUCT_CHECK_FAIL(210015, "supplier.consignment.product.check.fail"),

    /**
     * 采购订单上传失败
     */
    PURCHASE_ORDER_UPLOADER_ERROR(210016, "supplier.purchase.order.uploader.error"),
    /**
     * 采购单生成失败
     */
    TEMP_PURCHASE_ORDER_GENERATE_ERROR(210017, "supplier.temp.purchase.order.generate.error"),
    /**
     * 供应商商品状态不正确
     */
    SUPPLIER_PRODUCT_STATUS_NOT_CORRECT(210018, "supplier.product.status.not.correct"),

    ;
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

    @Override
    public String msg(Object... args) {
        return I18N.msg(getMsgCode(), args);
    }
}
