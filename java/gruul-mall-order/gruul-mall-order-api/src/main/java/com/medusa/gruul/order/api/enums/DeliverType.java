package com.medusa.gruul.order.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 配送方式
 *
 * @author 张治保
 * date 2022/3/9
 */
@Getter
@RequiredArgsConstructor
public enum DeliverType {

    /**
     * 无需物流发货
     */
    WITHOUT(0, false),
    /**
     * 普通发货 自己填 物流公司与 单号
     */
    EXPRESS(1, true),
    /**
     * 打印发货
     */
    PRINT_EXPRESS(2, true),

    /**
     * 同城 商家配送
     */
    IC_MERCHANT(3, false),

    /**
     * 同城 开放平台配送（如：UU跑腿）
     * 暂时只支持 uu跑腿
     */
    IC_OPEN(4, false),
    ;
    @EnumValue
    private final Integer value;

    /**
     * 是否需要物流公司信息
     */
    private final boolean needExpress;

}
