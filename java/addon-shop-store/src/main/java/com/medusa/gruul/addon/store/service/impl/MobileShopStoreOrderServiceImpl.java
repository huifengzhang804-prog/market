package com.medusa.gruul.addon.store.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson2.JSON;
import com.medusa.gruul.addon.store.constant.ShopStoreConstant;
import com.medusa.gruul.addon.store.model.dto.StoreOrderStockUpDTO;
import com.medusa.gruul.addon.store.model.vo.ShopStoreOrderInfoVO;
import com.medusa.gruul.addon.store.model.vo.ShopStoreTransactionSummaryVO;
import com.medusa.gruul.addon.store.mp.entity.ShopStore;
import com.medusa.gruul.addon.store.mp.entity.ShopStoreOrder;
import com.medusa.gruul.addon.store.mp.service.IShopStoreOrderService;
import com.medusa.gruul.addon.store.mp.service.IShopStoreService;
import com.medusa.gruul.addon.store.service.MobileShopStoreOrderService;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.order.api.enums.PackageStatus;
import com.medusa.gruul.order.api.model.OrderPackageKeyDTO;
import com.medusa.gruul.order.api.rpc.OrderRpcService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

/**
 * 移动端店铺门店订单服务实现层
 *
 * @author xiaoq
 * @Description 移动端店铺门店订单服务实现层
 * @date 2023-03-16 17:56
 */
@Service
@RequiredArgsConstructor
public class MobileShopStoreOrderServiceImpl implements MobileShopStoreOrderService {
    private final IShopStoreOrderService shopStoreOrderService;

    private final OrderRpcService orderRpcService;

    private final IShopStoreService shopStoreService;

    @Override
    public ShopStoreOrderInfoVO getStoreOrderCodeByOrderNo(Long storeId, String oderNo) {
        ShopStoreOrder shopStoreOrder = shopStoreOrderService.lambdaQuery()
                .select(ShopStoreOrder::getCode, ShopStoreOrder::getPickUpTime)
                .eq(ShopStoreOrder::getOrderNo, oderNo)
                .eq(ShopStoreOrder::getShopStoreId, storeId)
                .ne(ShopStoreOrder::getPackageStatus, PackageStatus.WAITING_FOR_DELIVER).one();

        ShopStore shopStore = shopStoreService.lambdaQuery()
                .select(ShopStore::getStoreName, ShopStore::getFunctionaryPhone,
                        ShopStore::getDetailedAddress).
                eq(BaseEntity::getId, storeId).one();

        if (shopStoreOrder == null || shopStore == null) {
            throw new GlobalException("当前门店或门店订单异常");
        }
        // 设置 ShopStoreOrderInfoVO
        return new ShopStoreOrderInfoVO().setShopStore(shopStore).setCode(shopStoreOrder.getCode()).setGetPickUpTime(shopStoreOrder.getPickUpTime());

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void storeOrderProceedStockUp(StoreOrderStockUpDTO storeOrderStockUp) {
        Long shopId = ISecurity.userMust().getShopId();
        Set<String> orderNos = storeOrderStockUp.getOrderNos();
        Long storeId = storeOrderStockUp.getShopStoreId();
        Long count = shopStoreOrderService.lambdaQuery()
                .eq(ShopStoreOrder::getShopId, shopId)
                .eq(ShopStoreOrder::getPackageStatus, PackageStatus.WAITING_FOR_DELIVER)
                .eq(ShopStoreOrder::getShopStoreId, storeId)
                .in(ShopStoreOrder::getOrderNo, orderNos)
                .count();

        if (count != orderNos.size()) {
            throw new GlobalException("当前备货订单已发生变化,请刷新页面重试");
        }

        Set<String> setCodes = new HashSet<>(storeOrderStockUp.getOrderNos());
        storeOrderStockUp.getOrderNos()
                .forEach(bean -> {
                    String code;
                    // 生成当前店铺下不重复的核销码
                    do {
                        code = RandomUtil.randomNumbers(CommonPool.NUMBER_TEN);
                        Long quantity = shopStoreOrderService.lambdaQuery().eq(ShopStoreOrder::getCode, code).eq(ShopStoreOrder::getShopId, shopId).count();
                        if (quantity == 0L && !setCodes.contains(code)) {
                            break;
                        }
                    } while (true);
                    setCodes.add(code);
                    //修改门店店铺订单
                    boolean update = shopStoreOrderService.lambdaUpdate()
                            .set(ShopStoreOrder::getCode, code)
                            .set(ShopStoreOrder::getPackageStatus, PackageStatus.WAITING_FOR_RECEIVE)
                            .eq(ShopStoreOrder::getShopId, shopId)
                            .eq(ShopStoreOrder::getPackageStatus, PackageStatus.WAITING_FOR_DELIVER)
                            .eq(ShopStoreOrder::getShopStoreId, storeId)
                            .eq(ShopStoreOrder::getOrderNo, bean)
                            .update();

                    if (!update) {
                        throw new GlobalException("门店订单备货失败");
                    }

                });
        // 修改订单状态
        orderRpcService.batchStoreOrderDeliver(orderNos, storeId);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Redisson(value = ShopStoreConstant.SHOP_STORE_ISSUE_LOCK, key = "#storeId+':'+#code")
    public void storeOrderVerification(Long storeId, String code) {
        ShopStoreOrder shopStoreOrder = shopStoreOrderService.lambdaQuery().eq(ShopStoreOrder::getShopId, ISecurity.userMust().getShopId()).eq(ShopStoreOrder::getShopStoreId, storeId).eq(ShopStoreOrder::getCode, code).one();

        if (shopStoreOrder == null) {
            throw new GlobalException("不是本门店订单，请认真核对");
        }
        if (shopStoreOrder.getPackageStatus() != PackageStatus.WAITING_FOR_RECEIVE) {
            throw new GlobalException("订单已核销过");
        }
        shopStoreOrder.setPackageStatus(PackageStatus.BUYER_WAITING_FOR_COMMENT);

        shopStoreOrderService.updateById(shopStoreOrder);
        OrderPackageKeyDTO orderPackageKey = JSON.parseObject(shopStoreOrder.getOrderPackage(), OrderPackageKeyDTO.class);
        // 门店订单进行核销
        orderRpcService.storeOrderConfirmPackage(orderPackageKey);

    }

    @Override
    public ShopStoreTransactionSummaryVO getStoreTransactionSummary(Long storeId) {
        ShopStoreTransactionSummaryVO storeTransactionSummary = shopStoreOrderService.getStoreTransactionSummary(storeId, ISecurity.userMust().getShopId());
        // 使用变量存储总金额和交易笔数，以避免重复计算
        Long todayTotalAmount = storeTransactionSummary.getTodayTotalAmount();
        Long todayTransactionCount = storeTransactionSummary.getTodayTransactionCount();
        Long monthTotalAmount = storeTransactionSummary.getMonthTotalAmount();
        Long monthTransactionCount = storeTransactionSummary.getMonthTransactionCount();

        // 计算平均交易金额
        Long todayAverageTransactionValue = todayTransactionCount != 0 ? todayTotalAmount / todayTransactionCount : 0L;
        Long monthAverageTransactionValue = monthTransactionCount != 0 ? monthTotalAmount / monthTransactionCount : 0L;


        storeTransactionSummary.setTodayAverageTransactionValue(todayAverageTransactionValue).setMonthAverageTransactionValue(monthAverageTransactionValue);
        return storeTransactionSummary;

    }
}
