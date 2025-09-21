package com.medusa.gruul.addon.supplier.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author miskw
 * @date 2023/8/9
 * @describe 描述
 */
@Getter
@RequiredArgsConstructor
public enum PaveGoodsStatus {
    /**
     * 待铺货
     */
    WAITING_PAVE_GOODS(0),
    /**
     * 已铺货
     */
    FINISH(1);
    @EnumValue
    private final int value;
}
