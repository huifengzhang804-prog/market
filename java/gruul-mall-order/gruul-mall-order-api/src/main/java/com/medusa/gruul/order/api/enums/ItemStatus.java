package com.medusa.gruul.order.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 订单项状态
 *
 * @author 张治保
 * date 2022/3/9
 */
@Getter
@RequiredArgsConstructor
public enum ItemStatus {

    /**
     * 正常
     */
    OK(1),

    /**
     * 已关闭
     */
    CLOSED(2),
    ;

    @EnumValue
    private final Integer value;
}
