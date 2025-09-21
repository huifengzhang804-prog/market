package com.medusa.gruul.order.service.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.order.api.entity.OrderDiscountItem;

import java.util.List;

/**
 * 订单优惠项 服务类
 *
 * @author 张治保
 * @since 2022-06-08
 */
public interface IOrderDiscountItemService extends IService<OrderDiscountItem> {

    /**
     * 查询订单优惠项
     *
     * @param orderNo 订单号
     * @param shopId  店铺id
     * @param itemId  订单商品项id
     * @return 该商品订单优惠项列表
     */
    List<OrderDiscountItem> getItem(String orderNo, Long shopId, Long itemId);
}
