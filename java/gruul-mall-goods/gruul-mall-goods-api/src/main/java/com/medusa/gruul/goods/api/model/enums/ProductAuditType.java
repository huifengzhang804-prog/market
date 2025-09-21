package com.medusa.gruul.goods.api.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 商品审核类型
 *
 * @author xiaoq
 * @Description ProductAuditType.java
 * @date 2023-10-19 13:41
 */
@Getter
@RequiredArgsConstructor
public enum ProductAuditType {

    /**
     * 人工审核
     */
    MANUALLY_AUDIT(0),

    /**
     * 系统审核(自动通过)
     */
    SYSTEM_AUDIT(1),
    ;


    @EnumValue
    private final int value;
}
