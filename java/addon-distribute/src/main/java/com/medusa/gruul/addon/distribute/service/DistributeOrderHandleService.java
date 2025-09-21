package com.medusa.gruul.addon.distribute.service;

import com.medusa.gruul.addon.distribute.model.dto.DistributorOrderQueryDTO;
import com.medusa.gruul.addon.distribute.model.vo.DistributorOrderPageVO;
import com.medusa.gruul.order.api.model.OrderCreatedDTO;
import com.medusa.gruul.order.api.model.OrderPaidBroadcastDTO;
import com.medusa.gruul.order.api.pojo.OrderInfo;

/**
 * @author 张治保 date 2023/5/12
 */
public interface DistributeOrderHandleService {


    /**
     * 分页查询分销商订单
     *
     * @param query 查询条件
     * @return 分页查询结果
     */
    DistributorOrderPageVO orderPage(DistributorOrderQueryDTO query);


    /**
     * 订单已创建详情
     *
     * @param orderCreated 订单创建详情
     */
    void orderCreated(OrderCreatedDTO orderCreated);


    /**
     * 订单关闭
     *
     * @param orderInfo 订单信息
     */
    void orderClosed(OrderInfo orderInfo);

    /**
     * 订单已支付
     *
     * @param orderPaidBroadcast 订单信息
     */
    void orderPaid(OrderPaidBroadcastDTO orderPaidBroadcast);

    /**
     * 导出分销订单
     *
     * @param queryDTO
     */
    void export(DistributorOrderQueryDTO queryDTO);
}
