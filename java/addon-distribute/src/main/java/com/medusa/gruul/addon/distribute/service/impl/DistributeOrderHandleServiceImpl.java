package com.medusa.gruul.addon.distribute.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.distribute.model.Bonus;
import com.medusa.gruul.addon.distribute.model.DistributeConstant;
import com.medusa.gruul.addon.distribute.model.DistributorBonus;
import com.medusa.gruul.addon.distribute.model.ShopOrderKey;
import com.medusa.gruul.addon.distribute.model.dto.DistributorOrderQueryDTO;
import com.medusa.gruul.addon.distribute.model.enums.DistributeOrderStatus;
import com.medusa.gruul.addon.distribute.model.enums.Level;
import com.medusa.gruul.addon.distribute.model.vo.DistributorMainOrderVO;
import com.medusa.gruul.addon.distribute.model.vo.DistributorOrderBonusStatisticVO;
import com.medusa.gruul.addon.distribute.model.vo.DistributorOrderPageVO;
import com.medusa.gruul.addon.distribute.model.vo.DistributorOrderVO;
import com.medusa.gruul.addon.distribute.mp.entity.DistributeProduct;
import com.medusa.gruul.addon.distribute.mp.entity.Distributor;
import com.medusa.gruul.addon.distribute.mp.entity.DistributorOrder;
import com.medusa.gruul.addon.distribute.mp.service.IDistributorOrderService;
import com.medusa.gruul.addon.distribute.mp.service.IDistributorService;
import com.medusa.gruul.addon.distribute.service.*;
import com.medusa.gruul.addon.distribute.service.task.AbstractDistributeOrderExportTask;
import com.medusa.gruul.addon.distribute.service.task.PlatformDistributeOrderExportTask;
import com.medusa.gruul.addon.distribute.service.task.ShopDistributeOrderExportTask;
import com.medusa.gruul.addon.distribute.util.DistributeUtil;
import com.medusa.gruul.carrier.pigeon.api.rpc.PigeonChatStatisticsRpcService;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.order.api.entity.ShopOrderItem;
import com.medusa.gruul.order.api.enums.OrderCloseType;
import com.medusa.gruul.order.api.model.OrderCreatedDTO;
import com.medusa.gruul.order.api.model.OrderPaidBroadcastDTO;
import com.medusa.gruul.order.api.pojo.OrderInfo;
import com.medusa.gruul.order.api.pojo.SkuStock;
import com.medusa.gruul.order.api.rpc.OrderRpcService;
import com.medusa.gruul.overview.api.enums.ExportDataType;
import com.medusa.gruul.overview.api.model.DataExportRecordDTO;
import com.medusa.gruul.overview.api.rpc.DataExportRecordRpcService;
import com.medusa.gruul.user.api.rpc.UserRpcService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * @author 张治保 date 2023/5/12
 */
@Service
@RequiredArgsConstructor
public class DistributeOrderHandleServiceImpl implements DistributeOrderHandleService {

    private final IDistributorService distributorService;
    private final DistributeBonusService distributeBonusService;
    private final IDistributorOrderService distributorOrderService;
    private final DistributorHandleService distributorHandleService;
    private final DistributeConfHandleService distributeConfHandleService;
    private final DistributeProductHandleService distributeProductHandleService;
    private final DataExportRecordRpcService dataExportRecordRpcService;
    private final Executor globalExecutor;
    private final PigeonChatStatisticsRpcService pigeonChatStatisticsRpcService;
    private final UserRpcService userRpcService;
    private final OrderRpcService orderRpcService;

