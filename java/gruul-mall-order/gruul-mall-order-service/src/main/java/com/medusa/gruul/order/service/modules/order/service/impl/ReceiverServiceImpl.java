package com.medusa.gruul.order.service.modules.order.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.order.api.addon.freight.FreightParam;
import com.medusa.gruul.order.api.addon.freight.PlatformFreightParam;
import com.medusa.gruul.order.api.addon.freight.ShopFreightParam;
import com.medusa.gruul.order.api.addon.freight.SkuInfoParam;
import com.medusa.gruul.order.api.entity.Order;
import com.medusa.gruul.order.api.entity.OrderReceiver;
import com.medusa.gruul.order.api.entity.ShopOrder;
import com.medusa.gruul.order.api.entity.ShopOrderItem;
import com.medusa.gruul.order.api.enums.ItemStatus;
import com.medusa.gruul.order.api.enums.ShopOrderStatus;
import com.medusa.gruul.order.service.model.dto.ReceiverChangeDTO;
import com.medusa.gruul.order.service.model.enums.OrderError;
import com.medusa.gruul.order.service.modules.order.addon.OrderAddonDistributionSupporter;
import com.medusa.gruul.order.service.modules.order.service.ReceiverService;
import com.medusa.gruul.order.service.mp.service.IOrderReceiverService;
import com.medusa.gruul.order.service.mp.service.IOrderService;
import com.medusa.gruul.order.service.mp.service.IShopOrderItemService;
import com.medusa.gruul.order.service.mp.service.IShopOrderService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 张治保
 * date 2022/8/2
 */
@Service
@RequiredArgsConstructor
public class ReceiverServiceImpl implements ReceiverService {

    private final IOrderService orderService;
    private final IShopOrderService shopOrderService;
    private final IShopOrderItemService shopOrderItemService;
    private final IOrderReceiverService orderReceiverService;
    private final OrderAddonDistributionSupporter orderAddonDistributionSupporter;

    /**
     * 获取店铺运费参数map  店铺id -> 运费模板id -> 商品信息
     *
     * @param shopOrderItems 商品信息列表
     * @return 店铺运费参数map  店铺id -> 运费模板id -> 商品信息
     */
    @NotNull
    private static Map<Long, Map<Long, List<SkuInfoParam>>> getShopFreightParamMap(List<ShopOrderItem> shopOrderItems) {
        Map<Long, Map<Long, List<SkuInfoParam>>> toFreightMap = new HashMap<>();
        for (ShopOrderItem shopOrderItem : shopOrderItems) {
            Long shopId = shopOrderItem.getShopId();
            if (SellType.CONSIGNMENT == shopOrderItem.getSellType()) {
                shopId = shopOrderItem.getSupplierId();
            }
            Map<Long, List<SkuInfoParam>> freightMap = toFreightMap.computeIfAbsent(shopId, k -> new HashMap<>());
            List<SkuInfoParam> skuInfoParams = freightMap.computeIfAbsent(shopOrderItem.getFreightTemplateId(), k -> new ArrayList<>());
            SkuInfoParam skuInfoParam = new SkuInfoParam();
            skuInfoParam.setWeight(shopOrderItem.getWeight());
            skuInfoParam.setPrice(BigDecimal.valueOf(shopOrderItem.getSalePrice()));
            skuInfoParam.setNum(shopOrderItem.getNum());
            skuInfoParam.setSkuId(shopOrderItem.getSkuId());
            skuInfoParams.add(skuInfoParam);
        }
        return toFreightMap;
    }

