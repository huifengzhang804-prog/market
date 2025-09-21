package com.medusa.gruul.addon.distribute.addon.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import com.medusa.gruul.addon.distribute.addon.DistributeAddonProvider;
import com.medusa.gruul.addon.distribute.model.DistributorBonus;
import com.medusa.gruul.addon.distribute.model.enums.DistributeOrderStatus;
import com.medusa.gruul.addon.distribute.model.enums.DistributorIdentity;
import com.medusa.gruul.addon.distribute.mp.entity.DistributorOrder;
import com.medusa.gruul.addon.distribute.mp.service.IDistributorOrderService;
import com.medusa.gruul.addon.distribute.service.DistributeBonusService;
import com.medusa.gruul.addon.distribute.service.DistributeProductHandleService;
import com.medusa.gruul.addon.distribute.service.DistributorHandleService;
import com.medusa.gruul.addon.distribute.util.DistributeUtil;
import com.medusa.gruul.common.addon.provider.AddonProvider;
import com.medusa.gruul.common.addon.provider.AddonProviders;
import com.medusa.gruul.common.model.enums.TransactionType;
import com.medusa.gruul.common.model.pay.Transaction;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.global.model.constant.Services;
import com.medusa.gruul.goods.api.constant.GoodsConstant;
import com.medusa.gruul.goods.api.model.param.EarningParam;
import com.medusa.gruul.order.api.entity.ShopOrderItem;
import com.medusa.gruul.order.api.model.OrderCompletedDTO;
import com.medusa.gruul.overview.api.model.OverViewStatementResp;
import com.medusa.gruul.overview.api.model.OverviewConstant;
import com.medusa.gruul.service.uaa.api.constant.UaaConstant;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 张治保 date 2023/6/10
 */
@Service
@DubboService
@AddonProviders
@RequiredArgsConstructor
public class DistributeAddonProviderImpl implements DistributeAddonProvider {

    private final DistributeBonusService distributeBonusService;
    private final IDistributorOrderService distributorOrderService;
    private final DistributorHandleService distributorHandleService;
    private final DistributeProductHandleService distributeProductHandleService;


    @Override
    @AddonProvider(service = Services.GRUUL_MALL_OVERVIEW, supporterId = OverviewConstant.OVERVIEW_STATEMENT_SUPPORT_ID, methodName = "createStatements")
    public OverViewStatementResp createStatements(OrderCompletedDTO orderCompleted) {
        Long buyerId = orderCompleted.getUserId();
        String orderNo = orderCompleted.getOrderNo();
        List<ShopOrderItem> shopOrderItems = orderCompleted.getShopOrderItems();
        if (CollUtil.isEmpty(shopOrderItems)) {
            return new OverViewStatementResp().setType(TransactionType.DISTRIBUTE).setUserIdAmountMap(Map.of());
        }
        List<DistributorOrder> orders = distributorOrderService.lambdaQuery()
                .eq(DistributorOrder::getUserId, buyerId)
                .eq(DistributorOrder::getOrderNo, orderNo)
                .eq(DistributorOrder::getShopId, orderCompleted.getShopId())
                .eq(DistributorOrder::getOrderStatus, DistributeOrderStatus.PAID)
                .list();
        if (CollUtil.isEmpty(orders)) {
            return new OverViewStatementResp().setType(TransactionType.DISTRIBUTE).setUserIdAmountMap(Map.of());
        }
        //当前订单完成 关联的分销订单
        List<DistributorOrder> involvedOrders = groupOrdersToComplete(shopOrderItems, orders);
        Map<Long, Long> userIdAndBonusMap = DistributeUtil.getUserIdAndBonusMap(
                involvedOrders.stream()
                        .filter(order -> DistributeOrderStatus.COMPLETED == order.getOrderStatus())
                        .toList()
        );
        if (CollUtil.isEmpty(userIdAndBonusMap)) {
            return new OverViewStatementResp().setType(TransactionType.DISTRIBUTE).setUserIdAmountMap(Map.of());
        }
        distributorOrderService.saveOrUpdateBatch(involvedOrders);
        Transaction transaction = orderCompleted.getTransaction();
        boolean profitSharing = transaction != null && BooleanUtil.isTrue(transaction.getProfitSharing());
        //发放佣金
        distributeBonusService.shareBonusToUser(
                userIdAndBonusMap.entrySet()
                        .stream()
                        .filter(entry -> entry.getValue() > 0)
                        .collect(
                                Collectors.toMap(
                                        Map.Entry::getKey,
                                        entry -> new DistributorBonus()
                                                .setTotal(entry.getValue())
                                                .setUndrawn(profitSharing ? 0 : entry.getValue())
                                                .setUnsettled(-entry.getValue())
                                )
                        )
        );
        return new OverViewStatementResp().setType(TransactionType.DISTRIBUTE).setUserIdAmountMap(userIdAndBonusMap);
    }

