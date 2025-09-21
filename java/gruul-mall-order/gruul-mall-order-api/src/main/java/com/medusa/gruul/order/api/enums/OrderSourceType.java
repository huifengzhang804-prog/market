package com.medusa.gruul.order.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <p>
 *     订单来源
 * </p>
 *
 * @author 张治保
 * date 2022/9/26
 */
@Getter
@RequiredArgsConstructor
public enum OrderSourceType {

    /**
     *  商品详情
     */
    PRODUCT(1),


    /**
     * 购物车
     **/
    CART(2);



    @EnumValue
    private final Integer value;
}
