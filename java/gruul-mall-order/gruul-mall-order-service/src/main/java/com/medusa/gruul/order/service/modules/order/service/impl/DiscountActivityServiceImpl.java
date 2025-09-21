package com.medusa.gruul.order.service.modules.order.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.global.model.constant.SecurityConst;
import com.medusa.gruul.global.model.helper.AmountCalculateHelper;
import com.medusa.gruul.order.api.addon.coupon.CouponResponse;
import com.medusa.gruul.order.api.addon.coupon.OrderCouponParam;
import com.medusa.gruul.order.api.addon.freight.FreightParam;
import com.medusa.gruul.order.api.addon.freight.PlatformFreightParam;
import com.medusa.gruul.order.api.addon.freight.ShopFreightParam;
import com.medusa.gruul.order.api.addon.freight.SkuInfoParam;
import com.medusa.gruul.order.api.addon.fullreduction.FullReductionResponse;
import com.medusa.gruul.order.api.addon.fullreduction.OrderFullReductionParam;
import com.medusa.gruul.order.api.entity.Order;
import com.medusa.gruul.order.api.entity.OrderDiscount;
import com.medusa.gruul.order.api.entity.ShopOrder;
import com.medusa.gruul.order.api.entity.ShopOrderItem;
import com.medusa.gruul.order.api.enums.DiscountSourceStatus;
import com.medusa.gruul.order.api.enums.DiscountSourceType;
import com.medusa.gruul.order.service.model.bo.MemberOrder;
import com.medusa.gruul.order.service.model.dto.ReceiverDTO;
import com.medusa.gruul.order.service.modules.order.addon.OrderAddonDistributionSupporter;
import com.medusa.gruul.order.service.modules.order.addon.OrderAddonSupporter;
import com.medusa.gruul.order.service.modules.order.service.DiscountActivityService;
import com.medusa.gruul.user.api.enums.RightsType;
import com.medusa.gruul.user.api.model.vo.CurrentMemberVO;
import com.medusa.gruul.user.api.model.vo.MemberAggregationInfoVO;
import com.medusa.gruul.user.api.model.vo.RelevancyRightsVO;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author 张治保
 * date 2022/11/9
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DiscountActivityServiceImpl implements DiscountActivityService {

    private final OrderAddonSupporter orderAddonSupporter;

    private final OrderAddonDistributionSupporter orderAddonDistributionSupporter;

    private static Map<Long, Map<Long, List<SkuInfoParam>>> getShopFreightMap(ShopOrder shopOrder) {
        List<ShopOrderItem> shopOrderItems = shopOrder.getShopOrderItems();
        Map<Long, Map<Long, List<SkuInfoParam>>> shopFreightMap = new HashMap<>(CommonPool.NUMBER_FIFTEEN);
        shopOrderItems.forEach(
                shopOrderItem -> {
                    Long freightTemplateId = shopOrderItem.getFreightTemplateId();
                    //获取运费计算目标的店铺 id 。 代销商品为供应商 id
                    Long shopId = shopOrderItem.getSellType() == SellType.CONSIGNMENT ? shopOrderItem.getSupplierId() : shopOrderItem.getShopId();
                    Map<Long, List<SkuInfoParam>> freightTemplateMap = shopFreightMap.computeIfAbsent(shopId, k -> new HashMap<>(CommonPool.NUMBER_FIFTEEN));
                    List<SkuInfoParam> skuInfoParams = freightTemplateMap.computeIfAbsent(freightTemplateId, k -> new ArrayList<>());
                    SkuInfoParam skuInfoParam = new SkuInfoParam();
                    skuInfoParam.setNum(shopOrderItem.getNum());
                    skuInfoParam.setPrice(BigDecimal.valueOf(shopOrderItem.getSalePrice()));
                    skuInfoParam.setSkuId(shopOrderItem.getSkuId());
                    skuInfoParam.setWeight(shopOrderItem.getWeight());
                    skuInfoParams.add(skuInfoParam);
                }
        );
        return shopFreightMap;
    }

    @Override
    public List<OrderDiscount> coupon(boolean budget, Map<Long, Long> shopCouponMap, Order order) {
        if (CollUtil.isEmpty(shopCouponMap)) {
            return List.of();
        }
        List<ShopOrder> shopOrders = order.getShopOrders();
        OrderCouponParam orderCouponParam = new OrderCouponParam()
                .setShopCouponMap(shopCouponMap)
                .setShopProductAmountMap(this.shopProductAmount(shopCouponMap, shopOrders));
        orderCouponParam.setOrderNo(order.getNo())
                .setBuyerId(order.getBuyerId())
                .setBudget(budget);
        CouponResponse couponResponse = orderAddonSupporter.coupon(orderCouponParam);
        if (couponResponse == null) {
            return List.of();
        }
        Map<Long, OrderDiscount> orderDiscountsMap = couponResponse.getOrderDiscounts();
        if (CollUtil.isEmpty(orderDiscountsMap)) {
            return List.of();
        }
        return workedDiscounts(order.getNo(), shopOrders, orderDiscountsMap);
    }

    /**
     * 会员抵扣
     *
     * @param memberOrder 会员与订单信息
     * @return 订单优惠项
     */
    @Override
    public OrderDiscount member(MemberOrder memberOrder) {
        Order order = memberOrder.getOrder();
        MemberAggregationInfoVO topMemberCardInfo = memberOrder.getMember();
        CurrentMemberVO currentMember = topMemberCardInfo.getCurrentMemberVO();
        Map<RightsType, List<RelevancyRightsVO>> relevancyRights;
        //会员信息为空 或 没有会员权益
        if (currentMember == null || (relevancyRights = currentMember.getRelevancyRights()) == null || relevancyRights.isEmpty()) {
            return null;
        }
        //是否有优先发货权
        order.setIsPriority(relevancyRights.containsKey(RightsType.PRIORITY_SHIPMENTS));
        //是否有商品折扣权益
        List<RelevancyRightsVO> goodsRights = relevancyRights.get(RightsType.GOODS_DISCOUNT);
        if (goodsRights == null) {
            return null;
        }
        OrderDiscount orderDiscount = new OrderDiscount();
        // 商品折扣
        Long extendValue = goodsRights.get(0).getExtendValue();
        if (extendValue == null || extendValue <= 0) {
            return null;
        }
        if (extendValue > MemberAggregationInfoVO.MAX_VIP_DISCOUNT_RATE) {
            extendValue = MemberAggregationInfoVO.MAX_VIP_DISCOUNT_RATE;
        }

        orderDiscount.setSourceDesc("会员商品折扣");
        orderDiscount.setOrderNo(order.getNo());
        orderDiscount.setSourceId(goodsRights.get(0).getMemberRightsId());
        orderDiscount.setSourceStatus(DiscountSourceStatus.OK);
        orderDiscount.setSourceType(DiscountSourceType.MEMBER_DEDUCTION);
        List<ShopOrder> shopOrders = order.getShopOrders();
        //计算折扣总额
        //商品总额
        long totalAmount = 0;
        for (ShopOrder shopOrder : shopOrders) {
            for (ShopOrderItem item : shopOrder.getShopOrderItems()) {
                long currentAmount = item.getDealPrice() * item.getNum();
                totalAmount += currentAmount;
                //当前折扣作用的商品 sku key
                orderDiscount.getSkuKeys().add(item.shopProductSkuKey());

            }
        }
        return orderDiscount.setTotalAmount(totalAmount)
                //计算总折扣
                //折扣比率 = （1000 - 折扣） / 1000
                //优惠金额 = 商品总额 * (折扣比率) 精确到分 向下取整
                .setSourceAmount(
                        extendValue == MemberAggregationInfoVO.MAX_VIP_DISCOUNT_RATE ?
                                totalAmount
                                : AmountCalculateHelper.getDiscountAmount(
                                totalAmount,
                                MemberAggregationInfoVO.MAX_VIP_DISCOUNT_RATE - extendValue,
                                MemberAggregationInfoVO.MAX_VIP_DISCOUNT_RATE
                        )
                );
    }


    @Override
    public List<OrderDiscount> fullReduction(boolean budget, Order order) {
        List<ShopOrder> shopOrders = order.getShopOrders();
        Map<Long, Map<Long, Long>> shopProductAmountMap = MapUtil.newHashMap();
        shopOrders.stream()
                .flatMap(shopOrder -> shopOrder.getShopOrderItems().stream())
                .forEach(
                        item -> {
                            Map<Long, Long> productAmountMap = shopProductAmountMap.computeIfAbsent(item.getShopId(), key -> MapUtil.newHashMap());
                            productAmountMap.compute(item.getProductId(), (productId, amount) -> item.totalPrice() + (amount == null ? 0L : amount));
                        }
                );
        OrderFullReductionParam param = new OrderFullReductionParam()
                .setShopProductAmountMap(shopProductAmountMap);
        String orderNo = order.getNo();
        param.setOrderNo(orderNo)
                .setBuyerId(order.getBuyerId())
                .setBudget(budget);
        FullReductionResponse fullReductionResponse = orderAddonSupporter.fullReduction(param);
        if (fullReductionResponse == null) {
            return List.of();
        }
        Map<Long, OrderDiscount> orderDiscountsMap = fullReductionResponse.getOrderDiscounts();
        if (CollUtil.isEmpty(orderDiscountsMap)) {
            return List.of();
        }
        return workedDiscounts(orderNo, shopOrders, orderDiscountsMap);
    }


    /**
     * 运费计算
     * todo 代销商品存在 bug：不同店铺的 同一个供应商的同一种物流模板 id 的商品，只会计算一次运费
     *
     * @param receiver         收货人信息
     * @param memberOrder      订单与会员详情
     * @param distributionMode 配送方式
     * @return 运费map key 为店铺id ｜ 店铺id+':'+templateId value 为运费
     */
    @Override
    public Map<String, BigDecimal> getFreightMap(ReceiverDTO receiver, MemberOrder memberOrder, DistributionMode distributionMode) {
        PlatformFreightParam freightParam = new PlatformFreightParam();
        freightParam.setAddress(receiver.getAddress());
        freightParam.setArea(receiver.getArea());
        freightParam.setShopFreights(new ArrayList<>());
        freightParam.setLocation(receiver.getLocation());
        for (ShopOrder shopOrder : memberOrder.getOrder().getShopOrders()) {
            Map<Long, Map<Long, List<SkuInfoParam>>> shopFreightMap = getShopFreightMap(shopOrder);
            shopFreightMap.forEach(
                    (shopId, freightTemplateMap) -> {
                        ShopFreightParam shopFreightParam = new ShopFreightParam();
                        shopFreightParam.setShopId(shopId);
                        shopFreightParam.setFreights(
                                freightTemplateMap.entrySet()
                                        .stream()
                                        .map(
                                                entry -> {
                                                    FreightParam templateParam = new FreightParam();
                                                    templateParam.setTemplateId(entry.getKey());
                                                    templateParam.setSkuInfos(entry.getValue());
                                                    return templateParam;
                                                }
                                        ).collect(Collectors.toList())
                        );
                        freightParam.getShopFreights().add(shopFreightParam);
                    }
            );
        }
        freightParam.setFreeRight(Boolean.FALSE);
        Map<String, BigDecimal> apply = distributionMode.getFunction()
                .apply(innerMode -> orderAddonDistributionSupporter.distributionCost(innerMode, freightParam));
        //计算配送价格
        return MapUtil.emptyIfNull(apply);
    }

    /**
     * 计算优惠项 作用的商品 并返回折扣项列表
     *
     * @param orderNo           订单号
     * @param shopOrders        店铺订单信息
     * @param orderDiscountsMap 优惠券优惠信息 map key 为店铺id value 为优惠信息
     */
    private List<OrderDiscount> workedDiscounts(String orderNo, List<ShopOrder> shopOrders, Map<Long, OrderDiscount> orderDiscountsMap) {
        List<OrderDiscount> orderDiscounts = CollUtil.newArrayList();
        // 平台优惠 作用于所有商品
        OrderDiscount platformDiscount = orderDiscountsMap.get(SecurityConst.NO_SHOP_ID_CLIENT_DEFAULT_SHOP_ID);
        boolean emptyPlatformDiscount = platformDiscount == null;
        for (ShopOrder shopOrder : shopOrders) {
            Long shopId = shopOrder.getShopId();
            OrderDiscount shopDiscount = orderDiscountsMap.get(shopId);
            boolean emptyShopDiscount = shopDiscount == null;
            //如果平台优惠为空 并且当前店铺的折扣为空 直接跳过
            if (emptyPlatformDiscount && emptyShopDiscount) {
                continue;
            }
            List<ShopOrderItem> shopOrderItems = shopOrder.getShopOrderItems();
            for (ShopOrderItem item : shopOrderItems) {
                ShopProductSkuKey skuKey = item.shopProductSkuKey();
                //如果平台优惠不为空 则把当前商品加入到平台优惠的商品项中
                if (!emptyPlatformDiscount) {
                    platformDiscount.getSkuKeys().add(skuKey);
                }
                //如果店铺优惠不为空 并且 当前优惠作用于所有商品或当前商品在店铺优惠的商品列表中 则把当前商品加入到店铺优惠的商品项中
                if (!emptyShopDiscount && (shopDiscount.getProductIds() == null || shopDiscount.getProductIds().contains(item.getProductId()))) {
                    shopDiscount.getSkuKeys().add(skuKey);
                }
            }
            if (emptyShopDiscount) {
                continue;
            }
            shopDiscount.setOrderNo(orderNo)
                    .setDiscountItems(null)
                    .setProductIds(null);
            orderDiscounts.add(shopDiscount);
        }
        if (emptyPlatformDiscount) {
            return orderDiscounts;
        }
        platformDiscount.setOrderNo(orderNo)
                .setDiscountItems(null)
                .setProductIds(null);
        orderDiscounts.add(platformDiscount);
        return orderDiscounts;
    }


    private Map<Long, Map<Long, Long>> shopProductAmount(Map<Long, Long> shopCoupon, List<ShopOrder> shopOrders) {
        return Option.of(shopCoupon.get(SecurityConst.NO_SHOP_ID_CLIENT_DEFAULT_SHOP_ID))
                .map(platformCouponId -> shopOrders.stream().collect(shopOrderMapCollector()))
                .getOrElse(() -> shopOrders.stream()
                        .filter(shopOrder -> shopCoupon.containsKey(shopOrder.getShopId()))
                        .collect(shopOrderMapCollector())
                );
    }

    private Collector<ShopOrder, ?, Map<Long, Map<Long, Long>>> shopOrderMapCollector() {
        return Collectors.toMap(ShopOrder::getShopId, shopOrder -> shopOrder.getShopOrderItems().stream().collect(Collectors.groupingBy(ShopOrderItem::getProductId)).entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> CollUtil.emptyIfNull(entry.getValue()).stream().map(item -> item.getSalePrice() * item.getNum()).reduce(0L, Long::sum))));
    }


}
