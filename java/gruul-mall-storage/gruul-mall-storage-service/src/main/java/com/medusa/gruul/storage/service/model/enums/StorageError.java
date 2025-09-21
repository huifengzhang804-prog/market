package com.medusa.gruul.storage.service.model.enums;

import com.medusa.gruul.global.i18n.I18N;
import com.medusa.gruul.global.model.exception.Error;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * date 2023/6/19
 */
@Getter
@RequiredArgsConstructor
public enum StorageError implements Error {

    /**
     * 库存不足
     */
    OUT_OF_STOCK(40000, "storage.out.of.stock"),

    /**
     * 商品已参加营销活动
     */
    PRODUCT_HAS_ACTIVITY(40001, "storage.product.has.activity"),

    /**
     * 库存规格设置不正确
     */
    STOCK_SKU_ERROR(40002, "storage.stock.sku.error"),

    /**
     * 商品不存在
     */
    PRODUCT_NOT_EXIST(40003, "storage.product.not.exist"),
    /**
     * excel导出失败
     */
    EXCEL_EXPORT_ERROR(40004, "storage.excel.export.error"),

    TEMP_EXCEL_GENERATE_ERROR(10003, "temp.excel.generate.error");
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
}
