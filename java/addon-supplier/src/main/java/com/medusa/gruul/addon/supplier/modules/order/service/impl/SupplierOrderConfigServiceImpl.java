package com.medusa.gruul.addon.supplier.modules.order.service.impl;

import com.medusa.gruul.addon.supplier.model.SupplierConst;
import com.medusa.gruul.addon.supplier.model.bo.OrderTimeout;
import com.medusa.gruul.addon.supplier.model.bo.PaymentMethod;
import com.medusa.gruul.addon.supplier.modules.order.service.SupplierOrderConfigService;
import com.medusa.gruul.common.redis.util.RedisUtil;
import io.vavr.control.Option;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author 张治保
 * date 2023/7/24
 */
@Service
public class SupplierOrderConfigServiceImpl implements SupplierOrderConfigService {

    private OrderTimeout timeout;

    @Override
    public void updateOrderTimeout(OrderTimeout timeout) {
        RedisUtil.setCacheMap(SupplierConst.ORDER_TIMEOUT_CONFIG_KEY, timeout);
        this.timeout = timeout;
    }

    @Override
    public OrderTimeout orderTimeout() {
        return this.timeout = Option.of(this.timeout)
                .orElse(() -> Option.of(RedisUtil.getCacheMap(SupplierConst.ORDER_TIMEOUT_CONFIG_KEY, OrderTimeout.class)))
                .getOrElse(OrderTimeout::new);
    }

    @Override
    public void configPaymentMethod(PaymentMethod method) {
        RedisUtil.setCacheMap(SupplierConst.ORDER_PAYMENT_METHOD, method);
    }

    @Override
    public PaymentMethod getPaymentMethod() {
        return Option.of(RedisUtil.getCacheMap(SupplierConst.ORDER_PAYMENT_METHOD, PaymentMethod.class))
                .getOrElse(PaymentMethod::initInstance);
    }


}
