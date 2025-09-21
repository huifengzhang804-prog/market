package com.medusa.gruul.storage.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 
 *     库存类型
 * 
 * 无限个
 * @author 张治保
 * date 2022/6/21
 */
@Getter
@RequiredArgsConstructor
public enum StockType {

    /**
     * 无限个
     */
    UNLIMITED(1),
    /**
     * 有限个
     */
    LIMITED(2);


    @EnumValue
    private final Integer value;
}
