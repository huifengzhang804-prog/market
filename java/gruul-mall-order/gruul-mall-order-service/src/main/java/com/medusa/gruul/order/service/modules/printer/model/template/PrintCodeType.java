package com.medusa.gruul.order.service.modules.printer.model.template;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * @since 2024/8/15
 */
@Getter
@RequiredArgsConstructor
public enum PrintCodeType {

    /**
     * 二维码
     */
    QR_CODE,

    /**
     * 条形码
     */
    BAR_CODE
}
