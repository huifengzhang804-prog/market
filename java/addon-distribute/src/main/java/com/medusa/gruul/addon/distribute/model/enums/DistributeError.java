package com.medusa.gruul.addon.distribute.model.enums;

import com.medusa.gruul.global.i18n.I18N;
import com.medusa.gruul.global.model.exception.Error;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author jipeng
 * @since 2025/2/15
 */
@Getter
@RequiredArgsConstructor
public enum DistributeError implements Error {
    /**
     * 数据导出类型不存在
     */
    DATA_EXPORT_TYPE_NOT_EXISTS(10001, "data_export_type_not_exists"),

    DISTRIBUTE_ORDER_UPLOADER_ERROR(10002, "distribute_order_uploader_error"),

    TEMP_EXCEL_GENERATE_ERROR(10003, "temp_excel_generate_error");
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
