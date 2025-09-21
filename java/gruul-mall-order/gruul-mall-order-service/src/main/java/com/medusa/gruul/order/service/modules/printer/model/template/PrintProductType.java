package com.medusa.gruul.order.service.modules.printer.model.template;

/**
 * 商品数据格式
 *
 * @author 张治保
 * @since 2024/8/19
 */
public enum PrintProductType {
    /**
     * 表格形式的对齐
     * ---------------------
     * 商品名称     数量  单价
     * ---------------------
     * 测试商品     x1   ￥2.00
     */
    FORMAT,

    /**
     * 两端对齐
     * -------商品信息---------
     * 测试商品       x1 ￥2.00
     */
    SPACE_BETWEEN
}
