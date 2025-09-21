package com.medusa.gruul.order.service.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
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
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Mapper 接口
 *
 * @author 张治保
 * @since 2022-06-08
 */
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 根据查询条件 分页查询订单信息
     *
     * @param queryPage 分页查询条件
     * @return 分页查询结果
     */
    IPage<Order> orderPage(@Param("query") OrderQueryDTO queryPage);

    /**
     * 查询店铺订单列表 根据条件
     *
     * @param query 查询条件
     * @return 店铺订单列表
     */
    List<ShopOrder> getShopOrders(@Param("query") ShopOrderQueryBO query);

    /**
     * 根据条件查询订单详情
     *
     * @param query 查询条件
     * @return 订单详情
     */
    Order getOrder(@Param("query") OrderQueryBO query);

    /**
     * 根据买家用户id查询订单统计
     *
     * @param buyerId 买家用户id
     * @return 查询结果
     */
    BuyerOrderCountVO buyerOrderCount(@Param("buyerId") Long buyerId);

    /**
     * 统计供应商时间范围内，代销商品的销售额和数量
     *
     * @param supplierId 供应商ID
     * @param startTime  统计开始时间
     * @param endTime    统计结束时间
     * @return {@link ConsignmentProductTradeStatisticDTO}
     */
    List<ConsignmentProductTradeStatisticDTO> countConsignmentProductTradeStatistic(@Param("supplierId") Long supplierId,
                                                                                    @Param("startTime") LocalDateTime startTime,
                                                                                    @Param("endTime") LocalDateTime endTime);

    /**
     * 统计供应商时间范围内代销商品TOP数据，以商品为维度.
     *
     * @param supplierId 供应商ID
     * @param beginDate  统计开始时间
     * @param endDate    统计结束时间
     * @return {@link ConsignmentProductTradeTopDTO}
     */
    List<ConsignmentProductTradeTopDTO> countConsignmentProductTradeTop(@Param("supplierId") Long supplierId,
                                                                        @Param("beginDate") LocalDateTime beginDate,
                                                                        @Param("endDate") LocalDateTime endDate);

    /**
     * 根据订单号，查询自营店铺订单和自营供货商订单
     *
     * @param dto 订单参数
     * @return 返回自营店铺订单和自营供货商订单
     */
    List<Order> getPlatFormDeliveryOrders(@Param("order") OrderPlatFormDeliveryDTO dto);

    /**
     * 查询砍价订单
     *
     * @param buyerId        用户
     * @param shopOrderItems 商品订单
     * @param activityId     活动id
     * @return 返回订单
     */
    List<Order> getBargainOrders(@Param("buyerId") Long buyerId,
                                 @Param("shopOrderItems") List<ShopOrderItem> shopOrderItems,
                                 @Param("activityId") Long activityId);

    /**
     * 查询店铺未付款的订单数
     *
     * @param shopIds
     * @return
     */
    List<Long> queryUnPaidOrderNum(@Param("shopIds") Set<Long> shopIds);

    /**
     * 查询用户已付款 但是订单未完成的信息
     *
     * @param shopIds 店铺ids
     * @return
     */

    List<Long> queryPaidNotFinishedOrderNum(@Param("shopIds") Set<Long> shopIds);

    /**
     * 订单数量统计
     *
     * @param query 查询条件
     * @return 订单数量统计
     */
    OrderCountVO orderCount(@Param("query") OrderCountQueryDTO query);
}