    @Override
    @AddonProvider(service = Services.GRUUL_MALL_UAA, supporterId = UaaConstant.UAA_SUPPORT_ID, methodName = "distributorCode")
    public String distributorCode(Long userId) {
        return distributorHandleService.getByUserId(userId)
                .map(distributor -> DistributorIdentity.AFFAIRS == distributor.getIdentity() ? distributor.getCode()
                        : null)
                .getOrNull();
    }

    /**
     * 分销预计赚
     *
     * @param param 预计赚请求参数
     * @return 预计赚金额
     */
    @Override
    @AddonProvider(filter = FILTER, service = Services.GRUUL_MALL_GOODS, supporterId = GoodsConstant.ADDON_SUPPORT_ID, methodName = "earning")
    public Long earning(EarningParam param) {
        return distributeProductHandleService.productPrecompute(param.getKey(), param.getUserId());
    }

    private List<DistributorOrder> groupOrdersToComplete(List<ShopOrderItem> shopOrderItems,
                                                         List<DistributorOrder> distributorOrders) {

        Map<String, List<ShopOrderItem>> itemMap = shopOrderItems.stream()
                .collect(
                        Collectors.groupingBy(
                                item -> RedisUtil.key(item.getShopId(), item.getProductId(), item.getSkuId(), item.getSpecs())
                        )
                );
        Map<String, DistributorOrder> orderMap = distributorOrders.stream()
                .collect(
                        Collectors.toMap(
                                order -> RedisUtil.key(order.getShopId(), order.getProductId(), order.getSkuId(), order.getSpecs()),
                                order -> order
                        )
                );
        List<DistributorOrder> orders = new ArrayList<>();
        for (Map.Entry<String, List<ShopOrderItem>> entry : itemMap.entrySet()) {
            DistributorOrder order = orderMap.get(entry.getKey());
            if (order == null) {
                continue;
            }
            Integer completedNum = entry.getValue().stream().map(ShopOrderItem::getNum).reduce(0, Integer::sum);
            Integer currentNum = order.getNum();
            orders.add(order);
            if (currentNum <= completedNum) {
                order.setOrderStatus(DistributeOrderStatus.COMPLETED);
                continue;
            }
            long one = DistributeUtil.calculateBonus(order.getOne(), currentNum, completedNum);
            long two = DistributeUtil.calculateBonus(order.getTwo(), currentNum, completedNum);
            long three = DistributeUtil.calculateBonus(order.getThree(), currentNum, completedNum);
            DistributorOrder distributorOrder = order.cloneIt()
                    .setOrderStatus(DistributeOrderStatus.COMPLETED)
                    .setNum(completedNum)
                    .setOne(one)
                    .setTwo(two)
                    .setThree(three)
                    .setId(null);
            orders.add(distributorOrder);
            order.setNum(currentNum - completedNum)
                    .setOne(order.getOne() - one)
                    .setTwo(order.getTwo() - two)
                    .setThree(order.getThree() - three);
        }
        return orders;
    }
}
