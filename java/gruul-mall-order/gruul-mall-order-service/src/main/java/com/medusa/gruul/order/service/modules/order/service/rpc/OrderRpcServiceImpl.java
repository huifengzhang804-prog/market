package com.medusa.gruul.order.service.modules.order.service.rpc;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.module.app.afs.AfsStatus;
import com.medusa.gruul.common.mp.model.SqlHelper;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.model.Platform;
import com.medusa.gruul.common.wechat.WechatProperties;
import com.medusa.gruul.global.model.express.UserAddressDTO;
import com.medusa.gruul.order.api.constant.OrderConstant;
import com.medusa.gruul.order.api.entity.*;
import com.medusa.gruul.order.api.enums.*;
import com.medusa.gruul.order.api.model.*;
import com.medusa.gruul.order.api.pojo.OrderInfo;
import com.medusa.gruul.order.api.pojo.SkuStock;
import com.medusa.gruul.order.api.rpc.OrderRpcService;
import com.medusa.gruul.order.service.model.bo.OrderQueryBO;
import com.medusa.gruul.order.service.model.bo.ShopOrderQueryBO;
import com.medusa.gruul.order.service.model.dto.OrderQueryDTO;
import com.medusa.gruul.order.service.model.enums.OrderError;
import com.medusa.gruul.order.service.model.vo.OrderShopOverviewVO;
import com.medusa.gruul.order.service.modules.afs.service.OrderAfsService;
import com.medusa.gruul.order.service.modules.order.service.CloseOrderService;
import com.medusa.gruul.order.service.modules.order.service.OrderOverviewService;
import com.medusa.gruul.order.service.modules.order.service.StoreOrderService;
import com.medusa.gruul.order.service.modules.order.task.OrderExportTask;
import com.medusa.gruul.order.service.mp.service.*;
import com.medusa.gruul.order.service.mq.service.OrderRabbitService;
import com.medusa.gruul.order.service.util.OrderUtil;
import com.medusa.gruul.payment.api.model.dto.RefundRequestDTO;
import com.medusa.gruul.payment.api.rpc.PaymentRpcService;
import io.vavr.Tuple;
import io.vavr.control.Option;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


/**
 * @author 张治保
 * date 2022/8/4
 */
@Slf4j
@Service
@DubboService
@RequiredArgsConstructor
public class OrderRpcServiceImpl implements OrderRpcService {

    private final Executor globalExecutor;
    private final IOrderService orderService;
    private final IShopOrderService shopOrderService;
    private final OrderAfsService orderAfsService;
    private final CloseOrderService closeOrderService;
    private final OrderRabbitService orderRabbitService;
    private final IShopOrderItemService shopOrderItemService;
    private final IOrderReceiverService orderReceiverService;
    private final IOrderEvaluateService orderEvaluateService;
    private final IOrderPaymentService orderPaymentService;
    private final StoreOrderService storeOrderService;
    private final PaymentRpcService paymentRpcService;
    private final IShopOrderPackageService orderPackageService;
    private final WechatProperties wechatProperties;
    private final OrderOverviewService orderOverviewService;


    @Override
    public Option<ShopOrderItem> getShopOrderItem(String orderNo, Long shopId, Long itemId) {
        return Option.of(TenantShop.disable(() -> shopOrderItemService.getCompleteItem(orderNo, shopId, itemId)))
                .peek(
                        shopOrderItem -> {
                            ShopOrder shopOrder = shopOrderItem.getShopOrder();
                            shopOrder.setOrderReceiver(orderReceiverService.getCurrentOrderReceiver(orderNo, shopOrder.getNo()));
                        }
                );

    }

