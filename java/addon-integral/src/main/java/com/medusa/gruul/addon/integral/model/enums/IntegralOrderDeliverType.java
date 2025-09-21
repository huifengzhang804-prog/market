package com.medusa.gruul.addon.integral.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 积分订单发货类型
 *
 * @author shishuqian
 * date 2023/2/6
 * time 13:25
 **/

@Getter
@RequiredArgsConstructor
public enum IntegralOrderDeliverType {

    /**
     * 无需物流发货
     */
    WITHOUT(0),
    /**
     * 普通发货 自己填 物流公司与 单号
     */
    EXPRESS(1);

    @EnumValue
    private final Integer value;
}
