package com.medusa.gruul.order.api.helper;

/**
 * @author 张治保
 * @since date 2023/9/13
 */
public interface OrderDiscountHelper {

    /**
     * 计算订单优惠金额构造器
     *
     * @return 计算订单优惠金额构造器
     */
    static DiscountBuilder builder() {
        return new DiscountBuilder();
    }


}
