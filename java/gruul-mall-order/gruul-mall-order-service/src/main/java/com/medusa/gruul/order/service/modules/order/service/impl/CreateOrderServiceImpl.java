package com.medusa.gruul.order.service.modules.order.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.common.mp.config.MybatisPlusConfig;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.common.system.model.model.Platform;
import com.medusa.gruul.common.wechat.WechatProperties;
import com.medusa.gruul.global.model.helper.CompletableTask;
import com.medusa.gruul.order.api.constant.OrderConstant;
import com.medusa.gruul.order.api.entity.*;
import com.medusa.gruul.order.api.enums.DiscountSourceType;
import com.medusa.gruul.order.api.enums.OrderCloseType;
import com.medusa.gruul.order.api.enums.OrderStatus;
import com.medusa.gruul.order.api.helper.DiscountBuilder;
import com.medusa.gruul.order.api.helper.ItemDiscount;
import com.medusa.gruul.order.api.helper.OrderDiscountHelper;
import com.medusa.gruul.order.api.model.OrderCreatedDTO;
import com.medusa.gruul.order.api.model.OrderPaidBroadcastDTO;
import com.medusa.gruul.order.api.pojo.OrderInfo;
import com.medusa.gruul.order.service.model.bo.MemberOrder;
import com.medusa.gruul.order.service.model.bo.OrderCost;
import com.medusa.gruul.order.service.model.bo.OrderDetailInfoBO;
import com.medusa.gruul.order.service.model.bo.OrderRecord;
import com.medusa.gruul.order.service.model.dto.OrderShopsDTO;
import com.medusa.gruul.order.service.model.dto.ProductDTO;
import com.medusa.gruul.order.service.model.dto.ShopPackageDTO;
import com.medusa.gruul.order.service.model.enums.OrderError;
import com.medusa.gruul.order.service.model.vo.OrderBudgetVO;
import com.medusa.gruul.order.service.model.vo.OrderRespVO;
import com.medusa.gruul.order.service.modules.order.addon.OrderAddonDistributionSupporter;
import com.medusa.gruul.order.service.modules.order.service.CreateOrderHelperService;
import com.medusa.gruul.order.service.modules.order.service.CreateOrderService;
import com.medusa.gruul.order.service.modules.order.service.GenerateOrderService;
import com.medusa.gruul.order.service.modules.order.service.OrderSaveService;
import com.medusa.gruul.order.service.mp.service.IOrderService;
import com.medusa.gruul.order.service.mq.service.OrderRabbitService;
import com.medusa.gruul.order.service.util.OrderUtil;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author 张治保
 * date 2022/6/8
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CreateOrderServiceImpl implements CreateOrderService {

    private static final Comparator<OrderDiscount> DISCOUNT_COMPARATOR = (o1, o2) -> o1.getSourceType().isPlatform() == o2.getSourceType().isPlatform() ? 0 : o2.getSourceType().isPlatform() ? -1 : 1;
    private final Executor globalExecutor;
    private final OrderSaveService orderSaveService;
    private final GenerateOrderService generateOrderService;
    private final OrderRabbitService orderRabbitService;
    private final CreateOrderHelperService createOrderHelperService;

    private final IOrderService orderService;
    private final WechatProperties wechatProperties;
    private final OrderAddonDistributionSupporter addonDistributionSupporter;
    private final OrderAddonDistributionSupporter orderAddonDistributionSupporter;


    @Override
    public OrderBudgetVO orderBudget(OrderShopsDTO orderShops) {
        SecureUser<Object> secureUser = ISecurity.userMust();
        int productCount = orderShops.getShopPackages()
                .stream()
                .mapToInt(
                        shopPackage -> shopPackage.getProducts()
                                .stream()
                                .mapToInt(ProductDTO::getNum)
                                .sum()
                ).sum();
        //校验微信小程序下单商品数不可超过10个
        validMiniAppOrderCount(productCount, orderShops.getDistributionMode()
        );
        /* 渲染订单与会员信息
         */
        MemberOrder memberOrder = generateOrderService.getOrder(secureUser, orderShops);
        createOrderHelperService.orderBudget(memberOrder, orderShops);
        Order order = memberOrder.getOrder();
        //计算折扣与商品价格渲染订单数据
        OrderRecord orderDetail = this.getOrderDetail(order);
        //计算总价
        OrderPayment orderPayment = orderDetail.getPayment();

        //渲染响应数据
        OrderBudgetVO budget = new OrderBudgetVO()
                .setOrderInfo(null)
                .setTotal(orderPayment.getTotalAmount())
                .setShopDiscount(0L)
                .setPlatformDiscount(0L)
                .setMemberDiscount(0L)
                .setFreight(orderPayment.getFreightAmount())
                .setPayAmount(orderPayment.getPayAmount())
                .setShopFull(Map.of());
        //店铺运费计算
        Map<Long, Long> shopFreight = MapUtil.newHashMap();
        for (ShopOrderItem shopOrderItem : orderDetail.getShopOrderItems()) {
            shopFreight.compute(
                    shopOrderItem.getShopId(),
                    (key, value) -> shopOrderItem.getFreightPrice() + ObjectUtil.defaultIfNull(value, (long) CommonPool.NUMBER_ZERO)
            );
        }
        budget.setShopFreight(shopFreight);
        //所有折扣项
        List<OrderDiscount> orderDiscounts = orderDetail.getDiscounts();
        //如果没有折扣 直接返回数据
        List<OrderDiscountItem> discountItems;
        if (CollUtil.isEmpty(orderDiscounts) || CollUtil.isEmpty(discountItems = orderDetail.getDiscountItems())) {
            return budget;
        }


        //店铺与满减优惠金额
        Map<Long, Long> shopFull = MapUtil.newHashMap();
        //计算折扣
        Map<Long, DiscountSourceType> discountSourceTypeMap = orderDiscounts.stream()
                .collect(
                        Collectors.toMap(OrderDiscount::getId, OrderDiscount::getSourceType)
                );
        for (OrderDiscountItem discountItem : discountItems) {
            //获取执行的函数
            DiscountSourceType sourceType = discountSourceTypeMap.get(discountItem.getDiscountId());
            if (DiscountSourceType.FULL_REDUCTION == sourceType) {
                shopFull.compute(
                        discountItem.getShopId(),
                        (key, value) -> discountItem.getDiscountAmount() + ObjectUtil.defaultIfNull(value, (long) CommonPool.NUMBER_ZERO)
                );
            }
            Consumer<Long> task = DiscountSourceType.MEMBER_DEDUCTION == sourceType ?
                    budget::incrMemberDiscount : (sourceType.isPlatform() ? budget::incrPlatformDiscount : budget::incrShopDiscount);
            task.accept(discountItem.getDiscountAmount());
        }
        //渲染返回数据
        return budget.setShopFull(shopFull);
    }

    @Override
    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    public OrderRespVO create(OrderShopsDTO orderShops) {

        SecureUser<Object> secureUser = ISecurity.userMust();
        Long buyerId = secureUser.getId();

        //校验砍价活动订单是否已下单，砍价活动订单每个用户只允许购买一次
        validBargainOrder(buyerId, orderShops);

        int productCount = orderShops.getShopPackages()
                .stream()
                .mapToInt(
                        shopPackage -> shopPackage.getProducts()
                                .stream()
                                .mapToInt(ProductDTO::getNum)
                                .sum()
                ).sum();
        //校验微信小程序下单商品数不可超过10个
        validMiniAppOrderCount(productCount, orderShops.getDistributionMode());
        if (DistributionMode.INTRA_CITY_DISTRIBUTION.equals(orderShops.getDistributionMode())) {
            //如果配送方式为同城配送 校验所有的店铺是否都支持同城配送
            Set<Long> shopIds = orderShops.getShopPackages().stream().map(ShopPackageDTO::getId).collect(Collectors.toSet());
            if (!orderAddonDistributionSupporter.checkAllSupportIc(shopIds)) {
                throw OrderError.NOT_SUPPORT_DISTRIBUTION_MODE.exception();
            }
        }

        /* 渲染订单与会员信息
         */
        MemberOrder memberOrder = generateOrderService.getOrder(secureUser, orderShops);
        String orderNo = memberOrder.getOrder().getNo();
        /* 渲染订单处理
         */
        Try.run(() -> createOrderHelperService.orderNecessaryProcess(memberOrder, orderShops))
                .onFailure(
                        exception -> {
                            log.debug("订单创建失败", exception);
                            orderRabbitService.sendOrderCreateFailed(
                                    new OrderInfo()
                                            .setCloseType(OrderCloseType.FULL)
                                            .setActivityType(memberOrder.getOrder().getType())
                                            .setActivityId(memberOrder.getOrder().getActivityId())
                                            .setBuyerId(buyerId)
                                            .setOrderNo(orderNo)
                                            .setSkuStocks(
                                                    OrderUtil.toSkuStocks(
                                                            memberOrder.getOrder().getShopOrders().stream().flatMap(shopOrder -> shopOrder.getShopOrderItems().stream()).toList()
                                                    )
                                            )
                            );

                            OrderUtil.deleteOrderCache(buyerId, orderNo);
                        }
                ).get();
        return new OrderRespVO().setOrderNo(orderNo).setExtra(memberOrder.getOrder().getActivityResp().getExtra());
    }

    /**
     * 校验微信小程序下单商品数不可超过10个
     *
     * @param productNum       商品数量
     * @param distributionMode 配送方式
     */
    @Override
    public void validMiniAppOrderCount(int productNum, DistributionMode distributionMode) {
        //小程序发货上传 未开启，不做校验
        if (!wechatProperties.getMiniAppDeliver()) {
            return;
        }
        //平台类型
        Platform platform = ISystem.platformMust();
        if (Platform.WECHAT_MINI_APP != platform) {
            return;
        }
        //微信小程序，快递配送模式，限制10个商品
        if (productNum > CommonPool.NUMBER_TEN && DistributionMode.EXPRESS == distributionMode) {
            throw OrderError.MINI_APP_ORDER_LIMIT_TEN.exception();
        }
    }

    /**
     * 校验是否已下单过砍价活动订单
     *
     * @param buyerId    用户id
     * @param orderShops 下单详情
     */
    private void validBargainOrder(Long buyerId, OrderShopsDTO orderShops) {
        if (OrderType.BARGAIN.equals(orderShops.getOrderType())) {
            List<ShopOrderItem> shopOrderItems = new ArrayList<>();
            orderShops.getShopPackages().forEach(
                    order -> {
                        Long shopId = order.getId();
                        order.getProducts().forEach(product -> {
                            ShopOrderItem item = new ShopOrderItem();
                            item.setProductId(product.getId());
                            item.setShopId(shopId);
                            shopOrderItems.add(item);
                        });
                    });
            List<Order> orders = TenantShop.disable(() -> orderService.getBargainOrders(
                    buyerId,
                    shopOrderItems,
                    orderShops.getActivity().getActivityId()));
            if (CollUtil.isNotEmpty(orders)) {
                throw OrderError.ORDER_BARGAIN_ONLY_BUY_ONE.exception();
            }
        }
    }

    public OrderPayment getOrderPayment(OrderCost orderCost, Order order) {
        OrderPayment orderPayment = new OrderPayment();
        long totalAmount = orderCost.getTotalAmount();
        long freightAmount = orderCost.getTotalFreightAmount();
        long discountAmount = orderCost.getTotalDiscountAmount();
        long payAmount = totalAmount + freightAmount - discountAmount;
        if (payAmount < 0) {
            payAmount = 0;
        }
        LocalDateTime createTime = order.getCreateTime();
        orderPayment.setOrderNo(order.getNo())
                .setTotalAmount(totalAmount)
                .setFreightAmount(freightAmount)
                .setDiscountAmount(discountAmount)
                .setPayAmount(payAmount)
                .setCreateTime(createTime);
        //若支付金额=0 则直接设置为已支付状态
        if (payAmount == 0) {
            order.setStatus(order.paidStatus());
            orderPayment.setTransactions(Map.of())
                    .setPayTime(createTime)
                    .setPayerId(order.getBuyerId())
                    .setType(PayType.BALANCE);
        }
        return orderPayment;
    }

    private OrderRecord getOrderDetail(OrderDetailInfoBO orderKey) {
        String orderNo = orderKey.getOrderNo();
        Long buyerId = orderKey.getBuyerId();
        // 订单
        Order order = OrderUtil.getCacheOrder(buyerId, orderNo);
        return getOrderDetail(order);
    }

    private OrderRecord getOrderDetail(Order order) {
        if (order == null) {
            return null;
        }
        //完整订单信息列表
        OrderReceiver receiver = order.getOrderReceiver();
        List<ShopOrder> shopOrders = CollUtil.emptyIfNull(order.getShopOrders());
        List<OrderDiscount> orderDiscounts = CollUtil.emptyIfNull(order.getOrderDiscounts());
        OrderCost orderCost = this.orderCostIno(shopOrders, orderDiscounts);
        OrderPayment orderPayment = getOrderPayment(orderCost, order);
        return new OrderRecord()
                .setOrder(order)
                .setReceiver(receiver)
                .setPayment(orderPayment)
                .setDiscounts(orderDiscounts)
                .setDiscountItems(orderCost.getOrderDiscountItems())
                .setShopOrders(shopOrders)
                .setShopOrderItems(orderCost.getShopOrderItems());
    }

    /**
     * 测试
     *
     * @param shopOrders     店铺订单
     * @param orderDiscounts 订单优惠信息
     */
    private OrderCost orderCostIno(List<ShopOrder> shopOrders, List<OrderDiscount> orderDiscounts) {
        //items 转 sku key 对应的 map
        Map<ShopProductSkuKey, List<ShopOrderItem>> shopOrderItemMap = shopOrders.stream()
                .flatMap(shopOrder -> shopOrder.getShopOrderItems().stream())
                .collect(
                        Collectors.groupingBy(ShopOrderItem::shopProductSkuKey)
                );
        //折扣项排序 按照既定顺序排序  平台优惠优先
        orderDiscounts.sort(DISCOUNT_COMPARATOR);
        //保存折扣项
        List<OrderDiscountItem> discountItems = CollUtil.newArrayList();
        for (OrderDiscount orderDiscount : orderDiscounts) {
            Set<ShopProductSkuKey> skuKeys = orderDiscount.getSkuKeys();
            if (CollUtil.isEmpty(skuKeys)) {
                continue;
            }
            //折扣id
            long discountId = MybatisPlusConfig.IDENTIFIER_GENERATOR.nextId(orderDiscount).longValue();
            orderDiscount.setId(discountId);
            //构建折扣生成器
            DiscountBuilder discountBuilder = OrderDiscountHelper.builder()
                    .discountTotalAmount(orderDiscount.getSourceAmount());
            for (ShopProductSkuKey skuKey : skuKeys) {
                List<ShopOrderItem> items = shopOrderItemMap.get(skuKey);
                if (items == null || items.isEmpty()) {
                    continue;
                }
                discountBuilder.addItems(items);
            }
            //当前折扣 按照支付价 均摊到sku
            Map<ShopProductSkuKey, ItemDiscount> discountMap = discountBuilder.toDiscount();
            discountMap.forEach(
                    (skuKey, itemDiscount) -> discountItems.add(
                            new OrderDiscountItem()
                                    .setDiscountId(discountId)
                                    .setShopId(skuKey.getShopId())
                                    .setItemId(itemDiscount.getItemId())
                                    .setDiscountAmount(itemDiscount.getDiscountAmount())
                    )
            );
        }
        //用于记录总订单费用
        OrderCost mainOrderCost = new OrderCost();
        for (ShopOrder shopOrder : shopOrders) {
            List<ShopOrderItem> items = shopOrder.getShopOrderItems();
            long totalAmount = 0, totalDiscount = 0, totalFreight = 0;
            for (ShopOrderItem item : items) {
                //当前 sku总价
                long totalPrice = item.totalPrice();
                totalAmount += totalPrice;
                //当前 sku优惠金额 = 总价 - 支付价
                long discountPrice = totalPrice - item.payPrice();
                totalDiscount += discountPrice;
                //总运费
                totalFreight += item.getFreightPrice();
            }
            //更新店铺订单总价、折扣价与运费
            shopOrder.setTotalAmount(totalAmount)
                    .setDiscountAmount(totalDiscount)
                    .setFreightAmount(totalFreight);
            mainOrderCost.incrTotalAmount(totalAmount)
                    .incrTotalDiscountAmount(totalDiscount)
                    .incrTotalFreightAmount(totalFreight);
        }
        return mainOrderCost
                .setShopOrderItems(shopOrderItemMap.values().stream().flatMap(Collection::stream).toList())
                .setOrderDiscountItems(discountItems);
    }


    /**
     * 完全相信 缓存中的数据,认为不会发生异常情况,所以不需要事务回滚
     *
     * @param orderKeys 订单key列表
     */
    @Log("保存订单到数据库")
    @Override
    @Redisson(name = OrderConstant.ORDER_EDIT_LOCK_KEY, batchParamName = "#orderKeys", key = "#item.orderNo")
    public void saveOrder2DbBatch(List<OrderDetailInfoBO> orderKeys) {
        if (CollUtil.isEmpty(orderKeys)) {
            return;
        }
        List<Order> orders = new ArrayList<>();
        List<OrderReceiver> orderReceivers = new ArrayList<>();
        List<OrderPayment> orderPayments = new ArrayList<>();
        List<OrderDiscount> orderDiscounts = new ArrayList<>();
        List<OrderDiscountItem> orderDiscountItems = new ArrayList<>();
        List<ShopOrder> shopOrders = new ArrayList<>();
        List<ShopOrderItem> shopOrderItems = new ArrayList<>();
        List<OrderRecord> orderDetails = new ArrayList<>();
        orderKeys.forEach(
                orderKey -> {
                    OrderRecord orderDetail = this.getOrderDetail(orderKey);
                    if (orderDetail == null) {
                        return;
                    }
                    orders.add(orderDetail.getOrder());
                    orderReceivers.add(orderDetail.getReceiver());
                    orderPayments.add(orderDetail.getPayment());
                    orderDiscounts.addAll(orderDetail.getDiscounts());
                    orderDiscountItems.addAll(orderDetail.getDiscountItems());
                    shopOrders.addAll(orderDetail.getShopOrders());
                    shopOrderItems.addAll(orderDetail.getShopOrderItems());
                    orderDetails.add(orderDetail);
                }
        );
        CompletableTask.allOf(
                globalExecutor,
                () -> {
                    orderSaveService.saveOrderBatch(orders);
                    orderSaveService.saveShopOrders(shopOrders);
                    orderSaveService.saveShopOrderItems(shopOrderItems);
                },
                () -> {
                    orderSaveService.saveOrderReceivers(orderReceivers);
                    orderSaveService.saveOrderPayments(orderPayments);
                    orderSaveService.saveOrderDiscounts(orderDiscounts);
                    orderSaveService.saveOrderDiscountItems(orderDiscountItems);
                }
        ).thenRun(
                () -> orderCreateNotify(orderKeys, orderDetails)
        );
    }

    private void orderCreateNotify(List<OrderDetailInfoBO> orderKeys, List<OrderRecord> orderDetails) {
        CompletableTask.allOf(
                globalExecutor,
                () -> OrderUtil.deletedOrderCacheBatch(orderKeys),
                () -> orderDetails.forEach(
                        orderDetail -> {
                            Order order = orderDetail.getOrder();
                            OrderPayment orderPayment = orderDetail.getPayment();
                            List<ShopOrder> shopOrders = orderDetail.getShopOrders();
                            List<ShopOrderItem> shopOrderItems = orderDetail.getShopOrderItems();
                            //发送订单已创建广播mq
                            OrderType type = order.getType();
                            Long buyerId = order.getBuyerId();
                            String orderNo = order.getNo();
                            Long activityId = order.getActivityId();
                            orderRabbitService.sendOrderCreated(
                                    new OrderCreatedDTO()
                                            .setSource(order.getSource())
                                            .setOrderType(type)
                                            .setBuyerId(buyerId)
                                            .setOrderNo(orderNo)
                                            .setPayAmount(orderPayment.getPayAmount())
                                            .setShopOrderItems(shopOrderItems)
                            );
                            //订单延迟自动取消mq
                            orderRabbitService.sendDelayOrderAutoClose(orderNo, order.getTimeout().getPayTimeoutMills());
                            //判断订单是否还需要支付 不需要直接发送订单支付成功mq
                            if (OrderStatus.UNPAID == order.getStatus()) {
                                return;
                            }
                            orderRabbitService.sendOrderPaid(
                                    new OrderPaidBroadcastDTO()
                                            .setActivityType(type)
                                            .setActivityId(activityId)
                                            .setOrderNo(orderNo)
                                            .setBuyerId(buyerId)
                                            .setBuyerNickname(order.getBuyerNickname())
                                            .setBuyerAvatar(order.getBuyerAvatar())
                                            .setPayerId(buyerId)
                                            .setPayAmount(orderPayment.getPayAmount())
                                            .setOrderPayment(orderPayment)
                                            .setNotifyTime(LocalDateTime.now())
                                            .setDistributionMode(order.getDistributionMode())
                                            .setExtra(order.getExtra())
                                            .setShopPayAmounts(
                                                    shopOrders.stream()
                                                            .map(shopOrder -> new OrderPaidBroadcastDTO.ShopPayAmountDTO()
                                                                    .setShopId(shopOrder.getShopId())
                                                                    .setAmount(shopOrder.payAmount())
                                                                    .setFreightAmount(shopOrder.getFreightAmount())
                                                                    .setDiscountAmount(shopOrder.getDiscountAmount())
                                                            )
                                                            .collect(Collectors.toList())
                                            )
                            );
                        }
                )
        );
    }


}
