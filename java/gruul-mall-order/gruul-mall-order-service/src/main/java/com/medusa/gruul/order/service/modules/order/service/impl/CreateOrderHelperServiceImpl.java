package com.medusa.gruul.order.service.modules.order.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.json.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.medusa.gruul.common.fastjson2.FastJson2;
import com.medusa.gruul.common.model.base.StackableDiscount;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.helper.CompletableTask;
import com.medusa.gruul.order.api.entity.Order;
import com.medusa.gruul.order.api.entity.OrderDiscount;
import com.medusa.gruul.order.api.entity.ShopOrderItem;
import com.medusa.gruul.order.service.model.bo.MemberOrder;
import com.medusa.gruul.order.service.model.bo.OrderDetailInfoBO;
import com.medusa.gruul.order.service.model.dto.OrderShopsDTO;
import com.medusa.gruul.order.service.model.dto.ReceiverDTO;
import com.medusa.gruul.order.service.model.enums.DiscountType;
import com.medusa.gruul.order.service.modules.order.service.CreateOrderHelperService;
import com.medusa.gruul.order.service.modules.order.service.DiscountActivityService;
import com.medusa.gruul.order.service.mq.service.OrderRabbitService;
import com.medusa.gruul.order.service.properties.OrderConfigurationProperties;
import com.medusa.gruul.order.service.util.OrderUtil;
import com.medusa.gruul.storage.api.bo.OrderStockBO;
import com.medusa.gruul.storage.api.rpc.StorageOrderRpcService;
import com.medusa.gruul.user.api.enums.RightsType;
import com.medusa.gruul.user.api.model.vo.CurrentMemberVO;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

/**
 * @author 张治保
 * date 2022/6/10
 */
@Service
@RequiredArgsConstructor
public class CreateOrderHelperServiceImpl implements CreateOrderHelperService {

    private final Executor globalExecutor;
    private final OrderRabbitService orderRabbitService;
    private final StorageOrderRpcService storageOrderRpcService;
    private final DiscountActivityService discountActivityService;
    private final OrderConfigurationProperties orderConfigurationProperties;
    private CreateOrderHelperService createOrderHelperService;

    @Autowired
    public void setCreateOrderHelperService(CreateOrderHelperService createOrderHelperService) {
        this.createOrderHelperService = createOrderHelperService;
    }

    @Override
    public void orderBudget(MemberOrder memberOrder, OrderShopsDTO orderShops) {
        //优惠项计算
        createOrderHelperService.generateOrderDiscount(true, orderShops.getDiscounts(), memberOrder);
        //运费计算
        DistributionMode mode = orderShops.getDistributionMode();
        //非虚拟配送和店铺自提计算运费
        if (mode.isNoFreight()) {
            return;
        }
        //预计算时如果收货人为空 则不计算运费
        if (orderShops.emptyReceiver()) {
            return;
        }
        createOrderHelperService.calculateFreight(memberOrder, orderShops.getReceiver(), mode);
    }

    /**
     * 订单必要处理流程
     *
     * @param memberOrder 订单与会员详情
     * @param orderShops  请求参数信息
     */
    @Override
    public void orderNecessaryProcess(MemberOrder memberOrder, OrderShopsDTO orderShops) {
        Order order = memberOrder.getOrder();
        Long buyerId = order.getBuyerId();
        Authentication authentication = ISecurity.getAuthentication(false);
        CompletableTask.getOrThrowException(
                CompletableTask.allOf(
                                globalExecutor,
                                //生成优惠项
                                () -> ISecurity.withAuthentication(
                                        authentication,
                                        () -> createOrderHelperService.generateOrderDiscount(false, orderShops.getDiscounts(), memberOrder)
                                ),
                                //计算运费
                                () -> {
                                    DistributionMode mode = orderShops.getDistributionMode();
                                    //非虚拟配送和店铺自提计算运费
                                    if (mode.isNoFreight()) {
                                        return;
                                    }
                                    ISecurity.withAuthentication(
                                            authentication,
                                            () -> createOrderHelperService.calculateFreight(memberOrder, orderShops.getReceiver(), mode)
                                    );
                                },
                                //减库存
                                () -> storageOrderRpcService.reduceSkuStock(orderSkuStock(buyerId, order))
                        )
                        //计算生成支付数据
                        .thenRunAsync(
                                () -> {
                                    //缓存订单信息
                                    OrderUtil.cacheOrder(buyerId, order, Duration.ofSeconds(orderConfigurationProperties.getCacheExpire().getCreateOrderCache()));
                                    //发送入库mq
                                    orderRabbitService.sendOrderFlush(
                                            new OrderDetailInfoBO().setActivityType(order.getType()).setActivityId(order.getActivityId()).setBuyerId(order.getBuyerId()).setOrderNo(order.getNo())
                                    );
                                },
                                globalExecutor
                        )
        );
    }

