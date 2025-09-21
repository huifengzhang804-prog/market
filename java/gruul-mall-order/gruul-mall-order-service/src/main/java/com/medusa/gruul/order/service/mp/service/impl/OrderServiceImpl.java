package com.medusa.gruul.order.service.mp.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
import com.medusa.gruul.order.service.mp.mapper.OrderMapper;
import com.medusa.gruul.order.service.mp.service.IOrderService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

/**
 * 服务实现类
 *
 * @author 张治保
 * @since 2022-06-08
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {
    @Override
    public IPage<Order> orderPage(OrderQueryDTO queryPage) {
        return baseMapper.orderPage(queryPage);
    }

    @Override
    public Order getOrder(OrderQueryBO orderQuery) {
        return baseMapper.getOrder(orderQuery);
    }

    @Override
    public List<ShopOrder> getShopOrders(ShopOrderQueryBO query) {
        return baseMapper.getShopOrders(query);
    }

    @Override
    public BuyerOrderCountVO buyerOrderCount(Long buyerId) {
        return baseMapper.buyerOrderCount(buyerId);
    }

    /**
     * 统计供应商时间范围内，代销商品的销售额和数量
     *
     * @param supplierId 供应商ID
     * @param beginDate  统计开始时间
     * @param endDate    统计结束时间
     * @return {@link ConsignmentProductTradeStatisticDTO}
     */
    @Override
    public List<ConsignmentProductTradeStatisticDTO> countConsignmentProductTradeStatistic(Long supplierId, LocalDate beginDate, LocalDate endDate) {
        return baseMapper.countConsignmentProductTradeStatistic(supplierId, beginDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
    }

    /**
     * 统计供应商时间范围内代销商品TOP数据，以商品为维度.
     *
     * @param supplierId 供应商ID
     * @param beginDate  统计开始时间
     * @param endDate    统计结束时间
     * @return {@link ConsignmentProductTradeTopDTO}
     */
    @Override
    public List<ConsignmentProductTradeTopDTO> countConsignmentProductTradeTop(Long supplierId, LocalDateTime beginDate, LocalDateTime endDate) {
        return this.baseMapper.countConsignmentProductTradeTop(supplierId, beginDate, endDate);
    }

    /**
     * 根据订单号，查询自营店铺订单和自营供货商订单
     *
     * @param dto 订单参数
     * @return 返回自营店铺订单和自营供货商订单
     */
    @Override
    public List<Order> getPlatFormDeliveryOrders(OrderPlatFormDeliveryDTO dto) {
        return this.baseMapper.getPlatFormDeliveryOrders(dto);
    }

    /**
     * 查询砍价订单
     *
     * @param buyerId        用户
     * @param shopOrderItems 商品订单
     * @param activityId     活动id
     * @return 返回订单
     */
    @Override
    public List<Order> getBargainOrders(Long buyerId, List<ShopOrderItem> shopOrderItems, Long activityId) {
        return baseMapper.getBargainOrders(buyerId, shopOrderItems, activityId);
    }

    @Override
    public List<Long> queryUnPaidOrderNum(Set<Long> shopIds) {
        return baseMapper.queryUnPaidOrderNum(shopIds);
    }

    @Override
    public List<Long> queryPaidNotFinishedOrderNum(Set<Long> finalShopIds1) {
        return baseMapper.queryPaidNotFinishedOrderNum(finalShopIds1);
    }

    @Override
    public OrderCountVO orderCount(OrderCountQueryDTO query) {
        return baseMapper.orderCount(query);
    }
}
