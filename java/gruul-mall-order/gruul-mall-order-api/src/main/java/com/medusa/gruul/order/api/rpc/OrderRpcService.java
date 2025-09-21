package com.medusa.gruul.order.api.rpc;

import com.medusa.gruul.global.model.express.UserAddressDTO;
import com.medusa.gruul.order.api.entity.Order;
import com.medusa.gruul.order.api.entity.ShopOrderItem;
import com.medusa.gruul.order.api.enums.OrderStatus;
import com.medusa.gruul.order.api.model.*;
import io.vavr.control.Option;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 张治保
 * date 2022/8/4
 */
public interface OrderRpcService {

    /**
     * 获取店铺订单商品详情
     *
     * @param orderNo 订单号
     * @param shopId  店铺id
     * @param itemId  店铺订单id
     * @return 店铺订单商品
     */
    Option<ShopOrderItem> getShopOrderItem(String orderNo, Long shopId, Long itemId);

    /**
     * 更新店铺订单商品售后状态
     *
     * @param changeAfsStatus 更新信息详情
     */
    void updateShopOrderItemAfsStatus(ChangeAfsStatusDTO changeAfsStatus);


    /**
     * 根据店铺id 列表批量查询店铺 商品总评分
     *
     * @param shopIds 店铺id列表
     * @return 查询结果 key -> shopId   value -> shopId对应的查询结果
     */
    Map<Long, OrderEvaluateCountDTO> getOrderEvaluateCount(@NotNull @Size(min = 1) Set<Long> shopIds);

    /**
     * 根据productIds查询已评价人数
     *
     * @param productIds 商品ids
     * @return 人数
     */
    Map<Long, Long> getEvaluatePerson(Set<Long> productIds);

    /**
     * 修改订单状态
     *
     * @param orderNos      订单号
     * @param currentStatus 当前订单状态
     * @param targetStatus  目标订单状态
     */
    void updateOrderStatus(Set<String> orderNos, OrderStatus currentStatus, OrderStatus targetStatus);


    /**
     * 批量门店订单发货(门店备货完成)
     *
     * @param orderNos 订单号s
     * @param storeId  门店id
     */
    void batchStoreOrderDeliver(Set<String> orderNos, Long storeId);


    /**
     * 门店订单 扫码核销/用户收货
     *
     * @param orderPackageKey 订单收货信息
     */
    void storeOrderConfirmPackage(OrderPackageKeyDTO orderPackageKey);

    /**
     * 判断用户是否是新用户
     *
     * @param userId 用户id
     * @param shopId 店铺id
     * @return true 是新用户 false 不是新用户
     */
    Boolean isNewUser(Long userId, Long shopId);

    /**
     * 获取订单票据信息
     *
     * @param orderNo 订单号
     * @return 订单票据信息
     */
    OrderBillDTO getOrderBillInfo(String orderNo);

    /**
     * 获取供应商代销订单统计数据
     *
     * @param supplierId 供应商ID
     * @return {@link ConsignmentOrderStatisticDTO}
     */
    ConsignmentOrderStatisticDTO countConsignmentOrderStatistic(@NotNull Long supplierId);

    /**
     * 以代销商品+店铺为维度,统计时间范围内热销排行榜(TOP10)
     *
     * @param beginTime 统计开始时间
     * @param endTime   统计结束时间
     * @return {@link ConsignmentProductHotSoldDTO}
     */
    List<ConsignmentProductHotSoldDTO> countConsignmentProductHotSoldList(@NotNull LocalDateTime beginTime,
                                                                          @NotNull LocalDateTime endTime);


    /**
     * 以店铺为维度,统计时间范围内代销商品的营业额排行榜(TOP5)
     *
     * @param beginTime 统计开始时间
     * @param endTime   统计结束时间
     * @return {@link ConsignmentProductHotSoldShopDTO}
     */
    List<ConsignmentProductHotSoldShopDTO> countConsignmentProductHotSoldShopList(@NotNull LocalDateTime beginTime,
                                                                                  @NotNull LocalDateTime endTime);

    /**
     * 统计供应商时间范围内，代销商品的销售额和数量
     *
     * @param supplierID 供应商ID
     * @param beginDate  统计开始时间
     * @param endDate    统计结束时间
     * @return {@link ConsignmentProductTradeStatisticDTO}
     */
    List<ConsignmentProductTradeStatisticDTO> countConsignmentProductTradeStatistic(@NotNull Long supplierID,
                                                                                    @NotNull LocalDate beginDate,
                                                                                    @NotNull LocalDate endDate);

    /**
     * 统计供应商时间范围内代销商品TOP数据，以商品为维度.
     *
     * @param supplierID 供应商ID
     * @param beginDate  统计开始时间
     * @param endDate    统计结束时间
     * @return {@link ConsignmentProductTradeTopDTO}
     */
    List<ConsignmentProductTradeTopDTO> countConsignmentProductTradeTop(@NotNull Long supplierID,
                                                                        @NotNull LocalDateTime beginDate,
                                                                        @NotNull LocalDateTime endDate);

    /**
     * 获取砍价活动订单
     *
     * @param activityId 活动id
     * @param productId  商品id
     * @param shopId     店铺id
     * @return 订单
     */
    List<Order> getBargainOrder(@NotNull Long activityId, @NotNull Long productId, @NotNull Long shopId);

    /**
     * 查询店铺订单商品项的售后状态
     *
     * @param shopOrderItemIds
     * @return
     */
    List<ShopOrderItem> queryShopOrderItemAfsStatus(Set<Long> shopOrderItemIds);

    /**
     * 根据售后工单查询是否还有待发货或未同意退款的订单
     *
     * @param afsNo 售后工单
     * @return true 存在 false 不存在
     */
    Boolean existsDeliverShopOrderItem(String afsNo);

    /**
     * 查询店铺未完成订单数量
     *
     * @param shopIds
     * @return
     */
    List<Long> queryShopUnCompleteOrderNum(Set<Long> shopIds);

    /**
     * 批量查询店铺订单用户收货地址
     */
    Map<String, UserAddressDTO> orderReceiverAddress(Long shopId, Set<String> orderNos);

    /**
     * 同城 包裹确认收货
     *
     * @param orderNo 订单号
     * @param shopId  店铺 id
     */
    void icSendDelayConfirmPackage(String orderNo, Long shopId);


    /**
     * 同城 重置订单状态
     *
     * @param param 重置参数
     */
    void resetOrderStatus(ResetOrderStatusDTO param);

    /**
     * 根据订单ids查询订单的信息
     * @param orderNos
     * @return
     */
    List<OrderInfoDTO> queryOrderInfoForExport(Set<String> orderNos);
}
