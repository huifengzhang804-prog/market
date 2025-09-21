package com.medusa.gruul.addon.rebate.service.imipl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.medusa.gruul.addon.rebate.model.RebateConstant;
import com.medusa.gruul.addon.rebate.model.bo.RebateCalculation;
import com.medusa.gruul.addon.rebate.model.bo.RebateItemPay;
import com.medusa.gruul.addon.rebate.model.bo.UserRebatePercent;
import com.medusa.gruul.addon.rebate.model.dto.RebateOrderQueryDTO;
import com.medusa.gruul.addon.rebate.model.enums.RebateOrderStatus;
import com.medusa.gruul.addon.rebate.model.enums.RebateType;
import com.medusa.gruul.addon.rebate.model.enums.SettlementStatus;
import com.medusa.gruul.addon.rebate.mp.entity.RebateOrder;
import com.medusa.gruul.addon.rebate.mp.entity.RebateOrderItem;
import com.medusa.gruul.addon.rebate.mp.entity.RebatePayment;
import com.medusa.gruul.addon.rebate.mp.entity.RebateTransactions;
import com.medusa.gruul.addon.rebate.mp.service.IRebateOrderService;
import com.medusa.gruul.addon.rebate.mp.service.IRebatePaymentService;
import com.medusa.gruul.addon.rebate.service.RebateConfHandleService;
import com.medusa.gruul.addon.rebate.service.RebateOrderHandlerService;
import com.medusa.gruul.addon.rebate.service.RebateTransactionsHandleService;
import com.medusa.gruul.addon.rebate.service.task.RebateOrderExportTask;
import com.medusa.gruul.carrier.pigeon.api.rpc.PigeonChatStatisticsRpcService;
import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.global.model.helper.AmountCalculateHelper;
import com.medusa.gruul.global.model.o.Final;
import com.medusa.gruul.global.model.o.KeyValue;
import com.medusa.gruul.goods.api.rpc.GoodsRpcService;
import com.medusa.gruul.order.api.entity.ShopOrderItem;
import com.medusa.gruul.order.api.enums.OrderCloseType;
import com.medusa.gruul.order.api.model.OrderCompletedDTO;
import com.medusa.gruul.order.api.model.OrderCreatedDTO;
import com.medusa.gruul.order.api.model.OrderPaidBroadcastDTO;
import com.medusa.gruul.order.api.pojo.OrderInfo;
import com.medusa.gruul.order.api.pojo.SkuStock;
import com.medusa.gruul.order.api.rpc.OrderRpcService;
import com.medusa.gruul.overview.api.entity.OverviewWithdraw;
import com.medusa.gruul.overview.api.enums.ExportDataType;
import com.medusa.gruul.overview.api.enums.WithdrawSourceType;
import com.medusa.gruul.overview.api.model.DataExportRecordDTO;
import com.medusa.gruul.overview.api.rpc.DataExportRecordRpcService;
import com.medusa.gruul.service.uaa.api.rpc.UaaRpcService;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author jinbu
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RebateOrderHandlerServiceImpl implements RebateOrderHandlerService {

    private final ShopRpcService shopRpcService;
    private final IRebateOrderService rebateOrderService;
    private final RebateConfHandleService rebateConfHandleService;
    private final IRebatePaymentService rebatePaymentService;
    private final RebateTransactionsHandleService rebateTransactionsHandleService;
    private final DataExportRecordRpcService dataExportRecordRpcService;
    private final OrderRpcService orderRpcService;
    private final PigeonChatStatisticsRpcService pigeonChatStatisticsRpcService;
    private final GoodsRpcService goodsRpcService;
    private final UaaRpcService uaaRpcService;
    private final Executor globalExecutor;


    /**
     * 订单已创建
     *
     * @param orderCreated 订单信息
     */
    @Override
    @Redisson(value = RebateConstant.REBATE_ORDER_CREATED_LOCK_KEY, key = "#orderCreated.orderNo")
    @Transactional(rollbackFor = Exception.class)
    public void saveRebateOrderCreated(OrderCreatedDTO orderCreated) {
        Long buyerId = orderCreated.getBuyerId();
        //获取返利百分比
        UserRebatePercent userRebatePercent = rebateConfHandleService.getUserRebatePercent(buyerId);
        if (userRebatePercent.noRebate()) {
            return;
        }
        if (rebateOrderService.lambdaQuery()
                .eq(RebateOrder::getBuyerId, buyerId)
                .eq(RebateOrder::getOrderNo, orderCreated.getOrderNo())
                .exists()
        ) {
            return;
        }
        saveRebateOrder(userRebatePercent.rebateRate(), orderCreated, buyerId, userRebatePercent.getVipCard().getUserNickname());
    }

    private void saveRebateOrder(BigDecimal rebateRate, OrderCreatedDTO orderCreated, Long buyerId, String buyerNickname) {
        List<ShopOrderItem> shopOrderItems = orderCreated.getShopOrderItems();
        Map<Long, ShopInfoVO> shopInfoMap = getLongShopInfoMap(
                shopOrderItems.stream()
                        .map(ShopOrderItem::getShopId)
                        .collect(Collectors.toSet())
        );
        if (shopInfoMap == null) {
            return;
        }
        List<RebateOrder> rebateOrders = new ArrayList<>(shopOrderItems.size());
        for (ShopOrderItem item : shopOrderItems) {
            ShopInfoVO shopInfo = shopInfoMap.get(item.getShopId());
            Integer serviceFeePercent = item.getExtra().getProfitRate();
            if (serviceFeePercent <= 0) {
                continue;
            }
            Long serviceFee = platformServiceFee(serviceFeePercent, item);
            RebateCalculation calculation = RebateCalculation.rebateCalculation(serviceFee, rebateRate);
            RebateOrder rebateOrder = new RebateOrder();
            rebateOrder.setStatus(RebateOrderStatus.UNPAID)
                    .setOrderNo(orderCreated.getOrderNo())
                    .setBuyerId(buyerId)
                    .setBuyerNickname(buyerNickname)
                    .setShopId(item.getShopId())
                    .setShopName(shopInfo.getName())
                    .setOrderItemId(item.getId())
                    .setProductId(item.getProductId())
                    .setProductName(item.getProductName())
                    .setProductType(item.getProductType())
                    .setSkuId(item.getSkuId())
                    .setSpecs(item.getSpecs())
                    .setNum(item.getNum())
                    .setImage(item.getImage())
                    .setSalePrice(item.getSalePrice())
                    .setDealPrice(item.getDealPrice())
                    .setRebatePercentage(rebateRate.multiply(CommonPool.BIG_DECIMAL_UNIT_CONVERSION_TEN_THOUSAND).longValue())
                    //平台服务费百分比 分润百分比
                    .setServiceFeePercent(serviceFeePercent)
                    .setPlatformServiceFee(serviceFee)
                    .setRebateCalculation(calculation.getExpression())
                    .setEstimatedRebate(calculation.getResult())
                    .setSettlementStatus(SettlementStatus.PENDING_SETTLEMENT);
            rebateOrders.add(rebateOrder);
        }
        if (CollUtil.isEmpty(rebateOrders)) {
            return;
        }
        rebateOrderService.saveBatch(rebateOrders);
    }


    /**
     * 计算平台服务费
     *
     * @param serviceFeePercent 平台服务费百分比
     * @param item              商品项信息
     * @return 平台服务费
     */
    public Long platformServiceFee(Integer serviceFeePercent, ShopOrderItem item) {
        long totalAmount = item.getDealPrice() * item.getNum() + item.getFixPrice();
        totalAmount = SellType.CONSIGNMENT == item.getSellType() ? (totalAmount - item.getExtra().getSkuPracticalSalePrice()) : totalAmount;
        //小于 1 毛钱不抽佣
        if (totalAmount <= CommonPool.NUMBER_TEN * CommonPool.UNIT_CONVERSION_HUNDRED) {
            return 0L;
        }
        return AmountCalculateHelper.getDiscountAmount(
                totalAmount,
                serviceFeePercent.longValue(),
                CommonPool.UNIT_CONVERSION_HUNDRED
        );
    }


    private Map<Long, ShopInfoVO> getLongShopInfoMap(Set<Long> shopIds) {
        // 批量获取店铺基本信息
        List<ShopInfoVO> shopInfos = shopRpcService.getShopInfoByShopIdList(shopIds);
        if (CollUtil.isEmpty(shopInfos)) {
            return null;
        }
        return shopInfos.stream()
                .collect(Collectors.toMap(ShopInfoVO::getId, v -> v));
    }


    /**
     * 订单已支付
     *
     * @param orderPaidBroadcast 订单信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @Redisson(value = RebateConstant.REBATE_BALANCE_LOCK_KEY, key = "#orderPaidBroadcast.buyerId")
    public void rebateOrderPayed(OrderPaidBroadcastDTO orderPaidBroadcast) {
        String orderNo = orderPaidBroadcast.getOrderNo();
        Long buyerId = orderPaidBroadcast.getBuyerId();
        List<RebateOrder> rebateOrders = rebateOrderService.lambdaQuery()
                .eq(RebateOrder::getStatus, RebateOrderStatus.UNPAID)
                .eq(RebateOrder::getBuyerId, buyerId)
                .eq(RebateOrder::getOrderNo, orderNo)
                .list();
        if (CollUtil.isEmpty(rebateOrders)) {
            return;
        }
        rebateOrderService.lambdaUpdate()
                .set(RebateOrder::getStatus, RebateOrderStatus.PAID)
                .eq(RebateOrder::getStatus, RebateOrderStatus.UNPAID)
                .eq(RebateOrder::getBuyerId, buyerId)
                .eq(RebateOrder::getOrderNo, orderNo)
                .update();
        // 待结算返利 使用消费返利支付
        long unsettledRebate = rebateOrders.stream()
                .mapToLong(RebateOrder::getEstimatedRebate)
                .sum();
        unsettledRebate = unsettledRebate < 0 ? 0 : unsettledRebate;
        rebateTransactionsHandleService.updateRebateBalance(
                new RebateTransactions()
                        .setUserId(buyerId)
                        .setUnsettledRebate(unsettledRebate),
                RebateType.INCOME, orderNo
        );

    }


    /**
     * 订单关闭
     *
     * @param orderInfo 订单信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @Redisson(value = RebateConstant.REBATE_BALANCE_LOCK_KEY, key = "#orderInfo.buyerId")
    public void rebateOrderClosed(OrderInfo orderInfo) {
        String orderNo = orderInfo.getOrderNo();
        Long buyerId = orderInfo.getBuyerId();
        OrderCloseType closeType = orderInfo.getCloseType();
        //查询返利支付数据
        RebatePayment payment = rebatePaymentService.lambdaQuery()
                .select(RebatePayment::getRebateItemPays, RebatePayment::getPayAmount, RebatePayment::getUserId)
                .eq(RebatePayment::getOrderNo, orderNo)
                .one();
        if (OrderCloseType.FULL == closeType) {
            //总的预计返利金额 作为失效金额
            Long expiredRebate = rebateOrderService.lambdaQuery()
                    .select(RebateOrder::getEstimatedRebate)
                    .eq(RebateOrder::getBuyerId, buyerId)
                    .eq(RebateOrder::getOrderNo, orderNo)
                    .in(RebateOrder::getStatus, RebateOrderStatus.UNPAID, RebateOrderStatus.PAID)
                    .list()
                    .stream()
                    .map(RebateOrder::getEstimatedRebate)
                    .reduce(0L, Long::sum);

            rebateOrderService.lambdaUpdate()
                    .set(RebateOrder::getStatus, RebateOrderStatus.CLOSED)
                    .set(RebateOrder::getSettlementStatus, SettlementStatus.EXPIRED)
                    .eq(RebateOrder::getBuyerId, buyerId)
                    .eq(RebateOrder::getOrderNo, orderNo)
                    .in(RebateOrder::getStatus, RebateOrderStatus.UNPAID, RebateOrderStatus.PAID)
                    .update();
            if (payment != null) {
                rebatePaymentService.lambdaUpdate()
                        .eq(RebatePayment::getOrderNo, orderNo)
                        .remove();
            }

            //归还该订单使用的返利余额
            Long payAmount = payment == null ? 0L : payment.getPayAmount();
            rebateTransactionsHandleService.updateRebateBalance(
                    new RebateTransactions()
                            .setUserId(buyerId)
                            .setRebateBalance(payAmount)
                            .setExpiredRebate(expiredRebate),
                    RebateType.REFUND, StrUtil.format("订单退款：{}", orderNo)
            );
            return;
        }
        Long shopId = orderInfo.getShopId();
        if (OrderCloseType.SHOP == closeType) {
            //总的预计返利金额 作为失效金额
            Long expiredRebate = rebateOrderService.lambdaQuery()
                    .select(RebateOrder::getEstimatedRebate)
                    .eq(RebateOrder::getBuyerId, buyerId)
                    .eq(RebateOrder::getOrderNo, orderNo)
                    .in(RebateOrder::getStatus, RebateOrderStatus.UNPAID, RebateOrderStatus.PAID)
                    .list()
                    .stream()
                    .map(RebateOrder::getEstimatedRebate)
                    .reduce(0L, Long::sum);

            rebateOrderService.lambdaUpdate()
                    .set(RebateOrder::getStatus, RebateOrderStatus.CLOSED)
                    .set(RebateOrder::getSettlementStatus, SettlementStatus.EXPIRED)
                    .in(RebateOrder::getStatus, RebateOrderStatus.UNPAID, RebateOrderStatus.PAID)
                    .eq(RebateOrder::getBuyerId, buyerId)
                    .eq(RebateOrder::getShopId, shopId)
                    .eq(RebateOrder::getOrderNo, orderNo)
                    .update();
            RebateTransactions rebateTransactions = new RebateTransactions()
                    .setUserId(buyerId)
                    .setExpiredRebate(expiredRebate);
            if (payment != null) {
                Map<ShopProductSkuKey, RebateItemPay> rebateItemPays = payment.getRebateItemPays();
                Map<ShopProductSkuKey, RebateItemPay> newRebateItemPays = new HashMap<>(rebateItemPays.size());
                Final<Long> refundRebate = new Final<>(0L);
                rebateItemPays.forEach(
                        (key, value) -> {
                            if (key.getShopId().equals(shopId)) {
                                refundRebate.set(refundRebate.get() + value.getPaidRebate());
                                return;
                            }
                            newRebateItemPays.put(key, value);
                        }
                );
                if (refundRebate.get() <= 0) {
                    return;
                }
                if (CollUtil.isEmpty(newRebateItemPays)) {
                    rebatePaymentService.lambdaUpdate()
                            .eq(RebatePayment::getOrderNo, orderNo)
                            .remove();
                } else {
                    rebatePaymentService.lambdaUpdate()
                            .set(RebatePayment::getRebateItemPays, JSON.toJSONString(KeyValue.of(newRebateItemPays)))
                            .set(RebatePayment::getPayAmount, payment.getPayAmount() - refundRebate.get())
                            .eq(RebatePayment::getOrderNo, orderNo)
                            .update();
                }
                rebateTransactions.setRebateBalance(refundRebate.get());
            }

            rebateTransactionsHandleService.updateRebateBalance(
                    rebateTransactions,
                    RebateType.REFUND, StrUtil.format("订单退款：{}", orderNo)
            );
        }
        // 以上是未支付，以下是已支付 退款逻辑
        rebateRefundHandle(payment, orderInfo);
    }

    private void rebateRefundHandle(RebatePayment payment, OrderInfo orderInfo) {

        List<SkuStock> skuStocks = orderInfo.getSkuStocks();
        OrderInfo.Afs afs;
        if (CollUtil.isEmpty(skuStocks) || (afs = orderInfo.getAfs()) == null) {
            return;
        }
        String orderNo = orderInfo.getOrderNo();
        SkuStock skuStock = skuStocks.get(0);
        Long productId = skuStock.getProductId();
        Long skuId = skuStock.getSkuId();
        Integer closeNum = skuStock.getNum();
        if (closeNum <= 0) {
            return;
        }
        Long buyerId = orderInfo.getBuyerId();
        RebateOrder rebateOrder = rebateOrderService.lambdaQuery()
                .eq(RebateOrder::getStatus, RebateOrderStatus.PAID)
                .eq(RebateOrder::getBuyerId, buyerId)
                .eq(RebateOrder::getShopId, orderInfo.getShopId())
                .eq(RebateOrder::getOrderNo, orderNo)
                .eq(RebateOrder::getProductId, productId)
                .eq(RebateOrder::getSkuId, skuId)
                .apply("specs = CAST({0} AS JSON)", JSON.toJSONString(afs.getSpecs()))
                .one();
        if (rebateOrder == null) {
            RebateTransactions rebateTransactions = new RebateTransactions().setUserId(buyerId);
            this.refundPaidRebate(payment, skuStock.skuKey(), closeNum, rebateTransactions);
            if (rebateTransactions.getRebateBalance() != null) {
                rebateTransactionsHandleService.updateRebateBalance(rebateTransactions, RebateType.REFUND, orderNo);
            }
            return;
        }
        Integer preNum = rebateOrder.getNum();
        int leftNum = preNum - closeNum;
        Long estimatedRebate = rebateOrder.getEstimatedRebate();
        //剩余返利比例
        BigDecimal remainRate = AmountCalculateHelper.getDiscountRate((long) leftNum, (long) preNum);
        //剩余返利
        Long remainRebate = AmountCalculateHelper.getDiscountAmount(estimatedRebate, remainRate);
        //失效的返利
        Long invalidRebate = estimatedRebate - remainRebate;
        //剩余服务费
        Long remainServiceFee = AmountCalculateHelper.getDiscountAmount(rebateOrder.getPlatformServiceFee(), remainRate);
        //失效的服务费
        Long invalidServiceFee = rebateOrder.getPlatformServiceFee() - remainServiceFee;
        rebateOrderService.updateById(
                rebateOrder.setNum(closeNum)
                        .setStatus(RebateOrderStatus.CLOSED)
                        .setSettlementStatus(SettlementStatus.EXPIRED)
                        .setEstimatedRebate(invalidRebate)
                        .setPlatformServiceFee(invalidServiceFee)
                        .setRebateCalculation(StrUtil.format("{} * {} = {}", AmountCalculateHelper.toYuan(invalidServiceFee), RebateCalculation.rebatePercentStr(rebateOrder.rebateRate()), AmountCalculateHelper.toYuan(invalidRebate)))
        );
        if (leftNum > 0) {
            rebateOrder.setId(null);
            rebateOrderService.save(
                    rebateOrder.setNum(leftNum)
                            .setStatus(RebateOrderStatus.PAID)
                            .setSettlementStatus(SettlementStatus.PENDING_SETTLEMENT)
                            .setEstimatedRebate(remainRebate)
                            .setPlatformServiceFee(remainServiceFee)
                            .setRebateCalculation(StrUtil.format("{} * {} = {}", AmountCalculateHelper.toYuan(remainServiceFee), RebateCalculation.rebatePercentStr(rebateOrder.rebateRate()), AmountCalculateHelper.toYuan(remainRebate)))

            );
        }
        // 更新用户待结算返利 失效返利
        RebateTransactions rebateTransactions = new RebateTransactions()
                .setUserId(buyerId)
                .setUnsettledRebate(-invalidRebate)
                .setExpiredRebate(invalidRebate);

        this.refundPaidRebate(payment, skuStock.skuKey(), closeNum, rebateTransactions);
        rebateTransactionsHandleService.updateRebateBalance(rebateTransactions, RebateType.REFUND, StrUtil.format("订单退款：{}", orderNo));
    }

    /**
     * 返利支付退款
     */
    private void refundPaidRebate(RebatePayment payment, ShopProductSkuKey skuKey, Integer closeNum, RebateTransactions rebateTransactions) {
        Map<ShopProductSkuKey, RebateItemPay> rebateItemPays;
        //是否使用了返利支付 如果使用了返利支付则需要退还
        if (payment == null || CollUtil.isEmpty(rebateItemPays = payment.getRebateItemPays())) {
            return;
        }
        RebateItemPay rebateItemPay = rebateItemPays.get(skuKey);
        if (rebateItemPay == null) {
            return;
        }
        String orderNo = payment.getOrderNo();
        //剩余返利比例
        Integer preNum = rebateItemPay.getNum();
        int leftNum = preNum - closeNum;
        BigDecimal remainRate = AmountCalculateHelper.getDiscountRate((long) leftNum, (long) preNum);
        Long rebateDiscount = rebateItemPay.getPaidRebate();
        //剩余返利支付金额
        Long remainRebatePayAmount = AmountCalculateHelper.getDiscountAmount(rebateDiscount, remainRate);
        if (remainRebatePayAmount == 0) {
            rebateItemPays.remove(skuKey);
        } else {
            rebateItemPay.setNum(leftNum).setPaidRebate(remainRebatePayAmount);
        }
        long refundRebate = rebateDiscount - remainRebatePayAmount;
        rebateTransactions.setRebateBalance(refundRebate);
        if (CollUtil.isEmpty(rebateItemPays)) {
            rebatePaymentService.lambdaUpdate()
                    .eq(RebatePayment::getOrderNo, orderNo)
                    .remove();
            return;
        }
        rebatePaymentService.lambdaUpdate()
                .set(RebatePayment::getPayAmount, payment.getPayAmount() - (refundRebate))
                .set(RebatePayment::getRebateItemPays, JSON.toJSONString(KeyValue.of(rebateItemPays)))
                .eq(RebatePayment::getOrderNo, orderNo)
                .update();
    }

    /**
     * 订单完成
     *
     * @param orderCompleted 订单信息
     */
    @Override
    public void rebateOrderAccomplish(OrderCompletedDTO orderCompleted) {
        // 平台服务费
        final long platformServiceFee = AmountCalculateHelper.priceFormat(true, orderCompleted.getExtra().getLong("serviceCharge", 0L));
        //只针对每个sku 进行的评价 所有的ShopOrderItem 的 sku key 都是一样的
        List<ShopOrderItem> shopOrderItems = orderCompleted.getShopOrderItems();
        if (CollUtil.isEmpty(shopOrderItems)) {
            return;
        }
        Long shopId = orderCompleted.getShopId();
        Map<String, List<ShopOrderItem>> itemsMap = shopOrderItems.stream()
                .collect(
                        Collectors.groupingBy(
                                item -> RedisUtil.key(shopId, item.getProductId(), item.getSkuId(), item.getSpecs())
                        )
                );

        List<RebateOrder> rebateOrders = rebateOrderService.lambdaQuery()
                .select(RebateOrder::getId,
                        RebateOrder::getNum,
                        RebateOrder::getShopName,
                        RebateOrder::getRebatePercentage,
                        RebateOrder::getEstimatedRebate,
                        RebateOrder::getSpecs,
                        RebateOrder::getProductId,
                        RebateOrder::getSkuId
                )
                .eq(RebateOrder::getShopId, shopId)
                .eq(RebateOrder::getOrderNo, orderCompleted.getOrderNo())
                .eq(RebateOrder::getStatus, RebateOrderStatus.PAID)
                .list();
        if (CollUtil.isEmpty(rebateOrders)) {
            return;
        }
        Map<String, RebateOrder> rebateOrderMap = rebateOrders.stream()
                .collect(Collectors.toMap(
                        order -> RedisUtil.key(shopId, order.getProductId(), order.getSkuId(), order.getSpecs()),
                        Function.identity()
                ));

        long unsettledRebate = 0;
        long accumulatedRebate = 0;
        //商品总价 根据每个item的支付价格 均分服务费
        long totalPayPrice = shopOrderItems.stream()
                .map(ShopOrderItem::payPrice)
                .reduce(0L, Long::sum);
        Iterator<Map.Entry<String, List<ShopOrderItem>>> iterator = itemsMap.entrySet().iterator();
        long remainPlatformServiceFee = platformServiceFee;
        while (iterator.hasNext()) {
            Map.Entry<String, List<ShopOrderItem>> entry = iterator.next();
            String key = entry.getKey();
            RebateOrder rebateOrder = rebateOrderMap.get(key);
            if (rebateOrder == null) {
                continue;
            }
            //计算当前商品的服务费返利
            long curItemServiceFee = remainPlatformServiceFee;
            //如果总价小于等于0 或 剩余服务费小于等于0 或 没有下一个商品 则当前商品服务费为剩余服务费
            if (totalPayPrice > 0 && curItemServiceFee > 0 && iterator.hasNext()) {
                //当前商品服务费总价
                long curPayPrice = entry.getValue()
                        .stream()
                        .map(ShopOrderItem::payPrice)
                        .reduce(0L, Long::sum);
                //根据总价占比 计算当前商品的服务费
                curItemServiceFee = curPayPrice <= 0 ? 0
                        : AmountCalculateHelper.getDiscountAmount(platformServiceFee, AmountCalculateHelper.getDiscountRate(curPayPrice, totalPayPrice));
            }
            //更新剩余的服务费
            remainPlatformServiceFee -= Math.min(curItemServiceFee, remainPlatformServiceFee);
            RebateCalculation calculation = RebateCalculation.rebateCalculation(curItemServiceFee, rebateOrder.rebateRate());
            unsettledRebate += rebateOrder.getEstimatedRebate();
            rebateOrder.setPlatformServiceFee(curItemServiceFee)
                    .setRebateCalculation(calculation.getExpression())
                    .setEstimatedRebate(calculation.getResult())
                    .setStatus(RebateOrderStatus.COMPLETED)
                    .setSettlementStatus(SettlementStatus.SETTLED);
            accumulatedRebate += calculation.getResult();
        }
        rebateOrderService.updateBatchById(rebateOrders);
        Long userId = orderCompleted.getUserId();
        //更新用户余额
        rebateTransactionsHandleService.updateRebateBalance(
                new RebateTransactions()
                        .setUserId(userId)
                        .setUnsettledRebate(-unsettledRebate)
                        .setRebateBalance(accumulatedRebate)
                        .setAccumulatedRebate(accumulatedRebate),
                RebateType.INCOME, orderCompleted.getOrderNo()
        );
    }


    /***
     * 分页查询消费返利订单
     * @param rebateOrderQuery 查询参数
     * @return 消费返利订单
     */
    @Override
    public RebateOrderQueryDTO pageRebateOrder(RebateOrderQueryDTO rebateOrderQuery) {
        RebateOrderQueryDTO pageRebateOrder = rebateOrderService.pageRebateOrder(rebateOrderQuery);
        List<RebateOrder> records = pageRebateOrder.getRecords();
        if (CollUtil.isEmpty(records)) {
            return pageRebateOrder;
        }
        //渲染订单item
        Set<String> orderNos = records.stream().map(RebateOrder::getOrderNo).collect(Collectors.toSet());
        List<RebateOrderItem> rebateOrderItems = rebateOrderService.pageRebateOrderItem(orderNos, rebateOrderQuery.getKeyword());
        Map<String, List<RebateOrderItem>> rebateOrderItemMap = rebateOrderItems
                .stream()
                .collect(Collectors.groupingBy(item -> RedisUtil.key(item.getOrderNo(), item.getShopId())));
        records.forEach(rebateOrder -> rebateOrder.setRebateOrderItems(rebateOrderItemMap.get(RedisUtil.key(rebateOrder.getOrderNo(), rebateOrder.getShopId()))));
        pageRebateOrder.setStatistic(
                rebateOrderService.rebateOrderStatistic(
                        pageRebateOrder.getRecords()
                                .stream()
                                .flatMap(order -> StrUtil.split(order.getIds(), ',').stream())
                                .map(Long::parseLong)
                                .collect(Collectors.toSet())
                )
        );
        return pageRebateOrder;
    }

    /**
     * 提现工单被关闭
     *
     * @param overviewWithdraw 提现工单信息
     */
    @Override
    @Redisson(value = RebateConstant.REBATE_BALANCE_LOCK_KEY, key = "#overviewWithdraw.ownerId")
    public void rebateWithdrawOrderForbidden(OverviewWithdraw overviewWithdraw) {
        if (WithdrawSourceType.CONSUMPTION_REBATE != overviewWithdraw.getSourceType()) {
            return;
        }
        //更新用户余额
        rebateTransactionsHandleService.updateRebateBalance(
                new RebateTransactions()
                        .setUserId(overviewWithdraw.getOwnerId())
                        .setRebateBalance(overviewWithdraw.getDrawType().getAmount()),
                RebateType.REFUND,
                StrUtil.format("提现申请未通过：{}", overviewWithdraw.getNo())
        );
    }

    @Override
    public Boolean updateRebateOrderInfo(List<ShopOrderItem> shopOrderItems, String orderNo) {
        List<RebateOrder> reabteOrderList = TenantShop.disable(() -> rebateOrderService.lambdaQuery()
                .eq(RebateOrder::getOrderNo, orderNo)
                .list());
        if (CollUtil.isEmpty(reabteOrderList)) {
            return Boolean.TRUE;
        }
        Map<Long, ShopOrderItem> shopOrderItemMap = shopOrderItems.stream()
                .collect(Collectors.toMap(ShopOrderItem::getId, x -> x));
        for (RebateOrder rebateOrder : reabteOrderList) {
            ShopOrderItem shopOrderItem = shopOrderItemMap.get(rebateOrder.getOrderItemId());
            if (Objects.isNull(shopOrderItem)) {
                continue;
            }
            //更新订单详情成交价
            rebateOrder.setDealPrice(shopOrderItem.getDealPrice());
            //更新平台服务费
            rebateOrder.setPlatformServiceFee(platformServiceFee(rebateOrder.getServiceFeePercent(), shopOrderItem));
            //更新返利计算公式
            RebateCalculation calculation = RebateCalculation.rebateCalculation(rebateOrder.getPlatformServiceFee(),
                    BigDecimal.valueOf(rebateOrder.getRebatePercentage()).divide(CommonPool.BIG_DECIMAL_UNIT_CONVERSION_TEN_THOUSAND));
            rebateOrder.setRebateCalculation(calculation.getExpression());
            rebateOrder.setEstimatedRebate(calculation.getResult());
        }
        return rebateOrderService.updateBatchById(reabteOrderList);

    }

    @Override
    public void export(RebateOrderQueryDTO queryDTO) {
        DataExportRecordDTO dto = new DataExportRecordDTO();
        dto.setExportUserId(ISecurity.userMust().getId())
                .setDataType(ExportDataType.PURCHASE_REBATE_ORDER)
                .setShopId(ISystem.shopIdMust())
                .setUserPhone(ISecurity.userMust().getMobile());
        //RPC保存导出记录
        Long exportRecordId = dataExportRecordRpcService.saveExportRecord(dto);

        RebateOrderExportTask rebateOrderExportTask=new RebateOrderExportTask(
                exportRecordId,dataExportRecordRpcService,this,queryDTO,
                uaaRpcService, orderRpcService, pigeonChatStatisticsRpcService,
                goodsRpcService);
        globalExecutor.execute(rebateOrderExportTask);


    }
}
