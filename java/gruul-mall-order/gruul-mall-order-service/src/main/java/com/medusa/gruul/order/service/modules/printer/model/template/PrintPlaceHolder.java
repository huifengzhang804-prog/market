package com.medusa.gruul.order.service.modules.printer.model.template;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 打印模板的占位符名称
 *
 * @author 张治保
 * @since 2024/8/19
 */
@Getter
@RequiredArgsConstructor
public enum PrintPlaceHolder {
    pickupCode(null),
    platformName(null),
    shopName(null),
    orderRemark("备注："),
    //商品列表
    productListInf(null),
    //结算信息
    orderStatistic(null),

    //订单信息
    orderNo("订单编号："),
    payType("支付方式："),
    orderTime("下单时间："),
    payTime("支付时间："),

    //收货人｜门店信息
    targetName(null),
    targetMobile(null),
    targetAddress(null),
    ;

    private final String label;

    public String placeholder() {
        return (label == null ? StrUtil.EMPTY : label) + "{" + name() + "}";
    }
}