    @Override
    public void updateShopOrderItemAfsStatus(ChangeAfsStatusDTO changeAfsStatus) {
        String orderNo = changeAfsStatus.getOrderNo();
        Long shopId = changeAfsStatus.getShopId();
        // item 检查
        ShopOrderItem completeItem = Option.of(TenantShop.disable(() -> shopOrderItemService.getCompleteItem(orderNo, shopId, changeAfsStatus.getItemId())))
                .getOrElseThrow(SystemCode.DATA_NOT_EXIST::exception);
        if (ItemStatus.CLOSED == completeItem.getStatus()) {
            throw OrderError.ORDER_CLOSED.exception();
        }
        //店铺订单检查
        ShopOrder shopOrder = Option.of(completeItem.getShopOrder()).getOrElseThrow(SystemCode.DATA_NOT_EXIST::exception);
        if (shopOrder.getStatus().isClosed()) {
            throw OrderError.ORDER_CLOSED.exception();
        }
        //订单检查
        Order order = Option.of(shopOrder.getOrder()).getOrElseThrow(SystemCode.DATA_NOT_EXIST::exception);
        if (order.getStatus().isClosed()) {
            throw OrderError.ORDER_CLOSED.exception();
        }
        if (OrderStatus.UNPAID == order.getStatus()) {
            throw OrderError.ORDER_CANNOT_AFS.exception();
        }
        //检查物流与售后状态
        AfsStatus nextStatus = changeAfsStatus.getStatus();
        this.checkStatus(completeItem.getPackageStatus(), completeItem.getAfsStatus(), nextStatus);
        //更新售后状态
        boolean success = orderAfsService.updateShopOrderItemAfsStatus(completeItem, changeAfsStatus.getAfsNo(), nextStatus);
        SystemCode.DATA_UPDATE_FAILED.falseThrow(success);
        //是否不是已退款 
        if (!nextStatus.isRefunded()) {
            return;
        }
        //已退款 表示这个订单项已被关闭
        //查询是否有 正常状态的 店铺订单项
        List<ShopOrderItem> okItems = TenantShop.disable(
                () -> shopOrderItemService.lambdaQuery()
                        .select(ShopOrderItem::getPackageStatus)
                        .eq(ShopOrderItem::getShopId, shopId)
                        .eq(ShopOrderItem::getOrderNo, orderNo)
                        .eq(ShopOrderItem::getStatus, ItemStatus.OK)
                        .list()
        );
        boolean emptyOkItems = CollUtil.isEmpty(okItems);
        //关闭前是代发货状态 判断是剩下的否全部已发货 如果已全部发货 则发送延迟确认收货 mq
        if (
                !emptyOkItems &&
                        PackageStatus.WAITING_FOR_DELIVER == completeItem.getPackageStatus() &&
                        okItems.stream().allMatch(item -> PackageStatus.WAITING_FOR_RECEIVE == item.getPackageStatus())
        ) {
            //发送延迟确认收货消息 mq
            orderRabbitService.sendDelayConfirmReceive(
                    false,
                    new OrderPackageKeyDTO()
                            .setBuyerId(order.getBuyerId())
                            .setOrderNo(orderNo)
                            .setShopOrderNo(shopOrder.getNo())
                            .setShopId(shopId),
                    order.getTimeout().getConfirmTimeoutMills(),
                    shopId

            );
        }
        //如果没有正常状态的订单项了直接关闭店铺订单
        Long itemId = completeItem.getId();
        if (emptyOkItems) {
            TenantShop.disable(() -> shopOrderService.lambdaUpdate()
                    .set(ShopOrder::getStatus, ShopOrderStatus.BUYER_CLOSED)
                    .eq(ShopOrder::getShopId, shopId)
                    .eq(ShopOrder::getOrderNo, orderNo)
                    .update());
        } else {
            //有正常状态的订单项 尝试转移运费
            transferFreightPrice(
                    orderNo,
                    shopId,
                    itemId,
                    completeItem.getFreightPrice(),
                    completeItem.getFreightTemplateId()
            );
        }
        //发送订单关闭mq
        orderRabbitService.sendOrderClose(
                new OrderInfo()
                        .setActivityType(order.getType())
                        .setActivityId(order.getActivityId())
                        .setCloseType(OrderCloseType.AFS)
                        .setShopId(completeItem.getShopId())
                        .setOrderNo(order.getNo())
                        .setBuyerId(order.getBuyerId())
                        .setAfs(
                                new OrderInfo.Afs()
                                        .setAfsNo(completeItem.getAfsNo())
                                        .setAfsTradeNo(changeAfsStatus.getAfsTradeNo())
                                        .setRefundAmount(changeAfsStatus.getRefundAmount())
                                        .setPackageId(completeItem.getPackageId())
                                        .setShopOrderItemId(itemId)
                                        .setSpecs(completeItem.getSpecs())
                        )
                        .setSkuStocks(
                                Collections.singletonList(
                                        new SkuStock().setShopId(completeItem.getShopId())
                                                .setProductId(completeItem.getProductId())
                                                .setSkuId(completeItem.getSkuId())
                                                .setNum(completeItem.getNum())
                                )
                        )
                        .setCloseTime(LocalDateTime.now())
                        .setHaveUncompletedItem(
                                okItems.stream()
                                        .anyMatch(
                                                item -> {
                                                    PackageStatus packageStatus = item.getPackageStatus();
                                                    return PackageStatus.SYSTEM_COMMENTED_COMPLETED != packageStatus
                                                            && PackageStatus.BUYER_COMMENTED_COMPLETED != packageStatus;
                                                }
                                        )
                        )
                        .setTransaction(orderPaymentService.lambdaQuery().select(OrderPayment::getTransactions).eq(OrderPayment::getOrderNo, completeItem.getOrderNo()).one().transaction(completeItem.getShopId()))
        );
    }