    /**
     * 订单转成 订单库存BO
     *
     * @param buyerId 买家id
     * @param order   订单
     * @return 订单库存BO
     */
    private OrderStockBO orderSkuStock(Long buyerId, Order order) {
        return new OrderStockBO()
                .setUserId(buyerId)
                .setNo(order.getNo())
                .setSkuKeyStSvMap(
                        OrderUtil.toSkuKeyStSvMap(
                                order.getType(),
                                order.getActivityId(),
                                order.getShopOrders()
                                        .stream()
                                        .flatMap(shopOrder -> shopOrder.getShopOrderItems().stream())
                                        .toList()
                        )
                );
    }

    @Override
    public void calculateFreight(MemberOrder memberOrder, ReceiverDTO receiver, DistributionMode distributionMode) {
        //同城配送运费计算
        Order order = memberOrder.getOrder();
        CurrentMemberVO member;
        //是否有免运费权益
        boolean freeRight = (
                memberOrder.getMember() != null
                        && (member = memberOrder.getMember().getCurrentMemberVO()) != null
                        && MapUtil.emptyIfNull(member.getRelevancyRights()).containsKey(RightsType.LOGISTICS_DISCOUNT)
        );
        HashMap<String, BigDecimal> freightMap = new HashMap<>(discountActivityService.getFreightMap(receiver, memberOrder, distributionMode));
        order.getShopOrders().stream()
                .flatMap(
                        shopOrder -> shopOrder.getShopOrderItems().stream()
                ).forEach(
                        shopOrderItem -> {
                            String key = getShopKey(shopOrderItem, distributionMode);
                            shopOrderItem.setFreightPrice(
                                    Option.of(freightMap.get(key))
                                            .map(
                                                    freight -> {
                                                        freightMap.remove(key);
                                                        return freight.signum() < CommonPool.NUMBER_ZERO ? CommonPool.NUMBER_ZERO : freight.multiply(BigDecimal.valueOf(CommonPool.UNIT_CONVERSION_TEN_THOUSAND)).longValue();
                                                    }
                                            ).getOrElse(0L)
                            );
                            Long freightPrice;
                            if (freeRight && (freightPrice = shopOrderItem.getFreightPrice()) > 0) {
                                shopOrderItem.getExtra().setDiscountFreight(freightPrice);
                                shopOrderItem.setFreightPrice(0L);
                            }

                        }
                );
    }

    private String getShopKey(ShopOrderItem shopOrderItem, DistributionMode distributionMode) {
        if (DistributionMode.INTRA_CITY_DISTRIBUTION.getValue().equals(distributionMode.getValue())) {
            return String.valueOf(shopOrderItem.getShopId());
        }
        Long shopId = shopOrderItem.getShopId();
        if (shopOrderItem.getSellType() == SellType.CONSIGNMENT) {
            shopId = shopOrderItem.getSupplierId();
        }
        return shopId + StrPool.COLON + shopOrderItem.getFreightTemplateId();
    }

    /**
     * 生成折扣 优惠信息
     *
     * @param budget      是否是预算
     * @param discounts   优惠参数信息
     * @param memberOrder 订单与会员详情
     */
    @Override
    public void generateOrderDiscount(boolean budget, Map<DiscountType, JSONObject> discounts, MemberOrder memberOrder) {
        Collection<OrderDiscount> orderDiscounts = new ConcurrentLinkedQueue<>();

        StackableDiscount stackable = memberOrder.getOrder().getActivityResp().getStackable();
        //组装优惠任务
        List<Runnable> discountTasks = new ArrayList<>(CommonPool.NUMBER_EIGHT);
        //优惠券
        JSONObject shopCoupons;
        if (stackable.isCoupon() && MapUtil.isNotEmpty(shopCoupons = discounts.get(DiscountType.COUPON))) {
            discountTasks.add(
                    () -> orderDiscounts.addAll(
                            discountActivityService.coupon(
                                    budget,
                                    FastJson2.convert(shopCoupons, new TypeReference<>() {
                                    }),
                                    memberOrder.getOrder()
                            )
                    )
            );
        }
        //会员折扣
        if (stackable.isVip()) {
            discountTasks.add(() -> {
                if (memberOrder.getMember() == null) {
                    return;
                }
                OrderDiscount memberDiscount = discountActivityService.member(memberOrder);
                if (memberDiscount == null) {
                    return;
                }
                orderDiscounts.add(memberDiscount);
            });
        }
        //满减
        if (stackable.isFull()) {
            discountTasks.add(
                    () -> orderDiscounts.addAll(
                            discountActivityService.fullReduction(
                                    budget,
                                    memberOrder.getOrder()
                            )
                    )
            );
        }
        CompletableTask.getOrThrowException(CompletableTask.allOf(globalExecutor, discountTasks.toArray(new Runnable[0])));
        memberOrder.getOrder().setOrderDiscounts(new ArrayList<>(orderDiscounts));
    }


}
