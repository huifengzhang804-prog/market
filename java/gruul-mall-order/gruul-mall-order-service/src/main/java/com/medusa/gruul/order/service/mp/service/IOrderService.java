package com.medusa.gruul.order.service.mp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.order.api.entity.Order;
import com.medusa.gruul.order.api.entity.ShopOrder;
import com.medusa.gruul.order.api.entity.ShopOrderItem;
import com.medusa.gruul.order.api.model.ConsignmentProductTradeStatisticDTO;
import com.medusa.gruul.order.api.model.ConsignmentProductTradeTopDTO;
import com.medusa.gruul.order.service.model.bo.OrderQueryBO;
import com.medusa.gruul.order.service.model.bo.ShopOrderQueryBO;
import com.medusa.gruul.order.service.model.dto.OrderCountQueryDTO;
import com.medusa.gruul.order.service.model.dto.OrderPlatFormDeliveryDTO;
import com.medusa.gruul.order.service.model.dto.OrderQueryDTO;
import com.medusa.gruul.order.service.model.vo.BuyerOrderCountVO;
import com.medusa.gruul.order.service.model.vo.OrderCountVO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * 订单服务类
 *
 * @author 张治保
 * @since 2022-06-08
 */
public interface IOrderService extends IService<Order> {

    /**
     * 订单分页查询
     *
     * @param queryPage 查询条件
     * @return 分页查询结果
     */
    IPage<Order> orderPage(OrderQueryDTO queryPage);

    /**
     * 根据条件查询订单详情(超级管理员 或 消费者查询接口)
     *
     * @param orderQuery 查询条件
     * @return 订单详情
     */
    Order getOrder(OrderQueryBO orderQuery);

    /**
     * 查询店铺订单列表 根据条件
     *
     * @param query 查询条件
     * @return 店铺订单列表
     */
    List<ShopOrder> getShopOrders(ShopOrderQueryBO query);


    /**
     * 根据买家用户id查询订单统计
     *
     * @param buyerId 买家用户id
     * @return 查询结果
     */
    BuyerOrderCountVO buyerOrderCount(Long buyerId);

    /**
     * 统计供应商时间范围内，代销商品的销售额和数量
     *
     * @param supplierId 供应商ID
     * @param beginDate  统计开始时间
     * @param endDate    统计结束时间
     * @return {@link ConsignmentProductTradeStatisticDTO}
     */
    List<ConsignmentProductTradeStatisticDTO> countConsignmentProductTradeStatistic(Long supplierId, LocalDate beginDate, LocalDate endDate);

    /**
     * 统计供应商时间范围内代销商品TOP数据，以商品为维度.
     *
     * @param supplierId 供应商ID
     * @param beginDate  统计开始时间
     * @param endDate    统计结束时间
     * @return {@link ConsignmentProductTradeTopDTO}
     */
    List<ConsignmentProductTradeTopDTO> countConsignmentProductTradeTop(Long supplierId, LocalDateTime beginDate, LocalDateTime endDate);

    /**
     * 根据订单号，查询自营店铺订单和自营供货商订单
     *
     * @param dto 订单参数
     * @return 返回自营店铺订单和自营供货商订单
     */
    List<Order> getPlatFormDeliveryOrders(OrderPlatFormDeliveryDTO dto);

    /**
     * 查询砍价订单
     *
     * @param buyerId        用户
     * @param shopOrderItems 商品订单
     * @param activityId     活动id
     * @return 返回订单
     */
    List<Order> getBargainOrders(Long buyerId, List<ShopOrderItem> shopOrderItems, Long activityId);

    /**
     * 查询未付款得分订单数量
     *
     * @param shopIds 店铺ids
     * @return 店铺, 未完成订单数Map
     */
    List<Long> queryUnPaidOrderNum(Set<Long> shopIds);

    /**
     * 查询用户已付款 但是未完成订单信息
     *
     * @param finalShopIds1 店铺ids
     * @return 店铺, 未完成订单数Map
     */
    List<Long> queryPaidNotFinishedOrderNum(Set<Long> finalShopIds1);

    /**
     * 订单数量统计
     *
     * @param query 查询条件
     * @return 订单数量统计
     */
    OrderCountVO orderCount(OrderCountQueryDTO query);
}
