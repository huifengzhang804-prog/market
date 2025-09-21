package com.medusa.gruul.shop.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 操作原因
 *
 * @author 张治保
 * date 2022/4/14
 */
@Getter
@RequiredArgsConstructor
public enum OperaReason {

    /**
     * 店铺更新
     */
    UPDATE(1),
    
    /**
     * 店铺删除
     */
    DELETED(2);

    @EnumValue
    private final Integer value;

}
