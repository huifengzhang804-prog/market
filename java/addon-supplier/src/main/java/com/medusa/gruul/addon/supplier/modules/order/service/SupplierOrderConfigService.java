package com.medusa.gruul.addon.supplier.modules.order.service;

import com.medusa.gruul.addon.supplier.model.bo.OrderTimeout;
import com.medusa.gruul.addon.supplier.model.bo.PaymentMethod;

/**
 * @author 张治保
 * date 2023/7/24
 */
public interface SupplierOrderConfigService {

    /**
     * 更新订单超时时间配置
     *
     * @param timeout 超时时间配置
     */
    void updateOrderTimeout(OrderTimeout timeout);


    /**
     * 获取订单超时时间配置
     *
     * @return 超时时间配置
     */
    OrderTimeout orderTimeout();

    /**
     * 设置付款方式
     * @param method
     */
    void configPaymentMethod(PaymentMethod method);

    /**
     * 获取采购订单的付款方式
     * @return
     */
    PaymentMethod getPaymentMethod();
}
