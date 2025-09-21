package com.medusa.gruul.addon.rebate.addon.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson2.JSONObject;
import com.medusa.gruul.addon.rebate.addon.AddonRebateProvider;
import com.medusa.gruul.addon.rebate.model.RebateConstant;
import com.medusa.gruul.addon.rebate.model.bo.RebateItemPay;
import com.medusa.gruul.addon.rebate.model.bo.UserRebatePercent;
import com.medusa.gruul.addon.rebate.model.enums.RebateType;
import com.medusa.gruul.addon.rebate.mp.entity.RebatePayment;
import com.medusa.gruul.addon.rebate.mp.entity.RebateTransactions;
import com.medusa.gruul.addon.rebate.mp.service.IRebatePaymentService;
import com.medusa.gruul.addon.rebate.mp.service.IRebateTransactionsService;
import com.medusa.gruul.addon.rebate.service.RebateConfHandleService;
import com.medusa.gruul.addon.rebate.service.RebateOrderHandlerService;
import com.medusa.gruul.addon.rebate.service.RebateTransactionsHandleService;
import com.medusa.gruul.common.addon.provider.AddonProvider;
import com.medusa.gruul.common.addon.provider.AddonProviders;
import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.mp.config.MybatisPlusConfig;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.global.model.constant.Services;
import com.medusa.gruul.global.model.helper.AmountCalculateHelper;
import com.medusa.gruul.goods.api.constant.GoodsConstant;
import com.medusa.gruul.goods.api.model.dto.EstimateRebateDTO;
import com.medusa.gruul.goods.api.model.param.EarningParam;
import com.medusa.gruul.order.api.addon.OrderAddonConstant;
import com.medusa.gruul.order.api.addon.rebate.RebateItemPrice;
import com.medusa.gruul.order.api.addon.rebate.RebatePayParam;
import com.medusa.gruul.order.api.addon.rebate.RebatePayResponse;
import com.medusa.gruul.order.api.entity.OrderDiscount;
import com.medusa.gruul.order.api.entity.OrderDiscountItem;
import com.medusa.gruul.order.api.entity.ShopOrderItem;
import com.medusa.gruul.order.api.enums.DiscountSourceStatus;
import com.medusa.gruul.order.api.enums.DiscountSourceType;
import com.medusa.gruul.order.api.helper.ItemDiscount;
import com.medusa.gruul.order.api.helper.OrderDiscountHelper;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 返利插件提供器实现类
 *
 * @author jinbu
 */
@Service
@DubboService
@AddonProviders
@RequiredArgsConstructor
public class AddonRebateProviderImpl implements AddonRebateProvider {

    private final IRebatePaymentService rebatePaymentService;
    private final RebateConfHandleService rebateConfHandleService;
    private final IRebateTransactionsService rebateTransactionsService;
    private final RebateTransactionsHandleService rebateTransactionsHandleService;
    private final RebateOrderHandlerService rebateOrderHandlerService;