    @Override
    public DistributorOrderPageVO orderPage(DistributorOrderQueryDTO query) {
        IPage<DistributorMainOrderVO> mainOrders = distributorOrderService.distributorOrderPage(query);
        List<DistributorMainOrderVO> records = mainOrders.getRecords();
        DistributorOrderPageVO result = new DistributorOrderPageVO()
                .setPage(mainOrders)
                .setStatistic(new DistributorOrderBonusStatisticVO());
        if (CollUtil.isEmpty(records)) {
            return result;
        }
        long size = query.getSize();
        query.setSize(-1);
        Map<ShopOrderKey, DistributorMainOrderVO> keyMainOrderMap = records.stream()
                .collect(
                        Collectors.toMap(
                                ord -> new ShopOrderKey().setOrderNo(ord.getOrderNo()).setShopId(ord.getShopId()),
                                ord -> ord
                        )
                );
        List<DistributorOrderVO> ordersByKey = distributorOrderService.getOrdersByKey(keyMainOrderMap.keySet(), query);

        Map<ShopOrderKey, List<DistributorOrderVO>> keyOrderMap = ordersByKey.stream()
                .collect(Collectors.groupingBy(
                        ord -> new ShopOrderKey().setOrderNo(ord.getOrderNo()).setShopId(ord.getShopId())));
        keyMainOrderMap.forEach(
                (key, mainOrder) -> mainOrder.setItems(keyOrderMap.get(key))
        );
        //如果当前用户 Id为空则 聚合查询订单的总佣金统计
        if (query.getCurrentUserId() == null) {
            result.setStatistic(
                    ObjectUtil.defaultIfNull(
                            distributorOrderService.getStatistic(keyMainOrderMap.keySet(), query),
                            result.getStatistic()
                    )
            );
            query.setSize(size);
            return result;
        }
        query.setSize(size);
        //否则 只查询该用户佣金统计否则
        Distributor distributor = distributorService.lambdaQuery()
                .select(Distributor::getTotal, Distributor::getUnsettled, Distributor::getInvalid)
                .eq(Distributor::getUserId, query.getCurrentUserId())
                .one();
        if (distributor == null) {
            return result;
        }
        result.getStatistic()
                .setTotal(distributor.getTotal())
                .setUnsettled(distributor.getUnsettled())
                .setInvalid(distributor.getInvalid());

        List<DistributorMainOrderVO> tempList = result.getPage().getRecords();
        //当前用户所属的分
        for (DistributorMainOrderVO distributorMainOrderVO : tempList) {
            List<DistributorOrderVO> items = distributorMainOrderVO.getItems();
            DistributorOrderVO distributorOrderVO = items.get(0);
            if (query.getCurrentUserId().equals(distributorOrderVO.getOne().getUserId())) {
                distributorMainOrderVO.setCurrentUserLevel(Level.ONE);
            } else if (query.getCurrentUserId().equals(distributorOrderVO.getTwo().getUserId())) {
                distributorMainOrderVO.setCurrentUserLevel(Level.TWO);
            } else if (query.getCurrentUserId().equals(distributorOrderVO.getThree().getUserId())) {
                distributorMainOrderVO.setCurrentUserLevel(Level.THREE);
            }
        }
        return result;
    }

    @Override
    @Redisson(value = DistributeConstant.ORDER_STATUS_UPDATE_LOCK_KEY, key = "#orderCreated.orderNo")
    public void orderCreated(OrderCreatedDTO orderCreated) {
        Long buyerId = orderCreated.getBuyerId();
        String orderNo = orderCreated.getOrderNo();
        List<ShopOrderItem> shopOrderItems = orderCreated.getShopOrderItems();
        if (StrUtil.isEmpty(orderNo) || CollUtil.isEmpty(shopOrderItems)) {
            return;
        }
        if (distributorOrderService.lambdaQuery().eq(DistributorOrder::getOrderNo, orderNo).exists()) {
            return;
        }
        Distributor distributor = distributorHandleService.getByUserId(buyerId).getOrNull();
        if (distributor == null) {
            return;
        }
        Map<ShopProductKey, List<ShopOrderItem>> shopProductKeyListMap = shopOrderItems.stream().collect(
                Collectors.groupingBy(
                        item -> new ShopProductKey().setShopId(item.getShopId()).setProductId(item.getProductId()))
        );
        Map<ShopProductKey, DistributeProduct> shopProductConfMap = distributeProductHandleService.shopProductConfMap(
                shopProductKeyListMap.keySet());
        if (CollUtil.isEmpty(shopProductConfMap)) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        List<DistributorOrder> orders = shopProductKeyListMap.entrySet()
                .stream()
                .filter(entry -> shopProductConfMap.containsKey(entry.getKey()))
                .flatMap(entry -> {
                    DistributeProduct distributeProduct = shopProductConfMap.get(entry.getKey());
                    return entry.getValue().stream().map(item -> {
                        Bonus bonus = DistributeUtil.getBonus(
                                distributor, distributeConfHandleService.configMust().getPurchase(), item.getNum(),
                                item.getDealPrice(), distributeProduct
                        );
                        return new DistributorOrder()
                                .setUserId(buyerId)
                                .setOrderNo(orderNo)
                                .setShopId(item.getShopId())
                                .setProductId(item.getProductId())
                                .setProductType(item.getProductType())
                                .setProductName(distributeProduct.getName())
                                .setSkuId(item.getSkuId())
                                .setImage(item.getImage())
                                .setSpecs(item.getSpecs())
                                .setNum(item.getNum())
                                .setDealPrice(item.getDealPrice())
                                .setOrderStatus(DistributeOrderStatus.UNPAID)
                                .setPurchase(bonus.getPurchase())
                                .setOneId(bonus.getOneId())
                                .setOne(bonus.getOne())
                                .setTwoId(bonus.getTwoId())
                                .setTwo(bonus.getTwo())
                                .setThreeId(bonus.getThreeId())
                                .setThree(bonus.getThree())
                                .setShareType(distributeProduct.getShareType())
                                .setShareOne(distributeProduct.getOne())
                                .setShareTwo(distributeProduct.getTwo())
                                .setShareThree(distributeProduct.getThree())
                                .setCreateTime(now);
                    });
                })
                .collect(Collectors.toList());
        distributorOrderService.saveBatch(orders);
    }



