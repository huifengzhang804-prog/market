package com.medusa.gruul.addon.invoice.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @Author: wufuzhong
 * @Date: 2023/10/12 11:10:08
 * @Description：发票设置删除状态enum
 * @Version: 1.0
 */

@Getter
@RequiredArgsConstructor
public enum InvoiceSettingsDeletedType {

    /**
     * 正常
     **/
    NORMAL(0),

    /**
     * 删除
     **/
    DELETED(1),;

    @EnumValue
    private final Integer value;
}
