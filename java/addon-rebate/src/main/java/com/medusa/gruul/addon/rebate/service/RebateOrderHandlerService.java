package com.medusa.gruul.addon.rebate.service;

import com.medusa.gruul.addon.rebate.model.dto.RebateOrderQueryDTO;
import com.medusa.gruul.order.api.entity.ShopOrderItem;
import com.medusa.gruul.order.api.model.OrderCompletedDTO;
import com.medusa.gruul.order.api.model.OrderCreatedDTO;
import com.medusa.gruul.order.api.model.OrderPaidBroadcastDTO;
import com.medusa.gruul.order.api.pojo.OrderInfo;
import com.medusa.gruul.overview.api.entity.OverviewWithdraw;
import java.util.List;

public interface RebateOrderHandlerService {

    /**
     * 订单已创建
     * @param orderCreated 订单信息
     */
    void saveRebateOrderCreated(OrderCreatedDTO orderCreated);

    /**
     * 订单已支付
     *
     * @param orderPaidBroadcast 订单信息
     */
    void rebateOrderPayed(OrderPaidBroadcastDTO orderPaidBroadcast);

    /**
     * 订单关闭
     *
     * @param orderInfo 订单信息
     */
    void rebateOrderClosed(OrderInfo orderInfo);

    /**
     * 订单完成
     *
     * @param orderCompleted 订单信息
     */
    void rebateOrderAccomplish(OrderCompletedDTO orderCompleted);

    /***
     * 分页查询消费返利订单
     * @param rebateOrderQuery 查询参数
     * @return 消费返利订单
     */
    RebateOrderQueryDTO pageRebateOrder(RebateOrderQueryDTO rebateOrderQuery);

    /**
     * 提现工单被关闭
     * @param overviewWithdraw 提现工单信息
     */
    void rebateWithdrawOrderForbidden(OverviewWithdraw overviewWithdraw);

    /**
     * 更新返利订单信息
     *
     * @param shopOrderItems 订单项信息
     * @param orderNo 订单号
     * @return 是否更新成功
     */
    Boolean updateRebateOrderInfo(List<ShopOrderItem> shopOrderItems, String orderNo);

    /**
     * 消费返利订单导出
     * @param queryDTO
     */
    void export(RebateOrderQueryDTO queryDTO);
}
