package com.medusa.gruul.order.service.model.bo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.medusa.gruul.order.api.entity.Order;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author 张治保
 * @since 2024/4/24
 */
@RequiredArgsConstructor
public class OrderPage implements IPage<Order> {

    @Getter(AccessLevel.NONE)
    private final IPage<Order> delegate;


    @Override
    public List<OrderItem> orders() {
        return delegate.orders();
    }

    @Override
    public List<Order> getRecords() {
        return delegate.getRecords();
    }

    @Override
    public IPage<Order> setRecords(List<Order> records) {
        return delegate.setRecords(records);
    }

    @Override
    public long getTotal() {
        return delegate.getTotal();
    }

    @Override
    public IPage<Order> setTotal(long total) {
        return delegate.setTotal(total);
    }

    @Override
    public long getSize() {
        return delegate.getSize();
    }

    @Override
    public IPage<Order> setSize(long size) {
        return delegate.setSize(size);
    }

    @Override
    public long getCurrent() {
        return delegate.getCurrent();
    }

    @Override
    public IPage<Order> setCurrent(long current) {
        return delegate.setCurrent(current);
    }
}
