package com.medusa.gruul.order.service.modules.printer.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * @since 2024/8/21
 */
@Getter
@RequiredArgsConstructor
public enum PrinterBrand {

    FEIE(1);

    @EnumValue
    private final int value;

}