    @Override
    public void updateReceiver(String orderNo, ReceiverChangeDTO receiver) {
        //检查主订单
        Order order = orderService.lambdaQuery()
                .select(Order::getDistributionMode, Order::getStatus)
                .eq(Order::getNo, orderNo)
                .one();
        if (order.getStatus().isClosed()) {
            throw OrderError.ORDER_CLOSED.exception();
        }
        String shopOrderNo = receiver.getShopOrderNo();
        Long shopId = receiver.getShopId();
        //检查店铺订单
        boolean selectedShop = StrUtil.isNotEmpty(shopOrderNo) || shopId != null;
        if (selectedShop) {
            Long finalShopId = shopId;
            ShopOrder shopOrder = TenantShop.disable(
                    () -> shopOrderService.lambdaQuery()
                            .select(ShopOrder::getShopId)
                            .eq(ShopOrder::getOrderNo, orderNo)
                            .eq(ShopOrder::getStatus, ShopOrderStatus.OK)
                            .eq(StrUtil.isNotEmpty(shopOrderNo), ShopOrder::getNo, shopOrderNo)
                            .eq(finalShopId != null, ShopOrder::getShopId, finalShopId)
                            .one()
            );
            if (shopOrder == null) {
                throw OrderError.ORDER_CLOSED.exception();
            }
            shopId = shopOrder.getShopId();
        }
        Long selectedShopId = shopId;
        DistributionMode distributionMode = order.getDistributionMode();
        //如果免运费 校验是否有未发货商品即可
        if (distributionMode.isNoFreight()) {
            //检查是否有未发货商品
            boolean exists = TenantShop.disable(
                    () -> shopOrderItemService.lambdaQuery()
                            .eq(ShopOrderItem::getOrderNo, orderNo)
                            .eq(ShopOrderItem::getStatus, ItemStatus.OK)
                            .eq(selectedShop, ShopOrderItem::getShopId, selectedShopId)
                            .isNull(ShopOrderItem::getPackageId)
                            .exists()
            );
            OrderError.NOT_CONTAIN_WAIT_DELIVER_GOODS.falseThrow(exists);
        } else {
            List<ShopOrderItem> shopOrderItems = TenantShop.disable(
                    () -> shopOrderItemService.lambdaQuery()
                            .select(
                                    ShopOrderItem::getShopId, ShopOrderItem::getSkuId, ShopOrderItem::getFreightTemplateId,
                                    ShopOrderItem::getWeight, ShopOrderItem::getNum, ShopOrderItem::getSalePrice,
                                    ShopOrderItem::getSellType, ShopOrderItem::getSupplierId
                            )
                            .eq(ShopOrderItem::getOrderNo, orderNo)
                            .eq(ShopOrderItem::getStatus, ItemStatus.OK)
                            .eq(selectedShop, ShopOrderItem::getShopId, selectedShopId)
                            .isNull(ShopOrderItem::getPackageId)
                            .list()
            );
            OrderError.NOT_CONTAIN_WAIT_DELIVER_GOODS.trueThrow(CollUtil.isEmpty(shopOrderItems));
            Map<Long, Map<Long, List<SkuInfoParam>>> toFreightMap = getShopFreightParamMap(shopOrderItems);
            //触发重新计算运费 以校验是否在配送范围内
            PlatformFreightParam freightParam = new PlatformFreightParam();
            freightParam.setFreeRight(Boolean.FALSE);
            freightParam.setLocation(receiver.getLocation());
            freightParam.setArea(receiver.getArea());
            freightParam.setAddress(receiver.getAddress());
            freightParam.setShopFreights(
                    toFreightMap.entrySet()
                            .stream()
                            .map(entry -> {
                                ShopFreightParam shopFreightParam = new ShopFreightParam();
                                shopFreightParam.setShopId(entry.getKey());
                                shopFreightParam.setFreights(entry.getValue().entrySet()
                                        .stream()
                                        .map(innerEntry -> {
                                            FreightParam param = new FreightParam();
                                            param.setTemplateId(innerEntry.getKey());
                                            param.setSkuInfos(innerEntry.getValue());
                                            return param;
                                        })
                                        .toList());
                                return shopFreightParam;
                            })
                            .toList()
            );
            //todo 暂用于校验是否在配送范围内 、没有重新处理运费，设置到二次退款、支付的问题
            distributionMode
                    .getFunction()
                    .apply(innerMode -> orderAddonDistributionSupporter.distributionCost(innerMode, freightParam));
        }

        OrderReceiver orderReceiver = orderReceiverService.lambdaQuery()
                .eq(OrderReceiver::getOrderNo, orderNo)
                .eq(selectedShop, OrderReceiver::getShopOrderNo, shopOrderNo)
                .eq(selectedShop, OrderReceiver::getShopId, selectedShopId)
                .isNull(!selectedShop, OrderReceiver::getShopOrderNo)
                .isNull(!selectedShop, OrderReceiver::getShopId)
                .one();
        if (orderReceiver == null) {
            orderReceiver = new OrderReceiver()
                    .setShopOrderNo(shopOrderNo)
                    .setShopId(selectedShopId)
                    .setOrderNo(orderNo);
        }
        orderReceiver.setName(receiver.getName())
                .setMobile(receiver.getMobile())
                .setLocation(receiver.getLocation())
                .setArea(receiver.getArea())
                .setAddress(receiver.getAddress());
        boolean success = orderReceiverService.saveOrUpdate(orderReceiver);
        SystemCode.DATA_UPDATE_FAILED.falseThrow(success);
    }
}
