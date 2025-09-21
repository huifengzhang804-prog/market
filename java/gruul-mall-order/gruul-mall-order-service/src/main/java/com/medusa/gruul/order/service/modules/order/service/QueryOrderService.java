package com.medusa.gruul.order.service.modules.order.service;

import com.medusa.gruul.order.api.entity.Order;
import com.medusa.gruul.order.api.entity.OrderPayment;
import com.medusa.gruul.order.api.entity.ShopOrder;
import com.medusa.gruul.order.api.entity.ShopOrderItem;
import com.medusa.gruul.order.service.model.dto.OrderCountQueryDTO;
import com.medusa.gruul.order.service.model.dto.OrderDetailQueryDTO;
import com.medusa.gruul.order.service.model.vo.BuyerOrderCountVO;
import com.medusa.gruul.order.service.model.vo.OrderCountVO;
import io.vavr.control.Option;

/**
 * 订单查询服务
 *
 * @author 张治保
 * date 2022/6/16
 */
public interface QueryOrderService {

    /**
     * 根据订单id获取订单详情
     *
     * @param orderDetailQuery 查询条件
     * @return 订单详情
     */
    Option<Order> orderDetail(OrderDetailQueryDTO orderDetailQuery);

    /**
     * 店铺订单详情
     *
     * @param orderNo     订单号
     * @param shopOrderNo 店铺订单号
     * @return 店铺订单详情
     */
    Option<ShopOrder> getShopOrderByNo(String orderNo, String shopOrderNo);

    /**
     * 根据订单id查询待支付订单支付信息
     *
     * @param orderNo 订单号
     * @return 订单支付信息
     */
    OrderPayment getUnpaidOrderPayment(String orderNo);

    /**
     * 我的订单统计
     *
     * @return 订单数量统计
     */
    BuyerOrderCountVO buyerOrderCount();

    /**
     * 查询订单创建情况 就是查看中订单的缓存还在不在 在
     *
     * @param orderNo 订单号
     * @return 是否已创建 true 缓存中已不存在已写入db, false缓存中存在
     */
    boolean orderCreation(String orderNo);


    /**
     * 根据订单号 与订单商品项id 查询订单商品项
     *
     * @param orderNo 订单号
     * @param itemId  订单商品项id
     * @return 订单商品项 可能为空
     */
    Option<ShopOrderItem> getShopOrderItem(String orderNo, Long itemId);

    /**
     * 订单数量统计
     *
     * @param query 查询条件
     * @return 订单数量统计
     */
    OrderCountVO orderCount(OrderCountQueryDTO query);

    /**
     * 小程序订单确认收货
     * @param orderNo
     */
    void miniAppDeliverConfirm(String orderNo);
}