    @Override
    @Transactional(rollbackFor = Exception.class)
    @Redisson(value = RebateConstant.REBATE_BALANCE_LOCK_KEY, key = "#payParam.userId")
    @AddonProvider(service = Services.GRUUL_MALL_ORDER, supporterId = OrderAddonConstant.ORDER_DISCOUNT_SUPPORT_ID, methodName = "rebateDiscount")
    public RebatePayResponse rebateDiscount(RebatePayParam payParam) {
        List<ShopOrderItem> orderItems = payParam.getOrderItems();
        if (CollUtil.isEmpty(orderItems)) {
            return null;
        }
        String orderNo = payParam.getOrderNo();
        Long userId = payParam.getUserId();
        //已存在直接不处理
        if (rebatePaymentService.lambdaQuery()
                .eq(RebatePayment::getUserId, userId)
                .eq(RebatePayment::getOrderNo, orderNo)
                .exists()) {
            return null;
        }
        UserRebatePercent userRebatePercent = rebateConfHandleService.getUserRebatePercent(userId);
        //返利支付比例 如果支付比例不大于0则不处理
        if (userRebatePercent.noPay()) {
            return null;
        }
        BigDecimal payRate = userRebatePercent.payRate();
        //获取用户当前返利余额
        RebateTransactions userRebateBalance = rebateTransactionsService.lambdaQuery()
                .select(RebateTransactions::getRebateBalance)
                .eq(RebateTransactions::getUserId, userId)
                .one();
        Long rebateBalance;
        if (userRebateBalance == null || (rebateBalance = userRebateBalance.getRebateBalance()) <= 0) {
            return null;
        }
        Tuple2<Long, Long> totalAmountAndRebateAmount = rebateAmount(orderItems, rebateBalance, payRate);
        Long totalAmount = totalAmountAndRebateAmount._1(), rebatePayAmount = totalAmountAndRebateAmount._2();
        if (rebatePayAmount <= 0) {
            return null;
        }
        RebatePayment payment = new RebatePayment();
        payment.setOrderNo(orderNo)
                .setUserId(userId)
                .setPayAmount(rebatePayAmount)
                .setRebateItemPays(new HashMap<>(orderItems.size()))
                .setId(MybatisPlusConfig.IDENTIFIER_GENERATOR.nextId(payment).longValue());
        OrderDiscount orderDiscount = new OrderDiscount()
                .setOrderNo(orderNo)
                .setSourceType(DiscountSourceType.CONSUMPTION_REBATE)
                .setSourceStatus(DiscountSourceStatus.OK)
                .setSourceId(payment.getId())
                .setSourceAmount(rebatePayAmount)
                .setTotalAmount(totalAmount)
                .setSourceDesc("返利支付")
                .setDiscountItems(new ArrayList<>());
        orderDiscount.setId(MybatisPlusConfig.IDENTIFIER_GENERATOR.nextId(orderDiscount).longValue());
        //折扣计算
        Map<ShopProductSkuKey, ItemDiscount> itemDiscountMap = OrderDiscountHelper.builder()
                .discountTotalAmount(rebatePayAmount)
                .addItems(orderItems)
                .toDiscount();
        //如果为空直接返回
        if (CollUtil.isEmpty(itemDiscountMap)) {
            return null;
        }
        Map<ShopProductSkuKey, RebateItemPrice> itemPriceMap = new HashMap<>(itemDiscountMap.size());
        itemDiscountMap.forEach(
                (key, value) -> {
                    itemPriceMap.put(
                            key,
                            new RebateItemPrice().setDealPrice(value.getDealPrice())
                                    .setFixPrice(value.getFixPrice())
                                    .setRebateDiscount(value.getDiscountAmount())
                    );
                    orderDiscount.getDiscountItems()
                            .add(
                                    new OrderDiscountItem()
                                            .setDiscountId(orderDiscount.getId())
                                            .setShopId(key.getShopId())
                                            .setItemId(value.getItemId())
                                            .setDiscountAmount(value.getDiscountAmount())
                            );
                    payment.getRebateItemPays()
                            .put(key, new RebateItemPay()
                                    .setNum(value.getNum())
                                    .setPaidRebate(value.getDiscountAmount())
                            );
                }
        );
        rebatePaymentService.save(payment);
        rebateTransactionsHandleService.updateRebateBalance(
                new RebateTransactions()
                        .setUserId(userId)
                        .setRebateBalance(-rebatePayAmount)
                , RebateType.EXPENDITURE, orderNo
        );
        return new RebatePayResponse().setRebateDiscount(orderDiscount).setItemKeyDealPriceMap(itemPriceMap);
    }

    /**
     * 商品详情查询返利金额
     *
     * @param estimate 商品金额与服务费比例
     * @return 商品返利金额
     */
    @Override
    @AddonProvider(service = Services.GRUUL_MALL_GOODS, supporterId = "goodsAddonSupporter", methodName = "getProductRebateAmount")
    public Long getProductRebateAmount(EstimateRebateDTO estimate) {
        return rebateTransactionsHandleService.getProductRebateAmount(null,estimate);
    }

    /**
     * 返利预计赚
     *
     * @param param 预计赚请求参数
     * @return 预计赚金额
     */
    @Override
    @AddonProvider(filter = FILTER, service = Services.GRUUL_MALL_GOODS, supporterId = GoodsConstant.ADDON_SUPPORT_ID, methodName = "earning")
    public Long earning(EarningParam param) {
        JSONObject extra = param.getExtra();
        if (extra == null) {
            return (long) CommonPool.NUMBER_ZERO;
        }
        EstimateRebateDTO estimate = extra.to(EstimateRebateDTO.class);
        estimate.setAmount(param.getCurAmount());
        return rebateTransactionsHandleService.getProductRebateAmount(param.getUserId(), estimate);
    }


    /**
     * 更新消费返利订单的信息
     * @param shopOrderItems
     * @return 是否更新成功
     */
    @Override
    @AddonProvider(service = Services.GRUUL_MALL_ORDER, supporterId = OrderAddonConstant.ORDER_DISCOUNT_SUPPORT_ID,
        methodName = "updateRebateOrderInfo")
    public Boolean updateRebateOrderInfo(List<ShopOrderItem> shopOrderItems,String orderNo) {
        if (CollUtil.isEmpty(shopOrderItems)) {
            return Boolean.TRUE;
        }
        return rebateOrderHandlerService.updateRebateOrderInfo(shopOrderItems,orderNo);


    }

    /**
     * 计算当前订单能使用的返利金额
     *
     * @return 返利金额
     */
    private Tuple2<Long, Long> rebateAmount(List<ShopOrderItem> orderItems, Long rebateBalance, BigDecimal payRate) {
        //计算当前支付总金额，
        long totalAmount = orderItems.stream()
                .reduce(0L, (sum, item) -> sum + item.payPrice(), Long::sum);
        //计算返利支付的最大金额
        Long rebatePayAmountMax = AmountCalculateHelper.getDiscountAmount(totalAmount, payRate);
        //最终使用的返利余额
        return Tuple.of(totalAmount, Math.min(totalAmount, Math.min(rebatePayAmountMax, rebateBalance)));
    }
}
