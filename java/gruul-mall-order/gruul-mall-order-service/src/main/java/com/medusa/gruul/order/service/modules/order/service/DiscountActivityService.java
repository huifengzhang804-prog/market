package com.medusa.gruul.order.service.modules.order.service;

import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.order.api.entity.Order;
import com.medusa.gruul.order.api.entity.OrderDiscount;
import com.medusa.gruul.order.service.model.bo.MemberOrder;
import com.medusa.gruul.order.service.model.dto.ReceiverDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 优惠活动 服务
 *
 * @author 张治保
 * date 2022/11/9
 */

public interface DiscountActivityService {

    /**
     * 优惠券活动
     *
     * @param budget        是否是预算
     * @param shopCouponMap 店铺优惠券 对应关系  平台优惠券 key为0
     * @param order         订单详情
     * @return 生成的订单优惠列表
     */
    List<OrderDiscount> coupon(boolean budget, Map<Long, Long> shopCouponMap, Order order);


    /**
     * 会员抵扣
     *
     * @param memberOrder 订单与会员详情
     * @return 订单优惠享
     */
    OrderDiscount member(MemberOrder memberOrder);


    /**
     * 请求 物流接口 计算运费
     *
     * @param receiver         收货人信息
     * @param memberOrder      订单与会员详情
     * @param distributionMode 配送方式
     * @return key -> {shopId}:{freightTemplateId} value -> 运费
     */
    Map<String, BigDecimal> getFreightMap(ReceiverDTO receiver, MemberOrder memberOrder, DistributionMode distributionMode);

    /**
     * 满减活动
     *
     * @param budget 是否是预算
     * @param order  订单详情
     * @return 生成的订单优惠列表
     */
    List<OrderDiscount> fullReduction(boolean budget, Order order);
}