	 /*增加了商品数量，
    关闭商品 和
    订单完成时需要拆分商品*/

    @Override
    @Redisson(value = DistributeConstant.ORDER_STATUS_UPDATE_LOCK_KEY, key = "#orderInfo.orderNo")
    public void orderClosed(OrderInfo orderInfo) {
        OrderCloseType closeType = orderInfo.getCloseType();
        String orderNo = orderInfo.getOrderNo();
        Long buyerId = orderInfo.getBuyerId();
        Long shopId = orderInfo.getShopId();
        //是否是关闭店铺订单
        boolean shopClosed = OrderCloseType.SHOP == closeType;
        //未支付是的订单关闭
        if (OrderCloseType.FULL == closeType || shopClosed) {
            List<DistributorOrder> orders = distributorOrderService.lambdaQuery()
                    .select(
                            DistributorOrder::getUserId, DistributorOrder::getPurchase,
                            DistributorOrder::getOneId, DistributorOrder::getOne,
                            DistributorOrder::getTwoId, DistributorOrder::getTwo,
                            DistributorOrder::getThreeId, DistributorOrder::getThree
                    )
                    .eq(DistributorOrder::getUserId, buyerId)
                    .eq(DistributorOrder::getOrderNo, orderNo)
                    .eq(DistributorOrder::getOrderStatus, DistributeOrderStatus.UNPAID)
                    .eq(shopClosed, DistributorOrder::getShopId, shopId)
                    .list();
            if (CollUtil.isEmpty(orders)) {
                return;
            }
            distributorOrderService.lambdaUpdate()
                    .eq(DistributorOrder::getUserId, buyerId)
                    .eq(DistributorOrder::getOrderNo, orderNo)
                    .eq(DistributorOrder::getOrderStatus, DistributeOrderStatus.UNPAID)
                    .eq(shopClosed, DistributorOrder::getShopId, shopId)
                    .set(DistributorOrder::getOrderStatus, DistributeOrderStatus.CLOSED)
                    .update();
            // 增加用户的失效佣金
            distributeBonusService.shareBonusToUser(
                    DistributeUtil.getUserIdAndBonusMap(orders)
                            .entrySet()
                            .stream()
                            .collect(
                                    Collectors.toMap(
                                            Map.Entry::getKey,
                                            entry -> new DistributorBonus().setInvalid(entry.getValue())
                                    )
                            )
            );
            return;
        }
        OrderInfo.Afs afs;
        //以上是未支付商品关闭 以下是已支付商品关闭
        List<SkuStock> skuStocks = orderInfo.getSkuStocks();
        if (CollUtil.isEmpty(skuStocks) || (afs = orderInfo.getAfs()) == null) {
            return;
        }
        SkuStock skuStock = skuStocks.get(0);
        Long productId = skuStock.getProductId();
        Long skuId = skuStock.getSkuId();
        Integer closeNum = skuStock.getNum();
        if (closeNum <= 0) {
            return;
        }
        DistributorOrder order = distributorOrderService.lambdaQuery()
                .eq(DistributorOrder::getUserId, buyerId)
                .eq(DistributorOrder::getOrderNo, orderNo)
                .eq(DistributorOrder::getShopId, shopId)
                .eq(DistributorOrder::getProductId, productId)
                .eq(DistributorOrder::getSkuId, skuId)
                .eq(DistributorOrder::getOrderStatus, DistributeOrderStatus.PAID)
                .apply("specs = CAST({0} AS JSON)", JSON.toJSONString(afs.getSpecs()))
                .one();
        if (order == null) {
            return;
        }
        Integer preNum = order.getNum();
        Map<Long, Long> bonusMap = DistributeUtil.getUserIdAndBonusMap(List.of(order));
        RLock lock = RedisUtil.getRedissonClient()
                .getLock(DistributeUtil.getSkuLockKey(orderNo, shopId, productId, skuId));
        lock.lock();
        try {

            Long onePre = order.getOne();
            Long twoPre = order.getTwo();
            Long threePre = order.getThree();
            long oneClose = DistributeUtil.calculateBonus(onePre, preNum, closeNum);
            long twoClose = DistributeUtil.calculateBonus(twoPre, preNum, closeNum);
            long threeClose = DistributeUtil.calculateBonus(threePre, preNum, closeNum);
            //当前订单状态更新为 已关闭
            distributorOrderService.updateById(
                    order.setNum(closeNum)
                            .setOne(oneClose)
                            .setTwo(twoClose)
                            .setThree(threeClose)
                            .setOrderStatus(DistributeOrderStatus.CLOSED)
            );
            if (bonusMap.containsKey(buyerId)) {
                bonusMap.put(buyerId, order.getOne());
            }
            if (order.getOneId() != null) {
                bonusMap.put(order.getOneId(), oneClose);
            }
            if (order.getTwoId() != null) {
                bonusMap.put(order.getTwoId(), twoClose);
            }
            if (order.getThreeId() != null) {
                bonusMap.put(order.getThreeId(), threeClose);
            }
            int leftNum = preNum - closeNum;
            if (leftNum > 0) {
                distributorOrderService.save(
                        order.cloneIt()
                                .setNum(leftNum)
                                .setOne(onePre - oneClose)
                                .setTwo(twoPre - twoClose)
                                .setThree(threePre - threeClose)
                                .setOrderStatus(DistributeOrderStatus.PAID)
                );
            }
        } finally {
            lock.unlock();
        }
        //关闭订单时，退还佣金
        distributeBonusService.shareBonusToUser(
                bonusMap.entrySet()
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        Map.Entry::getKey,
                                        entry -> new DistributorBonus().setInvalid(entry.getValue())
                                                .setUnsettled(-entry.getValue())
                                )
                        )
        );
        //关闭订单时，退还库存
        distributeProductHandleService.updateProductSales(
                Map.of(new ShopProductKey().setShopId(shopId).setProductId(productId), -closeNum.longValue()));
    }


    @Override
    @Log("订单支付")
    @Redisson(value = DistributeConstant.ORDER_STATUS_UPDATE_LOCK_KEY, key = "#orderPaidBroadcast.orderNo")
    public void orderPaid(OrderPaidBroadcastDTO orderPaidBroadcast) {
        String orderNo = orderPaidBroadcast.getOrderNo();
        Long buyerId = orderPaidBroadcast.getBuyerId();
        //该订单信息不存在
        List<DistributorOrder> orders = distributorOrderService.lambdaQuery()
                .eq(DistributorOrder::getUserId, buyerId)
                .eq(DistributorOrder::getOrderNo, orderNo)
                .eq(DistributorOrder::getOrderStatus, DistributeOrderStatus.UNPAID)
                .list();
        if (CollUtil.isEmpty(orders)) {
            return;
        }
        distributorOrderService.lambdaUpdate()
                .set(DistributorOrder::getOrderStatus, DistributeOrderStatus.PAID)
                .eq(DistributorOrder::getUserId, buyerId)
                .eq(DistributorOrder::getOrderNo, orderNo)
                .eq(DistributorOrder::getOrderStatus, DistributeOrderStatus.UNPAID)
                .update();
        Map<Long, Long> userIdAndBonusMap = DistributeUtil.getUserIdAndBonusMap(orders);
        //分配待结算佣金
        distributeBonusService.shareBonusToUser(
                userIdAndBonusMap.entrySet()
                        .stream()
                        .filter(entry -> entry.getValue() > 0)
                        .collect(
                                Collectors.toMap(
                                        Map.Entry::getKey,
                                        entry -> new DistributorBonus().setUnsettled(entry.getValue())
                                )
                        )
        );
        //增加销量
        distributeProductHandleService.updateProductSales(
                orders.stream()
                        .collect(
                                Collectors.toMap(
                                        order -> new ShopProductKey().setShopId(order.getShopId())
                                                .setProductId(order.getProductId()),
                                        order -> order.getNum().longValue(),
                                        Long::sum
                                )
                        )
        );
    }

    @Override
    public void export(DistributorOrderQueryDTO queryDTO) {
        ClientType clientType = ISystem.clientTypeMust();
        ExportDataType exportDataType = ExportDataType.DISTRIBUTE_ORDER;
        SecureUser<Object> user = ISecurity.userMust();
        DataExportRecordDTO record = DataExportRecordDTO.instanceForSave(user.getId(),
                user.getShopId(), exportDataType, user.getMobile());
        record.setId(dataExportRecordRpcService.saveExportRecord(record));
        AbstractDistributeOrderExportTask task = null;
        if (ClientType.PLATFORM_CONSOLE.equals(clientType)) {
            task = new PlatformDistributeOrderExportTask(
                    this,
                    pigeonChatStatisticsRpcService,
                    dataExportRecordRpcService,
                    userRpcService,
                    orderRpcService,
                    queryDTO,
                    record);
        } else {
            task = new ShopDistributeOrderExportTask(
                    this,
                    pigeonChatStatisticsRpcService,
                    dataExportRecordRpcService,
                    userRpcService,
                    orderRpcService,
                    queryDTO,
                    record);
        }

        globalExecutor.execute(task);

    }


}
