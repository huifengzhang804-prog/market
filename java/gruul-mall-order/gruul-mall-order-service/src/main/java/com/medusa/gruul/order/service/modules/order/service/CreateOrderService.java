package com.medusa.gruul.order.service.modules.order.service;

import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.order.service.model.bo.OrderDetailInfoBO;
import com.medusa.gruul.order.service.model.dto.OrderShopsDTO;
import com.medusa.gruul.order.service.model.vo.OrderBudgetVO;
import com.medusa.gruul.order.service.model.vo.OrderRespVO;

import java.util.List;

/**
 * 创建订单服务
 *
 * @author 张治保
 * date 2022/6/8
 */
public interface CreateOrderService {

    /**
     * 订单价格预算 非事务操作
     *
     * @param orderShops 订单信息
     * @return Object
     */
    OrderBudgetVO orderBudget(OrderShopsDTO orderShops);

    /**
     * 创建订单
     *
     * @param orderShops 订单信息
     * @return 创建的订单号
     */
    OrderRespVO create(OrderShopsDTO orderShops);

    /**
     * 批量保存
     *
     * @param orderDetails 订单key列表
     */
    void saveOrder2DbBatch(List<OrderDetailInfoBO> orderDetails);


    /**
     * 检验小程序下单商品数量
     *
     * @param productNum 商品数量
     * @param distributionMode 发货方式
     */
     void validMiniAppOrderCount(int productNum, DistributionMode distributionMode);




}
