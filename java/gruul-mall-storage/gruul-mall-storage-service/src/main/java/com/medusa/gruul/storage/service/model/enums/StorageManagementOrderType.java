package com.medusa.gruul.storage.service.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 库存管理订单类型
 *
 * @author xiaoq
 * @Description StorageManagementOrderType.java
 * @date 2023-07-25 15:29
 */
@Getter
@RequiredArgsConstructor
public enum StorageManagementOrderType {
    /**
     * 出入库
     */
    IN_OUT(0, Boolean.FALSE),

    /**
     * 盘点
     */
    INVENTORY(1, Boolean.TRUE),
    ;
    @EnumValue
    private final Integer value;

    private final Boolean changeable;

}