    /**
     * 运费转移
     *
     * @param orderNo           订单号
     * @param shopId            店铺id
     * @param itemId            订单项id
     * @param freightPrice      运费
     * @param freightTemplateId 运费模板id
     */
    private void transferFreightPrice(
            String orderNo,
            Long shopId,
            Long itemId,
            Long freightPrice,
            Long freightTemplateId
    ) {
        //有正常状态订单项 尝试 转移运费
        // 没有运费不处理
        if (freightPrice != null && freightPrice > 0) {
            return;
        }
        //更新 运费
        boolean success = TenantShop.disable(
                () -> shopOrderItemService.lambdaUpdate()
                        .set(ShopOrderItem::getFreightPrice, freightPrice)
                        .eq(ShopOrderItem::getOrderNo, orderNo)
                        .eq(ShopOrderItem::getShopId, shopId)
                        .eq(ShopOrderItem::getOrderNo, orderNo)
                        .eq(ShopOrderItem::getFreightTemplateId, freightTemplateId)
                        .ne(ShopOrderItem::getId, itemId)
                        .orderByAsc(ShopOrderItem::getId)
                        .last(SqlHelper.SQL_LIMIT_1)
                        .update()
        );
        //更新成功 把当前运费置为0
        if (success) {
            TenantShop.disable(() -> shopOrderItemService.lambdaUpdate()
                    .set(ShopOrderItem::getFreightPrice, 0)
                    .eq(ShopOrderItem::getOrderNo, orderNo)
                    .eq(ShopOrderItem::getShopId, shopId)
                    .eq(ShopOrderItem::getId, itemId)
                    .update()
            );
        }
    }

