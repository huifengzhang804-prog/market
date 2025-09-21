package com.medusa.gruul.order.service.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.order.api.entity.OrderDiscount;
import com.medusa.gruul.order.service.model.dto.OrderDetailQueryDTO;

import java.util.List;

/**
 * 订单优惠项 服务类
 *
 * @author 张治保
 * @since 2022-06-08
 */
public interface IOrderDiscountService extends IService<OrderDiscount> {

    /**
     * 查询订单折扣详情列表
     *
     * @param query 查询条件
     * @return 订单折扣详情列表
     */
    List<OrderDiscount> orderDiscounts(OrderDetailQueryDTO query);

    /**
     * 根据订单号查询订单优惠项
     * @param orderNos 订单号
     * @return 订单优惠项
     */
    List<OrderDiscount> orderDiscountsByOrderNos(List<String> orderNos);

}
