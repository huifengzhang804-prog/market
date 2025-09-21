package com.medusa.gruul.order.service.modules.order.service;

import cn.hutool.json.JSONObject;
import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.order.service.model.bo.MemberOrder;
import com.medusa.gruul.order.service.model.dto.OrderShopsDTO;
import com.medusa.gruul.order.service.model.dto.ReceiverDTO;
import com.medusa.gruul.order.service.model.enums.DiscountType;

import java.util.Map;

/**
 * @author 张治保
 * date 2022/6/10
 */
public interface CreateOrderHelperService {

    /**
     * 订单预算
     *
     * @param memberOrder 订单与会员详情
     * @param orderShops  请求参数信息
     */
    void orderBudget(MemberOrder memberOrder, OrderShopsDTO orderShops);

    /**
     * 订单必要处理流程
     *
     * @param memberOrder 订单与会员详情
     * @param orderShops  请求参数信息
     */
    void orderNecessaryProcess(MemberOrder memberOrder, OrderShopsDTO orderShops);

    /**
     * 计算运费
     *
     * @param receiver         收货人详情
     * @param memberOrder      订单与会员详情
     * @param distributionMode 配送方式
     */
    void calculateFreight(MemberOrder memberOrder, ReceiverDTO receiver, DistributionMode distributionMode);

    /**
     * 生成订单优惠项
     *
     * @param budget      是否是预算
     * @param discounts   优惠参数信息
     * @param memberOrder 订单与会员详情
     */
    void generateOrderDiscount(boolean budget, Map<DiscountType, JSONObject> discounts, MemberOrder memberOrder);
    
}
