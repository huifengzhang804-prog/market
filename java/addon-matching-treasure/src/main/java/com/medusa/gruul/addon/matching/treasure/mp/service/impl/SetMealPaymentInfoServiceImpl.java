package com.medusa.gruul.addon.matching.treasure.mp.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.medusa.gruul.addon.matching.treasure.constant.MatchingTreasureConstant;
import com.medusa.gruul.addon.matching.treasure.model.dto.SetMealProductStats;
import com.medusa.gruul.addon.matching.treasure.mp.entity.SetMeal;
import com.medusa.gruul.addon.matching.treasure.mp.entity.SetMealPaymentInfo;
import com.medusa.gruul.addon.matching.treasure.mp.mapper.SetMealPaymentInfoMapper;
import com.medusa.gruul.addon.matching.treasure.mp.service.ISetMealPaymentInfoService;
import com.medusa.gruul.addon.matching.treasure.mp.service.ISetMealService;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.order.api.model.OrderPaidBroadcastDTO;
import com.medusa.gruul.order.api.pojo.OrderInfo;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 套餐活动支付信息 服务实现类
 *
 * @author WuDi
 * @since 2023-03-21
 */
@Service
@RequiredArgsConstructor
public class SetMealPaymentInfoServiceImpl extends ServiceImpl<SetMealPaymentInfoMapper, SetMealPaymentInfo> implements ISetMealPaymentInfoService {

    private final ISetMealService setMealService;


    /**
     * 套餐活动支付信息
     *
     * @param orderPaidBroadcast 订单支付信息
     */
    @Override
    public void matchingTreasurePaymentInfo(OrderPaidBroadcastDTO orderPaidBroadcast) {
        Long activityId = orderPaidBroadcast.getActivityId();
        OrderPaidBroadcastDTO.ShopPayAmountDTO shopPayAmount = orderPaidBroadcast.getShopPayAmounts().get(CommonPool.NUMBER_ZERO);
        Long shopId = shopPayAmount.getShopId();
        boolean exists = setMealService.lambdaQuery()
                .eq(SetMeal::getId, activityId)
                .eq(SetMeal::getShopId, shopId)
                .exists();
        if (!exists) {
            return;
        }
        boolean save = this.save(
                new SetMealPaymentInfo()
                        .setActivityId(activityId)
                        .setOrderNo(orderPaidBroadcast.getOrderNo())
                        .setAmountReceivable(orderPaidBroadcast.getPayAmount())
                        .setShopId(shopId)
                        .setUserId(orderPaidBroadcast.getBuyerId()));
        if (!save) {
            throw new RuntimeException("套餐活动支付信息保存失败");
        }

    }

    /**
     * 套餐活动支付退款信息
     *
     * @param orderInfo 订单信息
     */
    @Override
    public void matchingTreasureRefundInfo(OrderInfo orderInfo) {
        Long activityId = orderInfo.getActivityId();
        String orderNo = orderInfo.getOrderNo();
        Long shopId = orderInfo.getShopId();
        boolean exists = setMealService.lambdaQuery()
                .eq(SetMeal::getId, activityId)
                .eq(SetMeal::getShopId, shopId)
                .exists();
        if (!exists) {
            return;
        }
        SetMealPaymentInfo setMealPaymentInfo = this.lambdaQuery()
                .eq(SetMealPaymentInfo::getActivityId, activityId)
                .eq(SetMealPaymentInfo::getShopId,shopId)
                .eq(SetMealPaymentInfo::getOrderNo, orderNo)
                .one();
        if (setMealPaymentInfo == null) {
            return;
        }
        // 退款金额
        Long refundAmount = orderInfo.getAfs().getRefundAmount();
        if (Objects.equals(refundAmount, setMealPaymentInfo.getAmountReceivable())) {
            this.lambdaUpdate()
                    .eq(SetMealPaymentInfo::getActivityId, activityId)
                    .eq(SetMealPaymentInfo::getOrderNo, orderNo)
                    .remove();
            return;
        }
        this.lambdaUpdate()
                .setSql(StrUtil.format(MatchingTreasureConstant.AMOUNT_RECEIVABLE_SQL_TEMPLATE, refundAmount))
                .eq(SetMealPaymentInfo::getActivityId, activityId)
                .eq(SetMealPaymentInfo::getOrderNo, orderNo)
                .update();

    }

    @Override
    public Map<Long, Integer> querySetMealOrderCount(Set<Long> setMealIds) {
       List<SetMealProductStats> list= baseMapper.querySetMealOrderCount(setMealIds);
      if (CollectionUtil.isEmpty(list)) {
        return Maps.newHashMap();
      }

        return list.stream().collect(Collectors.toMap(x->x.getSetMealId(),x->x.getOrderCount()));
    }
}
