package com.medusa.gruul.order.service.modules.printer.feie.api.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 飞鹅打印机机型
 *
 * @author 张治保
 * @since 2024/8/14
 */
@Getter
@RequiredArgsConstructor
public enum FeiePrinterType {

    /**
     * 58小票机
     * 58mm的机器,一行打印16个汉字,32个字母;
     */
    P_58(0, true),

    /**
     * 80小票机
     * 80mm的机器,一行打印24个汉字,48个字母
     */
    P_80(1, true),

    /**
     * 标签机
     */
    P_LABEL(2, false),

    /**
     * 出餐宝
     */
    P_CCB(3, false),
    ;
    /**
     * model机型，0：58小票机，1：80小票机，2：标签机，3：出餐宝
     */
    private final int value;

    /**
     * 是否是小票机
     */
    private final boolean ticket;
}
