package com.medusa.gruul.goods.api.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @Description: 供应商状态枚举
 * @Author: xiaoq
 * @Date : 2022-04-27 17:11
 */
@Getter
@RequiredArgsConstructor
public enum SupplierStatus {




    /**
     * 已审核
     */
    REVIEW(1),



    /**
     * 禁用中
     */
    FORBIDDEN(3);
    @EnumValue
    private final Integer value;
}