    @Override
    public Map<Long, OrderEvaluateCountDTO> getOrderEvaluateCount(Set<Long> shopIds) {
        if (CollUtil.isEmpty(shopIds)) {
            return Collections.emptyMap();
        }
        return TenantShop.disable(
                // 评价总分数 AS 为 productId，评价总条数 AS 为 skuId
                () -> orderEvaluateService.query()
                        .select("shop_id", "SUM(rate) AS product_id", "COUNT(*) AS sku_id")
                        .in("shop_id", shopIds)
                        .groupBy("shop_id")
                        .list()
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        OrderEvaluate::getShopId,
                                        (evaluate) -> new OrderEvaluateCountDTO()
                                                .setTotalScore(evaluate.getProductId())
                                                .setTotalCount(evaluate.getSkuId())
                                )
                        )
        );
    }

    /**
     * 根据productIds查询已评价人数
     *
     * @param productIds 商品ids
     * @return 人数
     */
    @Override
    public Map<Long, Long> getEvaluatePerson(Set<Long> productIds) {
        return orderEvaluateService.getEvaluatePerson(productIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Redisson(name = OrderConstant.ORDER_EDIT_LOCK_KEY, batchParamName = "#orderNos", key = "#item")
    public void updateOrderStatus(Set<String> orderNos, OrderStatus currentStatus, OrderStatus targetStatus) {
        if (CollUtil.isEmpty(orderNos)) {
            return;
        }
        closeOrderService.updateOrderStatus(orderNos, currentStatus, targetStatus);
    }

    /**
     * 批量门店订单发货
     *
     * @param orderNos 订单号s
     * @param storeId  门店id
     */
    @Override
    public void batchStoreOrderDeliver(Set<String> orderNos, Long storeId) {
        storeOrderService.batchStoreOrderDeliver(orderNos, storeId);
    }

    /**
     * 门店订单(核销/收货)
     *
     * @param orderPackageKey 订单收货信息
     */
    @Override
    public void storeOrderConfirmPackage(OrderPackageKeyDTO orderPackageKey) {
        storeOrderService.storeOrderConfirmPackage(orderPackageKey);
    }

    @Override
    public Boolean isNewUser(Long userId, Long shopId) {
        List<Order> orders = TenantShop.disable(
                () -> orderService.lambdaQuery()
                        .select(Order::getNo)
                        .eq(Order::getBuyerId, userId)
                        .eq(Order::getStatus, OrderStatus.PAID)
                        .list()
        );
        if (CollUtil.isEmpty(orders)) {
            return true;
        }
        Set<String> orderNos = orders.stream()
                .map(Order::getNo)
                .collect(Collectors.toSet());
        return !TenantShop.disable(
                () -> shopOrderService.lambdaQuery()
                        .in(ShopOrder::getOrderNo, orderNos)
                        .eq(ShopOrder::getShopId, shopId)
                        .eq(ShopOrder::getStatus, ItemStatus.OK)
                        .exists()
        );
    }

    /**
     * 获取订单票据信息
     *
     * @param orderNo 订单号
     * @return 订单票据信息
     */
    @Override
    public OrderBillDTO getOrderBillInfo(String orderNo) {
        OrderQueryBO query = new OrderQueryBO().setOrderNo(orderNo);
        ISecurity.match()
                .ifAnyShopAdmin(secureUser -> query.setShopOrderShopId(secureUser.getShopId()))
                .ifUser(secureUser -> query.setBuyerId(secureUser.getId()));
        Order order = TenantShop.disable(() -> orderService.getOrder(query));
        if (order.getStatus() != OrderStatus.PAID) {
            return null;
        }
        // 订单完成时间
        LocalDateTime updateTime = order.getUpdateTime();

        // 开票金额
        Long billMoney = order.getShopOrders()
                .stream()
                .filter(shopOrder -> !shopOrder.getStatus().isClosed())
                .flatMap(shopOrder -> shopOrder.getShopOrderItems().stream())
                .filter(shopOrderItem -> shopOrderItem.getPackageStatus().isCompleted())
                .mapToLong(shopOrderItem -> shopOrderItem.getDealPrice() * shopOrderItem.getNum() + shopOrderItem.getFreightPrice() + shopOrderItem.getFixPrice())
                .reduce(0L, Long::sum);


        return new OrderBillDTO().setBillMoney(billMoney).setOrderAccomplishTime(updateTime);
    }

    /**
     * 获取供应商代销订单统计数据
     *
     * @param supplierId 供应商ID
     * @return {@link ConsignmentOrderStatisticDTO}
     */
    @Override
    public ConsignmentOrderStatisticDTO countConsignmentOrderStatistic(Long supplierId) {
        OrderShopOverviewVO orderShopOverviewVO = orderOverviewService.orderShopOverView(
                new OrderQueryDTO()
                        .setSupplierId(supplierId)
                        .setSellType(SellType.CONSIGNMENT)
        );
        return new ConsignmentOrderStatisticDTO()
                .setPendingPaymentOrders(orderShopOverviewVO.getUnpaid())
                .setPendingDeliveredOrders(orderShopOverviewVO.getUndelivered())
                .setPendingReceivedOrders(orderShopOverviewVO.getUnreceived());
    }

    /**
     * 以代销商品+店铺为维度,统计时间范围内热销排行榜(TOP10)
     *
     * @param beginTime 统计开始时间
     * @param endTime   统计结束时间
     * @return {@link ConsignmentProductHotSoldDTO}
     */
    @Override
    public List<ConsignmentProductHotSoldDTO> countConsignmentProductHotSoldList(LocalDateTime beginTime, LocalDateTime endTime) {
        return TenantShop.disable(() -> this.shopOrderItemService.countConsignmentProductHotSoldList(beginTime, endTime));
    }

    /**
     * 以店铺为维度,统计时间范围内代销商品的营业额排行榜(TOP5)
     *
     * @param beginTime 统计开始时间
     * @param endTime   统计结束时间
     * @return {@link ConsignmentProductHotSoldShopDTO}
     */
    @Override
    public List<ConsignmentProductHotSoldShopDTO> countConsignmentProductHotSoldShopList(LocalDateTime beginTime, LocalDateTime endTime) {
        return TenantShop.disable(() -> this.shopOrderItemService.countConsignmentProductHotSoldShopList(beginTime, endTime));

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
        return TenantShop.disable(() ->
                this.orderService.countConsignmentProductTradeStatistic(supplierId, beginDate, endDate));
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
        return TenantShop.disable(() -> this.orderService.countConsignmentProductTradeTop(supplierId, beginDate, endDate));
    }

    /**
     * 获取砍价活动订单
     *
     * @param activityId 活动id
     * @param productId  商品id
     * @param shopId     店铺id
     * @return 订单
     */
    @Override
    public List<Order> getBargainOrder(Long activityId, Long productId, Long shopId) {
        List<ShopOrderItem> shopOrderItems = new ArrayList<>();
        ShopOrderItem shopOrderItem = new ShopOrderItem();
        shopOrderItem.setShopId(shopId);
        shopOrderItem.setProductId(productId);
        shopOrderItems.add(shopOrderItem);
        AtomicReference<List<Order>> orders = new AtomicReference<>();
        ISecurity.match().ifUser((secureUser) ->
                orders.set(
                        orderService.getBargainOrders(ISecurity.userMust().getId(), shopOrderItems, activityId)
                )
        );
        return orders.get();
    }

    @Override
    public List<ShopOrderItem> queryShopOrderItemAfsStatus(Set<Long> shopOrderItemIds) {
        return TenantShop.disable(
                () -> shopOrderItemService.queryShopOrderItemAfsStatus(shopOrderItemIds));

    }

    /**
     * /**
     * 根据售后工单查询是否还有待发货不处于售后、正在售后的明细
     *
     * @param afsNo 售后工单
     * @return true 存在 false 不存在
     */
    @Override
    public Boolean existsDeliverShopOrderItem(String afsNo) {
        ShopOrderItem shopOrderItem = TenantShop.disable(() -> shopOrderItemService.lambdaQuery()
                .eq(ShopOrderItem::getAfsNo, afsNo).one());

        OrderPayment orderPayment = TenantShop.disable(() -> orderPaymentService.lambdaQuery()
                .eq(OrderPayment::getOrderNo, shopOrderItem.getOrderNo()).one());
        //判断微信支付
        if (!PayType.WECHAT.equals(orderPayment.getType())) {
            return true;
        }

        if (shopOrderItem.getPackageId() != null) {
            return true;
        }
        ///判断该笔订单下是否还有待发货不处于售后、正在售后的明细
        log.warn("获取该笔订单下是否还有待发货不处于售后、正在售后的明细");
        return TenantShop.disable(() -> shopOrderItemService.lambdaQuery()
                .eq(ShopOrderItem::getOrderNo, shopOrderItem.getOrderNo())
                .ne(ShopOrderItem::getAfsNo, afsNo)
                .eq(ShopOrderItem::getPackageStatus, PackageStatus.WAITING_FOR_DELIVER)
                .in(ShopOrderItem::getAfsStatus,
                        AfsStatus.NONE, AfsStatus.REFUND_REQUEST, AfsStatus.REFUND_REJECT, AfsStatus.RETURN_REFUND_REQUEST,
                        AfsStatus.RETURN_REFUND_AGREE, AfsStatus.SYSTEM_RETURN_REFUND_AGREE,
                        AfsStatus.RETURNED_REFUND, AfsStatus.BUYER_CLOSED
                )
                .exists());
    }

    /**
     * 查询店铺未完成订单数量
     * todo 逻辑有问题 Set是无须的 可能不能正确和结果一一映射
     *
     * @param shopIds 店铺id集合
     * @return 店铺未完成订单数量
     */
    @Override
    public List<Long> queryShopUnCompleteOrderNum(Set<Long> shopIds) {
        //查询未付款的店铺订单数信息
        Set<Long> finalShopIds = shopIds;
        List<Long> result = TenantShop.disable(() -> orderService.queryUnPaidOrderNum(finalShopIds));
        shopIds = shopIds.stream().filter(shopId -> !result.contains(shopId)).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(shopIds)) {
            return result;
        }
        //查询用户已付款 发货状态没有到达评价完成的订单店铺信息

        Set<Long> finalShopIds1 = shopIds;
        List<Long> tempResult = TenantShop.disable(() -> orderService.queryPaidNotFinishedOrderNum(finalShopIds1));
        result.addAll(tempResult);
        return result;
    }

    @Override
    public Map<String, UserAddressDTO> orderReceiverAddress(Long shopId, Set<String> orderNos) {
        if (CollUtil.isEmpty(orderNos)) {
            return Map.of();
        }
        List<OrderReceiver> receivers = orderReceiverService.lambdaQuery()
                .select(
                        OrderReceiver::getOrderNo,
                        OrderReceiver::getShopOrderNo,
                        OrderReceiver::getName,
                        OrderReceiver::getMobile,
                        OrderReceiver::getLocation,
                        OrderReceiver::getArea,
                        OrderReceiver::getAddress
                )
                .in(OrderReceiver::getOrderNo, orderNos)
                .and(
                        qw -> qw.isNull(OrderReceiver::getShopId)
                                .or()
                                .eq(OrderReceiver::getShopId, shopId)
                )
                .list();
        Map<String, UserAddressDTO> orderReceiverMap = new HashMap<>(orderNos.size());
        for (OrderReceiver receiver : receivers) {
            orderReceiverMap.compute(
                    receiver.getOrderNo(),
                    (key, pre) -> {
                        if (pre != null && StrUtil.isEmpty(receiver.getShopOrderNo())) {
                            return pre;
                        }
                        return new UserAddressDTO()
                                .setName(receiver.getName())
                                .setMobile(receiver.getMobile())
                                .setLocation(receiver.getLocation())
                                .setArea(receiver.getArea())
                                .setAddress(receiver.getAddress());
                    }
            );
        }
        return orderReceiverMap;
    }

    @Override
    public void icSendDelayConfirmPackage(String orderNo, Long shopId) {
        //查询主订单
        Order order = orderService.lambdaQuery()
                .select(Order::getBuyerId, Order::getStatus, Order::getDistributionMode, Order::getTimeout, Order::getExtra, Order::getPlatform)
                .eq(Order::getNo, orderNo)
                .one();
        if (order == null) {
            throw OrderError.ORDER_NOT_EXIST.exception();
        }
        if (OrderStatus.PAID != order.getStatus()) {
            throw OrderError.INVALID_ORDER_STATUS.exception();
        }
        //查询店铺订单
        ShopOrder shopOrder = TenantShop.disable(() ->
                shopOrderService.lambdaQuery()
                        .select(ShopOrder::getNo)
                        .eq(ShopOrder::getShopId, shopId)
                        .eq(ShopOrder::getOrderNo, orderNo)
                        .eq(ShopOrder::getStatus, ShopOrderStatus.OK)
                        .one()
        );
        if (shopOrder == null) {
            throw OrderError.INVALID_ORDER_STATUS.exception();
        }
        //查询包裹信息
        ShopOrderPackage shopOrderPackage = TenantShop.disable(() -> orderPackageService.lambdaQuery()
                .select(ShopOrderPackage::getId)
                .eq(ShopOrderPackage::getShopId, shopId)
                .eq(ShopOrderPackage::getOrderNo, orderNo)
                .one()
        );
        if (shopOrderPackage == null) {
            throw OrderError.INVALID_ORDER_STATUS.exception();
        }
        //查询支付信息
        OrderPayment orderPayment = orderPaymentService.lambdaQuery()
                .select(OrderPayment::getTransactions, OrderPayment::getType)
                .eq(OrderPayment::getOrderNo, orderNo)
                .one();
        //查询商品项
        List<ShopOrderItem> shopOrderItems = TenantShop.disable(() -> shopOrderItemService.lambdaQuery()
                .select(ShopOrderItem::getFreightPrice, ShopOrderItem::getExtra)
                .eq(ShopOrderItem::getOrderNo, orderNo)
                .eq(ShopOrderItem::getShopId, shopId)
                .list()
        );
        //更新为可确认收货
        TenantShop.disable(
                () -> shopOrderService.lambdaUpdate()
                        .setSql(SqlHelper.renderJsonSetSql("extra", Tuple.of("icReceivable", true)))
                        .eq(ShopOrder::getShopId, shopId)
                        .eq(ShopOrder::getOrderNo, orderNo)
                        .eq(ShopOrder::getStatus, ShopOrderStatus.OK)
                        .update()
        );
        globalExecutor.execute(
                () -> {
                    orderRabbitService.sendDelayConfirmReceive(
                            false,
                            new OrderPackageKeyDTO()
                                    .setBuyerId(order.getBuyerId())
                                    .setOrderNo(orderNo)
                                    .setShopId(shopId),
                            order.getTimeout().getConfirmTimeoutMills(),
                            shopId
                    );
                    OrderPackageKeyDTO orderPackageKeyDTO = new OrderPackageKeyDTO()
                            .setDistributionMode(order.getDistributionMode())
                            .setBuyerId(order.getBuyerId())
                            .setOrderNo(orderNo)
                            .setShopOrderNo(shopOrder.getNo())
                            .setShopId(shopId)
                            .setExtra(order.getExtra())
                            .setPackageId(shopOrderPackage.getId())
                            .setTransaction(orderPayment.transaction(shopId));
                    //计算已支付运费与优惠运费
                    long paidFreight = 0, discountFreight = 0;
                    for (ShopOrderItem item : shopOrderItems) {
                        paidFreight += item.getFreightPrice();
                        discountFreight += item.getExtra().getDiscountFreight();
                    }
                    orderPackageKeyDTO.setPaidFreight(paidFreight);
                    orderPackageKeyDTO.setDiscountFreight(discountFreight);
                    orderRabbitService.sendOrderDeliver(orderPackageKeyDTO);
                    //小程序发货
                    if (wechatProperties.getMiniAppDeliver() && Platform.WECHAT_MINI_APP == order.getPlatform() && orderPayment.getType() == PayType.WECHAT) {
                        orderPackageKeyDTO.setLogisticsType(OrderUtil.getLogisticsType(order.getDistributionMode()));
                        orderRabbitService.sendMiniAppOrderDeliver(orderPackageKeyDTO);
                    }
                }
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Redisson(name = OrderConstant.ORDER_EDIT_LOCK_KEY, key = "#param.orderNo")
    @Redisson(value = OrderConstant.ORDER_PACKAGE_LOCK, key = "#param.orderNo+':'+#param.shopId")
    public void resetOrderStatus(ResetOrderStatusDTO param) {
        //解构参数
        String orderNo = param.getOrderNo();
        Long shopId = param.getShopId();
        ErrorHandlerStatus status = param.getStatus();
        //已送达 不需要处里
        if (ErrorHandlerStatus.DELIVERED == status) {
            return;
        }
        Order order = orderService.lambdaQuery()
                .select(Order::getActivityId, Order::getType, Order::getBuyerId)
                .eq(Order::getNo, orderNo)
                .eq(Order::getStatus, OrderStatus.PAID)
                .one();
        if (order == null) {
            throw OrderError.INVALID_ORDER_STATUS.exception();
        }
        ShopOrder shopOrder = TenantShop.disable(() -> shopOrderService.lambdaQuery()
                .eq(ShopOrder::getOrderNo, orderNo)
                .eq(ShopOrder::getShopId, shopId)
                .one());
        if (ShopOrderStatus.OK != shopOrder.getStatus()) {
            throw OrderError.INVALID_ORDER_STATUS.exception();
        }
        //如果重新发货
        if (ErrorHandlerStatus.RESHIP == status) {
            //删除已发货包裹 商品包裹状态重置为代发货状态
            TenantShop.disable(() -> orderPackageService.lambdaUpdate()
                    .eq(ShopOrderPackage::getOrderNo, orderNo)
                    .eq(ShopOrderPackage::getShopId, shopId)
                    .remove());
            TenantShop.disable(() -> shopOrderItemService.lambdaUpdate()
                    .set(ShopOrderItem::getPackageId, null)
                    .set(ShopOrderItem::getPackageStatus, PackageStatus.WAITING_FOR_DELIVER)
                    .eq(ShopOrderItem::getShopId, shopId)
                    .eq(ShopOrderItem::getOrderNo, orderNo)
                    .eq(ShopOrderItem::getStatus, ItemStatus.OK)
                    .update());
            return;
        }
        //关闭订单
        //先查询商品和库存信息
        List<ShopOrderItem> shopOrderItems = TenantShop.disable(() -> shopOrderItemService.lambdaQuery()
                .select(
                        ShopOrderItem::getProductId, ShopOrderItem::getSkuId, ShopOrderItem::getNum,
                        ShopOrderItem::getDealPrice, ShopOrderItem::getNum, ShopOrderItem::getFixPrice,
                        ShopOrderItem::getFreightPrice
                )
                .eq(ShopOrderItem::getShopId, shopId)
                .eq(ShopOrderItem::getOrderNo, orderNo)
                .eq(ShopOrderItem::getStatus, ItemStatus.OK)
                .list()
        );
        //是否存在其它店铺的订单
        boolean existOtherShopOrder = TenantShop.disable(() -> shopOrderService.lambdaQuery()
                .eq(ShopOrder::getOrderNo, orderNo)
                .eq(ShopOrder::getStatus, ShopOrderStatus.OK)
                .ne(ShopOrder::getShopId, shopId)
                .exists());
        //如果不存在 则直接关闭主订单
        if (!existOtherShopOrder) {
            orderService.lambdaUpdate()
                    .set(Order::getStatus, OrderStatus.SELLER_CLOSED)
                    .eq(Order::getNo, orderNo)
                    .update();
        }
        TenantShop.disable(() -> {
            shopOrderService.lambdaUpdate()
                    .set(ShopOrder::getStatus, ShopOrderStatus.SELLER_CLOSED)
                    .eq(ShopOrder::getOrderNo, orderNo)
                    .eq(ShopOrder::getShopId, shopId)
                    .update();
            shopOrderItemService.lambdaUpdate()
                    .set(ShopOrderItem::getStatus, ItemStatus.CLOSED)
                    .eq(ShopOrderItem::getOrderNo, orderNo)
                    .eq(ShopOrderItem::getShopId, shopId)
                    .update();
        });
        Long refundFee = shopOrderItems.stream()
                .map(item -> item.payPrice() + item.getFreightPrice())
                .reduce(0L, Long::sum);
        //退款
        if (refundFee > 0) {
            paymentRpcService.refundRequest(
                    new RefundRequestDTO()
                            .setOrderNum(orderNo)
                            .setShopId(shopId)
                            .setRefundFee(refundFee)
                            .setDesc("订单配送异常，关闭订单退款")
            );
        }
        orderRabbitService.sendOrderClose(
                new OrderInfo()
                        .setCloseType(existOtherShopOrder ? OrderCloseType.FULL : OrderCloseType.SHOP)
                        .setActivityType(order.getType())
                        .setActivityId(order.getActivityId())
                        .setOrderNo(orderNo)
                        .setBuyerId(order.getBuyerId())
                        .setShopId(shopId)
                        .setSkuStocks(OrderUtil.toSkuStocks(shopOrderItems))
                        .setCloseTime(LocalDateTime.now())
                        .setHaveUncompletedItem(false)

        );

    }

    @Override
    public List<OrderInfoDTO> queryOrderInfoForExport(Set<String> orderNos) {

        OrderQueryDTO queryDTo=new OrderQueryDTO();
        queryDTo.setExportOrderNos(orderNos);

        IPage<Order> page = TenantShop.disable(() -> orderService.orderPage(queryDTo));
        List<Order> orderList = page.getRecords();
        if (CollectionUtils.isEmpty(orderList)) {
            return Lists.newArrayList();
        }
        List<ShopOrder> shopOrders = TenantShop.disable(
                () -> orderService.getShopOrders(
                        new ShopOrderQueryBO().setOrderNos(orderNos)
                                )
                );
        Map<String, List<ShopOrder>> shopOrderMap = shopOrders.stream().collect(Collectors.groupingBy(ShopOrder::getOrderNo));
        orderList.forEach(order -> {
            order.setShopOrders(shopOrderMap.get(order.getNo()));
        });
        return orderList.stream().map(order -> {
            OrderInfoDTO orderInfoDTO=new OrderInfoDTO();
            orderInfoDTO.setOrderNo(order.getNo());
            orderInfoDTO.setStatus(order.getStatus());
            orderInfoDTO.setPayment(order.getOrderPayment());
            orderInfoDTO.setOrderDiscounts(order.getOrderDiscounts());
            orderInfoDTO.setOrderStatusContent(OrderExportTask.transOrderStatus(order));
            return orderInfoDTO;
        }).collect(Collectors.toList());
    }

    /**
     * 检查订单商品状态 与 售后状态
     *
     * @param packageStatus 订单状态
     * @param current       当前售后状态
     * @param target        目标售后状态
     */
    private void checkStatus(PackageStatus packageStatus, AfsStatus current, AfsStatus target) {
        log.debug("更改售后状态 {}:{} => {}", packageStatus, current, target);
        if (!packageStatus.isCanAfs()) {
            throw OrderError.ORDER_CANNOT_AFS.exception();
        }
        switch (target) {
            //退款申请 退货退款申请
            case REFUND_REQUEST, RETURN_REFUND_REQUEST -> {
                if (AfsStatus.RETURN_REFUND_REQUEST == target) {
                    this.checkPackageStatusForReturnedRefund(packageStatus);
                }
            }
            //同意退款申请 拒绝退款申请 系统自动同意退款申请
            case REFUND_AGREE, REFUND_REJECT, SYSTEM_REFUND_AGREE -> {
                if (AfsStatus.REFUND_REQUEST != current) {
                    throw OrderError.ORDER_AFS_STATUS_CHANGED.exception();
                }
            }
            //退款
            case REFUNDED -> {
                if (AfsStatus.REFUND_AGREE != current && AfsStatus.SYSTEM_REFUND_AGREE != current) {
                    throw OrderError.ORDER_AFS_STATUS_CHANGED.exception();
                }
            }
            //同意退货退款申请
            case RETURN_REFUND_AGREE, RETURN_REFUND_REJECT, SYSTEM_RETURN_REFUND_AGREE -> {
                if (AfsStatus.RETURN_REFUND_REQUEST != current) {
                    throw OrderError.ORDER_AFS_STATUS_CHANGED.exception();
                }
            }
            //用户退货
            case RETURNED_REFUND -> {
                if (AfsStatus.RETURN_REFUND_AGREE != current && AfsStatus.SYSTEM_RETURN_REFUND_AGREE != current) {
                    throw OrderError.ORDER_AFS_STATUS_CHANGED.exception();
                }
            }
            //商家确认收货 拒绝收货 系统自动确认收货
            case RETURNED_REFUND_CONFIRM, RETURNED_REFUND_REJECT, SYSTEM_RETURNED_REFUND_CONFIRM -> {
                if (AfsStatus.RETURNED_REFUND != current) {
                    throw OrderError.ORDER_AFS_STATUS_CHANGED.exception();
                }
            }
            //已退款
            case RETURNED_REFUNDED -> {
                if (AfsStatus.RETURNED_REFUND_CONFIRM != current && AfsStatus.SYSTEM_RETURNED_REFUND_CONFIRM != current) {
                    throw OrderError.ORDER_AFS_STATUS_CHANGED.exception();
                }
            }
            //用户关闭
            case BUYER_CLOSED, SYSTEM_CLOSED -> {
                if (!current.isCanClose()) {
                    throw OrderError.ORDER_AFS_STATUS_CHANGED.exception();
                }
            }
            default -> throw SystemCode.DATA_UPDATE_FAILED.exception();
        }

    }

    private void checkPackageStatusForReturnedRefund(PackageStatus packageStatus) {
        if (PackageStatus.WAITING_FOR_DELIVER == packageStatus) {
            throw OrderError.GOODS_NOT_DELIVERED_NOT_SUPPORT_AFS_TYPE.exception();
        }

    }


}
