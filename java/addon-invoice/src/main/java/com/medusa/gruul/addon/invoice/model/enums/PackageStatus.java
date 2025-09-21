package com.medusa.gruul.addon.invoice.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * date 2023/7/19
 */
@Getter
@RequiredArgsConstructor
public enum PackageStatus {
    /**
     * 待发货
     */
    WAITING_FOR_DELIVER(1),

    /**
     * 待入库
     */
    WAITING_FOR_RECEIVE(2),

    /**
     * 买家确认收货
     */
    COMPLETED(3),
    ;
    @EnumValue
    private final Integer value;
}
